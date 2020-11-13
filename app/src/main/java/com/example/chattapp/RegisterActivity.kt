package com.example.chattapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.chattapp.data.ChatUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)//SignOut
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        val register_btn = findViewById<Button>(R.id.register_btn)
        register_btn.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {

        val username = findViewById<EditText>(R.id.username_register)
        val email = findViewById<EditText>(R.id.email_register)
        val password = findViewById<EditText>(R.id.password_register)

        when {
            username.text.isBlank() -> {
                toastMaker("Please write username.")
            }
            email.text.isBlank() -> {
                toastMaker("Please write email.")
            }
            password.text.isBlank() -> {
                toastMaker("Please write password.")
            }
            else -> {
                mAuth.createUserWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUserID = mAuth.currentUser!!.uid

                                val chatUser = ChatUser(firebaseUserID, username.text.toString().trim(), search = username.text.toString().trim())

                                addUserToFirestore(chatUser)


                                /*val userHashMap = HashMap<String, Any>()
                                userHashMap["uid"] = firebaseUserID
                                userHashMap["username"] = username.text.toString().trim()
                                userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/chattapp-666b6.appspot.com/o/profile_image.png?alt=media&token=6e7f78cd-df94-4b29-9304-17cb586e57ef"
                                userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/chattapp-666b6.appspot.com/o/linear_green_cover.png?alt=media&token=bdf5ffe1-3171-4b09-a3af-3a6e201e23f4"
                                userHashMap["status"] = "offline"
                                userHashMap["search"] = username.text.toString().trim() //  username.toLowerCase fungerade ej så jag testade denna
                                //userHashMap["facebook"] = "https://m.facebook.com"
                                //userHashMap["instagram"] = "https://m.instagram.com"
                                //userHashMap["website"] = "https://www.google.com"
*/
/*                                refUsers.updateChildren(userHashMap)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(intent)
                                                finish()
                                            }
                                        }
                            } else {
                                Toast.makeText(this@RegisterActivity, "Error Message: " + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                            }*/


                            }

                        }
            }
        }
    }

    private fun addUserToFirestore(it: ChatUser) {
        val db = Firebase.firestore

        db.collection("users").document(it.uid).set(it)
                .addOnSuccessListener {
                    logMaker("successful to add user to DB")

                }
                .addOnFailureListener {
                    logMaker("failed to add a user.$it")
                }

        startMainActivity()
    }


    private fun startMainActivity() {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun toastMaker(text: String) {
        Toast.makeText(this@RegisterActivity, "$text", Toast.LENGTH_LONG).show()
    }

    private fun logMaker(text: String) {
        Log.v("ChatApp", text)
    }
}





