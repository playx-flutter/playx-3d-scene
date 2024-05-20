package io.sourcya.playx_3d_scene.core.model.glb.model

import io.sourcya.playx_3d_scene.core.model.animation.model.Animation
import io.sourcya.playx_3d_scene.core.model.common.model.Model
import io.sourcya.playx_3d_scene.core.shape.common.model.Position


class GlbModel(
    assetPath: String? = null,
    url: String? = null,
    fallback: Model? =null,
    scale: Float? =null,
    centerPosition: Position?,
    animation: Animation? =null,
) : Model(assetPath,url, fallback, scale,centerPosition, animation){

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

}