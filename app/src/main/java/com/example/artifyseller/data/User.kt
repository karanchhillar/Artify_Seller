package com.example.artifyseller.data

data class User(
    var name : String ?= null ,
    var phone_number : Long ?= null ,
    var email : String ?= null ,
    var DOB : String ?= null ,
    var address : String ?= null ,
    var profile_photo : String ?= null
)
