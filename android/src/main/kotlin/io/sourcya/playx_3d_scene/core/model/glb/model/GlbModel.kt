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
) : Model(assetPath,url, fallback, scale,centerPosition, animation)