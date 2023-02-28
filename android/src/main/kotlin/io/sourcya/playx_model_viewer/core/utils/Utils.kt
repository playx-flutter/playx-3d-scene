package io.sourcya.playx_model_viewer.core.utils

import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterAssets
import timber.log.Timber
import java.nio.ByteBuffer


    fun readAsset(pathKey:String?, flutterAssets: FlutterAssets,  context : Context): ByteBuffer? {

        val assetName = getAssetPathForFlutter(pathKey,flutterAssets) ?: return null
        Timber.d("Reading asset: $assetName")
        return try {
            val input =  context.assets.open(assetName)
            val bytes = ByteArray(input.available())
            input.read(bytes)
             ByteBuffer.wrap(bytes)
        }catch (_:Throwable){
            null
        }

    }



private fun getAssetPathForFlutter(pathKey:String?, flutterAssets: FlutterAssets): String? {
    return  try {
        val path = pathKey?.let { flutterAssets.getAssetFilePathByName(it) }
        path
    }catch (_:Throwable){
        return null
    }
}

