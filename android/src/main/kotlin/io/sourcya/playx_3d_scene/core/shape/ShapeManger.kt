package io.sourcya.playx_3d_scene.core.shape

import io.sourcya.playx_3d_scene.core.shape.common.geometry.Geometry
import io.sourcya.playx_3d_scene.core.shape.common.material.MaterialManger
import io.sourcya.playx_3d_scene.core.shape.common.model.Direction
import io.sourcya.playx_3d_scene.core.shape.common.model.Shape
import io.sourcya.playx_3d_scene.core.shape.common.model.ShapeState
import io.sourcya.playx_3d_scene.core.shape.cube.CubeGeometry
import io.sourcya.playx_3d_scene.core.shape.cube.model.Cube
import io.sourcya.playx_3d_scene.core.shape.plane.PlaneGeometry
import io.sourcya.playx_3d_scene.core.shape.plane.model.Plane
import io.sourcya.playx_3d_scene.core.shape.sphere.SphereGeometry
import io.sourcya.playx_3d_scene.core.shape.sphere.model.Sphere
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer

class ShapeManger(
    private val modelViewer: CustomModelViewer,
    private val materialManger: MaterialManger
) {
    val engine = modelViewer.engine

    private val currentShapesGeometries = mutableMapOf<Int, Geometry>()

    private val currentShapeState= modelViewer.currentShapesState

    suspend fun createShapes(shapes: List<Shape>?): Resource<String> {

        currentShapeState.value = ShapeState.LOADING
        if (shapes.isNullOrEmpty()) {
            currentShapeState.value = ShapeState.ERROR
            return Resource.Error("No shapes to create")
        }

        var createdCount = 0
        var failedCount = 0

        for (shape in shapes) {
            val result = createShape(shape)
            if (result is Resource.Success) {
                createdCount++
            } else {
                failedCount++
            }
        }
        currentShapeState.value = ShapeState.LOADED
        return Resource.Success("$createdCount shapes created successfully and $failedCount failed.")
    }


    private suspend fun createCube(cube: Cube?): Resource<String> {

        if (cube == null) return Resource.Error("Cube must be provided")
        if (cube.size == null) return Resource.Error("Size must be provided")
        if (cube.centerPosition == null) return Resource.Error("position must be provided")

        val materialInstanceResult = materialManger.getMaterialInstance(cube.material)

        val cubeGeometry = CubeGeometry.Builder(
            center = cube.centerPosition,
            size = cube.size,
        )
            .build(engine)
            .apply {
                setupScene(modelViewer, materialInstanceResult.data)
            }

        cube.id?.let { currentShapesGeometries.put(it, cubeGeometry) }
        return Resource.Success("created shape successfully")
    }


    private suspend fun createPlane(plane: Plane?): Resource<String> {

        if (plane == null) return Resource.Error("Plane must be provided")
        if (plane.size == null) return Resource.Error("Size must be provided")
        if (plane.centerPosition == null) return Resource.Error("position must be provided")

        val materialInstanceResult = materialManger.getMaterialInstance(plane.material)

        val planeGeometry = PlaneGeometry.Builder(
            center = plane.centerPosition,
            size = plane.size,
            normal = plane.normal ?: Direction(y = 1f)
        )
            .build(engine).apply {
                setupScene(modelViewer, materialInstanceResult.data)
            }
        plane.id?.let { currentShapesGeometries.put(it, planeGeometry) }
        return Resource.Success("created shape successfully")
    }

    private suspend fun createSphere(sphere: Sphere?): Resource<String> {

        if (sphere == null) return Resource.Error("sphere must be provided")
        if (sphere.radius == null) return Resource.Error("radius must be provided")
        if (sphere.centerPosition == null) return Resource.Error("position must be provided")

        val materialInstanceResult = materialManger.getMaterialInstance(sphere.material)

        val sphereGeometry = SphereGeometry.Builder(
            center = sphere.centerPosition,
            radius = sphere.radius,
            stacks = sphere.stacks ?: SphereGeometry.DEFAULT_STACKS,
            slices = sphere.slices ?: SphereGeometry.DEFAULT_SLICES
        )
            .build(engine)
            .apply {
                setupScene(modelViewer, materialInstanceResult.data)
            }


        sphere.id?.let { currentShapesGeometries.put(it, sphereGeometry) }
        return Resource.Success("created shape successfully")
    }
    suspend fun updateShape(id: Int?, shape: Shape?): Resource<String> {
        currentShapeState.value= ShapeState.LOADING

        if (id == null){
            currentShapeState.value= ShapeState.ERROR
            return Resource.Error("id must  be provided")
        }
        if (shape == null){
            currentShapeState.value= ShapeState.ERROR
            return Resource.Error("shape must be provided")
        }
        val currentShape = currentShapesGeometries.getOrElse(id) {
            //if it doesn't exist then create it
            val result = createShape(shape)
            return if (result is Resource.Success) {
                currentShapeState.value= ShapeState.LOADED
                Resource.Success("shape with id $id updated successfully")
            } else {
                currentShapeState.value= ShapeState.ERROR
                Resource.Error(result.message ?: "could not update shape")
            }
        }
        when (currentShape) {
            is PlaneGeometry -> {
                if (shape is Plane) {
                    val center = shape.centerPosition
                    val size = shape.size

                    if(center == null){
                        currentShapeState.value= ShapeState.ERROR
                        return Resource.Error("shape center position must be provided")
                    }
                    if(size == null){
                        currentShapeState.value= ShapeState.ERROR
                        return Resource.Error("shape size must be provided")
                    }
                    currentShape.update(
                        engine,
                        center,
                        size ,
                        shape.normal ?: Direction(y = 1f)
                    )
                    val materialInstanceResult = materialManger.getMaterialInstance(shape.material)
                    materialInstanceResult.data?.let {
                        currentShape.updateMaterial(
                            modelViewer,
                            it
                        )
                    }

                    shape.id?.let {
                        if (id != shape.id) {
                            currentShapesGeometries.remove(id)
                            currentShapesGeometries[it] = currentShape
                        }
                    }
                    currentShapeState.value= ShapeState.LOADED

                    return Resource.Success("shape with id $id updated successfully")
                } else {
                    removeShape(id)
                    val result = createShape(shape)
                    return if (result is Resource.Success) {
                        currentShapeState.value= ShapeState.LOADED
                        Resource.Success("shape with id $id updated successfully")
                    } else {
                        currentShapeState.value= ShapeState.ERROR
                        Resource.Error(result.message ?: "could not update shape")
                    }
                }

            }
            is CubeGeometry -> {
                if (shape is Cube) {
                    val center = shape.centerPosition
                    val size = shape.size

                    if(center == null){
                        currentShapeState.value= ShapeState.ERROR
                        return Resource.Error("shape center position must be provided")
                    }
                    if(size == null){
                        currentShapeState.value= ShapeState.ERROR
                        return Resource.Error("shape size must be provided")
                    }
                    currentShape.update(
                        engine,
                        center,
                        size,
                    )
                    val materialInstanceResult = materialManger.getMaterialInstance(shape.material)
                    materialInstanceResult.data?.let {
                        currentShape.updateMaterial(
                            modelViewer,
                            it
                        )
                    }
                    currentShapeState.value= ShapeState.LOADED

                    return Resource.Success("shape with id $id updated successfully")
                } else {
                    removeShape(id)
                    val result = createShape(shape)
                    return if (result is Resource.Success) {
                        currentShapeState.value= ShapeState.LOADED
                        Resource.Success("shape with id $id updated successfully")
                    } else {
                        currentShapeState.value= ShapeState.ERROR
                        Resource.Error(result.message ?: "could not update shape")
                    }
                }

            }
            is SphereGeometry ->{
                if (shape is Sphere) {
                    val center = shape.centerPosition
                    val radius = shape.radius

                    if(center == null){
                        currentShapeState.value= ShapeState.ERROR
                        return Resource.Error("shape center position must be provided")
                    }
                    if(radius == null){
                        currentShapeState.value= ShapeState.ERROR
                        return Resource.Error("shape radius must be provided")
                    }
                    currentShape.update(
                        engine,
                        radius,
                        center,
                        shape.stacks?: SphereGeometry.DEFAULT_STACKS,
                        shape.slices?: SphereGeometry.DEFAULT_SLICES,
                        )
                    val materialInstanceResult = materialManger.getMaterialInstance(shape.material)
                    materialInstanceResult.data?.let {
                        currentShape.updateMaterial(
                            modelViewer,
                            it
                        )
                    }
                    currentShapeState.value= ShapeState.LOADED

                    return Resource.Success("shape with id $id updated successfully")
                } else {
                    removeShape(id)
                    val result = createShape(shape)
                    return if (result is Resource.Success) {
                        currentShapeState.value= ShapeState.LOADED
                        Resource.Success("shape with id $id updated successfully")
                    } else {
                        currentShapeState.value= ShapeState.ERROR
                        Resource.Error(result.message ?: "could not update shape")
                    }
                }

            }
            else -> {
                currentShapeState.value= ShapeState.ERROR
                return Resource.Error("could identify shape with id $id")
            }

        }

    }


    fun removeShape(id: Int?): Resource<String> {

        currentShapeState.value= ShapeState.LOADING

        if (id == null){
            currentShapeState.value= ShapeState.ERROR

            return Resource.Error("shape id is required")
        }

        if (!currentShapesGeometries.containsKey(id)) {
            currentShapeState.value= ShapeState.ERROR
            return Resource.Error("couldn't find shape with id $id")
        }

        val shape = currentShapesGeometries.getOrElse(id) {
            currentShapeState.value= ShapeState.ERROR
            return Resource.Error("couldn't find shape with id $id")
        }

        currentShapesGeometries.remove(id)
        shape.removeGeometry(modelViewer)
        currentShapeState.value= ShapeState.LOADED

        return Resource.Success("removed shape with id $id successfully")
    }


    private suspend fun createShape(shape: Shape): Resource<String> {

        return when (shape) {
            is Plane -> createPlane(shape)
            is Cube -> createCube(shape)
            is Sphere -> createSphere(shape)
            else -> Resource.Error("couldn't identify shape")
        }
    }


    suspend fun addShape (shape: Shape?) : Resource<String> {
        currentShapeState.value= ShapeState.LOADING

        if (shape == null) return Resource.Error("shape must be provided")

        val result = createShape(shape)
        if(result is Resource.Success){
            currentShapeState.value= ShapeState.LOADED
        }else{
            currentShapeState.value= ShapeState.ERROR
        }
        return result
    }

    fun getCurrentCreatedShapeIds(): List<Int> {
        return currentShapesGeometries.keys.toList()
    }


}

