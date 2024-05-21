package io.sourcya.playx_3d_scene.core.shape.sphere.model

import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.Shape
import io.sourcya.playx_3d_scene.core.shape.common.model.convertJsonToPosition
import io.sourcya.playx_3d_scene.utils.getMapValue

class Sphere(
    id:Int? = null,
    centerPosition: Position? = null,
    material: Material? = null,
    val radius : Float? = null,
    val  stacks: Int?,
    val slices: Int?,

    ) : Shape(id,centerPosition , null, material) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Sphere) return false

        if (radius != other.radius) return false
        if (stacks != other.stacks) return false
        if (slices != other.slices) return false

        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (radius?.hashCode() ?: 0)
        result = 31 * result + (stacks?.hashCode() ?: 0)
        result = 31 * result + (slices?.hashCode() ?: 0)
        return result
    }

    companion object {
        fun fromMap(map: Map<String?, Any?>?): Sphere? {
            if (map == null) return null

            return Sphere(
                id =getMapValue("id", map),
                centerPosition = convertJsonToPosition(getMapValue("centerPosition", map)),
                material = Material.fromJson(getMapValue("material", map)),
                radius = (getMapValue<Double>("radius", map))?.toFloat(),
                stacks = getMapValue("stacks", map),
                slices = getMapValue("slices", map),
            )
        }
    }
}