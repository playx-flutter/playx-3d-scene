package io.sourcya.playx_3d_scene.core.scene.ground.model

import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.common.model.Direction
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.Size
import io.sourcya.playx_3d_scene.core.shape.common.model.convertJsonToDirection
import io.sourcya.playx_3d_scene.core.shape.common.model.convertJsonToPosition
import io.sourcya.playx_3d_scene.core.shape.common.model.convertJsonToSize
import io.sourcya.playx_3d_scene.core.shape.plane.model.Plane
import io.sourcya.playx_3d_scene.utils.getMapValue

class Ground(
    centerPosition: Position? = null,
    size: Size? = null,
    val isBelowModel: Boolean = false,
    normal: Direction? = null,
    material: Material? = null
) : Plane(0, centerPosition, size, normal, material) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ground) return false

        if (isBelowModel != other.isBelowModel) return false

        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + isBelowModel.hashCode()
        return result
    }

    companion object {
        fun fromMap(map: Map<String?, Any?>?): Ground? {
            if (map == null) return null
            return Ground(
                centerPosition = convertJsonToPosition(getMapValue("centerPosition", map)),
                size = convertJsonToSize(getMapValue("size", map)),
                normal = convertJsonToDirection(getMapValue("normal", map)),
                material = Material.fromJson(
                    getMapValue("material", map),
                ),
                isBelowModel = getMapValue("isBelowModel", map) ?: false
            )
        }
    }

}