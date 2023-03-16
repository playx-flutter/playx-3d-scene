package io.sourcya.playx_3d_scene.core.models.scene.camera

import com.google.android.filament.Camera
import com.google.android.filament.utils.Manipulator
import com.google.android.filament.utils.Manipulator.Mode

data class Camera (
     val exposure: Exposure? = null,
     val projection: Projection? = null,
     val lensProjection :LensProjection? = null,
     val scaling: DoubleArray? = null,
     val shift: DoubleArray? = null,

     val mode :Mode? =null,
     val targetPosition: FloatArray? = null,
     val upVector: FloatArray? = null,
     val zoomSpeed:Float? = null,
    //orbit
     val orbitHomePosition : FloatArray? = null,
     val orbitSpeed :FloatArray? = null,
     val fovDirection: Manipulator.Fov? = null,
     val fovDegrees :Float? = null,
     val farPlane :Float? = null,
    //map
     val mapExtent :FloatArray? = null,
     val mapMinDistance :Float? = null,
    //freeflight
     val flightStartPosition:FloatArray? = null,
     val flightStartOrientation :FloatArray? = null,
     val flightMaxMoveSpeed :Float? = null,
     val flightSpeedSteps:Int? = null,
     val flightMoveDamping :Float? = null,
     val groundPlane:FloatArray? = null,
    
    )



data class Projection(
     val projection: Camera.Projection? = null,
     val left: Double? = null,
     val right: Double? = null,
     val bottom: Double? = null,
     val top: Double? = null,
     val near: Double? = null,
     val far: Double? = null,


     val fovInDegrees: Double? = null,
     val aspect: Double? = null,
     val direction : Camera.Fov? = null,


    )

data class LensProjection(
     val focalLength: Double?= null,
     val aspect: Double? = null,
     val near: Double? = null,
     val far: Double? = null,
    )

data class Exposure(
    //Sets this camera's exposure (default is f/16, 1/125s, 100 ISO)
    // The exposure ultimately controls the scene's brightness,
    // just like with a real camera. The default values provide adequate exposure for a camera
    // placed outdoors on a sunny day with the sun at the zenith. With the default parameters,
    // the scene must contain at least one Light of intensity similar to the sun
    // (e.g.: a 100,000 lux directional light) and/or an indirect light of appropriate intensity (30,000).
    //Params:
    //aperture – Aperture in f-stops, clamped between 0.5 and 64. A lower aperture value increases the exposure,
    // leading to a brighter scene. Realistic values are between 0.95 and 32. shutterSpeed – Shutter speed in seconds,
    // clamped between 1/25,000 and 60. A lower shutter speed increases the exposure.
    // Realistic values are between 1/8000 and 30. sensitivity – Sensitivity in ISO, clamped between 10 and 204,800.
    // A higher sensitivity increases the exposure. Realistic values are between 50 and 25600.
     val aperture :Float? = null,
     val shutterSpeed :Float? = null,
     val sensitivity :Float?= null,

    /**
     * Sets this camera's exposure directly. Calling this method will set the aperture to 1.0,
     * the shutter speed to 1.2 and the sensitivity will be computed to match the requested exposure
     * (for a desired exposure of 1.0, the sensitivity will be set to 100 ISO).
     * This method is useful when trying to match the lighting of other engines or tools.
     * Many engines/tools use unit-less light intensities,
     * which can be matched by setting the exposure manually.
     * This can be typically achieved by setting the exposure to 1.0.
     */
     val exposure :Float?= null,

)