package io.sourcya.playx_3d_scene.utils

import androidx.lifecycle.Lifecycle

interface LifecycleProvider {
    fun  getLifecycle() : Lifecycle?
}