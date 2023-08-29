package com.example.artifyseller.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import com.example.artifyseller.data.ItemRecycler
import com.example.artifyseller.data.User
import com.example.artifyseller.data.UserItemAdd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URI
import java.util.Date

class ViewModel : ViewModel() {
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    val name_text = MutableLiveData<String?>()
    val phone_number = MutableLiveData<Long?>()
    val email_text = MutableLiveData<String?>()
    val dob = MutableLiveData<String?>()
    val address = MutableLiveData<String?>()
    val profile_photo = MutableLiveData<String?>()


    init {
        get_current_user()
        retrive_item_data()
    }

    private fun get_current_user() = viewModelScope.launch (Dispatchers.IO){
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("user").document(auth.currentUser?.uid.toString())
            .addSnapshotListener{value,error->
                if (error!=null){
                    return@addSnapshotListener
                }
                if (value!!.exists() && value!=null){
                    val userInfo = value.toObject(User::class.java)
                    name_text.value = userInfo?.name
                    phone_number.value = userInfo?.phone_number
                    email_text.value = userInfo?.email
                    dob.value = userInfo?.DOB
                    address.value = userInfo?.address
                    profile_photo.value = userInfo?.profile_photo
                }
            }
    }

    fun upload_user_data(user : User){
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

//    fun retrive_item_data(callback: (ItemRecycler) -> Unit){
//        val firestore = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//        Log.d("item_data" , "task fcfvdasfffffffffffffffff")
//
//        firestore.collection("item_data").document(auth.currentUser?.uid.toString())
//            .addSnapshotListener{value , error->
//
//                if (error != null){
//                    return@addSnapshotListener
//                }
//                value?.documen
//
////                if (task.exists()){
////                    Log.d("task" , "task Exists")
////                    val value = task.toObject(ItemRecycler::class.java)
////                    if (value != null) {
////                        Log.d("hello" , value.product_description.toString())
////                    }
////                    callback(value!!)
////
////                }
//            }
//    }
    val myItem = MutableLiveData<ItemRecycler>()
    fun retrive_item_data() =viewModelScope.launch (Dispatchers.IO){
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        try {
            firestore.collection("Seller_Item").document(auth.currentUser?.uid.toString())
                .addSnapshotListener{value , error->

                    if (error != null){
                        return@addSnapshotListener
                    }
                    if (value!!.exists()) {
                        val itemData = value.toObject(ItemRecycler::class.java)
                        myItem.value = itemData!!
                    }
                }
        }catch (_: Exception){}
    }
}
