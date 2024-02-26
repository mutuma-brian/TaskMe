package com.mutuma.taskme

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskList: MutableList<Duty>
    private lateinit var taskAdapter: TaskRecyclerViewAdapter
    private lateinit var buttonAddTask: FloatingActionButton
    private lateinit var buttonClearList: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonAddTaskListener()
        buttonClearListListener()

        taskRecyclerView = findViewById(R.id.taskRecyclerview)
        taskList = loadTasksFromLocalStorage()
        taskAdapter = TaskRecyclerViewAdapter(taskList, this)
        taskRecyclerView.adapter = taskAdapter
        taskRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
    }

    private fun buttonAddTaskListener() {
        buttonAddTask = findViewById(R.id.buttonAddTask)
        buttonAddTask.setOnClickListener {
            val emptyTask = createEmptyTask()
            taskList.add(emptyTask)
            taskAdapter.notifyItemInserted(taskList.size - 1)
        }
    }

    private fun buttonClearListListener() {
        buttonClearList = findViewById(R.id.buttonClear)
        buttonClearList.setOnClickListener {
            clearListInLocalStorage()
        }
    }

    private fun createEmptyTask(): Duty {
        return Duty(
            dutyNumber = "",
            dutyDescription = "",
            dueDate = "",
            isDone = false
        )
    }

    private fun loadTasksFromLocalStorage(): MutableList<Duty> {
        val sharedPreferences = getSharedPreferences("TaskSharedPreferences", MODE_PRIVATE)
        val tasksJson = sharedPreferences.getString("tasks", "")
        val typeToken = object : TypeToken<MutableList<Duty>>() {}.type
        return Gson().fromJson(tasksJson, typeToken) ?: mutableListOf()
    }

    private fun clearListInLocalStorage() {
        taskList.clear()
        saveTasksToLocalStorage()
        taskAdapter.notifyDataSetChanged()
        Toast.makeText(this, "List has been cleared", Toast.LENGTH_SHORT).show()
    }

    private fun saveTasksToLocalStorage() {
        val updatedTasksJson = Gson().toJson(taskList)
        val sharedPreferences = getSharedPreferences("TaskSharedPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("tasks", updatedTasksJson)
        editor.apply()
    }
}
