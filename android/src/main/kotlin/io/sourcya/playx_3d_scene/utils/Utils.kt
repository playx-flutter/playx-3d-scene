package io.sourcya.playx_3d_scene.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.sourcya.playx_3d_scene.core.model.common.deserialisers.ModelDeserializer
import io.sourcya.playx_3d_scene.core.model.common.model.Model
import io.sourcya.playx_3d_scene.core.scene.indirect_light.model.IndirectLight
import io.sourcya.playx_3d_scene.core.scene.light.deserialisers.LightDeserializer
import io.sourcya.playx_3d_scene.core.scene.skybox.deserialisers.SkyboxDeserializer
import io.sourcya.playx_3d_scene.core.scene.skybox.model.Skybox
import io.sourcya.playx_3d_scene.core.shape.common.deserialisers.ShapeDeserializer
import io.sourcya.playx_3d_scene.core.shape.common.model.Shape

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
    .registerTypeAdapter(Shape::class.java, ShapeDeserializer())
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


