package io.sourcya.playx_3d_scene.core.models.scene.light


abstract class IndirectLight(
    var assetPath: String? = null,
    var url: String? = null,
    var intensity: Double? = null,
   val radianceBands: Int? = null,
    val radianceSh: FloatArray? = null,
    val irradianceBands: Int? = null,
    val irradianceSh: FloatArray? = null,
    val rotation:FloatArray? = null,

    ){

    override fun toString(): String {
        return "Light(assetPath=$assetPath, url=$url, intensity=$intensity, radianceBands=$radianceBands, radianceSh=${radianceSh?.contentToString()}, irradianceBands=$irradianceBands, irradianceSh=${irradianceSh?.contentToString()}, rotation=${rotation?.contentToString()})"
    }
}


class HdrIndirectLight(
     assetPath: String? = null,
     url: String? = null,
     intensity: Double? = null,
    ): IndirectLight(assetPath,url,intensity)

class KtxIndirectLight(
    assetPath: String? = null,
    url: String? = null,
    intensity: Double? = null,
): IndirectLight(assetPath,url,intensity)

class DefaultIndirectLight(
     intensity: Double? = null,
     radianceBands: Int? = null,
     radianceSh: FloatArray? = null,
     irradianceBands: Int? = null,
     irradianceSh: FloatArray? = null,
     rotation:FloatArray? = null,
    ):
    IndirectLight(null,null,intensity,radianceBands,radianceSh,irradianceBands,irradianceSh,rotation){


    }