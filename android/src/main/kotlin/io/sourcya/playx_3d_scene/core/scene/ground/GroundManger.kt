package io.sourcya.playx_3d_scene.core.scene.ground

import io.sourcya.playx_3d_scene.core.shape.common.model.Direction
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.scene.common.model.SceneState
import io.sourcya.playx_3d_scene.core.scene.ground.model.Ground
import io.sourcya.playx_3d_scene.core.shape.common.material.MaterialManger
import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.plane.PlaneGeometry
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer

class GroundManger(
    private val modelViewer: CustomModelViewer,
    private val materialManger: MaterialManger
) {
    private val engine = modelViewer.engine
    var ground: Ground? = null
    var plane: PlaneGeometry? = null

    private suspend fun createGround(ground: Ground): Resource<String> {
        try{
            modelViewer.setGroundState(SceneState.LOADING)
            if (ground.size == null) {
                modelViewer.setGroundState(SceneState.ERROR)
                return Resource.Error("Size must be provided")
            }

            val materialInstanceResult = materialManger.getMaterialInstance(ground.material)


            val modelTransform = modelViewer.getModelTransform()

            val center = if (ground.isBelowModel && modelTransform != null) {
                Position(modelTransform[0, 3], modelTransform[1, 3], modelTransform[2, 3])
            } else {
                if (ground.centerPosition == null) {
                    modelViewer.setGroundState(SceneState.ERROR)
                    return Resource.Error("Position must be provided")
                }
                ground.centerPosition
            }
            this.ground = ground
            val plane = PlaneGeometry.Builder(
                center = center,
                size = ground.size,
                normal = ground.normal ?: Direction(y = 1f)
            ).build(engine)
            plane.setupScene(modelViewer, materialInstanceResult.data)

            this.plane = plane
            modelViewer.setGroundState(SceneState.LOADED)

            return Resource.Success("Ground created successfully")
        }catch (e:Throwable){
            modelViewer.setGroundState(SceneState.ERROR)
            return Resource.Error("couldn't create ground")
        }
    }


    suspend fun updateGround(newGround: Ground?): Resource<String> {

        try {
            modelViewer.setGroundState(SceneState.LOADING)

            if (newGround == null) {
                plane?.removeGeometry(modelViewer)
                modelViewer.setGroundState(SceneState.LOADED)

                return Resource.Success("Ground Updated successfully")
            }
            if (newGround.size == null){
                modelViewer.setGroundState(SceneState.ERROR)
                return Resource.Error("Size must be provided")
            }

            val isBelowModel = newGround.isBelowModel
            if (plane == null) {

                return createGround(newGround)
            } else {
                val modelTransform = modelViewer.getModelTransform()
                val center = if (isBelowModel && modelTransform != null) {
                    Position(modelTransform[0, 3], modelTransform[1, 3], modelTransform[2, 3])
                } else {
                    if (newGround.centerPosition == null) return Resource.Error("Position must be provided")
                    newGround.centerPosition
                }

                plane?.update(
                    engine,
                    center,
                    size = newGround.size,
                    normal = newGround.normal ?: Direction(y = 1f)
                )
            }
            modelViewer.setGroundState(SceneState.LOADED)
            return Resource.Success("updated ground successfully")
        }catch (e :Exception){
            modelViewer.setGroundState(SceneState.ERROR)
            return Resource.Error("couldn't update ground")
        }
    }


    suspend fun updateGroundMaterial(newMaterial: Material?): Resource<String> {
        modelViewer.setGroundState(SceneState.LOADING)

        if (newMaterial == null) {
            modelViewer.setGroundState(SceneState.ERROR)
            return Resource.Error("Material must be provided")
        }


        val materialInstanceResult = materialManger.getMaterialInstance(newMaterial)

        return if (materialInstanceResult is Resource.Success && materialInstanceResult.data != null) {
            if(plane == null) {
                modelViewer.setGroundState(SceneState.ERROR)
                 Resource.Error("couldn't find ground")
            }else{
                plane?.updateMaterial(modelViewer, materialInstanceResult.data)
                modelViewer.setGroundState(SceneState.LOADED)
                Resource.Success("updated ground material successfully")
            }

        } else {
            modelViewer.setGroundState(SceneState.ERROR)
            Resource.Error(
                materialInstanceResult.message ?: "couldn't update ground material"
            )

        }

    }

}