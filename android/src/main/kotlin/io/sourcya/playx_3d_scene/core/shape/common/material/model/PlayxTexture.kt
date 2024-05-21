package io.sourcya.playx_3d_scene.core.shape.common.material.model

import com.google.android.filament.TextureSampler
import com.google.android.filament.TextureSampler.MagFilter
import com.google.android.filament.TextureSampler.MinFilter
import com.google.android.filament.utils.TextureType
import io.sourcya.playx_3d_scene.utils.gson

data class PlayxTexture(val assetPath:String?,
                        val url:String?,
                        val type: TextureType?,
                        val sampler: PlayxTextureSampler?
) {
    companion object {
        fun fromJson(map: Map<*, *>?): PlayxTexture? {
            if(map == null) return null
            val json = gson.toJson(map)
            return gson.fromJson(json, PlayxTexture::class.java)
        }
    }
}

data class PlayxTextureSampler(val  min : MinFilter? = MinFilter.LINEAR_MIPMAP_LINEAR,
                               val  mag :MagFilter? = MagFilter.LINEAR,
                               val  wrap : TextureSampler.WrapMode? = TextureSampler.WrapMode.REPEAT,
                               val anisotropy: Float? =null){


    fun toTextureSampler(): TextureSampler {
        val sampler= TextureSampler(
            min?: MinFilter.LINEAR_MIPMAP_LINEAR,
            mag?:MagFilter.LINEAR,
            wrap?:TextureSampler.WrapMode.REPEAT
        )
        if(anisotropy != null) sampler.anisotropy = anisotropy
        return sampler
    }

}