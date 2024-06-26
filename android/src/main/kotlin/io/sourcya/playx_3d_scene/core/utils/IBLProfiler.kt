package io.sourcya.playx_3d_scene.core.utils

import com.google.android.filament.Engine
import com.google.android.filament.Texture
import com.google.android.filament.utils.IBLPrefilterContext

/**
 * IBLPrefilter creates and initializes GPU state common to all environment map filters
 *
 * Typically, only one instance per filament Engine of this object needs to exist.
 *
 * @see IBLPrefilterContext
 */
class IBLProfiler (engine:Engine){

    val context by lazy { IBLPrefilterContext(engine) }


    /**
     * EquirectangularToCubemap is use to convert an equirectangular image to a cubemap
     *
     * Creates a EquirectangularToCubemap processor.
     */
    private val equirectangularToCubeMap by lazy {
        IBLPrefilterContext.EquirectangularToCubemap(context)
    }


    /**
     * Converts an equirectangular image to a cubemap.
     *
     * @param equirect Texture to convert to a cubemap.
     * - Can't be null.
     * - Must be a 2d texture
     * - Must have equirectangular geometry, that is width == 2*height.
     * - Must be allocated with all mip levels.
     * - Must be SAMPLEABLE
     *
     * @return the cubemap texture
     *
     * @see IBLPrefilterContext.EquirectangularToCubemap
     */
    fun createCubeMapTexture(equirect: Texture): Texture {
       return equirectangularToCubeMap.run(equirect)

    }


    /**
     * Created specular (reflections) filter. This operation generates the kernel, so it's
     * important to keep it around if it will be reused for several cubemaps.
     * An instance of SpecularFilter is needed per filter configuration. A filter configuration
     * contains the filter's kernel and sample count.
     */
    private val specularFilter by lazy { IBLPrefilterContext.SpecularFilter(context) }

    /**
     * Generates a prefiltered cubemap.
     *
     * SpecularFilter is a GPU based implementation of the specular probe pre-integration filter.
     *
     * ** Launch the heaver computation. Expect 100-100ms on the GPU.**
     *
     * @param skybox Environment cubemap.
     * This cubemap is SAMPLED and have all its levels allocated.
     *
     * @return the reflections texture
     */
    fun getLightReflection(skybox: Texture): Texture = specularFilter.run(skybox)

    fun destroy() {
        runCatching { specularFilter.destroy() }
        runCatching { equirectangularToCubeMap.destroy() }
        runCatching { context.destroy() }
    }


}