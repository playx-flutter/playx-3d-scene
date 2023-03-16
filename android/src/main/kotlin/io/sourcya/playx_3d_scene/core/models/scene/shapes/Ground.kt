package io.sourcya.playx_3d_scene.core.models.scene.shapes

import io.sourcya.playx_3d_scene.core.models.scene.material.Material

class Ground (
    val centerPosition:Position? = null,
    val size :Size? = null,
    val normal:Direction? = null,
    val isBelowModel:Boolean = false,
    val material: Material? = null
)