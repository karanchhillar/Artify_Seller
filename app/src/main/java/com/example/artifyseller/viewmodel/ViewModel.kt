package com.example.artifyseller.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artifyseller.data.User
import com.example.artifyseller.data.UserItemAdd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    fun upload_user_data(user : User) = viewModelScope.launch(Dispatchers.IO){
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("user").document(auth.currentUser?.uid.toString()).set(user)
    }

    fun retrive_user_data(callback: (User) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        firestore.collection("user").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val value = documentSnapshot.toObject(User::class.java)
                callback(value!!)
            }
        }
    }

    fun upload_item_data(user_item_data : UserItemAdd) = viewModelScope.launch ( Dispatchers.IO ){
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("item_data").document(auth.currentUser?.uid.toString()).set(user_item_data)
    }

    fun retrive_item_data(callback: (UserItemAdd) -> Unit){
        val firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        firestore.collection("item_data").document(auth.currentUser?.uid.toString()).get()
            .addOnSuccessListener{task->
                if (task.exists()){
                    val value = task.toObject(UserItemAdd::class.java)
                    callback(value!!)
                }
            }
    }
}
