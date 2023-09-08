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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.artifyseller.data.ShowsData
import com.example.artifyseller.data.ShowsItemAdd
import com.example.artifyseller.data.UserItemAdd
import com.example.artifyseller.databinding.ActivityShowsAddBinding
import com.example.artifyseller.viewmodel.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.Date

class ShowsAdd : AppCompatActivity() {


//    var item = arrayOf("Comedy", "Poetry", "OpenMic","Chess","Drama","Tutorials","Magic","others")
    var item = arrayOf("Comedy", "Poetry", "Music")
    var autoCompleteTextView: AutoCompleteTextView? = null
    var adapterItems: ArrayAdapter<String>? = null

    private lateinit var binding : ActivityShowsAddBinding
    private lateinit var imageBitmap : Bitmap
    private lateinit var auth : FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var selectedImg: Uri? = null
    private lateinit var vm : ViewModel
    private var photoClicked : Int = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowsAddBinding.inflate(layoutInflater)
        setContentView(binding.root)


        autoCompleteTextView = findViewById(R.id.show_category_auto_comp_txt);
        adapterItems =  ArrayAdapter(  this, R.layout.auto_textview,item);
        autoCompleteTextView!!.setAdapter(adapterItems)


        val backImage : ImageView = findViewById(R.id.toolbarBackImage)


        //karanwork
        backImage.setOnClickListener {
            val intent = Intent(this, ItemFragment::class.java)
            startActivity(intent)
        }

        binding.saveItemButton.setOnClickListener {
            // this function also include the code to store data in fire store
            uploadImageToStorage()


        }

