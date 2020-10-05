package com.wegdut.wegdut.helper

import java.util.*

class SimpleTimer {
    private val timer = Timer()
    private val tasks = mutableListOf<TimerTask>()

    fun delay(delay: Long, something: () -> Unit): TimerTask {
        val task = object : TimerTask() {
            override fun run() {
                something()
            }
        }
        timer.schedule(task, delay)
        tasks.add(task)
        return task
    }

    fun clear() {
        for (t in tasks)
            t.cancel()
        tasks.clear()
    }
}