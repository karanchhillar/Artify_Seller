package com.example.artifyseller

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ItemAdd : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_add)

        val backImage : ImageView = findViewById(R.id.toolbarBackImage)
        val productImage : ImageView = findViewById(R.id.product_image)

        backImage.setOnClickListener {
            val intent = Intent(this, ItemFragment::class.java)
            startActivity(intent)
        }

        productImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
    }
}