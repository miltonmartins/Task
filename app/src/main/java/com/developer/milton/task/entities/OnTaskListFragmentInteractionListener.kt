package com.developer.milton.task.entities

interface OnTaskListFragmentInteractionListener {
    fun onListClick(taskId: String)
    fun onDeleteClick(taskId: String)
}