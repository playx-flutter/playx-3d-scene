package io.sourcya.playx_3d_scene.core.models.scene

import io.sourcya.playx_3d_scene.utils.toObject

data class Scene(
    val skybox: Skybox? = null,
    var indirectLight: IndirectLight? = null,
    val camera: Camera? = null,
    val ground: Ground? = null,
) {

    companion object {
        fun fromMap(map: Map<String?, Any?>?): Scene? {
            return map?.toObject<Scene>()
        }

    }

}


