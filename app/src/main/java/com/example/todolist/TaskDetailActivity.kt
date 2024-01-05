package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskDetailActivity : AppCompatActivity() {

    private  var editTextTaskTitle: EditText = findViewById(R.id.editTextTaskTitle)
    private var editTextTaskDescription: EditText = findViewById(R.id.editTextTaskDescription)
    private var spinnerPriority: Spinner = findViewById(R.id.spinnerPriority)
    private lateinit var buttonSave: Button
    private lateinit var taskRepository: TaskRepository
    private var taskId: Long = -1

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        taskRepository = TaskRepository(applicationContext)
        taskId = intent.getLongExtra(EXTRA_TASK_ID, -1)

        if (taskId != -1L) {
            // Editing an existing task, load task details
            loadTaskDetails()
        }

       setButtonSave()
    }

    private fun loadTaskDetails() {

        GlobalScope.launch {
            val task = withContext(Dispatchers.IO) {
                taskRepository.getTaskById(taskId)
            }
            runOnUiThread {
                // Populate UI with task details for editing
                editTextTaskTitle.setText(task.title)
                editTextTaskDescription.setText(task.description)
                spinnerPriority.setSelection(task.priority - 1) // Adjust index
            }
        }
    }

    private fun saveTask() {
        val title = editTextTaskTitle.text.toString()
        val description = editTextTaskDescription.text.toString()
        val priority = spinnerPriority.selectedItemPosition + 1 // Adjust priority

        GlobalScope.launch {
            if (taskId == -1L) {
                // New task, insert into the database
                taskRepository.insertTask(Task(0, title, description, priority, System.currentTimeMillis(), false))
            } else {
                // Existing task, update in the database
                val updatedTask = Task(taskId, title, description, priority, System.currentTimeMillis(), false)
                taskRepository.updateTask(updatedTask)
            }

            finish()
        }
    }

    private fun setButtonSave(){
        buttonSave = findViewById(R.id.buttonSave)

        buttonSave.setOnClickListener {
            saveTask()
        }
    }
}
