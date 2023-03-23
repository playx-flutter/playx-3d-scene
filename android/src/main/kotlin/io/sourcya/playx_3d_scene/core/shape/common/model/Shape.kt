package io.sourcya.playx_3d_scene.core.shape.common.model

import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.utils.convert

abstract class Shape(
    val id:Int? = null,
    val centerPosition: Position? = null,
    val normal: Direction? = null,
    val material: Material? = null
) {
     companion object {
         fun fromJson(shapeList: List<Any>?): List<Shape>? {
             return shapeList.convert()
         }
     }
 }