package com.arqathon.utils

import com.google.gson.*
import java.lang.reflect.Type

object SerializerUtil{
    val gsonBuilder = com.google.gson.GsonBuilder()

    /*fun setupGsonDateTimeParsing(): GsonBuilder {

        return gsonBuilder.apply {
            registerTypeAdapter(DateTime::class.java, object : JsonDeserializer<DateTime> {
                override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DateTime {
                    return DateTime.parse(json.asString)
                }
            })

            registerTypeAdapter(DateTime::class.java, object : JsonDeserializer<DateTime> {
                override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DateTime {
                    return DateTime.parse(json.asString)
                }
            })

            registerTypeAdapter(DateTime::class.java, object : JsonDeserializer<DateTime> {
                override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DateTime {
                    return DateTime.parse(json.asString)
                }
            })
            registerTypeAdapter(DateTime::class.java, object : JsonSerializer<DateTime> {
                override fun serialize(src: DateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
                    val jsonElement = JsonPrimitive(src.toString())
                    return jsonElement
                }
            })

            registerTypeAdapter(LocalDate::class.java, object : JsonDeserializer<LocalDate> {
                override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate? {
                    val jsonString: String = json.asString
                    return LocalDate.parse(jsonString)
                }
            })

            registerTypeAdapter(LocalDate::class.java, object : JsonSerializer<LocalDate> {
                override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
                    val jsonElement = JsonPrimitive(src.toString())
                    return jsonElement
                }
            })

            registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
                override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
                    return LocalDateTime.parse(json.asString)
                }
            })

        }
    }*/
}