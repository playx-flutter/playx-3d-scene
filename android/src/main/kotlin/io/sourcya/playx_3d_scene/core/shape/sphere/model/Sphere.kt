package io.sourcya.playx_3d_scene.core.shape.sphere.model

import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.shape.common.model.Shape

class Sphere(
    id:Int? = null,
    centerPosition: Position? = null,
    material: Material? = null,
    val radius : Float? = null,
    val  stacks: Int?,
    val slices: Int?,

    ) : Shape(id,centerPosition , null, material)