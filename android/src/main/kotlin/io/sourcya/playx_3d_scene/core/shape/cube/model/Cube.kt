package io.sourcya.playx_3d_scene.core.shape.cube.model

import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.Shape
import io.sourcya.playx_3d_scene.core.shape.common.model.Size

class Cube(
    id:Int? = null,
    centerPosition: Position? = null,
    val size : Size? = null,
    material: Material? = null
) : Shape(id,centerPosition, null, material)