package io.sourcya.playx_3d_scene.core.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.sourcya.playx_3d_scene.core.models.model.GlbModel
import io.sourcya.playx_3d_scene.core.models.model.GltfModel
import io.sourcya.playx_3d_scene.core.models.model.Model
import io.sourcya.playx_3d_scene.utils.gson
import java.lang.reflect.Type

class ModelDeserializer:JsonDeserializer<Model>{
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Model? {
        val isGlb = json?.asJsonObject?.get("isGlb")?.asBoolean
       return if (isGlb == null) {
            null
        } else {
            if (isGlb) {
                return gson.fromJson(json, object : TypeToken<GlbModel>() {}.type)
            } else {
                return gson.fromJson(json, object : TypeToken<GltfModel>() {}.type)
            }
        }
    }

}