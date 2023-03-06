package io.sourcya.playx_3d_scene.core.models.model

import com.google.android.filament.utils.Float3

 class GlbModel(
     assetPath: String? = null,
    url: String? = null,
    fallback: Model? =null,
    position: Float3? =null,
    scale: Float? =null,
    animation: Animation? =null,
    loadingListener: LoadingListener? =null,
    loadingProgress : Float?  =null,
) : Model(assetPath,url, fallback, position, scale, animation,loadingListener,loadingProgress) {

     override fun toString(): String {
         return super.toString()
     }
 }

