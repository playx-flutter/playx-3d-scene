package io.sourcya.playx_3d_scene.core.scene.camera

import android.view.MotionEvent
import android.view.SurfaceView
import android.view.TextureView
import androidx.annotation.Size
import com.google.android.filament.EntityManager
import com.google.android.filament.View
import com.google.android.filament.utils.GestureDetector
import com.google.android.filament.utils.Manipulator
import io.sourcya.playx_3d_scene.core.scene.camera.model.Camera
import io.sourcya.playx_3d_scene.core.scene.camera.model.Exposure
import io.sourcya.playx_3d_scene.core.scene.camera.model.LensProjection
import io.sourcya.playx_3d_scene.core.scene.camera.model.Projection
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer.Companion.kDefaultObjectPosition
import com.google.android.filament.Camera as FilamentCamera

class CameraManger(
    private val modelViewer: CustomModelViewer,
    private val width: Int,
    private val height: Int,
    private val view: View,
) {

    var cameraFocalLength = kDefaultFocalLength

    constructor(modelViewer: CustomModelViewer, view: View, textureView: TextureView) : this(
        modelViewer,
        textureView.width,
        textureView.height,
        view
    ) {
        this.textureView = textureView
        setDefaultCamera()
    }

    constructor(modelViewer: CustomModelViewer, view: View, surfaceView: SurfaceView) : this(
        modelViewer,
        surfaceView.width,
        surfaceView.height,
        view
    ) {
        this.surfaceView = surfaceView
        setDefaultCamera()

    }

    private val engine = modelViewer.engine
    lateinit var camera: FilamentCamera
    lateinit var cameraManipulator: Manipulator
    private var gestureDetector: GestureDetector? = null
    private var surfaceView: SurfaceView? = null
    private var textureView: TextureView? = null


    private var eyePos = DoubleArray(3)
    private var target = DoubleArray(3)
    private var upward = DoubleArray(3)

    private var lookEyePositions: DoubleArray? = null
    private var lookTarget: DoubleArray? = null
    private var lookUpward: DoubleArray? = null


    fun updateCamera(cameraInfo: Camera?): Resource<String> {

        if (cameraInfo == null) {
            return Resource.Error("Camera not found")
        }

        val exposure = cameraInfo.exposure
        updateExposure(exposure)
        val projection = cameraInfo.projection
        updateProjection(projection)
        val lensProjection = cameraInfo.lensProjection
        updateLensProjection(lensProjection)
        val shift = cameraInfo.shift
        updateCameraShift(shift)
        val scaling = cameraInfo.scaling
        updateCameraScaling(scaling)

        updateCameraManipulator(cameraInfo)

        return Resource.Success("Camera updated successfully")
    }


    private fun updateCameraManipulator(cameraInfo: Camera?) {
        if (cameraInfo == null) {
            return
        }
        val manipulatorBuilder = Manipulator.Builder()

        if (cameraInfo.targetPosition != null) {
            val x = cameraInfo.targetPosition.x
            val y = cameraInfo.targetPosition.y
            val z = cameraInfo.targetPosition.z
            manipulatorBuilder.targetPosition(x, y, z)
        } else {
            manipulatorBuilder.targetPosition(
                kDefaultObjectPosition.x,
                kDefaultObjectPosition.y,
                kDefaultObjectPosition.z
            )
        }



        cameraInfo.upVector?.let {
            val x = it.x
            val y = it.y
            val z = it.z
            manipulatorBuilder.upVector(x, y, z)
        }
        cameraInfo.zoomSpeed?.let {
            manipulatorBuilder.zoomSpeed(it)
        }
        cameraInfo.orbitHomePosition?.let {
            val x = it.x
            val y = it.y
            val z = it.z
            manipulatorBuilder.orbitHomePosition(x, y, z)
        }

        cameraInfo.orbitSpeed?.let {
            val x = cameraInfo.orbitSpeed.getOrNull(0)
            val y = cameraInfo.orbitSpeed.getOrNull(1)
            if (x != null && y != null)
                manipulatorBuilder.orbitSpeed(x, y)
        }


        cameraInfo.fovDirection?.let {
            manipulatorBuilder.fovDirection(it)
        }
        cameraInfo.fovDegrees?.let {
            manipulatorBuilder.fovDegrees(it)
        }
        cameraInfo.farPlane?.let {
            manipulatorBuilder.farPlane(it)
        }
        cameraInfo.mapExtent?.let {
            val width = it.getOrNull(0)
            val height = it.getOrNull(1)
            if (width != null && height != null) {
                manipulatorBuilder.mapExtent(width, height)
            }
        }

        cameraInfo.flightStartPosition?.let {
            val x = it.x
            val y = it.y
            val z = it.z
            manipulatorBuilder.flightStartPosition(x, y, z)
        }
        cameraInfo.flightStartOrientation?.let {
            val pitch = it.getOrElse(0) { 0f }
            val yaw = it.getOrElse(1) { 0f }
            manipulatorBuilder.flightStartOrientation(pitch, yaw)
        }

        cameraInfo.flightMoveDamping?.let {
            manipulatorBuilder.flightMoveDamping(it)
        }
        cameraInfo.flightSpeedSteps?.let {
            manipulatorBuilder.flightSpeedSteps(it)
        }
        cameraInfo.flightMaxMoveSpeed?.let {
            manipulatorBuilder.flightMaxMoveSpeed(it)
        }
        cameraInfo.groundPlane?.let {
            val a = it.getOrElse(0) { 0f }
            val b = it.getOrElse(1) { 0f }
            val c = it.getOrElse(2) { 1f }
            val d = it.getOrElse(3) { 0f }
            manipulatorBuilder.groundPlane(a, b, c, d)
        }

        manipulatorBuilder.viewport(width, height)
        cameraManipulator = manipulatorBuilder.build(cameraInfo.mode ?: Manipulator.Mode.ORBIT)

        val view = surfaceView ?: textureView
        view?.let {
            gestureDetector = GestureDetector(it, cameraManipulator)
        }
    }


    fun updateExposure(exposure: Exposure?): Resource<String> {

        if (exposure == null) return Resource.Error("Exposure not found")

        return if (exposure.exposure != null) {
            camera.setExposure(exposure.exposure)
            Resource.Success("Exposure updated successfully")
        } else if (exposure.aperture != null || exposure.shutterSpeed != null || exposure.sensitivity != null) {
            camera.setExposure(
                exposure.aperture ?: kAperture,
                exposure.shutterSpeed ?: kShutterSpeed,
                exposure.sensitivity ?: kSensitivity
            )
            Resource.Success("Exposure updated successfully")
        } else {
            Resource.Error("Exposure aperture and shutter speed and sensitivity must be provided")
        }

    }

    fun updateProjection(projection: Projection?): Resource<String> {
        if (projection == null) return Resource.Error("Projection not found")

        return if (projection.projection != null && projection.left != null && projection.right != null && projection.top != null
            && projection.bottom != null
        ) {
            camera.setProjection(
                projection.projection,
                projection.left,
                projection.right,
                projection.bottom,
                projection.top,
                projection.near ?: kNearPlane,
                projection.far ?: kFarPlane
            )
            Resource.Success("Projection updated successfully")
        } else if (projection.fovInDegrees != null && projection.direction != null) {
            val aspect = projection.aspect ?: calculateAspectRatio()

            camera.setProjection(
                projection.fovInDegrees,
                aspect,
                projection.near ?: kNearPlane,
                projection.far ?: kFarPlane,
                projection.direction
            )
            Resource.Success("Projection updated successfully")
        } else {
            Resource.Error("Projection info must be provided")
        }
    }


    fun updateLensProjection(lensProjection: LensProjection?): Resource<String> {
        if (lensProjection == null) return Resource.Error("Lens projection not found")


        return if (lensProjection.focalLength != null) {
            if (cameraFocalLength != lensProjection.focalLength) cameraFocalLength =
                lensProjection.focalLength
            val aspect = lensProjection.aspect ?: calculateAspectRatio()
            camera.setLensProjection(
                lensProjection.focalLength,
                aspect,
                lensProjection.near ?: kNearPlane,
                lensProjection.far ?: kFarPlane
            )
            Resource.Success("Lens projection updated successfully")
        } else {
            Resource.Error("Lens projection info must be provided")
        }

    }


    fun updateCameraShift(shift: DoubleArray?): Resource<String> {
        if (shift == null) return Resource.Error("Camera shift not found")
        val xShift = shift.getOrNull(0)
        val yShift = shift.getOrNull(1)
        return if (xShift != null && yShift != null) {
            camera.setShift(xShift, yShift)
            Resource.Success("Camera shift updated successfully")
        } else {
            Resource.Error("Camera shift info must be provided")
        }

    }

    fun updateCameraScaling(scaling: DoubleArray?): Resource<String> {
        if (scaling == null) return Resource.Error("Camera scaling must be provided")
        val xScaling = scaling.getOrNull(0)
        val yScaling = scaling.getOrNull(1)
        return if (xScaling != null && yScaling != null) {
            camera.setScaling(xScaling, yScaling)
            Resource.Success("Camera scaling updated successfully")
        } else {
            Resource.Error("Camera scaling info must be provided")
        }

    }


    fun setDefaultCamera() {
        cameraManipulator = Manipulator.Builder()
            .targetPosition(
                kDefaultObjectPosition.x,
                kDefaultObjectPosition.y,
                kDefaultObjectPosition.z
            )
            .viewport(width, height)
            .build(Manipulator.Mode.ORBIT)

        camera = engine.createCamera(engine.entityManager.create())
            .apply {
                setExposure(
                    kAperture,
                    kShutterSpeed,
                    kSensitivity
                )
            }
        val view = surfaceView ?: textureView
        view?.let {
            gestureDetector = GestureDetector(it, cameraManipulator)
        }

    }


    private fun updateCameraProjection() {
        val aspect = calculateAspectRatio()
        updateLensProjection(
            LensProjection(
                focalLength = cameraFocalLength,
                aspect = aspect
            )
        )
    }

    private fun calculateAspectRatio(): Double {
        val width = view.viewport.width
        val height = view.viewport.height
        return width.toDouble() / height.toDouble()
    }

    fun updateCameraOnResize(width: Int, height: Int) {
        cameraManipulator.setViewport(width, height)
        updateCameraProjection()
    }


    fun lookAtDefaultPosition(): Resource<String> {
        // Extract the camera basis from the helper and push it to the Filament camera.

         val tempEyePos = DoubleArray(3)
         val tempTarget = DoubleArray(3)
         val tempUpward = DoubleArray(3)

        cameraManipulator.getLookAt(tempEyePos, tempTarget, tempUpward)
        if(!tempEyePos.contentEquals(eyePos) ||!tempTarget.contentEquals(target) ||!tempUpward.contentEquals(upward)){
            lookEyePositions = null
            lookTarget = null
            lookUpward = null
            eyePos = tempEyePos
            target = tempTarget
            upward = tempUpward
        }

        if(lookEyePositions != null && lookTarget != null && lookUpward != null) {

            camera.lookAt(
                lookEyePositions?.getOrElse(0) { eyePos[0] } ?: eyePos[0],
                lookEyePositions?.getOrElse(1) { eyePos[1] } ?: eyePos[1],
                lookEyePositions?.getOrElse(2) { eyePos[2] }?: eyePos[2],
                lookTarget?.getOrElse(0) { target[0] }?: target[0],
                lookTarget?.getOrElse(1) { target[1] }?: target[1],
                lookTarget?.getOrElse(2) { target[2] }?: target[2],
                lookUpward?.getOrElse(0) { upward[0] }?: upward[0],
                lookUpward?.getOrElse(1) { upward[1] }?: upward[1],
                lookUpward?.getOrElse(2) { upward[2] }?: upward[2],
            )


        }else{
            camera.lookAt(
                eyePos[0], eyePos[1], eyePos[2],
                target[0], target[1], target[2],
                upward[0], upward[1], upward[2]
            )

        }

        return Resource.Success("Looked at default position successfully")

    }


    fun lookAtPosition(
        eyeArray: DoubleArray?,
        targetArray: DoubleArray?,
        upwardArray: DoubleArray?,
    ): Resource<String> {
        if (eyeArray == null || targetArray == null || upwardArray == null) return Resource.Error("positions must be provided must be provided")
        cameraManipulator.getLookAt(eyePos, target, upward)

        lookEyePositions = doubleArrayOf(
            eyeArray.getOrElse(0) { eyePos[0] },
            eyeArray.getOrElse(1) { eyePos[1] },
            eyeArray.getOrElse(2) { eyePos[2] },
        )
        lookTarget = doubleArrayOf(
            targetArray.getOrElse(0) { target[0] },
            targetArray.getOrElse(1) { target[1] },
            targetArray.getOrElse(2) { target[2] },
            )
        lookUpward = doubleArrayOf(
            upwardArray.getOrElse(0) { upward[0] },
            upwardArray.getOrElse(1) { upward[1] },
            upwardArray.getOrElse(2) { upward[2] },
            )

        camera.lookAt(
            eyeArray.getOrElse(0) { eyePos[0] },
            eyeArray.getOrElse(1) { eyePos[1] },
            eyeArray.getOrElse(2) { eyePos[2] },
            targetArray.getOrElse(0) { target[0] },
            targetArray.getOrElse(1) { target[1] },
            targetArray.getOrElse(2) { target[2] },
            upwardArray.getOrElse(0) { upward[0] },
            upwardArray.getOrElse(1) { upward[1] },
            upwardArray.getOrElse(2) { upward[2] },
        )

        return Resource.Success("Look at position updated successfully")
    }


    fun getLookAt(): Resource<List<Double>> {
        cameraManipulator.getLookAt(eyePos, target, upward)

        if (lookEyePositions != null && lookTarget != null && lookUpward != null) {
            val lookAt: MutableList<Double> = mutableListOf(
                lookEyePositions?.getOrElse(0) { eyePos[0] } ?: eyePos[0],
                lookEyePositions?.getOrElse(1) { eyePos[1] } ?: eyePos[1],
                lookEyePositions?.getOrElse(2) { eyePos[2] }?: eyePos[2],
                lookTarget?.getOrElse(0) { target[0] }?: target[0],
                lookTarget?.getOrElse(1) { target[1] }?: target[1],
                lookTarget?.getOrElse(2) { target[2] }?: target[2],
                lookUpward?.getOrElse(0) { upward[0] }?: upward[0],
                lookUpward?.getOrElse(1) { upward[1] }?: upward[1],
                lookUpward?.getOrElse(2) { upward[2] }?: upward[2],
            )
            return Resource.Success(lookAt.toList())
        } else {

            val lookAt: MutableList<Double> = mutableListOf(
                eyePos[0],
                eyePos[1],
                eyePos[2],
                target[0],
                target[1],
                target[2],
                upward[0],
                upward[1],
                upward[2]
            )
            return Resource.Success(lookAt.toList())
        }
    }

    fun scroll(x: Int?, y: Int?, scrollDelta: Float?): Resource<String> {
        if (x == null || y == null || scrollDelta == null) return Resource.Error("x and y and scrollDelta must be provided")
        cameraManipulator.scroll(x, y, scrollDelta)
        return Resource.Success("Scroll updated successfully")
    }


    /**
     * Given a viewport coordinate, picks a point in the ground plane.
     */
    @Size(min = 3)
    fun raycast(x: Int?, y: Int?): Resource<FloatArray> {
        if (x == null || y == null) return Resource.Error("x and y must be provided")

        val array = cameraManipulator.raycast(x, y)
        return if (array != null) {
            Resource.Success(array)
        } else {
            Resource.Error("Raycast failed")
        }
    }

    /**
     * Starts a grabbing session (i.e. the user is dragging around in the viewport).
     *
     * In MAP mode, this starts a panning session.
     * In ORBIT mode, this starts either rotating or strafing.
     * In FREE_FLIGHT mode, this starts a nodal panning session.
     *
     * @param x X-coordinate for point of interest in viewport space
     * @param y Y-coordinate for point of interest in viewport space
     * @param strafe ORBIT mode only; if true, starts a translation rather than a rotation
     */
    fun grabBegin(x: Int?, y: Int?, strafe: Boolean?): Resource<String> {
        if (x == null || y == null) return Resource.Error("x and y must be provided")

        cameraManipulator.grabBegin(x, y, strafe ?: false)
        return Resource.Success("Grab begin updated successfully")
    }

    /**
     * Updates a grabbing session.
     *
     * This must be called at least once between grabBegin / grabEnd to dirty the camera.
     */
    fun grabUpdate(x: Int?, y: Int?): Resource<String> {
        if (x == null || y == null) return Resource.Error("x and y must be provided")
        cameraManipulator.grabUpdate(x, y)
        return Resource.Success("Updated grabbing session successfully")

    }

    /**
     * Ends a grabbing session.
     */
    fun grabEnd(): Resource<String> {
        cameraManipulator.grabEnd()
        return Resource.Success("Ended grabbing session successfully")

    }


    fun destroyCamera() {
        engine.destroyCameraComponent(camera.entity)
        EntityManager.get().destroy(camera.entity)

    }

    fun onTouchEvent(event: MotionEvent) {
        gestureDetector?.onTouchEvent(event)
    }


    companion object {
        private const val kNearPlane = 0.05     // 5 cm
        private const val kFarPlane = 1000.0    // 1 km
        private const val kAperture = 16f
        private const val kShutterSpeed = 1f / 125f
        private const val kSensitivity = 100f
        private const val kDefaultFocalLength = 28.0

    }

}