package io.sourcya.playx_3d_scene.method_handler

import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.sourcya.playx_3d_scene.core.Playx3dSceneController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.logging.StreamHandler

class PlayxEventHandler (
    private val messenger: BinaryMessenger,
    private val modelViewer: Playx3dSceneController?,
    private val id: Int,
    ) {

    private var modelLoadingEventChannel: EventChannel = EventChannel(messenger, "${MODEL_STATE_CHANNEL_NAME}_$id")
    private var modelLoadingEventSink : EventChannel.EventSink? =null

    private var rendererEventChannel: EventChannel = EventChannel(messenger, "${RENDERER_CHANNEL_NAME}_$id")
    private var rendererEventSink : EventChannel.EventSink? =null

    private var sceneStateEventChannel: EventChannel = EventChannel(messenger, "${SCENE_STATE_CHANNEL_NAME}_$id")
    private var sceneStateEventSink : EventChannel.EventSink? =null

    private var shapeStateEventChannel: EventChannel = EventChannel(messenger, "${SHAPE_STATE_CHANNEL_NAME}_$id")
    private var shapeStateEventSink : EventChannel.EventSink? =null

    private var modelLoadingJob : Job? =null
    private var rendererJob : Job?= null
    private var sceneStateJob : Job?= null
    private var shapeStateJob : Job?= null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    private fun listenToModelLoading(){
        modelLoadingJob = coroutineScope.launch {
            modelViewer?.modelState?.collectLatest {
                modelLoadingEventSink?.success(it.toString())

            }
        }
    }


    private fun setUpModelStateEventChannel(){
        modelLoadingEventChannel.setStreamHandler(object : StreamHandler(),
            EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                modelLoadingEventSink = events
                modelLoadingEventSink?.success(modelViewer?.modelState?.value?.toString())
            }
            override fun onCancel(arguments: Any?) {
                modelLoadingEventSink =null
            }
        })

    }

    private fun cancelModelStateEventChannel(){
        modelLoadingJob?.cancel()
        modelLoadingEventChannel.setStreamHandler(null)
        modelLoadingEventSink = null


    }



    private fun listenToSceneState(){
        sceneStateJob = coroutineScope.launch {
            modelViewer?.sceneState?.collectLatest {
                sceneStateEventSink?.success(it.toString())

            }
        }
    }


    private fun setSceneStateEventChannel(){
        sceneStateEventChannel.setStreamHandler(object : StreamHandler(),
            EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                sceneStateEventSink = events
                sceneStateEventSink?.success(modelViewer?.sceneState?.value?.toString())
            }
            override fun onCancel(arguments: Any?) {
                sceneStateEventSink =null
            }
        })

    }

    private fun cancelSceneStateEventChannel(){
        sceneStateJob?.cancel()
        sceneStateEventChannel.setStreamHandler(null)
        sceneStateEventSink = null



    }




    private fun listenToShapeState(){
        shapeStateJob = coroutineScope.launch {
            modelViewer?.shapeState?.collectLatest {
                shapeStateEventSink?.success(it.toString())
            }
        }
    }


    private fun setShapeStateEventChannel(){
        shapeStateEventChannel.setStreamHandler(object : StreamHandler(),
            EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                shapeStateEventSink = events
                shapeStateEventSink?.success(modelViewer?.shapeState?.value?.toString())
            }
            override fun onCancel(arguments: Any?) {
                shapeStateEventSink =null
            }
        })

    }

    private fun cancelShapeStateEventChannel(){
        shapeStateJob?.cancel()
        shapeStateEventChannel.setStreamHandler(null)
        shapeStateEventSink = null



    }





    private fun listenToEachRender(){
        rendererJob = coroutineScope.launch {
            modelViewer?.getRenderStateFlow()?.collectLatest {
                rendererEventSink?.success(it)
            }
        }
    }


    private fun setUpRendererEventChannel(){

        rendererEventChannel.setStreamHandler(object : StreamHandler(),
            EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                rendererEventSink = events
            }
            override fun onCancel(arguments: Any?) {
                rendererEventSink =null
            }
        })

    }

    private fun cancelRendererEventChannel(){
        rendererJob?.cancel()
        rendererEventChannel.setStreamHandler(null)
        rendererEventSink = null


    }


    fun startListeningToEventChannels(){
        setUpModelStateEventChannel()
        setUpRendererEventChannel()
        setSceneStateEventChannel()
        setShapeStateEventChannel()

    }

    fun stopListeningToEventChannels(){
        cancelModelStateEventChannel()
        cancelRendererEventChannel()
        cancelSceneStateEventChannel()
        cancelShapeStateEventChannel()
    }

    fun handleOnResume() {
        listenToModelLoading()
        listenToEachRender()
        listenToSceneState()
        listenToShapeState()
    }

    fun handleOnPause() {
        modelLoadingJob?.cancel()
        sceneStateJob?.cancel()
        rendererJob?.cancel()
        modelLoadingJob = null
        sceneStateJob = null
        rendererJob = null

    }


    companion object{
        private  const val  MODEL_STATE_CHANNEL_NAME= "io.sourcya.playx.3d.scene.model_state_channel"
        private const val  RENDERER_CHANNEL_NAME= "io.sourcya.playx.3d.scene.renderer_channel"
        private const val SCENE_STATE_CHANNEL_NAME = "io.sourcya.playx.3d.scene.scene_state"
        private const val SHAPE_STATE_CHANNEL_NAME = "io.sourcya.playx.3d.scene.shape_state"

    }




}