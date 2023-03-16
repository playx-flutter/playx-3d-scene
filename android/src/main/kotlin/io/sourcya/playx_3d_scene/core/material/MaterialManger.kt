package io.sourcya.playx_3d_scene.core.material

import android.content.Context
import com.google.android.filament.MaterialInstance
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.sourcya.playx_3d_scene.core.material.loader.MaterialLoader
import io.sourcya.playx_3d_scene.core.material.loader.TextureLoader
import io.sourcya.playx_3d_scene.core.models.scene.material.Material
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import setParameter
import timber.log.Timber

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


        Timber.d("loading Textures : material $material param names : $materialParamNames")
        kotlin.runCatching {
            material.parameters?.let {
                for (param in it) {
                    if (materialParamNames.contains(param.name)) {
                        Timber.d("loading Textures : material param name :${param.name} , type: ${param.type}")
                        materialInstance.setParameter(param,textureLoader)

                    }
                }
            }

        }


        return Resource.Success(materialInstance)
    }


}