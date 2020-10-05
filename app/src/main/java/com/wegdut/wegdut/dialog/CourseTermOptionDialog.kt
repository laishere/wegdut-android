package com.wegdut.wegdut.dialog

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.view.DrawerItem

class CourseTermOptionDialog(context: Context) : BottomListDialog(context) {

    private val adapter: Adapter = Adapter()
    var onTermSelectedListener: OnTermSelectedListener? = null

    init {
        adapter.onTermSelectedListener = object : OnTermSelectedListener {
            override fun onTermSelected(yearIndex: Int, termIndex: Int) {
                onTermSelectedListener?.onTermSelected(yearIndex, termIndex)
            }
        }
    }

    override fun getAdapter(): RecyclerView.Adapter<*> {
        return adapter
    }

    fun setItems(items: List<CourseTerm>) {
        adapter.items = items
        adapter.notifyDataSetChanged()
    }

    fun setActiveTerm(yearIndex: Int, termIndex: Int) {
        adapter.updateActiveItem(yearIndex, termIndex)
    }

    override fun getTitle(): CharSequence? {
        return "选择学期"
    }

    override fun onInit() {}


    data class CourseTerm(
        val year: String,
        val terms: List<String>
    )

    interface OnTermSelectedListener {
        fun onTermSelected(yearIndex: Int, termIndex: Int)
    }

    class Adapter(items: List<CourseTerm>? = null) :
        ListRVAdapter<CourseTerm, Adapter.ViewHolder>(items) {

        var activeYear = -1
        var activeTerm = -1
        var onTermSelectedListener: OnTermSelectedListener? = null
        private var lastExtendedPos = -1

        // 作用：只允许一个item展开
        private val onDrawerItemChangeListener = object : OnDrawerItemChangeListener {
            override fun onChange(pos: Int, isExtended: Boolean) {
                if (!isExtended || pos == lastExtendedPos) return
                if (lastExtendedPos in 0 until itemCount)
                    notifyItemChanged(lastExtendedPos, 1)
                lastExtendedPos = pos
            }
        }

        fun updateActiveItem(yearIndex: Int, termIndex: Int) {
            if (activeYear in 0 until itemCount)
                notifyItemChanged(activeYear, 1)
            activeYear = yearIndex
            activeTerm = termIndex
            notifyItemChanged(activeYear, 1)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.activeYear = activeYear
            holder.activeTerm = activeTerm
            holder.extendedItem = lastExtendedPos
            holder.onTermSelectedListener = onTermSelectedListener
            holder.onDrawerItemChangeListener = onDrawerItemChangeListener
            super.onBindViewHolder(holder, position)
        }

        class ViewHolder(itemView: View) : ListRVAdapter.ViewHolder<CourseTerm>(itemView) {
            var activeYear = -1
            var activeTerm = -1
            var extendedItem = -1
            var onTermSelectedListener: OnTermSelectedListener? = null
            var onDrawerItemChangeListener: OnDrawerItemChangeListener? = null
            private val drawerItem = itemView as DrawerItem
            private val yearTextView: TextView = itemView.findViewById(R.id.year)
            private val termTextView1: TextView = itemView.findViewById(R.id.term1)
            private val termTextView2: TextView = itemView.findViewById(R.id.term2)

            init {
                drawerItem.onChangeListener = object : DrawerItem.OnChangeListener {
                    override fun onChange(isExtended: Boolean) {
                        onDrawerItemChangeListener?.onChange(adapterPosition, isExtended)
                    }
                }
            }

            override fun bind(data: CourseTerm) {
                if (adapterPosition != extendedItem)
                    drawerItem.setExtended(false)
                val isActiveItem = activeYear == adapterPosition
                yearTextView.isActivated = isActiveItem
                yearTextView.text = data.year
                val arr = arrayOf(termTextView1, termTextView2)
                val terms = data.terms
                for (i in 0..1) {
                    if (i < terms.size) {
                        arr[i].isActivated = isActiveItem && activeTerm == i
                        arr[i].visibility = View.VISIBLE
                        arr[i].text = terms[i]
                        arr[i].setOnClickListener {
                            onTermSelectedListener?.onTermSelected(adapterPosition, i)
                        }
                    } else arr[i].visibility = View.GONE
                }
            }

        }

        interface OnDrawerItemChangeListener {
            fun onChange(pos: Int, isExtended: Boolean)
        }

        override fun getItemViewType(position: Int): Int {
            return R.layout.item_course_term
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = inflate(parent, viewType)
            return ViewHolder(v)
        }
    }
}