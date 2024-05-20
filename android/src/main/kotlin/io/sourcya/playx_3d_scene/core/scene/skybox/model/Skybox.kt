package io.sourcya.playx_3d_scene.core.scene.skybox.model

abstract class Skybox(
    var assetPath: String? = null,
    var url: String? = null,
    var color: String? =null,
    ){

override fun toString(): String {
        return "Skybox(assetPath=$assetPath, url=$url, color=$color)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Skybox

        if (assetPath != other.assetPath) return false
        if (url != other.url) return false
        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int {
        var result = assetPath?.hashCode() ?: 0
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (color?.hashCode() ?: 0)
        return result
    }

}


class KtxSkybox(
    assetPath: String? = null,
    url: String? = null,
    ): Skybox(assetPath, url,null) {

    override fun toString(): String {
        return "KtxSkybox(assetPath=$assetPath)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as KtxSkybox

        return true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

}

class HdrSkybox(  assetPath: String? = null,
                     url: String? = null,
                   val showSun :Boolean? =null,

): Skybox(assetPath, url,null,) {

    override fun toString(): String {
        return "HdrSkybox(assetPath =$assetPath  showSun=$showSun)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as HdrSkybox

        return showSun == other.showSun
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (showSun?.hashCode() ?: 0)
        return result
    }
}

class ColoredSkybox(  color: String?= null ): Skybox(color = color){

    override fun toString(): String {
        return "ColoredSkybox(color=$color)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as ColoredSkybox

        return color == other.color
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (color?.hashCode() ?: 0)
        return result
    }


}