package io.sourcya.playx_3d_scene.core.utils

sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null
) {
 //   class Loading <out T>(data: T) : Resource<T>()
    class Success<out T>(data: T) : Resource<T>(data)
    class Error<out T>(message: String, data: T? = null) : Resource<T>(data, message)
}
