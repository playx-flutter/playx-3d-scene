package io.sourcya.playx_3d_scene.core.scene.ground.model

import io.sourcya.playx_3d_scene.core.shape.common.model.Direction
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.Size
import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.plane.model.Plane

 class Ground (
    centerPosition: Position? = null,
    size : Size? = null,
    val isBelowModel:Boolean = false,
    normal: Direction? = null,
    material: Material? = null
) : Plane(0,centerPosition,size,normal,material){
    override fun equals(other: Any?): Boolean {
        print("Ground.equals() called $this => $other")
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

}