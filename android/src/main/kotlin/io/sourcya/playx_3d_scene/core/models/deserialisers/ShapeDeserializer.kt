package io.sourcya.playx_3d_scene.core.models.deserialisers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import io.sourcya.playx_3d_scene.core.models.shapes.Cube
import io.sourcya.playx_3d_scene.core.models.shapes.Plane
import io.sourcya.playx_3d_scene.core.models.shapes.Shape
import io.sourcya.playx_3d_scene.core.models.shapes.Sphere
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