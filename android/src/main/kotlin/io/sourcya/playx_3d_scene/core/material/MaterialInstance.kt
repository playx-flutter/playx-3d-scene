
import com.google.android.filament.Colors
import com.google.android.filament.MaterialInstance
import com.google.android.filament.TextureSampler
import io.sourcya.playx_3d_scene.core.material.loader.TextureLoader
import io.sourcya.playx_3d_scene.core.models.scene.material.MaterialParameter
import io.sourcya.playx_3d_scene.core.models.scene.material.MaterialType
import io.sourcya.playx_3d_scene.core.models.scene.material.PlayxTexture
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.utils.colorOf
import io.sourcya.playx_3d_scene.utils.convertToObject
import timber.log.Timber


suspend fun MaterialInstance.setParameter(materialParameter: MaterialParameter, textureLoader: TextureLoader) {

    if (materialParameter.name.isNullOrEmpty() || materialParameter.type == null || materialParameter.value == null) return

    when (materialParameter.type) {
        MaterialType.COLOR -> {
            if (materialParameter.value is String) {
                val value = materialParameter.value
                val color = colorOf(value)
                this.setParameter(
                    materialParameter.name,
                    Colors.RgbaType.SRGB,
                    color.r,
                    color.g,
                    color.b,
                    color.a
                )
            }
        }
        MaterialType.BOOL -> {
            if (materialParameter.value is Boolean) {
                val value = materialParameter.value
                setParameter(materialParameter.name, value)
            }
        }
        MaterialType.BOOL_VECTOR -> {
            if (materialParameter.value is BooleanArray) {
                val value = materialParameter.value
                when (value.size) {
                    1 -> setParameter(materialParameter.name, value[0])
                    2 -> setParameter(materialParameter.name, value[0], value[1])
                    3 -> setParameter(materialParameter.name, value[0], value[1], value[2])
                    4 -> setParameter(
                        materialParameter.name,
                        value[0],
                        value[1],
                        value[2],
                        value[3]
                    )
                    else ->
                        if (value.size > 4) setParameter(
                            materialParameter.name,
                            value[0],
                            value[1],
                            value[2],
                            value[3]
                        )
                }
            }

        }
        MaterialType.FLOAT -> {
            if (materialParameter.value is Float) {
                val value = materialParameter.value
                setParameter(materialParameter.name, value)
            } else if (materialParameter.value is Double) {
                val value = materialParameter.value
                setParameter(materialParameter.name, value.toFloat())
            }
        }
        MaterialType.FLOAT_VECTOR -> {
            if (materialParameter.value is FloatArray) {
                val value = materialParameter.value
                when (value.size) {
                    1 -> setParameter(materialParameter.name, value[0])
                    2 -> setParameter(materialParameter.name, value[0], value[1])
                    3 -> setParameter(materialParameter.name, value[0], value[1], value[2])
                    4 -> setParameter(
                        materialParameter.name,
                        value[0],
                        value[1],
                        value[2],
                        value[3]
                    )
                    else ->
                        if (value.size > 4) setParameter(
                            materialParameter.name,
                            value[0],
                            value[1],
                            value[2],
                            value[3]
                        )
                }
            } else if (materialParameter.value is DoubleArray) {
                val value = materialParameter.value.map { it.toFloat() }
                when (value.size) {
                    1 -> setParameter(materialParameter.name, value[0])
                    2 -> setParameter(materialParameter.name, value[0], value[1])
                    3 -> setParameter(materialParameter.name, value[0], value[1], value[2])
                    4 -> setParameter(
                        materialParameter.name,
                        value[0],
                        value[1],
                        value[2],
                        value[3]
                    )
                    else ->
                        if (value.size > 4) setParameter(
                            name,
                            value[0],
                            value[1],
                            value[2],
                            value[3]
                        )
                }
            }
        }
        MaterialType.INT -> {
            if (materialParameter.value is Int) {
                val value = materialParameter.value
                setParameter(materialParameter.name, value)
            } else if (materialParameter.value is Long) {
                val value = materialParameter.value
                setParameter(materialParameter.name, value.toInt())
            }
        }
        MaterialType.INT_VECTOR -> {
            if (materialParameter.value is IntArray) {
                val value = materialParameter.value
                when (value.size) {
                    1 -> setParameter(materialParameter.name, value[0])
                    2 -> setParameter(materialParameter.name, value[0], value[1])
                    3 -> setParameter(materialParameter.name, value[0], value[1], value[2])
                    4 -> setParameter(
                        materialParameter.name,
                        value[0],
                        value[1],
                        value[2],
                        value[3]
                    )
                    else ->
                        if (value.size > 4) setParameter(
                            materialParameter.name,
                            value[0],
                            value[1],
                            value[2],
                            value[3]
                        )
                }
            } else if (materialParameter.value is LongArray) {
                val value = materialParameter.value.map { it.toInt() }
                when (value.size) {
                    1 -> setParameter(materialParameter.name, value[0])
                    2 -> setParameter(materialParameter.name, value[0], value[1])
                    3 -> setParameter(materialParameter.name, value[0], value[1], value[2])
                    4 -> setParameter(
                        materialParameter.name,
                        value[0],
                        value[1],
                        value[2],
                        value[3]
                    )
                    else ->
                        if (value.size > 4) setParameter(
                            name,
                            value[0],
                            value[1],
                            value[2],
                            value[3]
                        )
                }
            }
        }
        MaterialType.MAT3 -> {
            if (materialParameter.value is FloatArray) {
                val value = materialParameter.value
                setParameter(
                    materialParameter.name,
                    MaterialInstance.FloatElement.FLOAT3,
                    value,
                    0,
                    3
                )

            } else if (materialParameter.value is DoubleArray) {
                val value = materialParameter.value.map { it.toFloat() }.toFloatArray()
                setParameter(
                    materialParameter.name,
                    MaterialInstance.FloatElement.FLOAT3,
                    value,
                    0,
                    3
                )
            }

        }
        MaterialType.MAT4 -> {
            if (materialParameter.value is FloatArray) {
                val value = materialParameter.value
                setParameter(
                    materialParameter.name,
                    MaterialInstance.FloatElement.FLOAT4,
                    value,
                    0,
                    4
                )

            } else if (materialParameter.value is DoubleArray) {
                val value = materialParameter.value.map { it.toFloat() }.toFloatArray()
                setParameter(
                    materialParameter.name,
                    MaterialInstance.FloatElement.FLOAT4,
                    value,
                    0,
                    4
                )
            }
        }
        MaterialType.TEXTURE -> {
            Timber.d("loading Textures : MaterialType.TEXTURE value :${materialParameter.value} ")

            if (materialParameter.value is Map<*, *>) {
                val playxTexture = materialParameter.value.convertToObject<PlayxTexture>()

                val textureResult = textureLoader.loadTexture(playxTexture)
                Timber.d("loading Textures:${playxTexture} with result :${textureResult.message} Success :${textureResult is Resource.Success}")
                if (textureResult is Resource.Success) {
                    val texture = textureResult.data
                    Timber.d("loading Textures set :texture :${texture == null}}")

                    if (texture != null) {
                        val sampler = playxTexture.sampler?.toTextureSampler() ?: TextureSampler()
                        setParameter(materialParameter.name, texture, sampler)
                    }
                }

            }


        }
    }
}

