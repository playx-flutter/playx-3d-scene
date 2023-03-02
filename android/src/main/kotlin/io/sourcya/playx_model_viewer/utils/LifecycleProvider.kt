package io.sourcya.playx_model_viewer.utils

import androidx.lifecycle.Lifecycle

interface LifecycleProvider {


    fun  getLifecycle() : Lifecycle?
}