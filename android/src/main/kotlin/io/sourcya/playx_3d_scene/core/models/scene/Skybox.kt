package io.sourcya.playx_3d_scene.core.models.scene

abstract class Skybox(
    var assetPath: String? = null,
    var url: String? = null,
    var color: Int? =null,
    )


class KtxSkybox(
    assetPath: String? = null,
    url: String? = null,
    ):Skybox(assetPath, url,null) {

}

class HdrSkybox(  assetPath: String? = null,
                     url: String? = null,
                   val showSun :Boolean? =null,

):Skybox(assetPath, url,null,) {

    override fun toString(): String {
        return "HdrSkybox(assetPath =$assetPath  showSun=$showSun)"
    }
}

class ColoredSkybox(  color: Int?= null ):Skybox(color = color){


}