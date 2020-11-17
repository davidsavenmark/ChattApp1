package com.example.chattapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class WelcomeActivity : AppCompatActivity() {

    lateinit var register_welcome_btn:Button
    lateinit var login_welcome_btn:Button
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        register_welcome_btn = findViewById<Button>(R.id.register_welcome_btn)
        login_welcome_btn = findViewById<Button>(R.id.login_welcome_btn)

        register_welcome_btn.setOnClickListener{
            startRegisterActivity()
        }

        login_welcome_btn.setOnClickListener{
            startLoginActivity()
        }
    }
    //encapsulate the function- startWelcomeActivity() to avoid repeating codes
    private fun startRegisterActivity(){
        val intent = Intent(this@WelcomeActivity, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
    //encapsulate the function- startLoginActivity()to avoid
    private fun startLoginActivity(){
        val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    //encapsulate the function- startMainActivity()to avoid
    private fun startMainActivity(){
        val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null){
            startMainActivity()
        }
    }
}