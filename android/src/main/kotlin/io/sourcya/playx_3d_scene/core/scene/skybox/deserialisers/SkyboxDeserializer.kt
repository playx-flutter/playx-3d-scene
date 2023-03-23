package io.sourcya.playx_3d_scene.core.scene.skybox.deserialisers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.sourcya.playx_3d_scene.core.scene.skybox.model.ColoredSkybox
import io.sourcya.playx_3d_scene.core.scene.skybox.model.HdrSkybox
import io.sourcya.playx_3d_scene.core.scene.skybox.model.KtxSkybox
import io.sourcya.playx_3d_scene.core.scene.skybox.model.Skybox
import io.sourcya.playx_3d_scene.utils.gson
import java.lang.reflect.Type

class SkyboxDeserializer:JsonDeserializer<Skybox>{
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Skybox? {
        return when(json?.asJsonObject?.get("skyboxType")?.asInt){
            1-> gson.fromJson(json, object : TypeToken<KtxSkybox>() {}.type)
            2-> gson.fromJson(json, object : TypeToken<HdrSkybox>() {}.type)
            3-> gson.fromJson(json, object : TypeToken<ColoredSkybox>() {}.type)
            else -> null
        }
    }

}