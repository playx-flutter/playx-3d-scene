package io.sourcya.playx_3d_scene.core.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.filament.textured.TextureType
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.utils.getAssetPathForFlutter
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.FileNotFoundException
import kotlin.coroutines.resume

object ImageClient {



    suspend fun loadImageFromAsset(pathKey:String?, flutterAssets: FlutterPlugin.FlutterAssets, context : Context, type: TextureType): Resource<Bitmap> {
        val assetResource = getAssetPathForFlutter(pathKey, flutterAssets)
        return if (assetResource is Resource.Success) {
            val assetName = assetResource.data ?: ""
            try {
                val assetUri = Uri.parse("file:///android_asset/$assetName")
                val bitmap = loadImage(context,assetUri) ?: return Resource.Error("couldn't load image : $pathKey")
                Resource.Success(bitmap)
            } catch (e: FileNotFoundException) {
                Resource.Error("Couldn't find image asset on : $pathKey")
            } catch (t: Throwable) {
                Resource.Error(t.message ?: "Couldn't load asset")
            }
        }else{
            Resource.Error(assetResource.message ?: "Couldn't load asset")
        }

    }


    suspend fun loadImageFromUrl(url:String?,  context : Context, type: TextureType): Resource<Bitmap> {
        return if (!url.isNullOrEmpty()) {
            try {
                val bitmap = loadImage(context,url) ?: return Resource.Error("couldn't load image : $url")
                Resource.Success(bitmap)
            }  catch (t: Throwable) {
                Resource.Error(t.message ?: "Couldn't load asset")
            }
        }else{
            Resource.Error( "texture image url must be provided")
        }

    }


    private suspend fun loadImage(context : Context, uri: Uri) = suspendCancellableCoroutine {
        Glide.with(context)
            .asBitmap()
            .timeout(15_000)
            .load(uri)
            .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                it.resume(value = resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                it.resume(value = null)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                it.resume(value = null)
            }
        })
    }




    private suspend fun loadImage(context : Context, url: String?) = suspendCancellableCoroutine {
        if(url.isNullOrEmpty()) it.resume(null)
        Glide.with(context)
            .asBitmap()
            .timeout(15_000)
            .apply {
            }
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    it.resume(value = resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    it.resume(value = null)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    it.resume(value = null)
                }
            })
    }
}