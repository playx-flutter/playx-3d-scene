package io.sourcya.playx_3d_scene

import androidx.lifecycle.Lifecycle
import com.google.android.filament.Engine
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

    //register the android native view
    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        Timber.plant(Timber.DebugTree())
       engine= Engine.create()
        iblProfiler = IBLProfiler(engine)
        binding
            .platformViewRegistry
            .registerViewFactory(
                viewType,
                Playx3dSceneFactory(binding, engine,
                    iblProfiler,
                    object : LifecycleProvider {
                        override fun getLifecycle(): Lifecycle? {
                            return lifecycle
                        }
                    }),
                )
    }
    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        iblProfiler.destroy()
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
