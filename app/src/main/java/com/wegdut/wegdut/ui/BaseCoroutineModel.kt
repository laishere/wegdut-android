package com.wegdut.wegdut.ui

import com.wegdut.wegdut.MyLog
import kotlinx.coroutines.*

abstract class BaseCoroutineModel {
    open var scope = CoroutineScope(Dispatchers.Main)
    protected val jobs = Jobs()
    private var isAlive = false

    open fun start() {
        isAlive = true
    }

    open fun stop() {
        isAlive = false
        jobs.clear()
    }

    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        if (!isAlive) return Job().apply { cancel() }
        val job = scope.launch(block = block)
        jobs.add(job)
        return job
    }

    protected suspend fun <T> io(block: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.IO, block)

    protected fun <T> CoroutineScope.ioAsync(block: suspend CoroutineScope.() -> T) =
        async(Dispatchers.IO, block = block)

    protected suspend fun <T> compute(block: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.Default, block)

    protected suspend fun <T> tryIt(
        block: suspend () -> T,
        errorHandler: (e: Throwable) -> Unit = {}
    ) {
        try {
            block()
        } catch (e: CancellationException) {
        } catch (e: Throwable) {
            MyLog.error(this, e)
            errorHandler(e)
        }
    }
}

class Jobs : ArrayList<Job>() {
    override fun clear() {
        for (j in this) j.cancel()
        super.clear()
    }
}