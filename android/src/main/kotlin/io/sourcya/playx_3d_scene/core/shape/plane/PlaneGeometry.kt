package io.sourcya.playx_3d_scene.core.shape.plane

import com.google.android.filament.Engine
import io.sourcya.playx_3d_scene.core.shape.common.geometry.Geometry
import io.sourcya.playx_3d_scene.core.shape.common.model.*

class PlaneGeometry private constructor(
    center: Position,
    size: Size,
    normal: Direction,
    geometry: Geometry,
) : Geometry(
    geometry.vertexBuffer,
    geometry.indexBuffer,
    geometry.boundingBox,
    geometry.offsetsCounts,
    geometry.vertices,
    geometry.submeshes
) {




    /**
     * Center of the constructed plane
     */
    var center: Position = center
        private set

    /**
     * Size of the constructed plane
     */
    var size: Size = size
        private set

    var normal: Direction = normal
        private set


    /**
     * Creates a [Geometry] in the shape of a plane with the give specifications
     *
     * @param center Center of the constructed plane
     * @param size  Size of the constructed plane
     */
    class Builder(
        val center: Position = Position(0.0f,0f,-4f),
        val size: Size = Size(x = 2.0f, y = 2.0f),
        val normal : Direction = Direction(y=1f)
    ) : Geometry.Builder(

        vertices = getVertices(center, size,normal),
        submeshes = mutableListOf(
            Submesh(
                // First triangle for this side.
                3, 1, 0,
                // Second triangle for this side.
                3, 2, 1
            )
        )
    ) {
         override fun build(engine: Engine): PlaneGeometry = PlaneGeometry(center, size,normal,super.build(engine))
    }

    fun update(
        engine: Engine,
        center: Position = this.center,
        size: Size = this.size,
        normal: Direction
    ) {
        setBufferVertices(engine, getVertices(center, size,normal))
    }



    companion object {

        fun getVertices(
            center: Position = Position(0.0f),
            size: Size = Size(x = 2.0f, y = 2.0f),
            normal : Direction = Direction(y=1f)
        ): List<Vertex> = mutableListOf<Vertex>().apply {
            val extents = size / 2.0f

            val p0 = center + Size(x = -extents.x, -extents.y, extents.z)
            val p1 = center + Size(x = -extents.x, extents.y, -extents.z)
            val p2 = center + Size(x = extents.x, extents.y, -extents.z)
            val p3 = center + Size(x = extents.x, -extents.y, extents.z)

            val uv00 = UVCoordinates(x = 0.0f, y = 0.0f)
            val uv10 = UVCoordinates(1.0f, 0.0f)
            val uv01 = UVCoordinates(0.0f, 1.0f)
            val uv11 = UVCoordinates(1.0f, 1.0f)

            add(Vertex(position = p0, normal = normal, uvCoordinates = uv00))
            add(Vertex(position = p1, normal = normal, uvCoordinates = uv01))
            add(Vertex(position = p2, normal = normal, uvCoordinates = uv11))
            add(Vertex(position = p3, normal = normal, uvCoordinates = uv10))


            }
        }


}