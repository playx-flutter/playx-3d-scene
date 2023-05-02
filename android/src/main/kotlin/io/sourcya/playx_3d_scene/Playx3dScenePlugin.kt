package io.sourcya.playx_3d_scene

import androidx.lifecycle.Lifecycle
import com.google.android.filament.Engine
import com.google.android.filament.EntityManager
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.MaterialProvider
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.gltfio.UbershaderProvider
import com.google.android.filament.utils.Utils
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference
import io.sourcya.playx_3d_scene.core.utils.IBLProfiler
import io.sourcya.playx_3d_scene.factory.Playx3dSceneFactory
import io.sourcya.playx_3d_scene.utils.LifecycleProvider
import timber.log.Timber


/** PlayxModelViewerPlugin */
class Playx3dScenePlugin : FlutterPlugin, ActivityAware {
    private var lifecycle: Lifecycle? = null
    private lateinit var engine :Engine
    private lateinit var iblProfiler: IBLProfiler
    private lateinit var  materialProvider : MaterialProvider
    private lateinit var assetLoader :AssetLoader
    private lateinit var resourceLoader :ResourceLoader
    //register the android native view
    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        Timber.plant(Timber.DebugTree())
       engine= Engine.create()
         materialProvider = UbershaderProvider(engine)

         assetLoader = AssetLoader(engine, materialProvider, EntityManager.get())
         resourceLoader = ResourceLoader(engine, true)

        iblProfiler = IBLProfiler(engine)

        binding
            .platformViewRegistry
            .registerViewFactory(
                viewType,
                Playx3dSceneFactory(binding,
                    engine,
                    iblProfiler,
                    assetLoader,
                    resourceLoader,
                    object : LifecycleProvider {
                        override fun getLifecycle(): Lifecycle? {
                            return lifecycle
                        }
                    }),
                )
    }
    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        iblProfiler.destroy()
        materialProvider.destroyMaterials()
        materialProvider.destroy()
        assetLoader.destroy()
        resourceLoader.destroy()
        engine.destroy()

    }
    companion object {
        var viewType = "io.sourcya.playx.3d.scene.channel_3d_scene"
        init {
            Utils.init()
        }
    }
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        // get the activity lifecycle to handle lifecycle events
        lifecycle = (binding.lifecycle as HiddenLifecycleReference).lifecycle

    }
    override fun onDetachedFromActivityForConfigChanges() {

    }
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}

    override fun onDetachedFromActivity() {
        lifecycle = null
    }

}
