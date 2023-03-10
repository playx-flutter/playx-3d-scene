package io.sourcya.playx_3d_scene.core.models.model


 class GlbModel(
     assetPath: String? = null,
    url: String? = null,
    fallback: Model? =null,
    scale: Float? =null,
     centerPosition: FloatArray?,
     animation: Animation? =null,
) : Model(assetPath,url, fallback, scale,centerPosition, animation) {

     override fun toString(): String {
         return super.toString()
     }
 }

