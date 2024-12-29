package com.example.minimaltodolist.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.minimaltodolist.R
import com.example.minimaltodolist.databinding.EachToDoTaskBinding

class TodoAdapter(private val list: MutableList<TodoData>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

        private var listener: TodoAdapterClicksInterface? = null
    fun setListener(listener: TodoAdapterClicksInterface) {
        this.listener = listener
    }

    class TodoViewHolder(val binding: EachToDoTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding =
            EachToDoTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.todoTask.text = this.task

                binding.btnRemove.setOnClickListener{
                    listener?.onBtnRemoveClick(this)
                }

                binding.btnEdit.setOnClickListener{
                    listener?.onBtnEditClick(this)

                }
            }
        }
    }

    interface TodoAdapterClicksInterface {
        fun onBtnRemoveClick(todoData: TodoData)
        fun onBtnEditClick(todoData: TodoData)
    }
}