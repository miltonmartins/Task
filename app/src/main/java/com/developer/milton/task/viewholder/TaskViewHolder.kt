package com.developer.milton.task.viewholder

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.developer.milton.task.R
import com.developer.milton.task.entities.OnTaskListFragmentInteractionListener
import com.developer.milton.task.entities.TaskEntity


class TaskViewHolder(itemView: View, val mContext: Context, val mListener: OnTaskListFragmentInteractionListener) : RecyclerView.ViewHolder(itemView) {
    private val mTextDescription: TextView = itemView.findViewById(R.id.textDescription)
    private val mTextPriority: TextView = itemView.findViewById(R.id.textPriority)
    private val mImageTask: ImageView = itemView.findViewById(R.id.imageTask)
    private val mTextDate: TextView = itemView.findViewById(R.id.textDueDate)

    fun bindData(task: TaskEntity) {
        mTextDescription.text = task.description
        mTextPriority.text = task.priority
        mTextDate.text = task.dueDate

        if (task.status.equals("true"))
            mImageTask.setImageResource(R.drawable.ic_done)

        mTextDescription.setOnClickListener({
            mListener.onListClick(task.id)
        })

        mTextDescription.setOnLongClickListener({
            showConfirmationDialog(task.id)
            true
        })
    }

    private fun showConfirmationDialog(taskId: String) {
        AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.remove_task))
                .setMessage(mContext.getString(R.string.ask_remove))
                .setIcon(R.drawable.ic_delete_black)
                .setPositiveButton(mContext.getString(R.string.remove), { _, _ -> mListener.onDeleteClick(taskId)})
                .setNegativeButton(mContext.getString(R.string.cancel), null).show()
    }
}