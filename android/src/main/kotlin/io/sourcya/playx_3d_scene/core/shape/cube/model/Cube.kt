package io.sourcya.playx_3d_scene.core.shape.cube.model

import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.Shape
import io.sourcya.playx_3d_scene.core.shape.common.model.Size
import io.sourcya.playx_3d_scene.core.shape.common.model.convertJsonToPosition
import io.sourcya.playx_3d_scene.core.shape.common.model.convertJsonToSize
import io.sourcya.playx_3d_scene.utils.getMapValue

class Cube(
    id:Int? = null,
    centerPosition: Position? = null,
    val size : Size? = null,
    material: Material? = null
) : Shape(id,centerPosition, null, material){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cube) return false

        if (size != other.size) return false

        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (size?.hashCode() ?: 0)
        return result
    }

    companion object {
        fun fromMap(map: Map<String?, Any?>?): Cube? {
            if (map == null) return null

            return  Cube(
                id = getMapValue("id", map),
                centerPosition = convertJsonToPosition(getMapValue("centerPosition", map)),
                size = convertJsonToSize(getMapValue("size", map)),
                material = Material.fromJson(getMapValue("material", map))
            )
        }
    }

}