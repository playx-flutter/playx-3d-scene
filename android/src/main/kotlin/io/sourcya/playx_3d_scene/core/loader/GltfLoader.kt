package io.sourcya.playx_3d_scene.core.loader

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_3d_scene.core.models.states.ModelState
import io.sourcya.playx_3d_scene.core.network.NetworkClient
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.utils.readAsset
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.*
import java.nio.Buffer
import java.nio.ByteBuffer
import java.util.zip.ZipInputStream


internal class GltfLoader  constructor(
    private val modelViewer: CustomModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterAssets

) {
    suspend fun loadGltfFromAsset(
        path: String?,
        gltfImagePathPrefix: String,
        gltfImagePathPostfix: String,
        scale: Float?,
       isFallback: Boolean = false,

    ) :Resource<String>{
        modelViewer.setModelState( ModelState.LOADING)

        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    bufferResource.data?.let {
                        try {
                                modelViewer.modelLoader.loadModelGltfAsync(it,true,scale) { uri ->
                                    val assetPath = gltfImagePathPrefix + uri + gltfImagePathPostfix
                                    val assetResource = readAsset(assetPath, flutterAssets, context)
                                    assetResource.data
                                }


                        } catch (t: Throwable) {
                            modelViewer.setModelState( ModelState.ERROR)
                            return@withContext Resource.Error("Failed to load gltf")
                        }
                    }
                               modelViewer.setModelState(
                            if(isFallback) ModelState.FALLBACK_LOADED else  ModelState.LOADED)
                    return@withContext Resource.Success("Loaded glb model successfully from ${path ?: ""}")
                }
                is Resource.Error -> {
                    modelViewer.setModelState( ModelState.ERROR)
                    return@withContext Resource.Error(
                        bufferResource.message ?: "Couldn't load gltf model from asset"
                    )
                }
            }

        }

    }

     suspend fun loadGltfFromUrl(url :String?,
                         prefix: String,
                         postfix: String,
                                 scale: Float?,
                                 isFallback: Boolean =false,
     ):Resource<String> {

         Timber.d("My Playx3dScenePlugin  loadGltfFromUrl ")

         modelViewer.setModelState( ModelState.LOADING)

         if(url.isNullOrEmpty()) {
             modelViewer.setModelState( ModelState.ERROR)
             return Resource.Error("url is empty")
         }

         // To alleviate memory pressure, remove the old model before deflating the zip.
        withContext(Dispatchers.Main) {
            modelViewer.destroyModel()
        }

         val zipFile= NetworkClient.downloadZip(url)
         if(zipFile == null){
             modelViewer.setModelState( ModelState.ERROR)
             return Resource.Error("Couldn't download zip file")
         }

         return try {
             // Deflate each resource using the IO dispatcher, one by one.
             var gltfPath: String? = null
             var outOfMemory: String? = null
             val pathToBufferMapping = withContext(Dispatchers.IO) {
                 val deflater = ZipInputStream(zipFile.inputStream())
                 val mapping = HashMap<String, Buffer>()
                 while (true) {
                     val entry = deflater.nextEntry ?: break
                     if (entry.isDirectory) continue

                     // This isn't strictly required, but as an optimization
                     // we ignore common junk that often pollutes ZIP files.
                     if (entry.name.startsWith("__MACOSX")) continue
                     if (entry.name.startsWith(".DS_Store")) continue

                     val uri = entry.name
                     val byteArray: ByteArray? = try {
                         deflater.readBytes()
                     } catch (e: OutOfMemoryError) {
                         outOfMemory = uri
                         break
                     }
                     val buffer = byteArray?.let { ByteBuffer.wrap(it) }
                     buffer?.let { mapping[uri] = it }

                     if (uri.endsWith(".gltf") || uri.endsWith(".glb")) {
                         gltfPath = uri
                     }
                 }
                 mapping
             }

             zipFile.delete()

             if (gltfPath == null) {
                 Timber.d("Could not find .gltf or .glb in the zip.")
                 modelViewer.setModelState( ModelState.ERROR)
                 return Resource.Error("Could not find .gltf or .glb in the zip.")
             }

             if (outOfMemory != null) {
                 Timber.d("Out of memory while deflating $outOfMemory")
                 modelViewer.setModelState( ModelState.ERROR)
                 return Resource.Error("Out of memory while deflating $outOfMemory")
             }

             val gltfBuffer = pathToBufferMapping[gltfPath]!!

             // In a zip file, the gltf file might be in the same folder as resources, or in a different
             // folder. It is crucial to test against both of these cases. In any case, the resource
             // paths are all specified relative to the location of the gltf file.
             withContext(Dispatchers.Main) {
                 if (gltfPath!!.endsWith(".glb")) {
                     modelViewer.modelLoader.loadModelGlb(gltfBuffer,true,scale)
                     modelViewer.setModelState(
                         if(isFallback) ModelState.FALLBACK_LOADED else ModelState.LOADED)
                     return@withContext Resource.Success("Loaded glb model successfully from $url")
                 } else {
                     modelViewer.modelLoader.loadModelGltfAsync(gltfBuffer, true,scale) { uri ->
                         val path = prefix + uri + postfix
                         if (!pathToBufferMapping.contains(path)) {
                             Log.e("Playx3dScene", "Could not find '$uri' in zip using prefix '$prefix' and base path '${gltfPath!!}'")
                         }
                         pathToBufferMapping[path]
                     }
                     modelViewer.setModelState(
                     if(isFallback) ModelState.FALLBACK_LOADED else ModelState.LOADED)

                     return@withContext Resource.Success("Loaded GLTF model successfully from $url")

                 }
             }
         }catch (ex:Throwable){
             modelViewer.setModelState( ModelState.ERROR)
             return Resource.Error("Couldn't download zip file")
         }
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: GltfLoader? = null

        fun getInstance(modelViewer: CustomModelViewer, context: Context,  flutterAssets :FlutterAssets): GltfLoader =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: GltfLoader(modelViewer, context,flutterAssets).also {
                    INSTANCE = it
                }
            }

    }
}

