package io.sourcya.playx_3d_scene.core.shape.common.material.model

import io.sourcya.playx_3d_scene.utils.gson


data class Material(
    //for material
    val assetPath: String? = null,
    val url: String? = null,
    val parameters: List<MaterialParameter>? = null,
    ) {
    companion object {
        fun fromJson(material: Map<String?, Any?>?): Material? {
            if (material == null) return null
            val json = gson.toJson(material)
            return gson.fromJson(json, Material::class.java)
        }
    }
}
