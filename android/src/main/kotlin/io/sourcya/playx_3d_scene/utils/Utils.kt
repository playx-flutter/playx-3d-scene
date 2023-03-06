package io.sourcya.playx_3d_scene.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
