package io.sourcya.playx_3d_scene.core.model.gltf.model

import io.sourcya.playx_3d_scene.core.model.animation.model.Animation
import io.sourcya.playx_3d_scene.core.model.common.model.Model
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.convertJsonToPosition
import io.sourcya.playx_3d_scene.utils.getMapValue

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


    companion object{


        fun fromMap(map: Map<String?, Any?>?): GltfModel? {
            if(map == null) return null
            val fallback = fromJson(getMapValue("fallback", map))
            val animation = Animation.fromJson(
                getMapValue("animation", map)
            )
            return GltfModel(
                assetPath = getMapValue("assetPath", map),
                url = getMapValue("url", map),
                pathPrefix = getMapValue("pathPrefix", map) ?: "",
                pathPostfix = getMapValue("pathPostfix", map) ?: "",
                fallback = fallback,
                scale = getMapValue("scale", map),
                centerPosition = convertJsonToPosition(getMapValue("centerPosition", map)),
                animation = animation,

            )

        }
    }

}