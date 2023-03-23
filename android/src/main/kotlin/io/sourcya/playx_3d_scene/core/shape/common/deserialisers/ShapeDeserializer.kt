package io.sourcya.playx_3d_scene.core.shape.common.deserialisers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.sourcya.playx_3d_scene.core.shape.common.model.Shape
import io.sourcya.playx_3d_scene.core.shape.cube.model.Cube
import io.sourcya.playx_3d_scene.core.shape.plane.model.Plane
import io.sourcya.playx_3d_scene.core.shape.sphere.model.Sphere
import io.sourcya.playx_3d_scene.utils.gson
import java.lang.reflect.Type

class ShapeDeserializer:JsonDeserializer<Shape>{
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Shape? {
        return when(json?.asJsonObject?.get("shapeType")?.asInt){
            1-> gson.fromJson(json, object : TypeToken<Plane>() {}.type)
            2-> gson.fromJson(json, object : TypeToken<Cube>() {}.type)
            3-> gson.fromJson(json, object : TypeToken<Sphere>() {}.type)
            else -> null
        }
    }

}