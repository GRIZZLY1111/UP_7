package com.example.college

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.college.R
import com.example.college.Student

class StudentAdapter(
    private val onEdit: (Student) -> Unit,
    private val onDelete: (Student) -> Unit
) : ListAdapter<Student, StudentAdapter.ViewHolder>(StudentDiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(android.R.id.text1)
        private val details = itemView.findViewById<TextView>(android.R.id.text2)
        private val photo = itemView.findViewById<ImageView>(R.id.imageView)

        fun bind(student: Student, onEdit: (Student) -> Unit, onDelete: (Student) -> Unit) {
            name.text = student.fullName
            details.text = "${student.groupName}, курс ${student.course}"
            val resId = itemView.context.resources.getIdentifier(
                student.photoName,
                "drawable",
                itemView.context.packageName
            )
            photo.setImageResource(if (resId != 0) resId else R.drawable.photoone)

            itemView.setOnClickListener { onEdit(student) }
            itemView.setOnLongClickListener {
                onDelete(student)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onEdit, onDelete)
    }
}

class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
    override fun areItemsTheSame(oldItem: Student, newItem: Student) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Student, newItem: Student) = oldItem == newItem
}