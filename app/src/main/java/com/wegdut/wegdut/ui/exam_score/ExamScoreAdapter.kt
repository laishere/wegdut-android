package com.wegdut.wegdut.ui.exam_score

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.edu.exam_score.ExamScore
import com.wegdut.wegdut.ui.ListRVAdapter

class ExamScoreAdapter : ListRVAdapter<ExamScore, ExamScoreAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : ListRVAdapter.ViewHolder<ExamScore>(itemView) {
        private val subject: TextView = itemView.findViewById(R.id.subject)
        private val score: TextView = itemView.findViewById(R.id.score)
        override fun bind(data: ExamScore) {
            subject.text = data.subject
            var s = data.score
            if (data.gradePoint.isNotBlank())
                s += " / ${data.gradePoint}"
            score.text = s
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_exam_score
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = inflate(parent, viewType)
        return ViewHolder(v)
    }
}