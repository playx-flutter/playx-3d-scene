package io.sourcya.playx_3d_scene.core.shape.common.geometry

import com.google.android.filament.Box
import com.google.android.filament.Engine
import com.google.android.filament.EntityManager
import com.google.android.filament.IndexBuffer
import com.google.android.filament.MaterialInstance
import com.google.android.filament.RenderableManager
import com.google.android.filament.VertexBuffer
import com.google.android.filament.utils.Float3
import com.google.android.filament.utils.Quaternion
import com.google.android.filament.utils.cross
import com.google.android.filament.utils.dot
import com.google.android.filament.utils.max
import com.google.android.filament.utils.min
import com.google.android.filament.utils.normalize
import io.sourcya.playx_3d_scene.core.shape.common.model.Direction
import io.sourcya.playx_3d_scene.core.shape.common.model.Submesh
import io.sourcya.playx_3d_scene.core.shape.common.model.Transform
import io.sourcya.playx_3d_scene.core.shape.common.model.Vertex
import io.sourcya.playx_3d_scene.core.utils.geometry
import io.sourcya.playx_3d_scene.core.viewer.CustomModelViewer
import java.nio.FloatBuffer
import java.nio.IntBuffer


private const val kPositionSize = 3 // x, y, z
private const val kTangentSize = 4 // Quaternion: x, y, z, w
private const val kUVSize = 2 // x, y
private const val kColorSize = 4 // r, g, b, a

/**
 * Geometry parameters for building and updating a Renderable
 *
 * A renderable is made of several primitives.
 * You can ever declare only 1 if you want each parts of your Geometry to have the same material
 * or one for each triangle indices with a different material.
 * We could declare n primitives (n per face) and give each of them a different material
 * instance, setup with different parameters
 *
 */
