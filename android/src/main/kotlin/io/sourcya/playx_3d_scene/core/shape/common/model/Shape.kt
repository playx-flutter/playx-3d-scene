package io.sourcya.playx_3d_scene.core.shape.common.model

import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.cube.model.Cube
import io.sourcya.playx_3d_scene.core.shape.plane.model.Plane
import io.sourcya.playx_3d_scene.core.shape.sphere.model.Sphere
import io.sourcya.playx_3d_scene.utils.gson

abstract class Shape(
    val id: Int? = null,
    val centerPosition: Position? = null,
    val normal: Direction? = null,
    val material: Material? = null
) {
    companion object {
        fun fromJsonList(shapeList: List<Map<String?, Any?>>?): List<Shape>? {
            if (shapeList == null) return null
            val shapes=   shapeList.map {
                val map = it
                fromMap(map)
            }
            return  shapes.filterNotNull()
        }

        fun fromMap(map: Map<String?, Any?>?): Shape? {
                if (map == null) return  null
                val type = map["shapeType"] as Int?
                return when (type) {
                    1 -> gson.fromJson(gson.toJson(map), Plane::class.java)
                    2 -> gson.fromJson(gson.toJson(map), Cube::class.java)
                    3 -> gson.fromJson(gson.toJson(map), Sphere::class.java)
                    else -> null
                }


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