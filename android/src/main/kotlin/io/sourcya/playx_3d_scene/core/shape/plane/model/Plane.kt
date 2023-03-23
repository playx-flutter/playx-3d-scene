package io.sourcya.playx_3d_scene.core.shape.plane.model

import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.common.model.Direction
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.Shape
import io.sourcya.playx_3d_scene.core.shape.common.model.Size

open class Plane(
    id:Int?= null,
    centerPosition: Position? = null,
    val size : Size? = null,
    normal: Direction? = null,
    material: Material? = null
) : Shape(id,centerPosition,normal,material)