package io.sourcya.playx_3d_scene.core.geometry

import com.google.android.filament.Engine
import com.google.android.filament.utils.TWO_PI
import com.google.android.filament.utils.normalize
import io.sourcya.playx_3d_scene.core.models.shapes.Position
import io.sourcya.playx_3d_scene.core.models.shapes.Submesh
import io.sourcya.playx_3d_scene.core.models.shapes.UVCoordinates
import io.sourcya.playx_3d_scene.core.models.shapes.Vertex
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SphereGeometry private constructor(
    radius: Float,
    center: Position ,
    stacks: Int,
    slices: Int ,
    geometry: Geometry,
) : Geometry(
    geometry.vertexBuffer,
    geometry.indexBuffer,
    geometry.boundingBox,
    geometry.offsetsCounts,
    geometry.vertices,
    geometry.submeshes
){

   var radius: Float = radius
    private set

   var center: Position = center
    private set

   var stacks: Int =stacks
    private set

   var slices: Int = slices
    private set




    /**
     * Creates a [Geometry] in the shape of a sphere with the give specifications.
     *
     * @param radius the radius of the constructed sphere
     * @param center the center of the constructed sphere
     */
    class Builder(
        radius: Float = 1.0f,
        center: Position = Position(0.0f),
        stacks: Int = DEFAULT_STACKS,
        slices: Int = DEFAULT_SLICES
    ) : Geometry.Builder(
        vertices = getVertices(radius,center,stacks,slices),
        submeshes = mutableListOf<Submesh>().apply {
            var v = 0
            for (stack in 0 until stacks) {
                val triangleIndices = mutableListOf<Int>()
                for (slice in 0 until slices) {
                    // Skip triangles at the caps that would have an area of zero.
                    val topCap = stack == 0
                    val bottomCap = stack == stacks - 1
                    val next = slice + 1
                    if (!topCap) {
                        triangleIndices.add(v + slice)
                        triangleIndices.add(v + next)
                        triangleIndices.add(v + slice + slices + 1)
                    }
                    if (!bottomCap) {
                        triangleIndices.add(v + next)
                        triangleIndices.add(v + next + slices + 1)
                        triangleIndices.add(v + slice + slices + 1)
                    }
                }
                add(Submesh(triangleIndices))
                v += slices + 1
            }
        })



    fun update(
        engine: Engine,
        radius: Float = 1.0f,
        center: Position = Position(0.0f),
        stacks: Int = DEFAULT_STACKS,
        slices: Int = DEFAULT_SLICES
    ) {
        setBufferVertices(engine, getVertices(radius,center,stacks,slices))
    }
    companion object {

        fun getVertices(
            radius: Float = 1.0f,
            center: Position = Position(0.0f),
            stacks: Int = 24,
            slices: Int = 24
        ): List<Vertex> = mutableListOf<Vertex>().apply {
            for (stack in 0..stacks) {
                val phi = PI * stack.toFloat() / stacks.toFloat()
                for (slice in 0..slices) {
                    val theta = TWO_PI * (if (slice == slices) 0 else slice).toFloat() / slices
                    var position = Position(
                        x = (sin(phi) * cos(theta)).toFloat(),
                        y = cos(phi).toFloat(),
                        z = (sin(phi) * sin(theta)).toFloat()
                    ) * radius
                    val normal = normalize(position)
                    position += center
                    val uvCoordinate = UVCoordinates(
                        x = 1.0f - slice.toFloat() / slices, y = 1.0f - stack.toFloat() / stacks
                    )
                    add(
                        Vertex(
                            position = position,
                            normal = normal,
                            uvCoordinates = uvCoordinate
                        )
                    )
                }

            }
        }

        const val DEFAULT_STACKS = 24
        const val DEFAULT_SLICES = 24
    }

}