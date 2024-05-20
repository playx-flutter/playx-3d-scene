package io.sourcya.playx_3d_scene.core.model.gltf.model

import io.sourcya.playx_3d_scene.core.model.animation.model.Animation
import io.sourcya.playx_3d_scene.core.model.common.model.Model
import io.sourcya.playx_3d_scene.core.shape.common.model.Position

class GltfModel(
    assetPath: String? = null,
    url: String? = null,
    val pathPrefix: String = "",
    val pathPostfix: String = "",
    fallback: Model? = null,
    scale: Float? = null,
    centerPosition: Position?,
    animation: Animation? = null,
) : Model(assetPath,url, fallback, scale, centerPosition,animation){

    override fun toString(): String {
        return "GltfModel(pathPrefix='$pathPrefix', pathPostfix='$pathPostfix, animation=$animation, fallback=$fallback, position=$centerPosition, scale=$scale)')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as GltfModel

        if (pathPrefix != other.pathPrefix) return false
        if (pathPostfix != other.pathPostfix) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + pathPrefix.hashCode()
        result = 31 * result + pathPostfix.hashCode()
        return result
    }

}