package io.sourcya.playx_3d_scene.core.model.animation.model

import io.sourcya.playx_3d_scene.utils.getMapValue


data class Animation (
    val index: Int? = null,
    val name: String? = null,
    val autoPlay: Boolean = false,
    val duration:Long?= null,
    ){
    companion object{
        fun fromJson(map: Map<String?, Any?>?): Animation? {
            if(map == null) return null
            return  Animation(
                index = getMapValue("index",map),
                name = getMapValue("name",map),
                autoPlay = getMapValue("autoPlay",map) ?: false,
                duration = getMapValue("duration",map)
            )

        }
    }
}