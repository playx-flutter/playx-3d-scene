package io.sourcya.playx_3d_scene.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

inline fun <reified T> getMapValue(key: String, map : Map<String?, Any?>?, default: T? = null): T? {
    val item = map?.get(key)

    if (item is T) {
        return item
    }
    return default
}

val gson: Gson = GsonBuilder()
    .create()
