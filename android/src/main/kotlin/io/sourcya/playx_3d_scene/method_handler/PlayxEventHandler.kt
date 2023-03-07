package io.sourcya.playx_3d_scene.method_handler

import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.sourcya.playx_3d_scene.core.controller.ModelViewerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.logging.StreamHandler

class PlayxEventHandler (
    private val messenger: BinaryMessenger,
    private val modelViewer: ModelViewerController?,
    private val id: Int,
    ) {

    private var modelLoadingEventChannel: EventChannel = EventChannel(messenger, "${MODEL_STATE_CHANNEL_NAME}_$id")
    private var modelLoadingEventSink : EventChannel.EventSink? =null

    private var rendererEventChannel: EventChannel = EventChannel(messenger, "${RENDERER_CHANNEL_NAME}_$id")
    private var rendererEventSink : EventChannel.EventSink? =null


    private var modelLoadingJob : Job? =null
    private var rendererJob : Job?= null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    init {
        listenToModelLoading()
        listenToEachRender()
    }



    private fun listenToModelLoading(){
        modelLoadingJob = coroutineScope.launch {
            modelViewer?.modelState?.collectLatest {
                Timber.d("My Playx3dScenePlugin  listenToModelLoading method : $it")
                modelLoadingEventSink?.success(it.toString())

            }
        }
    }


    private fun setUpModelStateEventChannel(){
        Timber.d("My Playx3dScenePlugin : startListeningToEventChannels")
        modelLoadingEventChannel.setStreamHandler(object : StreamHandler(),
            EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                modelLoadingEventSink = events
                Timber.d("My Playx3dScenePlugin : onListen : ${modelViewer?.modelState?.value?.toString()}")
                modelLoadingEventSink?.success(modelViewer?.modelState?.value?.toString())
            }
            override fun onCancel(arguments: Any?) {
                Timber.d("My Playx3dScenePlugin : onCancel")
                modelLoadingEventSink =null
            }
        })

    }

    private fun cancelModelStateEventChannel(){
        Timber.d("My Playx3dScenePlugin : stopListeningToEventChannels")
        modelLoadingJob?.cancel()
        modelLoadingEventChannel.setStreamHandler(null)
        modelLoadingEventSink = null


    }



    private fun listenToEachRender(){
        rendererJob = coroutineScope.launch {
            modelViewer?.getRenderStateFlow()?.collectLatest {
             //   Timber.d("My Playx3dScenePlugin  listenToEachRender method : $it")
                rendererEventSink?.success(it)
            }
        }
    }


    private fun setUpRendererEventChannel(){
        Timber.d("My Playx3dScenePlugin : setUpRendererEventChannel")

        rendererEventChannel.setStreamHandler(object : StreamHandler(),
            EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                rendererEventSink = events
                Timber.d("My Playx3dScenePlugin : onListen :}")
            }
            override fun onCancel(arguments: Any?) {
                Timber.d("My Playx3dScenePlugin : onCancel")
                rendererEventSink =null
            }
        })

    }

    private fun cancelRendererEventChannel(){
        Timber.d("My Playx3dScenePlugin : stopListeningToEventChannels")
        rendererJob?.cancel()
        rendererEventChannel.setStreamHandler(null)
        rendererEventSink = null


    }


    fun startListeningToEventChannels(){
        setUpModelStateEventChannel()
        setUpRendererEventChannel()

    }

    fun stopListeningToEventChannels(){
        cancelModelStateEventChannel()
        cancelRendererEventChannel()
    }


    companion object{
        private  const val  MODEL_STATE_CHANNEL_NAME= "io.sourcya.playx.3d.scene.model_state_channel"
        private const val  RENDERER_CHANNEL_NAME= "io.sourcya.playx.3d.scene.renderer_channel"

    }




}