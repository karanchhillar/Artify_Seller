package com.example.artifyseller.data

data class UserItemAdd(
    var productName : String ?= null ,
    var price : Int ?= null ,
    var availableQuantity : String ?= null ,
    var productDescription : String ?= null ,
    var productImage : String ?= null
)

