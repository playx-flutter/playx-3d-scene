package io.sourcya.playx_3d_scene.core.shape.common.material

import android.content.Context
import com.google.android.filament.MaterialInstance
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.sourcya.playx_3d_scene.core.shape.common.material.loader.MaterialLoader
import io.sourcya.playx_3d_scene.core.shape.common.material.loader.TextureLoader
import io.sourcya.playx_3d_scene.core.shape.common.material.model.Material
import io.sourcya.playx_3d_scene.core.shape.common.material.utils.setParameter
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer

class MaterialManger(
    private val modelViewer: CustomModelViewer,
    private val context: Context,
    private val flutterAssets: FlutterPlugin.FlutterAssets
) {
    private val materialLoader = MaterialLoader(modelViewer, context, flutterAssets)
    private val textureLoader = TextureLoader(modelViewer, context, flutterAssets)

    suspend fun getMaterialInstance(material: Material?): Resource<MaterialInstance> {
        if (material == null) return Resource.Error("Material not found")

        val materialResult = if (!material.assetPath.isNullOrEmpty()) {
            materialLoader.loadMaterialFromAsset(material.assetPath)
        } else if (!material.url.isNullOrEmpty()) {
            materialLoader.loadMaterialFromUrl(material.url)
        } else {
            Resource.Error("You must provide material asset path or url")
        }
        val loadedMaterial = materialResult.data

        if (materialResult is Resource.Error || loadedMaterial == null) {
            return Resource.Error(materialResult.message ?: "Material not found")
        }

        val materialInstance = loadedMaterial.createInstance()

        val materialParamNames = loadedMaterial.parameters.map { it.name }

        kotlin.runCatching {
            material.parameters?.let {
                for (param in it) {
                    if (materialParamNames.contains(param.name)) {
                        materialInstance.setParameter(param,textureLoader)
                    }
                }
            }

        }

        return Resource.Success(materialInstance)
    }


}