package com.example.artifyseller

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.artifyseller.data.User
import com.example.artifyseller.databinding.ActivityUserInformationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class UserInformation : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var binding : ActivityUserInformationBinding
    private  lateinit var imageBitmap : Bitmap
    private var selectedImg: Uri? = null
    private var imageWhenNotChanged: String? = null
    private lateinit var vm : com.example.artifyseller.viewmodel.ViewModel
    private var photoClicked : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        vm = ViewModelProvider(this).get(com.example.artifyseller.viewmodel.ViewModel::class.java)


        vm.retrive_user_data{
            binding.nameText.setText(it.name.toString())
            binding.phoneNumberText.setText(it.phone_number.toString())
            binding.emailText.setText(it.email.toString())
            binding.dateOfBirthText.setText(it.DOB.toString())
            binding.addressText.setText(it.address.toString())
            Picasso.get().load(it.profile_photo).into(binding.profilePhoto)
            imageWhenNotChanged = it.profile_photo
        }

        binding.profilePhoto.setOnClickListener {
            imageOptionDialogue()
        }
        binding.saveButton.setOnClickListener {
            uploadImageToStorage()
            startActivity(Intent(this , MainActivity::class.java))
            finish()
        }
    }
    fun uploadImageToStorage(){
        if (photoClicked == 1){
            auth = FirebaseAuth.getInstance()
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference

            val storagePath = storageRef.child("Photos/${auth.currentUser?.uid}.jpg")
            val uploadTask = storagePath.putBytes(data)

            uploadTask.addOnSuccessListener { it ->
                val task = it.metadata?.reference?.downloadUrl
                task?.addOnSuccessListener {
                    selectedImg = it
                    val user = User(binding.nameText.text.toString() ,
                        binding.phoneNumberText.text.toString().toLong() ,
                        binding.emailText.text.toString() ,
                        binding.dateOfBirthText.text.toString(),
                        binding.addressText.text.toString(),
                        selectedImg.toString())

                    vm.upload_user_data(user)
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                }
                    ?.addOnFailureListener {
                        Toast.makeText(this, "task failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }else if (photoClicked == 0){
            auth = FirebaseAuth.getInstance()
            val user = User(binding.nameText.text.toString() ,
                binding.phoneNumberText.text.toString().toLong() ,
                binding.emailText.text.toString() ,
                binding.dateOfBirthText.text.toString(),
                binding.addressText.text.toString(),
                imageWhenNotChanged.toString())

            vm.upload_user_data(user)
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
        }

    }

    private fun imageOptionDialogue() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog_select_image_options)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<LinearLayout>(R.id.layoutTakePicture).setOnClickListener {
            fromcamera()
            dialog.dismiss()
        }

        dialog.findViewById<LinearLayout>(R.id.layoutSelectFromGallery).setOnClickListener {
            pickFromGallery()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            //See description at declaration
        }

        dialog.show()
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun pickFromGallery() {
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(pickPictureIntent, 2)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun fromcamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, 1)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photoClicked = 1

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> {
                    imageBitmap = data?.extras?.get("data") as Bitmap
                    try {
                        binding.profilePhoto.setImageBitmap(imageBitmap)
                    }catch (e: Exception){}
                }
                2 -> {
                    val imageUri = data?.data
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    try {
                        binding.profilePhoto.setImageBitmap(imageBitmap)
                    }catch (e :Exception){
                    }
                }
            }
        }
    }
}