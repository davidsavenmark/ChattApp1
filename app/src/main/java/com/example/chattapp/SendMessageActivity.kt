package com.example.chattapp

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chattapp.Fragments.APIService
import com.example.chattapp.Notifications.*
import com.example.chattapp.adapter.MessageAdapter
import com.example.chattapp.fragments.SettingsFragment
import com.example.chattapp.model.ChatLine
import com.example.chattapp.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.activity_send_message.*
import kotlinx.android.synthetic.main.fragment_settings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Ref

class SendMessageActivity : AppCompatActivity() {

    private lateinit var messageRef: CollectionReference
    private var messageList = mutableListOf<ChatLine>()
    private lateinit var mSelected: List<Uri>
    private lateinit var firebaseUserID: String
    private lateinit var friendUid: String
    private lateinit var friendUsername: String
    private lateinit var sharedPictureUri:Uri
    private lateinit var userRef: CollectionReference

    // Prövade lägga in dessa variabler för
    // att få koden längre ner ( rad 199 till 222) att fungera
    private var firebaseUser = FirebaseAuth.getInstance().currentUser
    private val db = Firebase.firestore
    private val message = ""
    private val receiverId = firebaseUser

    var notify = false

    var apiService: APIService? = null






    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SettingsFragment.REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data)
            sharedPictureUri = mSelected.first()
            showImage(sharedPictureUri, profile_image_settings)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)
        getVariables()

        username_mchat.text = friendUsername
        //val username = intent?.getStringExtra(CURRENTUSER)
        //prepareTestData()
        initDataBase()
        initListener()
        initRecyclerView()
        realTimeUpdateMessage()

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
    }

    override fun onStart() {
        super.onStart()
        getChatListData()
    }




