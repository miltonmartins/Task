package com.developer.milton.task.data

import com.google.firebase.firestore.FirebaseFirestore

import android.content.ContentValues.TAG
import android.util.Log
import com.developer.milton.task.constants.TaskConstants
import com.developer.milton.task.entities.TaskEntity

class DataBaseHelper {
    private val db = FirebaseFirestore.getInstance()
    private val dataTaskRef = db.collection(TaskConstants.RefCollectionFirestore.TASK)


    fun newTask(task: TaskEntity) {

        dataTaskRef
                .add(task)
                .addOnSuccessListener { documentReference -> updateTask(task, documentReference.id)  }
                .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }

    fun updateTask(mTaskEntity: TaskEntity, taskId: String) {
        mTaskEntity.id = taskId
        dataTaskRef
                .document(taskId)
                .set(mTaskEntity)
                .addOnSuccessListener { _ -> Log.d(TAG, "DocumentSnapshot written with ID: ") }
                .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
    }

    fun deleteTask(taskId: String) {
        dataTaskRef
                .document(taskId)
                .delete()
                .addOnSuccessListener { }
                .addOnFailureListener { }
    }
}