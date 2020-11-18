package com.example.chattapp.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.chattapp.ModelClasses.Users
import com.example.chattapp.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlin.coroutines.Continuation


/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

    var usersRefrence: DatabaseReference? = null
    var firebaseUser : FirebaseUser ?= null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null



    lateinit var username_settings : TextView
    lateinit var profile_image_settings: ImageView
    lateinit var cover_image_settings: ImageView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)


        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersRefrence = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")


        usersRefrence!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user: Users? = p0.getValue(Users::class.java)

                    if (context!= null){
                        username_settings.text = user!!.getUserName()
                        Picasso.get().load(user.getProfile())//.into(view.profile_image_settings)
                        Picasso.get().load(user.getCover())//.into(view.profile_image_cover)


                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        profile_image_settings.setOnClickListener {
            pickImage()
        }
        cover_image_settings.setOnClickListener {
            pickImage()
        }

        return view
    }

    private fun pickImage() {

        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageUri = data.data
            Toast.makeText(context, "Image uploading", Toast.LENGTH_SHORT).show()
            //uploadImageToDatabase()
        }
    }

    /*private fun uploadImageToDatabase() {
       val progressBar: ProgressDialog(context)
        progressBar.setMessage("Image is uploading, please wait")

        if (imageUri != null){

            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)


            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->

                if (!task.isSuccess){

                    task.exception?.let{
                        throw it
                    }
                }
                return @Continuation fileRef.downloadUrl
                }).ad
        }
    }

     */

}