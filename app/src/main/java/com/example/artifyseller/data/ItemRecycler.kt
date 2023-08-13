package com.example.artifyseller.data

data class ItemRecycler(
    var product_name : List<String> = emptyList(),
    var price : List<Int> = emptyList(),
    var available_quantity : List<String> = emptyList(),
    var product_description : List<String> = emptyList(),
    var product_image : List<String> = emptyList()
)
