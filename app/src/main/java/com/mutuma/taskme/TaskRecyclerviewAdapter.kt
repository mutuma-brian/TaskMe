package com.mutuma.taskme

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class TaskRecyclerViewAdapter(private var taskList: MutableList<Duty>, private val context: Context) :
    RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder>() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TaskSharedPreferences", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dutyNumber: TextView = itemView.findViewById(R.id.taskNumber)
        var duty: EditText = itemView.findViewById(R.id.editTextTask)
        var date: ImageView = itemView.findViewById(R.id.imageViewDate)
        var delete: ImageView = itemView.findViewById(R.id.imageViewDelete)
        var done: CheckBox = itemView.findViewById(R.id.checkBoxDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_recyclerview, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        holder.dutyNumber.text = taskList[position].dutyNumber
        holder.duty.setText(taskList[position].dutyDescription)
        holder.done.isChecked = taskList[position].isDone

        // Set listeners
        holder.date.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                holder.itemView.context,
                { _, year, month, day ->
                    val updatedDate = "$day/${month + 1}/$year"
                    taskList[position].dueDate = updatedDate
                    saveTasksToLocalStorage()
                    notifyDataSetChanged()
                },
                taskList[position].dueDate?.split("/")?.get(2)?.toInt() ?: 2024,
                taskList[position].dueDate?.split("/")?.get(1)?.toInt()?.minus(1) ?: 0,
                taskList[position].dueDate?.split("/")?.get(0)?.toInt() ?: 1
            )
            datePickerDialog.show()
        }

        holder.delete.setOnClickListener {
            deleteTask(position)
        }

        holder.done.setOnCheckedChangeListener { _, isChecked ->
            taskList[position].isDone = isChecked
            saveTasksToLocalStorage()
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    private fun deleteTask(position: Int) {
        taskList.removeAt(position)
        saveTasksToLocalStorage()
        notifyDataSetChanged()
    }

    private fun saveTasksToLocalStorage() {
        // Load existing tasks
        val existingTasksJson = sharedPreferences.getString("tasks", "")
        val existingTypeToken = object : TypeToken<MutableList<Duty>>() {}.type
        val existingTasks: MutableList<Duty> = Gson().fromJson(existingTasksJson, existingTypeToken) ?: mutableListOf()

        // Append new tasks to existing tasks
        existingTasks.addAll(taskList)

        // Save the updated tasks
        val updatedTasksJson = Gson().toJson(existingTasks)
        editor.putString("tasks", updatedTasksJson)
        editor.apply()
    }
}
