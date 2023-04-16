package io.sourcya.playx_3d_scene.core.model.gltf.loader

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_3d_scene.core.model.common.model.ModelState
import io.sourcya.playx_3d_scene.core.network.NetworkClient
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.utils.readAsset
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.*
import java.nio.Buffer
import java.nio.ByteBuffer
import java.util.zip.ZipInputStream


internal class GltfLoader constructor(
    private val modelViewer: CustomModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterAssets

) {
    suspend fun loadGltfFromAsset(
        path: String?,
        gltfImagePathPrefix: String,
        gltfImagePathPostfix: String,
        scale: Float?,
        centerPosition: Position?,
        isFallback: Boolean = false,

        ): Resource<String> {
        modelViewer.setModelState(ModelState.LOADING)
        return withContext(Dispatchers.IO) {
            when (val bufferResource = readAsset(path, flutterAssets, context)) {
                is Resource.Success -> {

                    bufferResource.data?.let {
                            kotlin.runCatching {
                                modelViewer.modelLoader.loadModelGltfAsync(
                                    it,
                                    true,
                                    scale,
                                    centerPosition
                                ) { uri ->
                                    val assetPath = gltfImagePathPrefix + uri + gltfImagePathPostfix
                                    val assetResource = readAsset(assetPath, flutterAssets, context)
                                    assetResource.data
                                }
                            }.onFailure {
                                modelViewer.setModelState(ModelState.ERROR)
                                return@withContext Resource.Error("Failed to load gltf")
                            }

                    }
                    modelViewer.setModelState(
                        if (isFallback) ModelState.FALLBACK_LOADED else ModelState.LOADED
                    )
                    return@withContext Resource.Success("Loaded gltf model successfully from ${path ?: ""}")
                }
                is Resource.Error -> {
                    modelViewer.setModelState(ModelState.ERROR)
                    return@withContext Resource.Error(
                        bufferResource.message ?: "Couldn't load gltf model from asset"
                    )
                }
            }
        }

    }

    suspend fun loadGltfFromUrl(
        url: String?,
        prefix: String,
        postfix: String,
        scale: Float?,
        centerPosition: Position?,

        isFallback: Boolean = false,
    ): Resource<String> {

        modelViewer.setModelState(ModelState.LOADING)

        if (url.isNullOrEmpty()) {
            modelViewer.setModelState(ModelState.ERROR)
            return Resource.Error("url is empty")
        }

        // To alleviate memory pressure, remove the old model before deflating the zip.
        withContext(Dispatchers.Main) {
            modelViewer.destroyModel()
        }

        val zipFile = NetworkClient.downloadZip(url)
        if (zipFile == null) {
            modelViewer.setModelState(ModelState.ERROR)
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
                modelViewer.setModelState(ModelState.ERROR)
            }

            if (outOfMemory != null) {
                modelViewer.setModelState(ModelState.ERROR)
                return Resource.Error("Out of memory while deflating $outOfMemory")
            }

            val gltfBuffer = pathToBufferMapping[gltfPath]!!

            // In a zip file, the gltf file might be in the same folder as resources, or in a different
            // folder. It is crucial to test against both of these cases. In any case, the resource
            // paths are all specified relative to the location of the gltf file.
            withContext(Dispatchers.Main) {
                if (gltfPath!!.endsWith(".glb")) {
                    modelViewer.modelLoader.loadModelGlb(gltfBuffer, true, centerPosition, scale)
                    modelViewer.setModelState(
                        if (isFallback) ModelState.FALLBACK_LOADED else ModelState.LOADED
                    )
                    return@withContext Resource.Success("Loaded glb model successfully from $url")
                } else {
                    modelViewer.modelLoader.loadModelGltfAsync(
                        gltfBuffer,
                        true,
                        scale,
                        centerPosition
                    ) { uri ->
                        val path = prefix + uri + postfix
                        pathToBufferMapping[path]
                    }
                    modelViewer.setModelState(
                        if (isFallback) ModelState.FALLBACK_LOADED else ModelState.LOADED
                    )

                    return@withContext Resource.Success("Loaded GLTF model successfully from $url")

                }
            }
        } catch (ex: Throwable) {
            modelViewer.setModelState(ModelState.ERROR)
            return Resource.Error("Couldn't download zip file")
        }
    }



}

