package io.sourcya.playx_3d_scene.core.scene.light.model

import com.google.android.filament.LightManager
import io.sourcya.playx_3d_scene.core.shape.common.model.Direction
import io.sourcya.playx_3d_scene.core.shape.common.model.Position


data class Light(
    val type: LightManager.Type? = null,
    val color:String? =null,
    val colorTemperature: Float? =null,
    val intensity : Float? =null,
    val position: Position? =null,
    val direction :Direction? =null,
    val castLight:Boolean? =null,
    val castShadows :Boolean? =null,
    val falloffRadius:Float? =null,
    val spotLightConeInner:Float? =null,
    val spotLightConeOuter:Float? =null,
    val sunAngularRadius:Float? =null,
    val sunHaloSize:Float? =null,
    val sunHaloFalloff:Float? =null,
) {
}
