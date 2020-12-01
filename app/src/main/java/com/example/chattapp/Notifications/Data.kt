package com.example.chattapp.Notifications

class Data
{
    private var user: String = ""
    private var icon: String = 0.toString()
    private var body: String = ""
    private var title: String = ""
    private var sented: String = ""

    constructor(){}


    constructor(user: String, icon: String, body: String, title: String, sented: String) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sented = sented
    }

    fun getUser(): String?{
        return user
    }

    fun setUser(user: String?){
        this.user = user.toString()
    }

    fun getIcon(): String {
        return icon
    }

    fun setIcon(icon: Int){
        this.icon = icon.toString()
    }

    fun getBody(): String?{
        return body
    }


    fun setBody(body: String){
        this.body = body
    }

    fun getTitle(): String?{
        return title
    }

    fun setTitle(title: String){
        this.title = title
    }

    fun getSented(): String?{
         return sented
    }


    fun setSented(sented: String){
         this.sented = sented
    }



}