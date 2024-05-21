package io.sourcya.playx_3d_scene.core.scene.common.model

import io.sourcya.playx_3d_scene.core.scene.camera.model.Camera
import io.sourcya.playx_3d_scene.core.scene.ground.model.Ground
import io.sourcya.playx_3d_scene.core.scene.indirect_light.model.IndirectLight
import io.sourcya.playx_3d_scene.core.scene.light.model.Light
import io.sourcya.playx_3d_scene.core.scene.skybox.model.Skybox
import io.sourcya.playx_3d_scene.utils.getMapValue

data class Scene(
    val skybox: Skybox? = null,
    var light: Light? = null,
    var indirectLight: IndirectLight? = null,
    val camera: Camera? = null,
    val ground: Ground? = null,
) {

    companion object {
        fun fromMap(map: Map<String?, Any?>?): Scene? {
            if (map == null) return null

            return Scene(
                skybox = Skybox.fromMap(getMapValue("skybox", map)),
                light = Light.fromMap(getMapValue("light", map)),
                indirectLight = IndirectLight.fromJson(getMapValue("indirectLight", map)),
                camera = Camera.fromMap(getMapValue("camera", map)),
                ground = Ground.fromMap(getMapValue("ground", map)),
            )

        }

    }

}


