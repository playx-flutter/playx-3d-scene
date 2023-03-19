package io.sourcya.playx_3d_scene.core.models.shapes

import io.sourcya.playx_3d_scene.core.models.scene.material.Material

 class Sphere(
     id:Int? = null,
     centerPosition: Position? = null,
     material: Material? = null,
     val radius : Float? = null,
     val  stacks: Int?,
     val slices: Int?,

     ) : Shape(id,centerPosition , null, material)