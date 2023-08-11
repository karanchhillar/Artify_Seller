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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.artifyseller.data.UserItemAdd
import com.example.artifyseller.databinding.ActivityItemAddBinding
import com.example.artifyseller.viewmodel.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.Date

class ItemAdd : AppCompatActivity() {

    private lateinit var binding : ActivityItemAddBinding
    private lateinit var imageBitmap : Bitmap
    private lateinit var auth : FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var selectedImg: Uri? = null
    private lateinit var vm : ViewModel
    private var photoClicked : Int = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backImage : ImageView = findViewById(R.id.toolbarBackImage)

        backImage.setOnClickListener {
            val intent = Intent(this, ItemFragment::class.java)
            startActivity(intent)
        }

        binding.saveItemButton.setOnClickListener {
            // this function also include the code to store data in fire store
            uploadImageToStorage(imageBitmap = null)

            val intent = Intent(this , MainActivity::class.java)
            startActivity(intent)
        }

        binding.productImage.setOnClickListener {
            imageOptionDialogue()
        }
    }
    // code to send image to storage and the getting uri
    fun uploadImageToStorage(imageBitmap: Bitmap?){
        if (photoClicked == 1){
            auth = FirebaseAuth.getInstance()
            vm = ViewModelProvider(this).get(ViewModel::class.java)
            val baos = ByteArrayOutputStream()
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference

            val storagePath = storageRef.child("Photos/${Date().time.toString()}.jpg")
            val uploadTask = storagePath.putBytes(data)

            uploadTask.addOnSuccessListener { it ->
                val task = it.metadata?.reference?.downloadUrl
                task?.addOnSuccessListener {
                    selectedImg = it
                    val user_item_data = UserItemAdd(binding.productNameTextView.text.toString() ,
                        binding.priceTextView.text.toString().toInt() ,
                        binding.availableQuantityTextView.text.toString() ,
                        binding.productDescription.text.toString(),
                        selectedImg.toString())

                    uploadItem(user_item_data)
                    Toast.makeText(this, "New product added", Toast.LENGTH_SHORT).show()
                }
                    ?.addOnFailureListener {
                        Toast.makeText(this, "task failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }else if (photoClicked == 0){
            vm = ViewModelProvider(this).get(ViewModel::class.java)
            val user_item_data = UserItemAdd(binding.productNameTextView.text.toString() ,
                binding.priceTextView.text.toString().toInt() ,
                binding.availableQuantityTextView.text.toString() ,
                binding.productDescription.text.toString(),
                selectedImg.toString())

            uploadItem(user_item_data)
            Toast.makeText(this, "New product added", Toast.LENGTH_SHORT).show()
        }


    }

    // code for choosing btw camera and media
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

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> {
                    imageBitmap = data?.extras?.get("data") as Bitmap
                    try {
                        binding.productImage.setImageBitmap(imageBitmap)
                    }catch (e: Exception){}
                }
                2 -> {
                    val imageUri = data?.data
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    try {
                        binding.productImage.setImageBitmap(imageBitmap)
                    }catch (e :Exception){
                    }
                }
            }
        }
    }

private fun uploadItem(item : UserItemAdd) {
//    var timeOfUpload : Long = Utils.getTime()
//    var timeOfCompletion : Long = timeOfUpload + 86400
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val itemAddRef = firestore.collection("item_add").document(auth.currentUser?.uid.toString())
    itemAddRef.get().addOnSuccessListener {
        if (it.exists()){
            val productName = it.get("product_name") as List<*>
            val newProduct = productName.toMutableList()
            newProduct.add(item.productName)

            val price = it.get("price") as List<*>
            val newPrice = price.toMutableList()
            newPrice.add(item.price)

            val availableQuantity = it.get("available_quantity") as List<*>
            val newQuantity = availableQuantity.toMutableList()
            newQuantity.add(item.availableQuantity)

            val productDescription = it.get("product_description") as List<*>
            val newProductDescription = productDescription.toMutableList()
            newProductDescription.add(item.productDescription)

            val productImage = it.get("product_image") as List<*>
            val newProductImage = productImage.toMutableList()
            newProductImage.add(item.productImage)

            itemAddRef.update(hashMapOf("product_name" to newProduct,"price" to newPrice,
                "available_quantity" to newQuantity , "product_description" to newProductDescription
                ,"product_image" to newProductImage) as Map<String, Any>)

        }else{
            val itemData = hashMapOf("product_name" to listOf(item.productName)
                ,"price" to listOf(item.price)
                ,"available_quantity" to listOf(item.availableQuantity)
                , "product_description" to listOf(item.productDescription)
                ,"product_image" to listOf(item.productImage))
            itemAddRef.set(itemData)
        }
    }

}
}