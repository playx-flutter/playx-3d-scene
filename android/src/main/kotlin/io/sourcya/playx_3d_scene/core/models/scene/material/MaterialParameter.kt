package io.sourcya.playx_3d_scene.core.models.scene.material

data class MaterialParameter(
val name:String?,
val type:MaterialType?,
val value:Any?,
)

enum class MaterialType {
    //color can be presented by int or Color like Colors.white
    COLOR,
    BOOL,
    BOOL_VECTOR,
    FLOAT,
    FLOAT_VECTOR,
    INT,
    INT_VECTOR,
    MAT3,
    MAT4,
    TEXTURE,
}

