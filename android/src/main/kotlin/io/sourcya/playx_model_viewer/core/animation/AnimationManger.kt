package io.sourcya.playx_model_viewer.core.animation

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.filament.utils.ModelViewer
import io.sourcya.playx_model_viewer.core.viewer.CustomModelViewer
import java.util.*

internal class AnimationManger  constructor(
    private val modelViewer: CustomModelViewer?,
    private val context: Context
){

    private var _currentIndex = -1


     fun showAnimation(index : Int, seconds: Double){
         _currentIndex = index
        modelViewer?.animator?.apply {
            if (animationCount > 0 && index >=0  && index < animationCount) {
                applyAnimation(index, seconds.toFloat())
            }
            updateBoneMatrices()
        }
    }


    fun getCurrentIndex(): Int =_currentIndex

    fun getAnimationCount() =  modelViewer?.animator?.animationCount ?: 0


     fun getAnimationNameByIndex(index: Int) = modelViewer?.animator?.getAnimationName(index)

    fun getAnimationNames(): List<String> {
        val names = mutableListOf<String>()

        for(i in 0..getAnimationCount()){
            getAnimationNameByIndex(i)?.let { names.add(it) }
        }
        return names
    }

    fun getAnimationIndexByName(name: String) :Int{
        val names = getAnimationNames().map {
             it.lowercase()
        }
        return names.indexOf(name.lowercase())
    }



    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AnimationManger? = null

        fun getInstance(modelViewer: CustomModelViewer, context: Context): AnimationManger =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AnimationManger(modelViewer, context).also {
                    INSTANCE = it
                }
            }

    }
}