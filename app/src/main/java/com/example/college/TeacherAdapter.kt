package com.example.college

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TeacherAdapter(
    private val onEdit: (Teacher) -> Unit,
    private val onDelete: (Teacher) -> Unit
) : ListAdapter<Teacher, TeacherAdapter.ViewHolder>(TeacherDiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(android.R.id.text1)
        private val email = itemView.findViewById<TextView>(android.R.id.text2)

        fun bind(teacher: Teacher, onEdit: (Teacher) -> Unit, onDelete: (Teacher) -> Unit) {
            name.text = teacher.fullName
            email.text = teacher.email
            itemView.setOnClickListener { onEdit(teacher) }
            itemView.setOnLongClickListener {
                onDelete(teacher)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onEdit, onDelete)
    }
}

class TeacherDiffCallback : DiffUtil.ItemCallback<Teacher>() {
    override fun areItemsTheSame(oldItem: Teacher, newItem: Teacher) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Teacher, newItem: Teacher) = oldItem == newItem
}