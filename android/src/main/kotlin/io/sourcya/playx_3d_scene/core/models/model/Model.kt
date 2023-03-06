package io.sourcya.playx_3d_scene.core.models.model

import com.google.android.filament.utils.Float3
import io.sourcya.playx_3d_scene.utils.getMapValue
import io.sourcya.playx_3d_scene.utils.toObject

abstract class Model(
   val  assetPath: String? = null,
   val url: String? = null,
    val fallback: Model?,
    val position: Float3?,
    val scale: Float?,
    val animation: Animation?,
    val loadingListener: LoadingListener?,
    val loadingProgress: Float?,

    ) {






    fun changeLoadingState(loading: Boolean) {
        loadingListener?.onLoadingChanged(loading)
    }



    companion object {
        fun fromMap(map: Map<String?, Any?>?): Model? {
            return map?.let {
                val isGlb = getMapValue<Boolean>("isGlb", map)
                if (isGlb == null) {
                    null
                } else {
                    if (isGlb) {
                        return it.toObject<GlbModel>();
                    } else {
                        return it.toObject<GltfModel>();
                    }
                }
            }

        }
    }
    override fun toString(): String {
        return "Model(assetPath=$assetPath, url=$url, fallback=$fallback, position=$position, scale=$scale, animation=$animation, loadingListener=$loadingListener, loadingProgress=$loadingProgress)"
    }

}