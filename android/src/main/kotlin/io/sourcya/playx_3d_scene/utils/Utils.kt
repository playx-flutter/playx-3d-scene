package io.sourcya.playx_3d_scene.utils

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import io.sourcya.playx_3d_scene.core.models.model.Model
import io.sourcya.playx_3d_scene.core.models.scene.light.IndirectLight
import io.sourcya.playx_3d_scene.core.models.scene.Skybox
import io.sourcya.playx_3d_scene.core.models.deserialisers.LightDeserializer
import io.sourcya.playx_3d_scene.core.models.deserialisers.ModelDeserializer
import io.sourcya.playx_3d_scene.core.models.deserialisers.SkyboxDeserializer

inline fun <reified T> getMapValue(key: String, map : Map<String?, Any?>?, default: T? = null): T? {
    val item = map?.get(key)

    if (item is T) {
        return item
    }
    return default
}

val gson: Gson = GsonBuilder()
    .registerTypeAdapter(Model::class.java, ModelDeserializer())
    .registerTypeAdapter(Skybox::class.java, SkyboxDeserializer())
    .registerTypeAdapter(IndirectLight::class.java, LightDeserializer())
    .create()

inline fun <reified T> Map<String?, Any?>.toObject(): T {
    return convert()
}

inline fun <reified T> Map<*, *>.convertToObject(): T {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}