open class Geometry(
    val vertexBuffer: VertexBuffer,
    val indexBuffer: IndexBuffer,
    var boundingBox: Box? = null,
    var offsetsCounts: List<Pair<Int, Int>> = mutableListOf(),
    var vertices: List<Vertex> = mutableListOf(),
    var submeshes: List<Submesh> = mutableListOf(),
    ) {
    var renderable = EntityManager.get().create()



    open class Builder(val vertices: List<Vertex>, val submeshes: List<Submesh>) {
        open fun build(engine: Engine): Geometry {

            val vertexBuffer = VertexBuffer.Builder().apply {
                bufferCount(
                    1 + // Position is never null
                            (if (vertices.hasNormals) 1 else 0) +
                            (if (vertices.hasUvCoordinates) 1 else 0) +
                            (if (vertices.hasColors) 1 else 0)
                )
                vertexCount(vertices.size)

                // Position Attribute
                var bufferIndex = 0
                attribute(
                    VertexBuffer.VertexAttribute.POSITION,
                    bufferIndex,
                    VertexBuffer.AttributeType.FLOAT3,
                    0,
                    kPositionSize * Float.SIZE_BYTES
                )
                // Tangents Attribute
                if (vertices.hasNormals) {
                    bufferIndex++
                    attribute(
                        VertexBuffer.VertexAttribute.TANGENTS,
                        bufferIndex,
                        VertexBuffer.AttributeType.FLOAT4,
                        0,
                        kTangentSize * Float.SIZE_BYTES
                    )
                    normalized(VertexBuffer.VertexAttribute.TANGENTS)
                }
                // Uv Attribute
                if (vertices.hasUvCoordinates) {
                    bufferIndex++
                    attribute(
                        VertexBuffer.VertexAttribute.UV0,
                        bufferIndex,
                        VertexBuffer.AttributeType.FLOAT2,
                        0,
                        kUVSize * Float.SIZE_BYTES
                    )
                }
                // Color Attribute
                if (vertices.hasColors) {
                    bufferIndex++
                    attribute(
                        VertexBuffer.VertexAttribute.COLOR,
                        bufferIndex,
                        VertexBuffer.AttributeType.FLOAT4,
                        0,
                        kColorSize * Float.SIZE_BYTES
                    )
                    normalized(VertexBuffer.VertexAttribute.COLOR)
                }
            }.build(engine)

            val indexBuffer = IndexBuffer.Builder()
                // Determine how many indices there are
                .indexCount(submeshes.sumOf { it.triangleIndices.size })
                .bufferType(IndexBuffer.Builder.IndexType.UINT)
                .build(engine)

            return Geometry(vertexBuffer, indexBuffer).apply {
                setBufferVertices(engine, this@Builder.vertices)
                setBufferIndices(engine, this@Builder.submeshes)
                setBoundingBox(this@Builder.vertices)
            }
        }

    }


    fun setupScene(modelViewer: CustomModelViewer, materialInstance: MaterialInstance?) {

        val builder = RenderableManager.Builder(
            submeshes.size
        ).geometry(this)

        materialInstance?.let {
            for(i in 0..submeshes.size){
                builder.material(i,it)
            }
        }

        builder.build(modelViewer.engine, renderable)

        // Add the entity to the scene to render it
        modelViewer.scene.addEntity(renderable)
    }




    fun updateMaterial(modelViewer: CustomModelViewer,materialInstance: MaterialInstance) {
        val builder = RenderableManager.Builder(
            submeshes.size
        ).geometry(this)
            for(i in 0..submeshes.size){
                builder.material(i,materialInstance)
            }
        builder.build(modelViewer.engine, renderable)
    }

    fun setBufferVertices(engine: Engine, vertices: List<Vertex>) {
        this.vertices = vertices
        var bufferIndex = 0

        // Create position Buffer
        vertexBuffer.setBufferAt(
            engine, bufferIndex,
            FloatBuffer.allocate(vertices.size * kPositionSize).apply {
                vertices.forEach { put(it.position.toFloatArray()) }
                // Make sure the cursor is pointing in the right place in the byte buffer
                flip()
            }, 0,
            vertices.size * kPositionSize
        )

        // Create tangents Buffer
        if (vertices.hasNormals) {
            bufferIndex++
            vertexBuffer.setBufferAt(
                engine, bufferIndex,
                FloatBuffer.allocate(vertices.size * kTangentSize).apply {
                    vertices.forEach {
                        it.normal?.let { normal ->
                            put(normalToTangent(normal).toFloatArray())
                        }
                    }
                    flip()
                }, 0,
                vertices.size * kTangentSize
            )
        }

        // Create UV Buffer
        if (vertices.hasUvCoordinates) {
            bufferIndex++
            vertexBuffer.setBufferAt(
                engine, bufferIndex,
                FloatBuffer.allocate(vertices.size * kUVSize).apply {
                    vertices.forEach {
                        it.uvCoordinates?.toFloatArray()?.let { uvCoordinates ->
                            put(uvCoordinates)
                        }
                    }
                    rewind()
                }, 0,
                vertices.size * kUVSize
            )
        }
        // Create color Buffer
        if (vertices.hasColors) {
            bufferIndex++
            vertexBuffer.setBufferAt(
                engine, bufferIndex,
                FloatBuffer.allocate(vertices.size * kColorSize).apply {
                    vertices.forEach {
                        it.color?.toFloatArray()?.let { color ->
                            put(color)
                        }
                    }
                    rewind()
                }, 0,
                vertices.size * kColorSize
            )
        }

    }



    fun setBufferIndices(engine: Engine, submeshes: List<Submesh>) {

        this.submeshes = submeshes

        // Fill the index buffer with the data
        indexBuffer.setBuffer(engine,
            IntBuffer.allocate(submeshes.sumOf { it.triangleIndices.size }).apply {
                submeshes.flatMap { it.triangleIndices }.forEach { put(it) }
                flip()
            })

        var indexStart = 0
        offsetsCounts = submeshes.map { submesh ->
            (indexStart to submesh.triangleIndices.size).also {
                indexStart += submesh.triangleIndices.size
            }
        }
    }

    fun setBoundingBox(vertices: List<Vertex>) {
        // Calculate the Aabb in one pass through the vertices.
        var minPosition = vertices.first().position
        var maxPosition = vertices.first().position
        vertices.forEach { vertex ->
            minPosition = min(minPosition, vertex.position)
            maxPosition = max(maxPosition, vertex.position)
        }
        val halfExtent = (maxPosition - minPosition) * 0.5f
        val center = minPosition + halfExtent


        boundingBox = Box(center.toFloatArray(), halfExtent.toFloatArray())

    }

    fun removeGeometry(modelViewer:CustomModelViewer){

        modelViewer.scene.removeEntity(renderable)
        modelViewer.engine.destroyEntity(renderable)

        modelViewer.engine.destroyGeometry(this)

    }
}

val List<Vertex>.hasNormals get() = any { it.normal != null }
val List<Vertex>.hasUvCoordinates get() = any { it.uvCoordinates != null }
val List<Vertex>.hasColors get() = any { it.color != null }

fun Engine.destroyGeometry(geometry: Geometry) {
    destroyVertexBuffer(geometry.vertexBuffer)
    destroyIndexBuffer(geometry.indexBuffer)

}




fun normalToTangent(normal: Float3): Quaternion {
    var tangent: Float3
    val bitangent: Float3

    // Calculate basis vectors (+x = tangent, +y = bitangent, +z = normal).
    tangent = cross(Direction(y = 1.0f), normal)

    // Uses almostEqualRelativeAndAbs for equality checks that account for float inaccuracy.
    if (dot(tangent, tangent) == 0.0f) {
        bitangent = normalize(cross(normal, Direction(x = 1.0f)))
        tangent = normalize(cross(bitangent, normal))
    } else {
        tangent = normalize(tangent)
        bitangent = normalize(cross(normal, tangent))
    }
    // Rotation of a 4x4 Transformation Matrix is represented by the top-left 3x3 elements.
    return Transform(right = tangent, up = bitangent, forward = normal).toQuaternion()
}

