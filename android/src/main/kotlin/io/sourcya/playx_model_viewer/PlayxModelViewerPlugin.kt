package io.sourcya.playx_model_viewer

import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference
import io.sourcya.playx_model_viewer.factory.PlayXModelViewerFactory
import io.sourcya.playx_model_viewer.view.LifecycleProvider

import timber.log.Timber


/** PlayxModelViewerPlugin */
class PlayxModelViewerPlugin : FlutterPlugin, ActivityAware {
    private var lifecycle: Lifecycle? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        Timber.plant(Timber.DebugTree())
        Timber.d("My Fluttter View  onAttachedToEngine }")

        binding
            .platformViewRegistry
            .registerViewFactory(
                viewType,
                PlayXModelViewerFactory(binding,
                    object
                        : LifecycleProvider {
                        override fun getLifecycle(): Lifecycle? {
                            return lifecycle
                        }
                    }),

                )

    }


    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {}


    companion object {
        const val channelName = "io.sourcya.playx.model_viewer.channel"
        var viewType = "${channelName}_model_view"
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        lifecycle = (binding.lifecycle as HiddenLifecycleReference).lifecycle

    }

    override fun onDetachedFromActivityForConfigChanges() {
        lifecycle = null

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}

    override fun onDetachedFromActivity() {}

}
