package com.wegdut.wegdut

import com.google.gson.reflect.TypeToken
import com.wegdut.wegdut.room.ListConverter
import org.junit.Assert
import org.junit.Test

class ListConverterTest {
    private val converter = object : ListConverter<Int>() {
        override val typeToken: TypeToken<List<Int>> = object : TypeToken<List<Int>>() {}
    }

    @Test
    fun `fromJson测试`() {
        val list = "[1,2,3]"
        var res = converter.fromJson(list)
        Assert.assertEquals(listOf(1, 2, 3), res)

        res = converter.fromJson(null)
        Assert.assertEquals(emptyList<Int>(), res)
    }
}