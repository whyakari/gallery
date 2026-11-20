package com.example.gallery.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {
    private val gson = Gson()
    private val longListType: Type = TypeToken.getParameterized(List::class.java, Long::class.java).type
    private val stringListType: Type = TypeToken.getParameterized(List::class.java, String::class.java).type
    private val intListType: Type = TypeToken.getParameterized(List::class.java, Int::class.java).type
    private val floatArrayType: Type = FloatArray::class.java

    @TypeConverter
    fun fromLongList(value: String?): List<Long> {
        return value?.let { gson.fromJson(it, longListType) } ?: emptyList()
    }

    @TypeConverter
    fun toLongList(list: List<Long>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return value?.let { gson.fromJson(it, stringListType) } ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromIntList(value: String?): List<Int> {
        return value?.let { gson.fromJson(it, intListType) } ?: emptyList()
    }

    @TypeConverter
    fun toIntList(list: List<Int>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromFloatArray(value: String?): FloatArray? {
        return value?.let { gson.fromJson(it, floatArrayType) }
    }

    @TypeConverter
    fun toFloatArray(array: FloatArray?): String? {
        return array?.let { gson.toJson(it) }
    }

    // BoundingBox converter
    @TypeConverter
    fun fromBoundingBox(value: String?): com.example.gallery.domain.model.BoundingBox? {
        return value?.let { gson.fromJson(it, com.example.gallery.domain.model.BoundingBox::class.java) }
    }

    @TypeConverter
    fun toBoundingBox(box: com.example.gallery.domain.model.BoundingBox?): String? {
        return box?.let { gson.toJson(it) }
    }

    // PointF converter
    @TypeConverter
    fun fromPointF(value: String?): com.example.gallery.domain.model.PointF? {
        return value?.let { gson.fromJson(it, com.example.gallery.domain.model.PointF::class.java) }
    }

    @TypeConverter
    fun toPointF(point: com.example.gallery.domain.model.PointF?): String? {
        return point?.let { gson.toJson(it) }
    }
}