        binding.showImage.setOnClickListener {
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
                        val user_item_data = ShowsItemAdd(

                            "${auth.currentUser?.uid}"+"${Date().time}",
                            binding.showNameTextView.text.toString() ,
                            binding.showTicketPriceTextView.text.toString().toInt() ,
                            binding.showsNoOfSeatsTextView.text.toString().toInt() ,
                            binding.showDescription.text.toString(),
                            selectedImg.toString(),
                            binding.showVenueTextView.text.toString(),
                            binding.showDateTextView.text.toString(),
                            binding.showTimingTextView.text.toString(),
                            binding.showDurationTextView.text.toString(),
                            binding.showLanguageTextView.text.toString(),
                            binding.artistNameTextView.text.toString(),
                            binding.showCategoryAutoCompTxt.text.toString(),
                            "${auth.currentUser?.uid}"


                        )
//                        uploadItem(user_item_data , "Seller_Item", auth.currentUser?.uid.toString())
                        uploadItem(user_item_data , "Shows", user_item_data.show_category!!)
//                        uploadItem(user_item_data , "Category", user_item_data.show_category!!)
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
                val user_item_data = ShowsItemAdd(

                    "${auth.currentUser?.uid}"+"${Date().time}",
                    binding.showNameTextView.text.toString() ,
                    binding.showTicketPriceTextView.text.toString().toInt() ,
                    binding.showsNoOfSeatsTextView.text.toString().toInt() ,
                    binding.showDescription.text.toString(),
                    selectedImg.toString(),
                    binding.showVenueTextView.text.toString(),
                    binding.showDateTextView.text.toString(),
                    binding.showTimingTextView.text.toString(),
                    binding.showDurationTextView.text.toString(),
                    binding.showLanguageTextView.text.toString(),
                    binding.artistNameTextView.text.toString(),
                    binding.showCategoryAutoCompTxt.text.toString(),
                    "${auth.currentUser?.uid}"


                )
//                uploadItem(user_item_data , "Seller_Item", auth.currentUser?.uid.toString())
                uploadItem(user_item_data , "Shows", user_item_data.show_category!!)
//                uploadItem(user_item_data , "Category", user_item_data.show_category!!)
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
                        binding.showImage.setImageBitmap(imageBitmap)
                    }catch (e: Exception){}
                }
                2 -> {
                    val imageUri = data?.data
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                    try {
                        binding.showImage.setImageBitmap(imageBitmap)
                    }catch (e :Exception){
                    }
                }
            }
        }
    }

    private fun uploadItem(item : ShowsItemAdd, collectionName : String, documentName : String) {
//    var timeOfUpload : Long = Utils.getTime()
//    var timeOfCompletion : Long = timeOfUpload + 86400
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val itemAddRef = firestore.collection(collectionName).document(documentName)
        itemAddRef.get().addOnSuccessListener {
            if (it.exists()){
                val showID = it.get("show_id") as List<*>
                val newshowID = showID.toMutableList()
                newshowID.add(item.show_id)

                val showName = it.get("show_name") as List<*>
                val newShowName = showName.toMutableList()
                newShowName.add(item.show_name)

                val ticketPrice = it.get("ticket_price") as List<*>
                val newTicketPrice = ticketPrice.toMutableList()
                newTicketPrice.add(item.ticket_price)

                val no_of_seats = it.get("no_of_seats") as List<*>
                val new_no_of_seats = no_of_seats.toMutableList()
                new_no_of_seats.add(item.no_of_seats)

                val show_description = it.get("show_description") as List<*>
                val new_show_description = show_description.toMutableList()
                new_show_description.add(item.show_description)

                val show_image = it.get("show_image") as List<*>
                val newshow_image = show_image.toMutableList()
                newshow_image.add(item.show_image)

                val show_venue = it.get("show_venue") as List<*>
                val newshow_venue = show_venue.toMutableList()
                newshow_venue.add(item.show_venue)

                val show_date = it.get("show_date") as List<*>
                val newshow_date = show_date.toMutableList()
                newshow_date.add(item.show_date)


                val show_timing = it.get("show_timing") as List<*>
                val newshow_timing = show_timing.toMutableList()
                newshow_timing.add(item.show_timing)

                val show_duration = it.get("show_duration") as List<*>
                val newshow_duration = show_duration.toMutableList()
                newshow_duration.add(item.show_duration)

                val show_language = it.get("show_language") as List<*>
                val newshow_language = show_language.toMutableList()
                newshow_language.add(item.show_language)

                val artist_name = it.get("artist_name") as List<*>
                val newartist_name = artist_name.toMutableList()
                newartist_name.add(item.artist_name)

                val show_category = it.get("show_category") as List<*>
                val newshow_category = show_category.toMutableList()
                newshow_category.add(item.show_category)

                val show_host_id = it.get("show_host_id") as List<*>
                val newshow_host_id = show_host_id.toMutableList()
                newshow_host_id.add(item.show_host_id)

                itemAddRef.update(hashMapOf(
                    "show_id" to newshowID,
                    "show_name" to newShowName,
                    "ticket_price" to newTicketPrice,
                    "no_of_seats" to new_no_of_seats,
                    "show_description" to new_show_description,
                    "show_image" to newshow_image,
                    "show_venue" to newshow_venue,
                    "show_date" to newshow_date,
                    "show_timing" to newshow_timing,
                    "show_duration" to newshow_duration,
                    "show_language" to newshow_language,
                    "artist_name" to newartist_name ,
                    "show_category" to newshow_category,
                    "show_host_id" to newshow_host_id,
                ) as Map<String, Any>)

            }else{
                val itemData = hashMapOf(
                    "show_id" to listOf(item.show_id),
                    "show_name" to listOf(item.show_name),
                    "ticket_price" to listOf(item.ticket_price),
                    "no_of_seats" to listOf(item.no_of_seats),
                    "show_description" to listOf(item.show_description),
                    "show_image" to listOf(item.show_image),
                    "show_venue" to listOf(item.show_venue),
                    "show_date" to listOf(item.show_date),
                    "show_timing" to listOf(item.show_timing),
                    "show_duration" to listOf(item.show_duration),
                    "show_language" to listOf(item.show_language),
                    "artist_name" to listOf(item.artist_name),
                    "show_category" to listOf(item.show_category),
                    "show_host_id" to listOf(item.show_host_id),
                )
                itemAddRef.set(itemData)
            }
        }

    }

    private fun checkForEmpty(): Boolean {






        if(binding.showNameTextView.text.isEmpty()) {
            Toast.makeText(this, "Name field is Empty!", Toast.LENGTH_SHORT).show()
            binding.showNameTextView.requestFocus()
            return false
        }

        if(binding.showTicketPriceTextView.text.isEmpty()) {
            Toast.makeText(this, "Price field is Empty!", Toast.LENGTH_SHORT).show()
            binding.showTicketPriceTextView.requestFocus()
            return false
        }

        if(binding.showCategoryAutoCompTxt.text.isEmpty()) {
            Toast.makeText(this, "Category field is Empty!", Toast.LENGTH_SHORT).show()
            binding.showCategoryAutoCompTxt.requestFocus()
            return false
        }

        if(binding.showsNoOfSeatsTextView.text.isEmpty()) {
            Toast.makeText(this, "Quantity field is Empty!", Toast.LENGTH_SHORT).show()
            binding.showsNoOfSeatsTextView.requestFocus()
            return false
        }
        if(binding.showDescription.text.isEmpty()) {
            Toast.makeText(this, "Description field is Empty!", Toast.LENGTH_SHORT).show()
            binding.showDescription.requestFocus()
            return false
        }

        if(binding.showVenueTextView.text.isEmpty()) {
            Toast.makeText(this, "Venue field is Empty!", Toast.LENGTH_SHORT).show()
            binding.showVenueTextView.requestFocus()
            return false
        }

        if(binding.showDateTextView.text.isEmpty()) {
            Toast.makeText(this, "Date field is Empty!", Toast.LENGTH_SHORT).show()
            binding.showDateTextView.requestFocus()
            return false
        }

        if(binding.showTimingTextView.text.isEmpty()) {
            Toast.makeText(this, "Timing field is Empty!", Toast.LENGTH_SHORT).show()
            binding.showTimingTextView.requestFocus()
            return false
        }
        if(binding.showDurationTextView.text.isEmpty()) {
            Toast.makeText(this, "Duration field is Empty!", Toast.LENGTH_SHORT).show()
            binding.showDurationTextView.requestFocus()
            return false
        }
        if(binding.showLanguageTextView.text.isEmpty()) {
            Toast.makeText(this, "Language field is Empty!", Toast.LENGTH_SHORT).show()
            binding.showLanguageTextView.requestFocus()
            return false
        }
        if(binding.artistNameTextView.text.isEmpty()) {
            Toast.makeText(this, "Artist Name field is Empty!", Toast.LENGTH_SHORT).show()
            binding.artistNameTextView.requestFocus()
            return false
        }

        if(binding.showLanguageTextView.text.isEmpty()) {
            Toast.makeText(this, "Language field is Empty!", Toast.LENGTH_SHORT).show()
            binding.showLanguageTextView.requestFocus()
            return false
        }
        return true
    }
}

//
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//
//class ShowsAdd : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_shows_add)
//    }
//}