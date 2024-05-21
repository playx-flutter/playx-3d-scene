package io.sourcya.playx_3d_scene.core.scene.indirect_light.model

import io.sourcya.playx_3d_scene.utils.getMapValue


abstract class IndirectLight(
    var assetPath: String? = null,
    var url: String? = null,
    var intensity: Double? = null,

    ){

    override fun toString(): String {
        return "Light(assetPath=$assetPath, url=$url, intensity=$intensity})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IndirectLight

        if (assetPath != other.assetPath) return false
        if (url != other.url) return false
        if (intensity != other.intensity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = assetPath?.hashCode() ?: 0
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (intensity?.hashCode() ?: 0)
        return result
    }

    companion object{
        fun fromJson(map:Map<String?, Any?>?): IndirectLight? {
            if (map == null) return null
            val type= map["lightType"] as Int?
            return when(type){
                1-> KtxIndirectLight.fromJson(map)
                2-> HdrIndirectLight.fromJson(map)
                3-> DefaultIndirectLight.fromJson(map)
                else -> null
            }
        }
    }

    }


class HdrIndirectLight(
     assetPath: String? = null,
     url: String? = null,
     intensity: Double? = null,
    ): IndirectLight(assetPath,url,intensity){
    override fun toString(): String {
        return "HdrIndirectLight(assetPath=$assetPath, url=$url, intensity=$intensity)"
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as HdrIndirectLight

        return true
        }

    override fun hashCode(): Int {
        val result = super.hashCode()
        return result
    }

    companion object{
        fun fromJson(map:Map<String?, Any?>?): HdrIndirectLight? {
            if (map == null) return null
            return HdrIndirectLight(
                assetPath = getMapValue("assetPath", map),
                url = getMapValue("url", map),
                intensity = getMapValue("intensity", map),
            )

        }
    }
    }

class KtxIndirectLight(
    assetPath: String? = null,
    url: String? = null,
    intensity: Double? = null,
): IndirectLight(assetPath,url,intensity){

    override fun toString(): String {
        return "KtxIndirectLight(assetPath=$assetPath, url=$url, intensity=$intensity)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as KtxIndirectLight

        return true
    }

    override fun hashCode(): Int {
        val result = super.hashCode()
        return result
    }

    companion object{
        fun fromJson(map:Map<String?, Any?>?): KtxIndirectLight? {
            if (map == null) return null
            return KtxIndirectLight(
                assetPath = getMapValue("assetPath", map),
                url = getMapValue("url", map),
                intensity = getMapValue("intensity", map),
            )
        }

    }
}

class DefaultIndirectLight(
    intensity: Double? = null,
    val radianceBands: Int? = null,
    val radianceSh: FloatArray? = null,
    val irradianceBands: Int? = null,
    val irradianceSh: FloatArray? = null,
    val rotation:FloatArray? = null,
    ):
    IndirectLight(null,null,intensity){

        override fun toString(): String {
            return "DefaultIndirectLight(intensity=$intensity, radianceBands=$radianceBands, radianceSh=${radianceSh?.contentToString()}, irradianceBands=$irradianceBands, irradianceSh=${irradianceSh?.contentToString()}, rotation=${rotation?.contentToString()})"
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as DefaultIndirectLight

        if (radianceBands != other.radianceBands) return false
        if (radianceSh != null) {
            if (other.radianceSh == null) return false
            if (!radianceSh.contentEquals(other.radianceSh)) return false
        } else if (other.radianceSh != null) return false
        if (irradianceBands != other.irradianceBands) return false
        if (irradianceSh != null) {
            if (other.irradianceSh == null) return false
            if (!irradianceSh.contentEquals(other.irradianceSh)) return false
        } else if (other.irradianceSh != null) return false
        if (rotation != null) {
            if (other.rotation == null) return false
            if (!rotation.contentEquals(other.rotation)) return false
        } else if (other.rotation != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (radianceBands ?: 0)
        result = 31 * result + (radianceSh?.contentHashCode() ?: 0)
        result = 31 * result + (irradianceBands ?: 0)
        result = 31 * result + (irradianceSh?.contentHashCode() ?: 0)
        result = 31 * result + (rotation?.contentHashCode() ?: 0)
        return result
        }

    companion object {
        fun fromJson(map:Map<String?, Any?>?): DefaultIndirectLight? {
            if (map == null) return null
            return DefaultIndirectLight(
                intensity = getMapValue("intensity", map),
                radianceBands = getMapValue("radianceBands", map),
                radianceSh = getMapValue<List<Double>>("radianceSh", map)
                    ?.convertDoubleListToFloatArray(),
                irradianceBands = getMapValue("irradianceBands", map),
                irradianceSh = getMapValue<List<Double>>("irradianceSh", map)
                    ?.convertDoubleListToFloatArray(),
                rotation = getMapValue<List<Double>>("rotation", map)?.convertDoubleListToFloatArray(),
                )
        }
    }



    }

private fun List<Double>?.convertDoubleListToFloatArray(): FloatArray? {
    if (this == null) return null
    return this.map { it.toFloat() }.toFloatArray()

}
