package com.example.chattapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar: Toolbar = findViewById(R.id.toolbar_login)
        val loginButton: Button = findViewById(R.id.login_btn)


        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        loginButton.setOnClickListener {

        }

    }
}