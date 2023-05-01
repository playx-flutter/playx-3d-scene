package io.sourcya.playx_3d_scene.core.utils

import com.google.android.filament.utils.Float4
import com.google.android.filament.utils.pow

typealias Color = Float4

fun colorOf(r: Float = 0.0f, g: Float = 0.0f, b: Float = 0.0f, a: Float = 1.0f) = Color(r, g, b, a)
fun colorOf(rgb: Float = 0.0f, a: Float = 1.0f) = colorOf(r = rgb, g = rgb, b = rgb, a = a)

fun colorOf(color: Int) = colorOf(
    r = android.graphics.Color.red(color) / 255.0f,
    g = android.graphics.Color.green(color) / 255.0f,
    b = android.graphics.Color.blue(color) / 255.0f,
    a = android.graphics.Color.alpha(color) / 255.0f
)

fun colorOf(colorStr: String): Color {
    val colorValue = android.graphics.Color.parseColor(colorStr)
    return colorOf(colorValue)
}

fun FloatArray.toColor() = Color(this[0], this[1], this[2], this.getOrNull(3) ?: 1.0f)

/**
 * If rendering in linear space, first convert the gray scaled values to linear space by rising to
 * the power 2.2
 */
fun Color.toLinearSpace() = transform { pow(it, 2.2f) }


fun Color.red( )= this[0]
fun Color.green() = this[1]
fun Color.blue() = this[2]
fun Color.alpha() = this[3]
