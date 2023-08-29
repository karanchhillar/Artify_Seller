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
import android.util.Patterns
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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


    var item = arrayOf("Mens", "Womens", "Home Decor","Pottery","Painting","Toys","Bags","Others")
    var autoCompleteTextView: AutoCompleteTextView? = null
    var adapterItems: ArrayAdapter<String>? = null

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


        autoCompleteTextView = findViewById(R.id.auto_comp_txt);
        adapterItems =  ArrayAdapter(  this, R.layout.auto_textview,item);
        autoCompleteTextView!!.setAdapter(adapterItems)


        val backImage : ImageView = findViewById(R.id.toolbarBackImage)

        backImage.setOnClickListener {
            val intent = Intent(this, ItemFragment::class.java)
            startActivity(intent)
        }

        binding.saveItemButton.setOnClickListener {
            // this function also include the code to store data in fire store
            uploadImageToStorage()


        }

        binding.productImage.setOnClickListener {
            imageOptionDialogue()
        }
    }
    // code to send image to storage and the getting uri
    fun uploadImageToStorage(){
        if (photoClicked == 1){
            auth = FirebaseAuth.getInstance()
            vm = ViewModelProvider(this).get(ViewModel::class.java)
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
            val data = baos.toByteArray()

            storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference

            val storagePath = storageRef.child("Photos/${Date().time.toString()}.jpg")
            val uploadTask = storagePath.putBytes(data)

            uploadTask.addOnSuccessListener { it ->
                val task = it.metadata?.reference?.downloadUrl
                task?.addOnSuccessListener {
                    selectedImg = it
                    if (checkForEmpty()){
                        val user_item_data = UserItemAdd(
                            binding.productNameTextView.text.toString() ,
                            binding.priceTextView.text.toString().toInt() ,
                            binding.autoCompTxt.text.toString(),
                            "${auth.currentUser?.uid}"+"${Date().time}",
                            "${auth.currentUser?.uid}",
                            binding.availableQuantityTextView.text.toString() ,
                            binding.productDescription.text.toString(),
                            selectedImg.toString(),
                            "${Date().time}"
                        )
                        uploadItem(user_item_data , "Seller_Item", auth.currentUser?.uid.toString())
                        uploadItem(user_item_data , "All_Products", "All_Products")
                        uploadItem(user_item_data , "Category", user_item_data.category!!)
                        Toast.makeText(this, "New product added", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this , MainActivity::class.java)
                        startActivity(intent)
                    }



                }
                    ?.addOnFailureListener {
                        Toast.makeText(this, "task failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }else if (photoClicked == 0){
            vm = ViewModelProvider(this).get(ViewModel::class.java)
            auth = FirebaseAuth.getInstance()
            if (checkForEmpty()){
                val user_item_data = UserItemAdd(
                    binding.productNameTextView.text.toString() ,
                    binding.priceTextView.text.toString().toInt() ,
                    binding.autoCompTxt.text.toString(),
                    "${auth.currentUser?.uid}"+"${Date().time}",
                    "${auth.currentUser?.uid}",
                    binding.availableQuantityTextView.text.toString() ,
                    binding.productDescription.text.toString(),
                    "https://firebasestorage.googleapis.com/v0/b/artify-53e51.appspot.com/o/Photos%2F1692640012779.jpg?alt=media&token=cdd81bc9-c688-4055-83b3-9b869f3dfe5c",
                    "${Date().time}"
                )
                uploadItem(user_item_data , "Seller_Item", auth.currentUser?.uid.toString())
                uploadItem(user_item_data , "All_Products", "All_Products")
                uploadItem(user_item_data , "Category", user_item_data.category!!)
                Toast.makeText(this, "New product added", Toast.LENGTH_SHORT).show()
                val intent = Intent(this , MainActivity::class.java)
                startActivity(intent)
            }

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
        photoClicked = 1

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

private fun uploadItem(item : UserItemAdd , collectionName : String ,documentName : String) {
//    var timeOfUpload : Long = Utils.getTime()
//    var timeOfCompletion : Long = timeOfUpload + 86400
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val itemAddRef = firestore.collection(collectionName).document(documentName)
    itemAddRef.get().addOnSuccessListener {
        if (it.exists()){
            val productName = it.get("product_name") as List<*>
            val newProduct = productName.toMutableList()
            newProduct.add(item.productName)

            val price = it.get("price") as List<*>
            val newPrice = price.toMutableList()
            newPrice.add(item.price)

            val category = it.get("category") as List<*>
            val newCategory = category.toMutableList()
            newCategory.add(item.category)

            val productId = it.get("product_id") as List<*>
            val newProductId = productId.toMutableList()
            newProductId.add(item.product_id)

            val sellerID = it.get("seller_id") as List<*>
            val newSellerID = sellerID.toMutableList()
            newSellerID.add(item.seller_id)


            val availableQuantity = it.get("available_quantity") as List<*>
            val newQuantity = availableQuantity.toMutableList()
            newQuantity.add(item.availableQuantity)

            val productDescription = it.get("product_description") as List<*>
            val newProductDescription = productDescription.toMutableList()
            newProductDescription.add(item.productDescription)

            val productImage = it.get("product_image") as List<*>
            val newProductImage = productImage.toMutableList()
            newProductImage.add(item.productImage)

            val timeUpload = it.get("time_upload") as List<*>
            val newTimeUpload = timeUpload.toMutableList()
            newTimeUpload.add(item.time_upload)

            itemAddRef.update(hashMapOf("product_name" to newProduct,"price" to newPrice,
                "category" to newCategory,
                "product_id" to newProductId,
                "seller_id" to newSellerID,
                "available_quantity" to newQuantity , "product_description" to newProductDescription
                ,"product_image" to newProductImage,
                "time_upload" to newTimeUpload

                ) as Map<String, Any>)

        }else{
            val itemData = hashMapOf("product_name" to listOf(item.productName)
                ,"price" to listOf(item.price),
                "category" to listOf(item.category),
                "product_id" to listOf(item.product_id),
                "seller_id" to listOf(item.seller_id),
                "available_quantity" to listOf(item.availableQuantity)
                , "product_description" to listOf(item.productDescription)
                ,"product_image" to listOf(item.productImage),
                "time_upload" to listOf(item.time_upload)
            )
            itemAddRef.set(itemData)
        }
    }

}

    private fun checkForEmpty(): Boolean {



        if(binding.productNameTextView.text.isEmpty()) {
            Toast.makeText(this@ItemAdd, "Name field is Empty!", Toast.LENGTH_SHORT).show()
            binding.productNameTextView.requestFocus()
            return false
        }

        if(binding.priceTextView.text.isEmpty()) {
            Toast.makeText(this@ItemAdd, "Price field is Empty!", Toast.LENGTH_SHORT).show()
            binding.priceTextView.requestFocus()
            return false
        }

        if(binding.autoCompTxt.text.isEmpty()) {
            Toast.makeText(this@ItemAdd, "Category field is Empty!", Toast.LENGTH_SHORT).show()
            binding.autoCompTxt.requestFocus()
            return false
        }

        if(binding.availableQuantityTextView.text.isEmpty()) {
            Toast.makeText(this@ItemAdd, "Quantity field is Empty!", Toast.LENGTH_SHORT).show()
            binding.availableQuantityTextView.requestFocus()
            return false
        }
        if(binding.productDescription.text.isEmpty()) {
            Toast.makeText(this@ItemAdd, "Description field is Empty!", Toast.LENGTH_SHORT).show()
            binding.productDescription.requestFocus()
            return false
        }

        return true
    }
}