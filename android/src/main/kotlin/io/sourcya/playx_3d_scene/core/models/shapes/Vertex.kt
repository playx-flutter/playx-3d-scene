package io.sourcya.playx_3d_scene.core.models.shapes

import com.google.android.filament.Box
import com.google.android.filament.utils.*
import io.sourcya.playx_3d_scene.core.utils.Color


data class Vertex(val position: Position = Position(),
                  val normal: Direction? =null,
                  val uvCoordinates: UVCoordinates? = null,
                  val color: Color? = null
)

typealias Position = Float3
typealias Direction = Float3
typealias Size = Float3


typealias UVCoordinates = Float2

data class Submesh(val triangleIndices: List<Int>) {
    constructor(vararg triangleIndices: Int) : this(triangleIndices.toList())
}
fun FloatArray.toFloat3() = this.let { (x, y, z) -> Float3(x, y, z) }

fun Box(center: Position, halfExtent: Size) = Box(center.toFloatArray(), halfExtent.toFloatArray())

var Box.centerPosition: Position
    get() = center.toPosition()
    set(value) {
        setCenter(value.x, value.y, value.z)
    }
var Box.halfExtentSize: Size
    get() = halfExtent.toSize()
    set(value) {
        setHalfExtent(value.x, value.y, value.z)
    }
var Box.size
    get() = halfExtentSize * 2.0f
    set(value) {
        halfExtentSize = value / 2.0f
    }


fun FloatArray.toPosition() = this.let { (x, y, z) -> Position(x, y, z) }
fun FloatArray.toSize() = this.let { (x, y, z) -> Size(x, y, z) }

typealias Transform = Mat4

fun Mat4.toDoubleArray() : DoubleArray = toFloatArray().map { it.toDouble() }.toDoubleArray()
val Mat4.quaternion: Quaternion get() = rotation(this).toQuaternion()
