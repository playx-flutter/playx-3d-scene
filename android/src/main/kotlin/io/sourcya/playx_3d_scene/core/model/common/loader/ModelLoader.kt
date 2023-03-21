package io.sourcya.playx_3d_scene.core.model.common.loader

import com.google.android.filament.EntityManager
import com.google.android.filament.gltfio.*
import com.google.android.filament.utils.*
import io.sourcya.playx_3d_scene.core.shape.common.model.Position
import io.sourcya.playx_3d_scene.core.utils.Resource
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import kotlinx.coroutines.*
import java.nio.Buffer

class ModelLoader(private val modelViewer: CustomModelViewer) {
    private val engine = modelViewer.engine

    var asset: FilamentAsset? = null
        private set

    private val readyRenderables = IntArray(128) // add up to 128 entities at a time


    private var assetLoader: AssetLoader
    private var materialProvider: MaterialProvider = UbershaderProvider(engine)
    private var resourceLoader: ResourceLoader

    private var fetchResourcesJob: Job? = null


    var normalizeSkinningWeights = true

    init {
        assetLoader = AssetLoader(engine, materialProvider, EntityManager.get())
        resourceLoader = ResourceLoader(engine, normalizeSkinningWeights)

    }


    /**
     * Loads a monolithic binary glTF and populates the Filament scene.
     */
    suspend fun loadModelGlb(buffer: Buffer, transformToUnitCube: Boolean = false,
                             centerPosition: Position?, scale: Float?) {
        withContext(Dispatchers.Main) {
            destroyModel()
            asset = assetLoader.createAsset(buffer)

            asset?.let { asset ->
                resourceLoader.asyncBeginLoad(asset)
                modelViewer.animator = asset.instance.animator
                asset.releaseSourceData()

                if (transformToUnitCube) {
                    transformToUnitCube(centerPoint = centerPosition, scale=scale)
                }
            }
        }
    }

    /**
     * Loads a JSON-style glTF file and populates the Filament scene.
     * The given callback is triggered for each requested resource.
     */
    suspend fun loadModelGltf(
        buffer: Buffer,
        transformToUnitCube: Boolean = false,
        scale: Float?,
        centerPosition: Position?,
        callback: suspend (String) -> Buffer?,

    ) {
        destroyModel()
        asset = assetLoader.createAsset(buffer)
        asset?.let { asset ->
            for (uri in asset.resourceUris) {
                val resourceBuffer = callback(uri)
                if (resourceBuffer == null) {
                    this.asset = null
                    return
                }
                resourceLoader.addResourceData(uri, resourceBuffer)
            }
            resourceLoader.asyncBeginLoad(asset)
            modelViewer.animator = asset.instance.animator
            asset.releaseSourceData()
            if (transformToUnitCube) {
                transformToUnitCube(centerPoint = centerPosition,scale = scale)
            }
        }
    }

    /**
     * Loads a JSON-style glTF file and populates the Filament scene.
     *
     * The given callback is triggered from a worker thread for each requested resource.
     */
    suspend fun loadModelGltfAsync(
        buffer: Buffer,
        transformToUnitCube: Boolean = false,
        scale: Float?,
        centerPosition: Position?,
        callback: suspend (String) -> Buffer?,
    ) {
        withContext(Dispatchers.Main) {
            destroyModel()
            asset = assetLoader.createAsset(buffer)
            withContext(Dispatchers.IO) {
                    asset?.let { fetchResources(it, callback) }
            }

            if (transformToUnitCube) {
                transformToUnitCube(centerPosition,scale=scale)
            }
        }
    }


    /**
     * Frees all entities associated with the most recently-loaded model.
     */
    fun destroyModel() {
        fetchResourcesJob?.cancel()
        resourceLoader.asyncCancelLoad()
        resourceLoader.evictResourceData()
        asset?.let { asset ->
            modelViewer.scene.removeEntities(asset.entities)
            assetLoader.destroyAsset(asset)
            this.asset = null
            this.modelViewer.animator = null
        }
    }

