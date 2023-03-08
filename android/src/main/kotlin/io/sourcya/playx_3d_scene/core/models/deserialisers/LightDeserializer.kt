package io.sourcya.playx_3d_scene.core.models.deserialisers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.sourcya.playx_3d_scene.core.models.model.Model
import io.sourcya.playx_3d_scene.core.models.scene.*
import io.sourcya.playx_3d_scene.utils.gson
import java.lang.reflect.Type

class LightDeserializer:JsonDeserializer<Light>{
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Light? {
        return when(json?.asJsonObject?.get("lightType")?.asInt){
            1-> gson.fromJson(json, object : TypeToken<KtxLight>() {}.type)
            2-> gson.fromJson(json, object : TypeToken<HdrLight>() {}.type)
            3-> gson.fromJson(json, object : TypeToken<IndirectLight>() {}.type)
            else -> null
        }
    }

}