package com.wegdut.wegdut.ui.user

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.edu.exam_plan.ExamPlan
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.utils.DateUtils
import java.util.*

class ExamPlanAdapter(items: List<ExamPlan>? = null) :
    ListRVAdapter<ExamPlan, ExamPlanAdapter.ExamPlanViewHolder>(items) {

    class ExamPlanViewHolder(itemView: View) : ListRVAdapter.ViewHolder<ExamPlan>(itemView) {
        private val label: View = itemView.findViewById(R.id.label)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val location: TextView = itemView.findViewById(R.id.location)
        private val time: TextView = itemView.findViewById(R.id.time)
        override fun bind(data: ExamPlan) {
            name.text = data.subject
            location.text = data.classroom
            time.text = DateUtils.format(data.start, DateUtils.FormatType.SHORT_DATE)
            val now = System.currentTimeMillis()
            val start = data.start.time
            val end = data.end.time
            val deltaDay = DateUtils.deltaDay(Date(), data.start)
            val isToday = deltaDay == 0L
            name.isSelected = isToday
            location.isSelected = isToday
            time.isSelected = isToday
            itemView.isSelected = isToday

            // 必须在后面，以免selected受父view影响
            label.isSelected = false
            label.isHovered = false
            when {
                now < start -> {
                }
                now in start..end -> label.isHovered = true
                else -> label.isSelected = true
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_user_exam_plan_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamPlanViewHolder {
        val v = inflate(parent, viewType)
        return ExamPlanViewHolder(v)
    }
}