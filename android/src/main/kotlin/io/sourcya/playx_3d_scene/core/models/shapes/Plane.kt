package io.sourcya.playx_3d_scene.core.models.shapes

import io.sourcya.playx_3d_scene.core.models.scene.material.Material

open class Plane(
     id:Int?= null,
     centerPosition: Position? = null,
     size : Size? = null,
     normal: Direction? = null,
     material: Material? = null
) : Shape(id,centerPosition,size,normal,material)