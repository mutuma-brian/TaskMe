package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: List<Task>,
    private val onTaskClickListener: OnTaskClickListener
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    interface OnTaskClickListener {
        fun onTaskClick(task: Task)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val priorityTextView: TextView = itemView.findViewById(R.id.priorityTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.titleTextView.text = currentTask.title
        holder.priorityTextView.text = when (currentTask.priority) {
            1 -> "Low"
            2 -> "Medium"
            3 -> "High"
            else -> "Unknown"
        }

        holder.itemView.setOnClickListener {
            onTaskClickListener.onTaskClick(currentTask)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}
