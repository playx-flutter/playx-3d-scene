package io.sourcya.playx_3d_scene.core.shape.common.model

import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.utils.convert

abstract   class Shape(
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

    override fun toString(): String {
        return "Shape(id=$id, centerPosition=$centerPosition, normal=$normal, material=$material)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Shape) return false

        if (id != other.id) return false
        if (centerPosition != other.centerPosition) return false
        if (normal != other.normal) return false
        if (material != other.material) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (centerPosition?.hashCode() ?: 0)
        result = 31 * result + (normal?.hashCode() ?: 0)
        result = 31 * result + (material?.hashCode() ?: 0)
        return result
    }




 }