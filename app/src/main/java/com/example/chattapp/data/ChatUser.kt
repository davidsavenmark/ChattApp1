package com.example.chattapp.data

class ChatUser{
       /* private var uid: String = ""
        private var username: String = ""
        private var profile: String = "https://firebasestorage.googleapis.com/v0/b/chattapp-666b6.appspot.com/o/profile_image.png?alt=media&token=6e7f78cd-df94-4b29-9304-17cb586e57ef"
        private var cover: String = "https://firebasestorage.googleapis.com/v0/b/chattapp-666b6.appspot.com/o/linear_green_cover.png?alt=media&token=bdf5ffe1-3171-4b09-a3af-3a6e201e23f4"
        private var status: String = "offline"
        private var search: String = ""


        */

        private var uid: String = ""
        private var username: String = ""
        private var profile: String = ""
        private var cover: String = ""
        private var status: String = ""
        private var search: String = ""
        private var facebook: String = ""
        private var instagram: String = ""
        private var website: String = ""

        constructor()
        constructor(
                uid: String,
                username: String,
                profile: String,
                cover: String,
                status: String,
                search: String,
                facebook: String,
                instagram: String,
                website: String
        ) {
                this.uid = uid
                this.username = username
                this.profile = profile
                this.cover = cover
                this.status = status
                this.search = search
                this.facebook = facebook
                this.instagram = instagram
                this.website = website
        }

        fun getUID(): String?{
                return uid
        }

        fun setUID(uid: String?){
                this.uid = uid
        }

}
