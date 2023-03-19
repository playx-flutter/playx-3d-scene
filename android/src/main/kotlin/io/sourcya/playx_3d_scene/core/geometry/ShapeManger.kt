package io.sourcya.playx_3d_scene.core.geometry

import io.sourcya.playx_3d_scene.core.material.MaterialManger
import io.sourcya.playx_3d_scene.core.models.shapes.Cube
import io.sourcya.playx_3d_scene.core.models.shapes.Direction
import io.sourcya.playx_3d_scene.core.models.shapes.Plane
import io.sourcya.playx_3d_scene.core.models.shapes.Shape
import io.sourcya.playx_3d_scene.core.models.states.ShapeState
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import timber.log.Timber

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

        Timber.d("create shape cube :$cube id :${cube?.id}")
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

        Timber.d("create shape plane :$plane id :${plane?.id}")
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


    suspend fun updateShape(id: Int?, shape: Shape?): Resource<String> {
        Timber.d("create shape update $id")
        currentShapeState.value= ShapeState.LOADING
        Timber.d("GotMATERIAL : updateShape")


        if (id == null){
            currentShapeState.value= ShapeState.ERROR
            return Resource.Error("id must  be provided")
        }
        if (shape == null){
            currentShapeState.value= ShapeState.ERROR
            return Resource.Error("shape must be provided")
        }
        if (shape.size == null) {
            currentShapeState.value= ShapeState.ERROR
            return Resource.Error("Size must be provided")
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
                        ?: return Resource.Error("shape center position must be provided")
                    currentShape.update(
                        engine,
                        center,
                        shape.size,
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
                        ?: return Resource.Error("shape center position must be provided")
                    currentShape.update(
                        engine,
                        center,
                        shape.size,
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

        Timber.d("create shape remove $id")
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


    suspend fun createShape(shape: Shape): Resource<String> {

        return when (shape) {
            is Plane -> createPlane(shape)
            is Cube -> createCube(shape)
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

