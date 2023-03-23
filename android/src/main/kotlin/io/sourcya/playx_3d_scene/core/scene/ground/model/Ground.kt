package io.sourcya.playx_3d_scene.core.scene.ground.model

import io.sourcya.playx_3d_scene.core.shape.common.model.Direction
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.Size
import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.plane.model.Plane

class Ground (
    centerPosition: Position? = null,
    size : Size? = null,
    val isBelowModel:Boolean = false,
    normal: Direction? = null,
    material: Material? = null
) : Plane(0,centerPosition,size,normal,material)