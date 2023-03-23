package io.sourcya.playx_3d_scene.core.model.common.model

import io.sourcya.playx_3d_scene.core.model.animation.model.Animation
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.utils.toObject

abstract class Model(
    val assetPath: String? = null,
    val url: String? = null,
    val fallback: Model?,
    var scale: Float?,
    var centerPosition: Position?,
    val animation: Animation?,
) {


    companion object {
        fun fromMap(map: Map<String?, Any?>?): Model? {
            return map?.toObject<Model>()
        }
    }

    override fun toString(): String {
        return "Model(assetPath=$assetPath, url=$url, fallback=$fallback, position=$centerPosition, scale=$scale, animation=$animation)"
    }

}