package io.sourcya.playx_3d_scene.core.models.deserialisers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.sourcya.playx_3d_scene.core.models.scene.light.DefaultIndirectLight
import io.sourcya.playx_3d_scene.core.models.scene.light.HdrIndirectLight
import io.sourcya.playx_3d_scene.core.models.scene.light.IndirectLight
import io.sourcya.playx_3d_scene.core.models.scene.light.KtxIndirectLight
import io.sourcya.playx_3d_scene.utils.gson
import java.lang.reflect.Type

class LightDeserializer:JsonDeserializer<IndirectLight>{
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): IndirectLight? {
        return when(json?.asJsonObject?.get("lightType")?.asInt){
            1-> gson.fromJson(json, object : TypeToken<KtxIndirectLight>() {}.type)
            2-> gson.fromJson(json, object : TypeToken<HdrIndirectLight>() {}.type)
            3-> gson.fromJson(json, object : TypeToken<DefaultIndirectLight>() {}.type)
            else -> null
        }
    }

}