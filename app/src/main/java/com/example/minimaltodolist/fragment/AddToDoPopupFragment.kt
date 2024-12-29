package com.example.minimaltodolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.minimaltodolist.R
import com.example.minimaltodolist.databinding.FragmentAddToDoPopupBinding
import com.example.minimaltodolist.utils.TodoData
import com.google.android.material.textfield.TextInputEditText


class AddToDoPopupFragment : DialogFragment() {

    private lateinit var binding: FragmentAddToDoPopupBinding
    private lateinit var listener: DialogNextBtnClickListener
    private var todoData: TodoData? = null

    fun setListener(listener: DialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddToDoPopupFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String) = AddToDoPopupFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddToDoPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            todoData = TodoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString()
            )
            binding.edtTask.setText(todoData?.task)
        }

        registerEvents()
    }

    private fun registerEvents() {
        binding.btnAddTask.setOnClickListener {
            val todoTask = binding.edtTask.text.toString()
            if (todoTask.isNotEmpty()) {
                if (todoData == null) {
                    listener.onSaveTask(todoTask, binding.edtTask)
                } else {
                    todoData?.task = todoTask
                    listener.onUpdateTask(todoData!!, binding.edtTask)

                }
            } else {
                Toast.makeText(context, "Please type some task", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            dismiss()
        }
    }

    interface DialogNextBtnClickListener {
        fun onSaveTask(todo: String, edtTask: TextInputEditText)
        fun onUpdateTask(todoData: TodoData, edtTask: TextInputEditText)
    }

}