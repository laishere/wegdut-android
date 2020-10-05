package com.wegdut.wegdut

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.wegdut.wegdut.room.v1.AppDatabaseV1
import com.wegdut.wegdut.room.v1.edu.TermEntity
import com.wegdut.wegdut.room.v1.kv_storage.KVEntity
import com.wegdut.wegdut.room.v1.news.NewsEntity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
class DaoTest {
    private lateinit var db: AppDatabaseV1

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabaseV1::class.java
        ).build()

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun kv() {
        val dao = db.kvDao()
        val now = Date()
        val key = "test"
        val kv = KVEntity(key, "hello", now)
        dao.save(kv)
        val kv2 = dao.get(key)
        assertNotNull(kv2)
        assertEquals("hello", kv2!!.value)

        dao.delete(kv)
        assertNull(dao.get(key))

        dao.save(kv)
        assertNotNull(dao.get(key))
        dao.delete(key)
        assertNull(dao.get(key))
    }

    @Test
    fun news() {
        val dao = db.newsDao()
        val now = Date()
        val news1 = List(6) {
            NewsEntity(it.toLong(), "校内通知", "教务处", "", "", now, false)
        }
        dao.saveAll(news1)
        var res = dao.getHomeNotificationOnlyForStudent(100)
        assertEquals(0, res.size)
        res = dao.getHomeNotification(100)
        assertEquals(6, res.size)
        val news2 = List(6) {
            NewsEntity(100 + it.toLong(), "校内通知", "教务处", "", "", now, true)
        }
        dao.saveAll(news2)
        res = dao.getHomeNotificationOnlyForStudent(100)
        assertEquals(6, res.size)
        res = dao.getHomeNotification(100)
        assertEquals(12, res.size)
    }

    @Test
    fun termDao_getCurrentTerm() {
        val dao = db.termDao()
        val formatter = SimpleDateFormat("yyyy-M-d", Locale.CHINA)
        val start = formatter.parse("2020-9-1")!!
        val end = formatter.parse("2021-1-15")!!
        val term = TermEntity("202001", "上", "大二", start, end)
        dao.saveAll(listOf(term))
        val currentTerm = dao.getCurrentTerm()
        assertNotNull(currentTerm)
    }

    @Test
    fun termDao_getNextTerm() {
        val dao = db.termDao()
        val formatter = SimpleDateFormat("yyyy-M-d", Locale.CHINA)
        val start = formatter.parse("2020-9-1")!!
        val end = formatter.parse("2021-1-15")!!
        val term = TermEntity("202001", "上", "大二", start, end)
        dao.saveAll(listOf(term))
        val nextTerm = dao.getNextTerm()
        assertNotNull(nextTerm)
    }

}