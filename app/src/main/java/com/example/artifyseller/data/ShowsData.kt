package com.example.artifyseller.data

data class ShowsData(
    var show_id : List<String> = emptyList(),
    var show_name :  List<String> = emptyList(),
    var ticket_price : List<Int> = emptyList(),
    var no_of_seats : List<Int> = emptyList(),
    var show_description : List<String> = emptyList(),
    var show_image : List<String> = emptyList(),
    var show_venue : List<String> = emptyList(),
    var show_date : List<String> = emptyList(),
    var show_timing : List<String> = emptyList(),
    var show_duration : List<String> = emptyList(),
    var show_language : List<String> = emptyList(),
    var artist_name : List<String> = emptyList(),
    var show_category : List<String> = emptyList(),
    var show_host_id : List<String> = emptyList()
)