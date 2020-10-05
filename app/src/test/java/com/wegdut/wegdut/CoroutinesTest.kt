package com.wegdut.wegdut

import kotlinx.coroutines.*
import org.junit.Test

class CoroutinesTest {

    @Test
    fun `cancel 后 scope 不可以继续运行任务`() {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.cancel()
        val job = scope.launch {}
        assert(job.isCancelled)
    }

    @Test
    fun `复合scope不受控于父scope`() {
        val parentScope = CoroutineScope(Dispatchers.Default)
        val scope = parentScope + Job()
        parentScope.cancel()
        assert(!parentScope.isActive)
        assert(scope.isActive)
    }

    @Test
    fun `job 可以重复cancel而不报错`() {
        val job = GlobalScope.launch { }
        assert(!job.isCancelled)
        job.cancel()
        assert(job.isCancelled)
        job.cancel()
    }

}