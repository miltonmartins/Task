package com.developer.milton.task.views

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.developer.milton.task.R
import com.developer.milton.task.adapter.TaskListAdapter
import com.developer.milton.task.constants.TaskConstants
import com.developer.milton.task.entities.OnTaskListFragmentInteractionListener
import com.developer.milton.task.entities.TaskEntity
import com.developer.milton.task.data.DataBaseHelper
import com.google.firebase.firestore.FirebaseFirestore

class TaskListFragment : Fragment(), View.OnClickListener {

    private lateinit var mRecyclerTaskList: RecyclerView
    private lateinit var mContext: Context
    private var mTaskFilter: Int = 0
    private var mDataBaseHelper =  DataBaseHelper()
    private lateinit var mListener: OnTaskListFragmentInteractionListener
    private val db = FirebaseFirestore.getInstance()
    private val dataTaskRef = db.collection(TaskConstants.RefCollectionFirestore.TASK)
    private var taskList = mutableListOf<TaskEntity>()

    companion object {

        fun newInstance(taskFilter: Int): TaskListFragment {
            val fragment = TaskListFragment()
            val args = Bundle()

            args.putInt(TaskConstants.TaskFilter.KEY, taskFilter)

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mTaskFilter = arguments.getInt(TaskConstants.TaskFilter.KEY)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.floatAddTask -> startActivity(Intent(mContext, TaskFormActivity::class.java))
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_task_list, container, false)
        mContext = rootView.context
        rootView.findViewById<FloatingActionButton>(R.id.floatAddTask).setOnClickListener(this)

        mListener = object : OnTaskListFragmentInteractionListener {
            override fun onListClick(taskId: String) {
                val bundle = Bundle()
                val intent = Intent(mContext, TaskFormActivity::class.java)

                bundle.putString(TaskConstants.Bundle.TASKID, taskId)
                intent.putExtras(bundle)

                startActivity(intent)
            }

            override fun onDeleteClick(taskId: String) {
                mDataBaseHelper.deleteTask(taskId)
                Toast.makeText(mContext, getString(R.string.task_removed), Toast.LENGTH_LONG).show()
                loadTasks()
            }
        }

        //RecyclerView
        mRecyclerTaskList = rootView.findViewById(R.id.recyclerTaskList)
        mRecyclerTaskList.layoutManager = LinearLayoutManager(mContext)

        return rootView
    }

    private fun loadTasks() {
        // mDataBaseHelper.bindData(mRecyclerTaskList, mTaskFilter)
        var mTaskEntity: TaskEntity
        taskList.removeAll(taskList)

        dataTaskRef
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (doc in task.result) {
                            mTaskEntity = TaskEntity(doc.data["description"].toString(), doc.data["dueDate"].toString(),
                                    doc.data["priority"].toString(), doc.data["status"].toString(), doc.reference.id)

                            taskList.add(mTaskEntity)
                        }

                        var taskFilterList: List<TaskEntity>

                        when (mTaskFilter) {
                            0 -> taskFilterList = taskList.filter { it -> it.status.equals("false") }
                            else -> taskFilterList = taskList.filter { it -> it.status.equals("true") }
                        }

                        mRecyclerTaskList.adapter = TaskListAdapter(taskFilterList, mListener)

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }
    }
}