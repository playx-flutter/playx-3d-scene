package io.sourcya.playx_3d_scene.core.models.scene

import com.google.android.filament.LightManager


data class Light(
    val type: LightManager.Type? = null,
    val color:String? =null,
    val colorTemperature: Float? =null,
    val intensity : Float? =null,
    val position: FloatArray? =null,
    val direction :FloatArray? =null,
    val castLight:Boolean? =null,
    val castShadows :Boolean? =null,
    val falloffRadius:Float? =null,
    val spotLightConeInner:Float? =null,
    val spotLightConeOuter:Float? =null,
    val sunAngularRadius:Float? =null,
    val sunHaloSize:Float? =null,
    val sunHaloFalloff:Float? =null,
)
