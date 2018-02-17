package com.developer.milton.task.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.developer.milton.task.R
import com.developer.milton.task.entities.OnTaskListFragmentInteractionListener
import com.developer.milton.task.entities.TaskEntity
import com.developer.milton.task.viewholder.TaskViewHolder

class TaskListAdapter(var taskList: List<TaskEntity>, val listener: OnTaskListFragmentInteractionListener) : RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.row_task_list, parent, false)

        return TaskViewHolder(view, context, listener)
    }

    override fun getItemCount(): Int {
        return taskList.count()
    }

    override fun onBindViewHolder(holder: TaskViewHolder?, position: Int) {
        val task = taskList[position]
        holder?.bindData(task)
    }
}