package io.sourcya.playx_3d_scene.core.models.shapes

import io.sourcya.playx_3d_scene.core.models.scene.material.Material
import io.sourcya.playx_3d_scene.utils.convert

abstract class Shape(
    val id:Int? = null,
     val centerPosition: Position? = null,
     val size : Size? = null,
     val normal: Direction? = null,
     val material: Material? = null
) {
     companion object {
         fun fromJson(shapeList: List<Any>?): List<Shape>? {
             return shapeList.convert()
         }
     }
 }