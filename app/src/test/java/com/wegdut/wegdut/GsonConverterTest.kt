package com.wegdut.wegdut

import com.google.gson.reflect.TypeToken
import com.wegdut.wegdut.room.GsonConverter
import org.junit.Assert.assertEquals
import org.junit.Test

class GsonConverterTest {
    private val converter = object : GsonConverter<List<Int>>() {
        override val typeToken: TypeToken<List<Int>> = object : TypeToken<List<Int>>() {}
    }

    @Test
    fun `toJson测试`() {
        val list = listOf(1, 2, 3)
        var res = converter.toJson(list)
        assertEquals("[1,2,3]", res)
        val nullList: List<Int>? = null
        res = converter.toJson(nullList)
        assertEquals(null, res)
    }

    @Test
    fun `fromJson测试`() {
        val list = "[1,2,3]"
        var res = converter.fromJson(list)
        assertEquals(listOf(1, 2, 3), res)

        res = converter.fromJson(null)
        assertEquals(null, res)
    }
}