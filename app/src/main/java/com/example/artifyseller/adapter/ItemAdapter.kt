package com.example.artifyseller.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.artifyseller.data.ItemRecycler
import com.example.artifyseller.databinding.FragmentItemBinding
import com.example.artifyseller.databinding.ItemLayoutBinding
import com.squareup.picasso.Picasso


class ItemAdapter :     RecyclerView.Adapter<ItemViewHolder>(){

    var itemList = ItemRecycler()
    private lateinit var binding: ItemLayoutBinding

    fun setItemList1(item : ItemRecycler){
        itemList = item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return  ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.price.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val n = itemList.price.size - position -1
        val currentProductName : String = itemList.product_name[n]
        val currentproductImage : String = itemList.product_image[n]
        val currentproductDetails: String = itemList.product_description[n]
        val currentproductQuantity: String = itemList.available_quantity[n]
        val currentproductPrice: Int = itemList.price[n]

        holder.productName.text = currentProductName
        holder.productDetails.text = currentproductDetails
        holder.quantity.text = currentproductQuantity
        holder.price.text = "₹ $currentproductPrice"
        Picasso.get().load(currentproductImage).into(holder.productImage)
    }
}

class ItemViewHolder(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
    val productImage = binding.imgItem
    val productName = binding.prodName
    val productDetails = binding.prodDetails
    val quantity = binding.availableQuantity
    val price = binding.priceText
    val delImage = binding.imgDel
}
