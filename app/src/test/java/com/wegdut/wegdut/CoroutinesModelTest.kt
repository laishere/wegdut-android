package com.wegdut.wegdut

import com.wegdut.wegdut.CoroutinesModelTestUtils.runBlocking
import com.wegdut.wegdut.ui.BaseCoroutineModel
import kotlinx.coroutines.Deferred
import org.junit.Test
import kotlin.random.Random

class CoroutinesModelTest {

    @Test
    fun `每次IO都应该启动新线程`() {
        val threads = mutableSetOf<Thread>()
        val model = object : BaseCoroutineModel() {
            fun startIO() {
                launch {
                    io {
                        Thread.sleep(Random.nextLong(500, 2000)) // 模拟现有的io阻塞调用
                        synchronized(threads) {
                            threads.add(Thread.currentThread())
                        }
                    }
                }
            }
        }
        model.start()
        model.runBlocking {
            repeat(20) {
                model.startIO()
            }
        }
        assert(threads.size == 20)
    }

    @Test
    fun `AsyncIO测试`() {
        val oneDelay = 1000L
        val times = 10
        val model = object : BaseCoroutineModel() {
            fun run() {
                launch {
                    val results = mutableListOf<Deferred<*>>()
                    repeat(times) {
                        val r = ioAsync {
                            Thread.sleep(oneDelay) // 模拟现有的io阻塞调用
                            it
                        }
                        results.add(r)
                    }
                    println(results.map { it.await() })
                }
            }
        }
        model.start()
        val t1 = System.currentTimeMillis()
        model.runBlocking {
            model.run()
        }
        val t2 = System.currentTimeMillis()
        assert(t2 - t1 < oneDelay + 1000L)
        println(t2 - t1)
    }
}