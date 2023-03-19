package io.sourcya.playx_3d_scene.core.geometry

import com.google.android.filament.Engine
import io.sourcya.playx_3d_scene.core.models.shapes.*


/**
 * Creates a [Geometry] in the shape of a cube with the give specifications.
 *
 * @param size the size of the constructed cube
 * @param center the center of the constructed cube
 */
class CubeGeometry private constructor(
    center: Position,
    size : Size,
    geometry: Geometry,
) : Geometry(
    geometry.vertexBuffer,
    geometry.indexBuffer,
    geometry.boundingBox,
    geometry.offsetsCounts,
    geometry.vertices,
    geometry.submeshes
){


    /**
     * Center of the constructed cube
     */
    var center: Position = center
        private set

    /**
     * length of the constructed cube
     */
    var  size : Size = size
        private set




    class Builder(val  center: Position = Position(0.0f), val size : Size = Size(2f),) : Geometry.Builder(
        vertices = getVertices(center,size),
        submeshes = mutableListOf<Submesh>().apply {
            val sideCount = 6
            val verticesPerSide = 4
            for (i in 0 until sideCount) {
                add(
                    Submesh(
                        // First triangle for this side.
                        3 + verticesPerSide * i,
                        1 + verticesPerSide * i,
                        0 + verticesPerSide * i,
                        // Second triangle for this side.
                        3 + verticesPerSide * i,
                        2 + verticesPerSide * i,
                        1 + verticesPerSide * i
                    )
                )
            }
        }


    ) {
        override fun build(engine: Engine): CubeGeometry = CubeGeometry(center,size , super.build(engine))
    }

    fun update(
        engine: Engine,
        center: Position = this.center,
        size : Size = this.size,
    ) {
        setBufferVertices(engine, getVertices(center, size))
    }
    companion object {

        fun getVertices(
            center: Position = Position(0.0f),
            size : Size = Size(2f),
        ): List<Vertex> =  mutableListOf<Vertex>().apply {
            val extents = size * 0.5f
            val p0 = center + Size(-extents.x, -extents.y, extents.z)
            val p1 = center + Size(extents.x, -extents.y, extents.z)
            val p2 = center + Size(extents.x, -extents.y, -extents.z)
            val p3 = center + Size(-extents.x, -extents.y, -extents.z)
            val p4 = center + Size(-extents.x, extents.y, extents.z)
            val p5 = center + Size(extents.x, extents.y, extents.z)
            val p6 = center + Size(extents.x, extents.y, -extents.z)
            val p7 = center + Size(-extents.x, extents.y, -extents.z)
            val up = Direction(y = 1.0f)
            val down = Direction(y = -1.0f)
            val front = Direction(z = -1.0f)
            val back = Direction(z = 1.0f)
            val left = Direction(x = -1.0f)
            val right = Direction(x = 1.0f)
            val uv00 = UVCoordinates(x = 0.0f, y = 0.0f)
            val uv10 = UVCoordinates(x = 1.0f, y = 0.0f)
            val uv01 = UVCoordinates(x = 0.0f, y = 1.0f)
            val uv11 = UVCoordinates(x = 1.0f, y = 1.0f)
            addAll(
                listOf(
                    // Bottom
                    Vertex(position = p0, normal = down,  uvCoordinates = uv01),
                    Vertex(position = p1, normal = down, uvCoordinates = uv11),
                    Vertex(position = p2, normal = down, uvCoordinates = uv10),
                    Vertex(position = p3, normal = down, uvCoordinates = uv00),
                    // Left
                    Vertex(position = p7, normal = left, uvCoordinates = uv01),
                    Vertex(position = p4, normal = left, uvCoordinates = uv11),
                    Vertex(position = p0, normal = left, uvCoordinates = uv10),
                    Vertex(position = p3, normal = left, uvCoordinates = uv00),
                    // Back
                    Vertex(position = p4, normal = back, uvCoordinates = uv01),
                    Vertex(position = p5, normal = back, uvCoordinates = uv11),
                    Vertex(position = p1, normal = back, uvCoordinates = uv10),
                    Vertex(position = p0, normal = back, uvCoordinates = uv00),
                    // Front
                    Vertex(position = p6, normal = front, uvCoordinates = uv01),
                    Vertex(position = p7, normal = front, uvCoordinates = uv11),
                    Vertex(position = p3, normal = front, uvCoordinates = uv10),
                    Vertex(position = p2, normal = front, uvCoordinates = uv00),
                    // Right
                    Vertex(position = p5, normal = right, uvCoordinates = uv01),
                    Vertex(position = p6, normal = right, uvCoordinates = uv11),
                    Vertex(position = p2, normal = right, uvCoordinates = uv10),
                    Vertex(position = p1, normal = right, uvCoordinates = uv00),
                    // Top
                    Vertex(position = p7, normal = up, uvCoordinates = uv01),
                    Vertex(position = p6, normal = up, uvCoordinates = uv11),
                    Vertex(position = p5, normal = up, uvCoordinates = uv10),
                    Vertex(position = p4, normal = up, uvCoordinates = uv00)
                )
            )
        }
    }


}