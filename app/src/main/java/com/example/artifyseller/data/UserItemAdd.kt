package com.example.artifyseller.data

data class UserItemAdd(
    var productName : String ?= null ,
    var price : Int ?= null ,
    var category :  String? = null,
    var product_id : String? = null,
    var seller_id : String?= null,
    var availableQuantity : String ?= null ,
    var productDescription : String ?= null ,
    var productImage : String ?= null,
    var time_upload : String ?= null
)