//Get two variables from the last fragment.
    private fun getVariables() {
        friendUid = intent.getStringExtra("FRIENDUID").toString()
        friendUsername = intent.getStringExtra("FRIENDUSERNAME").toString()
    }

    private fun initDataBase() {
        firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val path = getMessageDocumentPath(firebaseUserID, friendUid)
        messageRef = db
            .collection("messages").document(path)
            .collection("ChatLine")
    }

    //Create the same list of chat recorder between two persons, give it to the same path in document.
    private fun getMessageDocumentPath(sendUid: String, receiverUid: String): String {
        return if (sendUid > receiverUid) {
            "$receiverUid-$sendUid"
        } else {
            "$sendUid-$receiverUid"
        }
    }

    private fun getChatListData() {
        messageRef.get().addOnSuccessListener { result ->
            messageList.clear()
            for (document in result) {
                val text = document.data["text_message"] as String
                val id = document.data["senderUid"] as String
                messageList.add(ChatLine(text, id))
            }

            if (recycler_view_chats.adapter != null) {
                val temp = recycler_view_chats.adapter as MessageAdapter
                temp.updateDataList()
                Log.d("TAG", "adapter is not null now!!! ohohoh")
                recycler_view_chats.scrollToPosition(temp.itemCount - 1)
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListener() {

        send_message_btn.setOnClickListener {
            notify = true
            val message = text_message.text.toString()
            if (message.isBlank()) {
                return@setOnClickListener
            }

            val x = messageRef.document("ChatLine${System.currentTimeMillis()}")
            //Add the information to Firestore from the "send" button.
            val chatLine = ChatLine(message, firebaseUserID)
            x.set(chatLine, SetOptions.merge())
                .addOnSuccessListener { logMaker("DocumentSnapshot successfully written") }
                .addOnFailureListener { exception -> logMaker("Error writing document, $exception") }

            toastMaker("Successful sending message $message")
            text_message.setText("")
        }
        attact_image_file_btn.setOnClickListener {

            notify = true
            val sharedPicture: Uri = sharedPictureUri
            val storage: FirebaseStorage= Firebase.storage
            val storageRef = storage.reference
            val riversRef = storageRef.child("images/${sharedPicture.lastPathSegment}")
            val uploadTask = riversRef.putFile(sharedPicture)

            uploadTask
                .addOnFailureListener {
                    logMaker("Upload failed!($sharedPicture)")
                }
                .addOnSuccessListener { taskSnapshot ->
                    logMaker("Upload success!($sharedPicture),${taskSnapshot.uploadSessionUri}")
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
                        userRef=db.collection("users")
                        userRef.document(firebaseUserID)
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
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful)
                                {

                                    // implementerar push notifications via fcm ( firebase cloud messaging )
                                    val reference = FirebaseDatabase.getInstance().reference.
                                    child("Users").child(firebaseUser!!.uid)


                                    reference.addValueEventListener(object: ValueEventListener{
                                        override fun onDataChange(p0: DataSnapshot)

                                        {

                                            val user = p0.getValue(Users::class.java)
                                            if (notify)
                                            {

                                                // testade skriva detta istället för user!!.getUserName()

                                                sendNotification(firebaseUserID.toString(), user!!.toString(), "sent you an image.")
                                            }
                                            notify = false

                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })







                                }

                            }





                    }
                }

        }

        // implementerar push notifications via fcm ( firebase cloud messaging )
        val usersReference = FirebaseDatabase.getInstance().reference.
        child("Users").child(firebaseUser!!.uid)


        usersReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)

            {

                val user = p0.getValue(Users::class.java)
                if (notify)
                {

                    // testade skriva detta istället för user!!.getUserName()

                    sendNotification(receiverId.toString(), user!!.toString(), message)
                }
                notify = false

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    private fun sendNotification(receiverId: String?, userName: String?, message: String?)

    {

        val ref = FirebaseDatabase.getInstance().getReference().child("Tokens")

        val query = ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener (object : ValueEventListener{
            override fun onDataChange (p0: DataSnapshot)
            {
                for (dataSnapshot in p0.children)
                {
                    val token: Token? = dataSnapshot.getValue(Token::class.java)

                    val data = Data (firebaseUser!!.uid,
                        R.mipmap.ic_launcher.toString(),
                        "$userName: $message", "New Message",
                        friendUid
                    )

                    val sender = Sender(data!!, token!!.getToken().toString())

                    apiService!!.sendNotification(sender).enqueue(object : Callback<MyResponse>
                    {
                        override fun onResponse(
                            call: Call<MyResponse>,
                            response: Response<MyResponse>
                        )
                        {
                            if(response.code() == 200)
                            {
                                if(response.body()!!.sucess !== 1)
                                {
                                    Toast.makeText(this@SendMessageActivity, "Failed, Nothing happened", Toast.LENGTH_LONG).show()
                                }
                            }

                        }

                        override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                        }

                    })


                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    //"showImage" function : To load the uri in the position - imageView.
    private fun showImage(uri: Uri, imageView: ImageView) {
        Glide.with(this)
            .asBitmap()
            .load(uri)
            .placeholder(R.drawable.ic_profile)
            .into(imageView)
    }
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recycler_view_chats.layoutManager = layoutManager
        val adapter = MessageAdapter(messageList, firebaseUserID)
        recycler_view_chats.adapter = adapter
        recycler_view_chats.scrollToPosition(adapter.itemCount - 1)
    }

    private fun toastMaker(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun logMaker(text: String) {
        Log.d("TAG", text)
    }

    //Update the new sending messages in messageList.If Added, document changes.MODIFIED,REMOVED can we add in the future.
    private fun realTimeUpdateMessage() {
        messageRef.addSnapshotListener { snapshots, e ->
            if (e != null) {
                return@addSnapshotListener
            }
//listen for realtime updates.
            for (dc in snapshots!!.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        val text = dc.document.data["text_message"] as String
                        val id = dc.document.data["senderUid"] as String
                        //get the chat data.
                        messageList.add(ChatLine(text, id))
                        if (recycler_view_chats.adapter != null) {
                            val temp = recycler_view_chats.adapter as MessageAdapter
                            temp.updateDataList()
                            Log.d("TAG", "adapter is not null now!!! ohohoh")
                            recycler_view_chats.scrollToPosition(temp.itemCount - 1)
                        }
                    }
/*For further use of the app, for example modified or remove text messages if you regret.*/
                    DocumentChange.Type.MODIFIED -> Log.d(
                        "TAG",
                        "Modified city: ${dc.document.data}"
                    )
                    DocumentChange.Type.REMOVED -> Log.d("TAG", "Removed city: ${dc.document.data}")
                    else -> Log.d("TAG", "Nothing happened!!")
                }
            }
        }
    }
}