    private suspend fun fetchResources(
        asset: FilamentAsset,
        callback: suspend (String) -> Buffer?
    ) :Resource<String>{
        val items = HashMap<String, Buffer>()
        val resourceUris = asset.resourceUris

        for (resourceUri in resourceUris) {
            items[resourceUri] = callback(resourceUri) ?: throw Exception("couldn't load file from :$resourceUri")
        }
        withContext(Dispatchers.Main) {
            for ((uri, buffer) in items) {
                resourceLoader.addResourceData(uri, buffer)
            }
                resourceLoader.asyncBeginLoad(asset)
                modelViewer.animator = asset.instance.animator
                asset.releaseSourceData()
        }

        return Resource.Success("Loaded Gltf model successfully")
    }


    /**
     * Sets up a root transform on the current model to make it fit into a unit cube.
     *
     * @param centerPoint Coordinate of center point of unit cube, defaults to < 0, 0, -4 >
     */
    fun transformToUnitCube(centerPoint: Position? , scale:Float? )   {

        val modelScale = if(scale ==null) 1f else {if(scale <=0) 1f else scale}
        val centerPosition = if(centerPoint == null) CustomModelViewer.kDefaultObjectPosition else{
            Float3(x = centerPoint.x, y = centerPoint.y, z = centerPoint.z)
        }

        asset?.let { asset ->
            val tm = engine.transformManager
            var center = asset.boundingBox.center.let { v -> Float3(v[0], v[1], v[2]) }
            val halfExtent = asset.boundingBox.halfExtent.let { v -> Float3(v[0], v[1], v[2]) }
            val maxExtent = 2.0f * max(halfExtent)
            val scaleFactor = 2.0f *modelScale  / maxExtent
            center -= centerPosition / scaleFactor
            val transform = scale(Float3(scaleFactor)) * translation(-center)
            tm.setTransform(tm.getInstance(asset.root), transpose(transform).toFloatArray())
        }
    }

    fun getModelTransform(): Mat4? {
        return asset?.root?.getTransform()

    }

    /**
     * Removes the transformation that was set up via transformToUnitCube.
     */
    fun clearRootTransform() {
        asset?.let {
            val tm = engine.transformManager
            tm.setTransform(tm.getInstance(it.root), Mat4().toFloatArray())
        }
    }



    fun updateScene() {
        // Allow the resource loader to finalize textures that have become ready.
        resourceLoader.asyncUpdateLoad()

        // Add renderable entities to the scene as they become ready.
        asset?.let {
            populateScene(it)
        }

    }


    private fun populateScene(asset: FilamentAsset) {
        val rcm = engine.renderableManager
        var count = 0
        val popRenderables = { count = asset.popRenderables(readyRenderables); count != 0 }
        while (popRenderables()) {
            for (i in 0 until count) {
                val ri = rcm.getInstance(readyRenderables[i])
                rcm.setScreenSpaceContactShadows(ri, true)
            }
            modelViewer.scene.addEntities(readyRenderables.take(count).toIntArray())
        }
        modelViewer.scene.addEntities(asset.lightEntities)
    }


    fun destroy() {
        assetLoader.destroy()
        materialProvider.destroyMaterials()
        materialProvider.destroy()
        resourceLoader.destroy()

    }


    @Suppress("unused")
    val progress
        get() = resourceLoader.asyncGetLoadProgress()


    private fun Int.getTransform(): Mat4 {
        val tm = modelViewer.engine.transformManager
        return Mat4.of(*tm.getTransform(tm.getInstance(this), null as? FloatArray?))
    }

    private fun Int.setTransform(mat: Mat4) {
        val tm = modelViewer.engine.transformManager
        tm.setTransform(tm.getInstance(this), mat.toFloatArray())
    }
}

