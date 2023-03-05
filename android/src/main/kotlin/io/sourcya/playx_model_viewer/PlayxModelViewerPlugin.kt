package io.sourcya.playx_model_viewer

import androidx.lifecycle.Lifecycle
import com.google.android.filament.Engine
import com.google.android.filament.utils.Utils
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference
import io.sourcya.playx_model_viewer.factory.PlayXModelViewerFactory
import io.sourcya.playx_model_viewer.utils.LifecycleProvider
import timber.log.Timber


/** PlayxModelViewerPlugin */
class PlayxModelViewerPlugin : FlutterPlugin, ActivityAware {
    private var lifecycle: Lifecycle? = null
    private lateinit var engine :Engine;

    //register the android native view
    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
       engine= Engine.create()
        binding
            .platformViewRegistry
            .registerViewFactory(
                viewType,
                PlayXModelViewerFactory(binding, engine,
                    object : LifecycleProvider {
                        override fun getLifecycle(): Lifecycle? {
                            return lifecycle
                        }
                    }),
                )
    }
    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        engine.destroy()
    }
    companion object {
        const val channelName = "io.sourcya.playx.model_viewer.channel"
        var viewType = "${channelName}_model_view"
        init {
            Utils.init()
        }
    }
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        // get the activity lifecycle to handle lifecycle events
        lifecycle = (binding.lifecycle as HiddenLifecycleReference).lifecycle
    }
    override fun onDetachedFromActivityForConfigChanges() {
        lifecycle = null
    }
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}

    override fun onDetachedFromActivity() {}

}
