package io.sourcya.playx_3d_scene.core.shape.common.material.model

import com.google.android.filament.TextureSampler
import com.google.android.filament.TextureSampler.MagFilter
import com.google.android.filament.TextureSampler.MinFilter
import com.google.android.filament.textured.TextureType

data class PlayxTexture(val assetPath:String?,
                        val url:String?,
                        val type: TextureType ?,
                        val sampler: PlayxTextureSampler?
)

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