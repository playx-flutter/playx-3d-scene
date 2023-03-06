package io.sourcya.playx_3d_scene.core.models.scene

import com.google.android.filament.utils.Float3


class Light(
    var assetPath: String? = null,
    var url: String? = null,
    var intensity: Double? = null,
    val target: Float3? = null,
    val position: Float3? = null,
    )