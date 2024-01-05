 package com.example.todolist

 import android.content.Intent
 import android.os.Bundle
 import android.widget.Button
 import androidx.appcompat.app.AppCompatActivity
 import androidx.recyclerview.widget.LinearLayoutManager
 import androidx.recyclerview.widget.RecyclerView
 import kotlinx.coroutines.Dispatchers
 import kotlinx.coroutines.GlobalScope
 import kotlinx.coroutines.launch
 import kotlinx.coroutines.withContext

 class MainActivity : AppCompatActivity(), TaskAdapter.OnTaskClickListener {

     private lateinit var taskAdapter: TaskAdapter
     private lateinit var taskRepository: TaskRepository
     private lateinit var myRecyclerView: RecyclerView
     private lateinit var floatingActionButton: Button

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)

         taskRepository = TaskRepository(applicationContext)

         setRecyclerView()
         buttonOnClick()
         loadTasks()
     }

     private fun loadTasks() {
         GlobalScope.launch {
             val tasks = withContext(Dispatchers.IO) {
                 taskRepository.getAllTasks()
             }
             updateUI(tasks)
         }
     }

     private fun updateUI(tasks: List<Task>) {
         runOnUiThread {
             taskAdapter = TaskAdapter(tasks, this)
             myRecyclerView.adapter = taskAdapter
         }
     }
     private fun buttonOnClick() {
         floatingActionButton = findViewById(R.id.floatingActionButton)

         floatingActionButton.setOnClickListener {
             val intent = Intent(this, TaskDetailActivity::class.java)
             startActivity(intent)
         }
     }
     private fun setRecyclerView(){
         myRecyclerView = findViewById(R.id.myRecyclerView)
         taskAdapter = TaskAdapter(emptyList(), this)

         myRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
         myRecyclerView.adapter = taskAdapter
     }

     override fun onTaskClick(task: Task) {
         val intent = Intent(this, TaskDetailActivity::class.java)
         intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
         startActivity(intent)
     }

 }

