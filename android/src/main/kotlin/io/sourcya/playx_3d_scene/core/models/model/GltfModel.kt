package io.sourcya.playx_3d_scene.core.models.model

import com.google.android.filament.utils.Float3

class GltfModel(
    assetPath: String? = null,
    url: String? = null,
    val pathPrefix: String = "",
    val pathPostfix: String = "",
    fallback: Model? = null,
    position: Float3? = null,
    scale: Float? = null,
    animation: Animation? = null,
    loadingProgress: Float? = null,
) : Model(assetPath,url, fallback, position, scale, animation, loadingProgress){

    override fun toString(): String {
        return "GltfModel(pathPrefix='$pathPrefix', pathPostfix='$pathPostfix, animation=$animation, fallback=$fallback, position=$position, scale=$scale)')"
    }
}