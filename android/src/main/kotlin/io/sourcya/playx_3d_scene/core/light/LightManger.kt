package io.sourcya.playx_3d_scene.core.light

import android.graphics.Color
import com.google.android.filament.Colors
import com.google.android.filament.Entity
import com.google.android.filament.EntityManager
import com.google.android.filament.LightManager
import io.sourcya.playx_3d_scene.core.models.scene.Light
import io.sourcya.playx_3d_scene.core.utils.*
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer

// Always add a direct light source since it is required for shadowing.
// We highly recommend adding an indirect light as well.
internal class LightManger(private val modelViewer: CustomModelViewer) {

    @Entity
    var entityLight: Int = EntityManager.get().create()


    init {
        setDefaultLight()
    }

    fun setDefaultLight() {
        changeLight(
            Light(
                colorTemperature = 6_500.0f,
                intensity = 100_000.0f,
                direction = floatArrayOf(0.0f, -1.0f, 0.0f),
                castShadows = true
            )
        )

    }


    fun changeLight(light: Light?): Resource<String> {

        if (light == null) return Resource.Error("Light not found")
        if (light.type == null) return Resource.Error("Light type must be provided")

        val builder = LightManager.Builder(light.type)

        val color = light.color
        if (color != null) {
            val colorValue = colorOf(color)
            builder.color(colorValue.red(), colorValue.green(), colorValue.blue())
        } else if (light.colorTemperature != null) {
            val (red, green, blue) = Colors.cct(light.colorTemperature)
            builder.color(red, green, blue)

        }

        light.intensity?.let { builder.intensity(it) }
        light.position?.let {
            builder.position(
                it.getOrElse(0) { 0f },
                it.getOrElse(1) { 0f },
                it.getOrElse(2) { 0f })
        }
        light.direction?.let {
            builder.direction(
                it.getOrElse(0) { 0f },
                it.getOrElse(1) { -1f },
                it.getOrElse(2) { 0f })
        }
        light.castLight?.let { builder.castLight(it) }
        light.castShadows?.let { builder.castShadows(it) }
        light.falloffRadius?.let { builder.falloff(it) }
        if (light.spotLightConeInner != null && light.spotLightConeOuter != null) {
            builder.spotLightCone(light.spotLightConeInner, light.spotLightConeOuter)
        }
        light.sunAngularRadius?.let { builder.sunAngularRadius(it) }
        light.sunHaloSize?.let { builder.sunHaloSize(it) }
        light.sunHaloFalloff?.let { builder.sunHaloFalloff(it) }

        builder.build(modelViewer.engine, entityLight)

        modelViewer.scene.removeEntity(entityLight)
        modelViewer.scene.addEntity(entityLight)
        return Resource.Success("Light created Successfully")

    }


    fun destroyLight() {
        modelViewer.engine.destroyEntity(entityLight)
        EntityManager.get().destroy(entityLight)
    }


}