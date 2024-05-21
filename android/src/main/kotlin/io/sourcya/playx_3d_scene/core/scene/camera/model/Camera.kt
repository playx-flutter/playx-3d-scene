package io.sourcya.playx_3d_scene.core.scene.camera.model

import com.google.android.filament.utils.Manipulator
import com.google.android.filament.utils.Manipulator.Mode
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.utils.gson

data class Camera (
     val exposure: Exposure? = null,
     val projection: Projection? = null,
     val lensProjection : LensProjection? = null,
     val scaling: DoubleArray? = null,
     val shift: DoubleArray? = null,

     val mode :Mode? =null,
     val targetPosition: Position? = null,
     val upVector: Position? = null,
     val zoomSpeed:Float? = null,
    //orbit
     val orbitHomePosition : Position? = null,
     val orbitSpeed :FloatArray? = null,
     val fovDirection: Manipulator.Fov? = null,
     val fovDegrees :Float? = null,
     val farPlane :Float? = null,
    //map
     val mapExtent :FloatArray? = null,
     val mapMinDistance :Float? = null,
    //freeflight
     val flightStartPosition:Position? = null,
     val flightStartOrientation :FloatArray? = null,
     val flightMaxMoveSpeed :Float? = null,
     val flightSpeedSteps:Int? = null,
     val flightMoveDamping :Float? = null,
     val groundPlane:FloatArray? = null,

     ) {
     override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (javaClass != other?.javaClass) return false

          other as io.sourcya.playx_3d_scene.core.scene.camera.model.Camera

          if (exposure != other.exposure) return false
          if (projection != other.projection) return false
          if (lensProjection != other.lensProjection) return false
          if (scaling != null) {
               if (other.scaling == null) return false
               if (!scaling.contentEquals(other.scaling)) return false
          } else if (other.scaling != null) return false
          if (shift != null) {
               if (other.shift == null) return false
               if (!shift.contentEquals(other.shift)) return false
          } else if (other.shift != null) return false
          if (mode != other.mode) return false
          if (targetPosition != other.targetPosition) return false
          if (upVector != other.upVector) return false
          if (zoomSpeed != other.zoomSpeed) return false
          if (orbitHomePosition != other.orbitHomePosition) return false
          if (orbitSpeed != null) {
               if (other.orbitSpeed == null) return false
               if (!orbitSpeed.contentEquals(other.orbitSpeed)) return false
          } else if (other.orbitSpeed != null) return false
          if (fovDirection != other.fovDirection) return false
          if (fovDegrees != other.fovDegrees) return false
          if (farPlane != other.farPlane) return false
          if (mapExtent != null) {
               if (other.mapExtent == null) return false
               if (!mapExtent.contentEquals(other.mapExtent)) return false
          } else if (other.mapExtent != null) return false
          if (mapMinDistance != other.mapMinDistance) return false
          if (flightStartPosition != other.flightStartPosition) return false
          if (flightStartOrientation != null) {
               if (other.flightStartOrientation == null) return false
               if (!flightStartOrientation.contentEquals(other.flightStartOrientation)) return false
          } else if (other.flightStartOrientation != null) return false
          if (flightMaxMoveSpeed != other.flightMaxMoveSpeed) return false
          if (flightSpeedSteps != other.flightSpeedSteps) return false
          if (flightMoveDamping != other.flightMoveDamping) return false
          if (groundPlane != null) {
               if (other.groundPlane == null) return false
               if (!groundPlane.contentEquals(other.groundPlane)) return false
          } else if (other.groundPlane != null) return false

          return true
     }

     override fun hashCode(): Int {
          var result = exposure?.hashCode() ?: 0
          result = 31 * result + (projection?.hashCode() ?: 0)
          result = 31 * result + (lensProjection?.hashCode() ?: 0)
          result = 31 * result + (scaling?.contentHashCode() ?: 0)
          result = 31 * result + (shift?.contentHashCode() ?: 0)
          result = 31 * result + (mode?.hashCode() ?: 0)
          result = 31 * result + (targetPosition?.hashCode() ?: 0)
          result = 31 * result + (upVector?.hashCode() ?: 0)
          result = 31 * result + (zoomSpeed?.hashCode() ?: 0)
          result = 31 * result + (orbitHomePosition?.hashCode() ?: 0)
          result = 31 * result + (orbitSpeed?.contentHashCode() ?: 0)
          result = 31 * result + (fovDirection?.hashCode() ?: 0)
          result = 31 * result + (fovDegrees?.hashCode() ?: 0)
          result = 31 * result + (farPlane?.hashCode() ?: 0)
          result = 31 * result + (mapExtent?.contentHashCode() ?: 0)
          result = 31 * result + (mapMinDistance?.hashCode() ?: 0)
          result = 31 * result + (flightStartPosition?.hashCode() ?: 0)
          result = 31 * result + (flightStartOrientation?.contentHashCode() ?: 0)
          result = 31 * result + (flightMaxMoveSpeed?.hashCode() ?: 0)
          result = 31 * result + (flightSpeedSteps ?: 0)
          result = 31 * result + (flightMoveDamping?.hashCode() ?: 0)
          result = 31 * result + (groundPlane?.contentHashCode() ?: 0)
          return result
     }


     companion object {
          fun fromMap(map: Map<String?, Any?>?): Camera? {
               if (map == null) return null
               val json = gson.toJson(map)
               return gson.fromJson(json, Camera::class.java)
          }
     }

}


data class Projection(
     val projection: com.google.android.filament.Camera.Projection? = null,
     val left: Double? = null,
     val right: Double? = null,
     val bottom: Double? = null,
     val top: Double? = null,
     val near: Double? = null,
     val far: Double? = null,


     val fovInDegrees: Double? = null,
     val aspect: Double? = null,
     val direction : com.google.android.filament.Camera.Fov? = null,


     ){
        companion object {
            fun fromMap(map: Map<String?, Any?>?): Projection? {
                if (map == null) return null
                val json = gson.toJson(map)
                return gson.fromJson(json, Projection::class.java)
            }
        }
}

data class LensProjection(
     val focalLength: Double?= null,
     val aspect: Double? = null,
     val near: Double? = null,
     val far: Double? = null,
    ){
        companion object {
            fun fromMap(map: Map<String?, Any?>?): LensProjection? {
                if (map == null) return null
                val json = gson.toJson(map)
                return gson.fromJson(json, LensProjection::class.java)
            }
        }
}

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

){
     companion object {
          fun fromMap(map: Map<String?, Any?>?): Exposure? {
               if (map == null) return null
               val json = gson.toJson(map)
               return gson.fromJson(json, Exposure::class.java)
          }
     }
}