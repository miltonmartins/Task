package com.developer.milton.task.views

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import com.developer.milton.task.R
import com.developer.milton.task.constants.TaskConstants
import com.developer.milton.task.entities.TaskEntity
import com.developer.milton.task.data.DataBaseHelper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_task_form.*
import java.text.SimpleDateFormat
import java.util.*

class TaskFormActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private val mSimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val mDataBaseHelper = DataBaseHelper()
    private var mTaskId: String = ""
    private val db = FirebaseFirestore.getInstance()
    private val dataTaskRef = db.collection(TaskConstants.RefCollectionFirestore.TASK)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)

        setListeners()
        loadPriorities()
        loadDataFromActivity()
    }

    private fun setListeners() {
        buttonDate.setOnClickListener(this)
        buttonSaveTask.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonDate -> openDatePickerDialog()
            R.id.buttonSaveTask -> handleSave()
        }
    }

    private fun openDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, this, year, month, day).show()
    }

    override fun onDateSet(view: DatePicker?, y: Int, m: Int, d: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(y, m, d)

        buttonDate.text = mSimpleDateFormat.format(calendar.time)
    }

    private fun loadPriorities() {
        val array = arrayOf("Low", "Medium", "High")

        val adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, array)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = adapter
    }

    private fun loadDataFromActivity() {
        val bundle = intent.extras

        if (bundle != null) {
            mTaskId = bundle.getString(TaskConstants.Bundle.TASKID)

            dataTaskRef
                    .document(mTaskId)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val task = TaskEntity(documentSnapshot.data["description"].toString(), documentSnapshot.data["dueDate"].toString(),
                                documentSnapshot.data["priority"].toString(), documentSnapshot.data["status"].toString(), documentSnapshot.reference.id)

                        mTaskId = documentSnapshot.reference.id
                        editDescription.setText(task.description)
                        buttonDate.text = task.dueDate
                        checkDone.isChecked = task.status.toBoolean()
                        spinnerPriority.setSelection(getIndex(task.priority))
                    }
        }
    }


    private fun getIndex(status: String?): Int {
        var id: Int
        when (status) {
            "Low" -> id = 0
            "Medium" -> id = 1
            else -> id = 2
        }

        return id
    }

    private fun handleSave() {
        val mTaskEntity: TaskEntity

        try {
            mTaskEntity = TaskEntity(editDescription.text.toString(), buttonDate.text.toString(), spinnerPriority.selectedItem.toString(), checkDone.isChecked.toString(), mTaskId)
            val bundle = intent.extras
            if (bundle != null) mDataBaseHelper.updateTask(mTaskEntity, mTaskId)
            else mDataBaseHelper.newTask(mTaskEntity)

            Toast.makeText(this, getString(R.string.task_added), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_addTask), Toast.LENGTH_SHORT).show()
        } finally {
            finish()
        }
    }
}
