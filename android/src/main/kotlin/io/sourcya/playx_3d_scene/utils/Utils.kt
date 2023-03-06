package io.sourcya.playx_3d_scene.utils

import android.os.Build.VERSION_CODES.O
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import io.sourcya.playx_3d_scene.core.models.model.Model
import java.lang.reflect.Type

inline fun <reified T> getMapValue(key: String, map : Map<String?, Any?>?, default: T? = null): T? {
    val item = map?.get(key)

    if (item is T) {
        return item
    }
    return default
}

val gson = Gson()

inline fun <reified T> Map<String?, Any?>.toObject(): T {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}

val modelGson: Gson = GsonBuilder()
    .registerTypeAdapter(Model::class.java, ModelDeserializer())
    .create()
 fun  Map<String?, Any?>.toModel(): Model? {
    return convertToModel()
}
 fun<I> I.convertToModel(): Model? {

    val json = modelGson.toJson(this)
    return modelGson.fromJson(json, object : TypeToken<Model>() {}.type)
}
