package io.sourcya.playx_3d_scene.core.utils

import android.content.Context
import com.google.android.filament.RenderableManager
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import io.sourcya.playx_3d_scene.core.geometry.Geometry
import java.io.FileNotFoundException
import java.nio.ByteBuffer


fun readAsset(pathKey:String?, flutterAssets: FlutterAssets,  context : Context): Resource<ByteBuffer> {
          val assetResource = getAssetPathForFlutter(pathKey, flutterAssets)
           if (assetResource is Resource.Success) {
               val assetName = assetResource.data ?: ""
               return try {
                   val input = context.assets.open(assetName)
                   val bytes = ByteArray(input.available())
                   input.read(bytes)
                   Resource.Success(ByteBuffer.wrap(bytes))

               } catch (e: FileNotFoundException) {
                   Resource.Error("Couldn't find asset file : $pathKey")
               } catch (t: Throwable) {
                   Resource.Error(t.message ?: "Couldn't load asset")
               }
           }else{
               return Resource.Error(assetResource.message ?: "Couldn't load asset")
           }

    }





 fun getAssetPathForFlutter(pathKey:String?, flutterAssets: FlutterAssets): Resource<String> {

    return if(pathKey.isNullOrEmpty()){
        Resource.Error("Asset Path is empty")
    }else{
        try {
            val path =flutterAssets.getAssetFilePathByName(pathKey)
            Resource.Success(path)
        }catch (e:FileNotFoundException){
            Resource.Error("Couldn't find asset file : $pathKey")
        }catch (t:Throwable){
            Resource.Error(t.message ?:"Couldn't load asset")

        }

    }


}

fun RenderableManager.Builder.geometry(geometry: Geometry) = apply {
    geometry.offsetsCounts.forEachIndexed { primitiveIndex, (offset, count) ->
        geometry(
            primitiveIndex,
            RenderableManager.PrimitiveType.TRIANGLES,
            geometry.vertexBuffer,
            geometry.indexBuffer,
            offset,
            count
        )
    }
    // Overall bounding box of the renderable
    geometry.boundingBox?.let { boundingBox(it) }
}
