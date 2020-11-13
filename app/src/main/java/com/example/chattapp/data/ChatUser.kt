package com.example.chattapp.data

data class ChatUser(
        var uid: String = "",
        var username: String = "",
        var profile: String = "https://firebasestorage.googleapis.com/v0/b/chattapp-666b6.appspot.com/o/profile_image.png?alt=media&token=6e7f78cd-df94-4b29-9304-17cb586e57ef",
        var cover: String = "https://firebasestorage.googleapis.com/v0/b/chattapp-666b6.appspot.com/o/linear_green_cover.png?alt=media&token=bdf5ffe1-3171-4b09-a3af-3a6e201e23f4",
        var status: String = "offline",
        var search: String = ""
)