package io.sourcya.playx_3d_scene.core.model.common.model

import io.sourcya.playx_3d_scene.core.model.animation.model.Animation
import io.sourcya.playx_3d_scene.core.model.glb.model.GlbModel
import io.sourcya.playx_3d_scene.core.model.gltf.model.GltfModel
import io.sourcya.playx_3d_scene.core.shape.common.model.Position

abstract class Model(
    val assetPath: String? = null,
    val url: String? = null,
    val fallback: Model?,
    var scale: Float?,
    var centerPosition: Position?,
    val animation: Animation?,
) {


    companion object {
        fun fromJson(map: Map<String?, Any?>?): Model? {
            if (map == null) return null
            val isGlb = map["isGlb"] as? Boolean? ?: return null
            return if (isGlb)
                GlbModel.fromMap(map)
            else
                GltfModel.fromMap(map)
        }
    }

    override fun toString(): String {
        return "Model(assetPath=$assetPath, url=$url, fallback=$fallback, position=$centerPosition, scale=$scale, animation=$animation)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Model) return false

        if (assetPath != other.assetPath) return false
        if (url != other.url) return false
        if (fallback != other.fallback) return false
        if (scale != other.scale) return false
        if (centerPosition != other.centerPosition) return false
        if (animation != other.animation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = assetPath?.hashCode() ?: 0
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (fallback?.hashCode() ?: 0)
        result = 31 * result + (scale?.hashCode() ?: 0)
        result = 31 * result + (centerPosition?.hashCode() ?: 0)
        result = 31 * result + (animation?.hashCode() ?: 0)
        return result
    }

}