package com.example.minimaltodolist.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minimaltodolist.R
import com.example.minimaltodolist.databinding.FragmentHomeBinding
import com.example.minimaltodolist.utils.TodoAdapter
import com.example.minimaltodolist.utils.TodoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), AddToDoPopupFragment.DialogNextBtnClickListener,
    TodoAdapter.TodoAdapterClicksInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private lateinit var binding2:TodoAdapter
    private var popupFragment: AddToDoPopupFragment? = null
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var list: MutableList<TodoData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFireBase()
        registerEvents()
    }

    private fun getDataFromFireBase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (taskSnapShot in snapshot.children) {
                    val todoTask = taskSnapShot.key?.let {
                        TodoData(it, taskSnapShot.value.toString())
                    }
                    if (todoTask != null) {
                        list.add(todoTask)
                    }
                }
                todoAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun registerEvents() {
        binding.btnAdd.setOnClickListener {
            if (popupFragment != null)
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
            popupFragment = AddToDoPopupFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(childFragmentManager, AddToDoPopupFragment.TAG)
        }
    }


    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Tasks")
            .child(auth.currentUser?.uid.toString())

        binding.rcvTodo.setHasFixedSize(true)
        binding.rcvTodo.layoutManager = LinearLayoutManager(context)
        list = mutableListOf()
        todoAdapter = TodoAdapter(list)
        todoAdapter.setListener(this)
        binding.rcvTodo.adapter = todoAdapter
    }

    override fun onSaveTask(todo: String, edtTask: TextInputEditText) {
        databaseRef.push().setValue(todo).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "To do task save successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            edtTask.text = null
            popupFragment!!.dismiss()
        }
    }

    override fun onUpdateTask(todoData: TodoData, edtTask: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[todoData.taskId] = todoData.task
        databaseRef.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Update successfully", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            edtTask.text = null
            popupFragment!!.dismiss()
        }
    }

    override fun onBtnRemoveClick(todoData: TodoData) {
        databaseRef.child(todoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "To do task remove successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBtnEditClick(todoData: TodoData) {
        if (popupFragment != null)
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()

        popupFragment = AddToDoPopupFragment.newInstance(todoData.taskId, todoData.task)
        popupFragment!!.setListener(this)
        popupFragment!!.show(childFragmentManager, AddToDoPopupFragment.TAG)

    }

}