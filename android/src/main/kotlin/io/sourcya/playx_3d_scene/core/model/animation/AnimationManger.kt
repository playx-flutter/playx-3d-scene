package io.sourcya.playx_3d_scene.core.model.animation

import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer

internal class AnimationManger  constructor(
    private val modelViewer: CustomModelViewer?){

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


}