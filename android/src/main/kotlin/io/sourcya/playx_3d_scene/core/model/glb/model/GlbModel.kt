package io.sourcya.playx_3d_scene.core.model.glb.model

import io.sourcya.playx_3d_scene.core.model.animation.model.Animation
import io.sourcya.playx_3d_scene.core.model.common.model.Model
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.convertJsonToPosition
import io.sourcya.playx_3d_scene.utils.getMapValue


class GlbModel(
    assetPath: String? = null,
    url: String? = null,
    fallback: Model? = null,
    scale: Float? = null,
    centerPosition: Position?,
    animation: Animation? = null,
) : Model(assetPath, url, fallback, scale, centerPosition, animation) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        return true
    }

    override fun hashCode(): Int {
        val result = super.hashCode()
        return result
    }

    companion object {
        fun fromMap(map: Map<String?, Any?>?): GlbModel? {
            if (map == null) return null

            val fallback = fromJson(getMapValue("fallback", map))
            val animation = Animation.fromJson(
                getMapValue("animation", map)
            )
            return GlbModel(
                assetPath = getMapValue("assetPath", map),
                url = getMapValue("url", map),
                fallback = fallback,
                scale = getMapValue("scale", map),
                centerPosition = convertJsonToPosition(getMapValue("centerPosition", map)),
                animation = animation,
            )

        }
    }

}