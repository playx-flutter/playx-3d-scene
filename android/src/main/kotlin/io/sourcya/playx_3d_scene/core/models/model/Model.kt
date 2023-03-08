package io.sourcya.playx_3d_scene.core.models.model

import android.renderscript.Float3
import com.google.android.filament.utils.Float3
import io.sourcya.playx_3d_scene.utils.toObject

abstract class Model(
    val  assetPath: String? = null,
    val url: String? = null,
    val fallback: Model?,
    val position: Float3?,
    val scale: Float?,
    val animation: Animation?,
    val loadingProgress: Float?,

    ) {



    companion object {
        fun fromMap(map: Map<String?, Any?>?): Model? {
            return map?.toObject<Model>()
        }
    }
    override fun toString(): String {
        return "Model(assetPath=$assetPath, url=$url, fallback=$fallback, position=$position, scale=$scale, animation=$animation, loadingProgress=$loadingProgress)"
    }

}