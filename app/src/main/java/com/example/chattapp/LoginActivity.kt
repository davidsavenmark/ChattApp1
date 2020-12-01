package com.example.chattapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar: Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.login_btn).setOnClickListener {
            loginUser()
        }

        textView_forgot_password.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Forgot Password")
            val view = layoutInflater.inflate(R.layout.dialog_forgot_password,null)
            val username = view.findViewById<EditText>(R.id.et_username)
            builder.setView(view)
            builder.setPositiveButton("Reset", DialogInterface.OnClickListener {_,_ ->
                forgotPassword(username)
            })
            builder.setNegativeButton("Close", DialogInterface.OnClickListener {_,_->})
            builder.show()
        }


    }

    private fun forgotPassword(username : EditText){
        if (username.text.toString().isEmpty()){
            return
        }

        mAuth.sendPasswordResetEmail(username.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    (Toast.makeText(this,"Email sent",Toast.LENGTH_SHORT).show())
                }

            }





    }

    private fun loginUser() {

        val email = findViewById<EditText>(R.id.email_login)
        val password = findViewById<EditText>(R.id.password_login)


        if (email.equals("")) {
            Toast.makeText(this@LoginActivity, "Please write email.", Toast.LENGTH_LONG).show()
        } else if (password.equals("")) {
            Toast.makeText(this@LoginActivity, "Please write password.", Toast.LENGTH_LONG).show()
        } else {
            mAuth.signInWithEmailAndPassword(
                email.text.toString().trim(),
                password.text.toString().trim()
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startMainActivity()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error Message: " + task.exception!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}

