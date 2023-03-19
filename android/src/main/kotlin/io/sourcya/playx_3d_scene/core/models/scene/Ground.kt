package io.sourcya.playx_3d_scene.core.models.scene

import io.sourcya.playx_3d_scene.core.models.scene.material.Material
import io.sourcya.playx_3d_scene.core.models.shapes.Direction
import io.sourcya.playx_3d_scene.core.models.shapes.Plane
import io.sourcya.playx_3d_scene.core.models.shapes.Position
import io.sourcya.playx_3d_scene.core.models.shapes.Size

class Ground (
    centerPosition: Position? = null,
    size : Size? = null,
    val isBelowModel:Boolean = false,
    normal: Direction? = null,
    material: Material? = null
) : Plane(0,centerPosition,size,normal,material)