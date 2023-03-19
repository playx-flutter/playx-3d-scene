package io.sourcya.playx_3d_scene.core.models.states

enum class SceneState {
    NONE,
    LOADING,
    LOADED,
    ERROR;

    companion object{

        fun getSceneState(first :SceneState , second:SceneState): SceneState {
            return if(getValue(first) > getValue(second)) {
                first;
            }else{
                second
            }
        }

        fun getSceneState(first :SceneState , second:SceneState, third:SceneState): SceneState {
            val firstAndSecondState = getSceneState(first,second)
            return getSceneState(firstAndSecondState,third)
        }

        private fun getValue(state:SceneState): Int {
           return when(state){
                NONE -> 1
                LOADING -> 4
                LOADED -> 3
                ERROR -> 2
            }
        }



    }


}