package com.example.chattapp.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.chattapp.R
import com.example.chattapp.ModelClasses.ChatUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    //    var usersRefrence: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private lateinit var storage: FirebaseStorage
    private var coverChecker: String? = ""
    private var socialChecker: String? = ""
    private lateinit var mSelected: List<Uri>
    private lateinit var profileImageUri: Uri
    val db = Firebase.firestore
    private lateinit var userRef: CollectionReference

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data)
            profileImageUri = mSelected.first()
            showImage(profileImageUri, profile_image_settings)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callMatisse()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You denied the permission",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        storage = Firebase.storage
        val storageRef = storage.reference
        //fix the show of the username, OBS!!!!Just email there.
        username_settings.text = firebaseUser!!.email.toString()
        userRef = db.collection("users")
        userRef.document(firebaseUser!!.uid).get()
            .addOnSuccessListener { result ->
                val profile = result["profile"] as String
                if (profile != ChatUser.profileDefault) {
                    showImage(profile.toUri(), profile_image_settings)
                }
            }
            .addOnFailureListener { exception ->

            }
        //Add a button "update", if click it,upload the picture you choose in local mobil to Storage, path is /images.
        update_button.setOnClickListener {
            val file: Uri = profileImageUri
            val riversRef = storageRef.child("images/${file.lastPathSegment}")
            val uploadTask = riversRef.putFile(file)

            uploadTask
                .addOnFailureListener {
                    logMaker("Upload failed!($file)")
                }
                .addOnSuccessListener { taskSnapshot ->
                    logMaker("Upload success!($file),${taskSnapshot.uploadSessionUri}")
                }
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    riversRef.downloadUrl
                }
                //If the upload is successful, give downloadUri.
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        logMaker("downloadUri:($downloadUri)")

//Update information of current user in firestore, profile which comes from Storage, pictures uri.OBS!!!downloadUri is not a String.Put downloadUri in the profile field.
                        userRef.document(firebaseUser!!.uid)
                            .update("profile", downloadUri.toString())
                            .addOnSuccessListener {
                                Log.d(
                                    "TAG!!!",
                                    "DocumentSnapshot successfully updated!"
                                )
                            }
                            .addOnFailureListener { exception ->
                                Log.w(
                                    "TAG!!!",
                                    "Error updating document",
                                    exception
                                )
                            }

                    }
                }
        }

//Profile management, when click on the picture, first judge is the app have the permission to visit the local pictures. Request for permission granted.
        profile_image_settings.setOnClickListener {
            //check if you have the permission to get the picture from local mobile, if no, ask for request,if yes, call Matisse.Just the first time to start needs this,after that,no need.
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                activity?.let { it1 ->
                    ActivityCompat.requestPermissions(
                        it1,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                callMatisse()
            }


        }
    }

    //"showImage" function : To load the uri in the position - imageView.
    private fun showImage(uri: Uri, imageView: ImageView) {
        Glide.with(requireContext())
            .asBitmap()
            .load(uri)
            .placeholder(R.drawable.ic_profile)
            .into(imageView)
    }
//Call the function Matisse, a TTP library.
    private fun callMatisse() {
        Matisse.from(this@SettingsFragment)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(1)
            //.addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
            //.gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showPreview(false) // Default is `true`
            .forResult(REQUEST_CODE_CHOOSE)
    }

    companion object {
        const val REQUEST_CODE_CHOOSE = 2020
        const val PERMISSION_REQUEST_CODE = 1
    }

    private fun logMaker(text: String) {
        Log.v("ChatApp", text)
    }
}