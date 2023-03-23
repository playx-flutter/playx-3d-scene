package io.sourcya.playx_3d_scene.core.shape.common.material.loader

import android.content.Context
import android.graphics.Bitmap
import com.google.android.filament.Texture
import com.google.android.filament.android.TextureHelper
import com.google.android.filament.utils.TextureType
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.sourcya.playx_3d_scene.core.network.ImageClient
import io.sourcya.playx_3d_scene.core.shape.common.material.model.PlayxTexture
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TextureLoader(
    private val modelViewer: CustomModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterPlugin.FlutterAssets
) {

    val engine = modelViewer.engine


    suspend fun loadTexture(texture: PlayxTexture?): Resource<Texture?> {

        if (texture == null) return Resource.Error("Texture not found")

        return if (!texture.assetPath.isNullOrEmpty()) {
            loadTextureFromAsset(texture.assetPath, texture.type ?: TextureType.NORMAL)
        } else if (!texture.url.isNullOrEmpty()) {
            loadTextureFromUrl(texture.url, texture.type ?: TextureType.NORMAL)
        } else {
            Resource.Error("You must provide texture images asset path or url")
        }
    }


    private suspend fun loadTextureFromAsset(path: String?, type: TextureType): Resource<Texture?> {
        return withContext(Dispatchers.IO) {
            when (val bitmapResult =
                ImageClient.loadImageFromAsset(path, flutterAssets, context)) {
                is Resource.Success -> {
                    val bitmap = bitmapResult.data
                    if (bitmap != null) {
                        return@withContext loadTexture(bitmap, type)
                    } else {
                        return@withContext Resource.Error(
                            bitmapResult.message ?: "Couldn't load texture images from asset"
                        )
                    }
                }
                is Resource.Error -> {
                    return@withContext Resource.Error(
                        bitmapResult.message ?: "Couldn't load glb model from asset"
                    )
                }
            }
        }
    }

    private suspend fun loadTextureFromUrl(
        url: String?, type: TextureType
    ): Resource<Texture?> {
        return if (url.isNullOrEmpty()) {
            Resource.Error("Url is empty")
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val bitmapResult = ImageClient.loadImageFromUrl(url, context)
                    if (bitmapResult is Resource.Success && bitmapResult.data != null) {
                        return@withContext loadTexture(bitmapResult.data, type)
                    } else {
                        return@withContext Resource.Error("Couldn't load texture images from url: $url")
                    }
                } catch (e: Throwable) {
                    return@withContext Resource.Error("Couldn't load texture images from url: $url")
                }

            }
        }
    }


    private fun loadTexture(bitmap: Bitmap, type: TextureType): Resource<Texture> {
        try {
            val texture = Texture.Builder()
                .width(bitmap.width)
                .height(bitmap.height)
                .sampler(Texture.Sampler.SAMPLER_2D)
                .format(internalFormat(type))
                // This tells Filament to figure out the number of mip levels
                .levels(0xff)
                .build(engine)
                .apply {
                    TextureHelper.setBitmap(engine, this, 0, bitmap)
                    this.generateMipmaps(engine)
                }

            return Resource.Success(texture)
        } catch (t: Throwable) {
            return Resource.Error(t.message ?: "Couldn't load textures")
        }

    }

    private fun internalFormat(type: TextureType) = when (type) {
        TextureType.COLOR -> Texture.InternalFormat.SRGB8_A8
        TextureType.NORMAL -> Texture.InternalFormat.RGBA8
        TextureType.DATA -> Texture.InternalFormat.RGBA8
    }


}