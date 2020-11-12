package com.example.chattapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private  var firebaseUserID: String = ""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar_register)
       setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        mAuth = FirebaseAuth.getInstance()

        val register_btn = findViewById<Button>(R.id.register_btn)

        register_btn.setOnClickListener{
            registerUser()
        }



    }

    private fun registerUser() {

        val username = findViewById<EditText>(R.id.username_register)
        val email = findViewById<EditText>(R.id.email_register)
        val password = findViewById<EditText>(R.id.password_register)

            if (username.equals(""))

            {
                Toast.makeText(this@RegisterActivity,"Please write username.", Toast.LENGTH_LONG).show()
            }
            else if (email.equals(""))
            {
                Toast.makeText(this@RegisterActivity,"Please write email.", Toast.LENGTH_LONG).show()
            }
             else if (password.equals(""))
            {
                Toast.makeText(this@RegisterActivity,"Please write password.", Toast.LENGTH_LONG).show()
            }
            else
            {
                mAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener{task ->
                    if (task.isSuccessful)

                    {
                    firebaseUserID  = mAuth.currentUser!!.uid
                    refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)
                    val userHashMap = HashMap<String, Any>()
                    userHashMap["uid"] = firebaseUserID
                    userHashMap["username"] = username
                    userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/chattapp-666b6.appspot.com/o/profile_image.png?alt=media&token=6e7f78cd-df94-4b29-9304-17cb586e57ef"
                    userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/chattapp-666b6.appspot.com/o/linear_green_cover.png?alt=media&token=bdf5ffe1-3171-4b09-a3af-3a6e201e23f4"
                    userHashMap["status"] = "offline"
                    userHashMap["search"] =  username
                    //userHashMap["facebook"] = "https://m.facebook.com"
                    //userHashMap["instagram"] = "https://m.instagram.com"
                    //userHashMap["website"] = "https://www.google.com"

                    refUsers.updateChildren(userHashMap)
                        .addOnCompleteListener{task ->
                    if (task.isSuccessful)
                        {
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    }
                    else
                    {
                    Toast.makeText(this@RegisterActivity,"Error Message: " + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }

                    }

            }
        }
    }

}

}





