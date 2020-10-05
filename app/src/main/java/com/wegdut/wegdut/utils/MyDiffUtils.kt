package com.wegdut.wegdut.utils

import androidx.recyclerview.widget.DiffUtil
import com.wegdut.wegdut.ui.BaseCoroutineModel
import com.wegdut.wegdut.ui.BaseTypeRVAdapter
import com.wegdut.wegdut.ui.ListRVAdapter

object MyDiffUtils : BaseCoroutineModel() {

    init {
        start()
    }

    private fun <T> simpleDiff(
        old: List<T>,
        new: List<T>,
        diffCallback: SimpleDiffCallback<in T>? = null,
        done: ((DiffUtil.DiffResult) -> Unit)? = null
    ) {
        launch {
            tryIt({
                val result = compute {
                    val callback = diffCallback ?: SimpleDiffCallback()
                    callback.old = old
                    callback.new = new
                    DiffUtil.calculateDiff(callback)
                }
                done?.invoke(result)
            })
        }
    }

    fun <T> diffToListRVAdapter(
        new: List<T>, adapter: ListRVAdapter<T, out ListRVAdapter.ViewHolder<T>>,
        diffCallback: SimpleDiffCallback<in T>? = null,
        done: () -> Unit = {}
    ) {
        simpleDiff(adapter.items ?: emptyList(), new, diffCallback) {
            adapter.items = new
            it.dispatchUpdatesTo(adapter)
            done()
        }
    }

    fun diffToBaseTypeRVAdapter(
        new: List<BaseTypeRVAdapter.Item>, adapter: BaseTypeRVAdapter,
        diffCallback: SimpleDiffCallback<BaseTypeRVAdapter.Item>? = null,
        alwaysUpdate: Boolean = false,
        done: () -> Unit = {}
    ) {
        val callback = diffCallback ?: BaseTypeDiffCallback(alwaysUpdate)
        simpleDiff(adapter.items ?: emptyList(), new, callback) { result ->
            adapter.items = new
            result.dispatchUpdatesTo(adapter)
            done()
        }
    }

    open class SimpleDiffCallback<T> : DiffUtil.Callback() {
        lateinit var old: List<T>
        lateinit var new: List<T>

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }

        override fun getOldListSize(): Int {
            return old.size
        }

        override fun getNewListSize(): Int {
            return new.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return 1
        }
    }

    class BaseTypeDiffCallback(private val alwaysUpdate: Boolean) :
        SimpleDiffCallback<BaseTypeRVAdapter.Item>() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition].isSame(new[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (alwaysUpdate) return false
            return old[oldItemPosition].hasSameContent(new[newItemPosition])
        }
    }
}