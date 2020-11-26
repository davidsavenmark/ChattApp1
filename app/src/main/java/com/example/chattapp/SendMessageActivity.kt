package com.example.chattapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattapp.AdapterClasses.MessageAdapter
import com.example.chattapp.ModelClasses.ChatLine
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
import kotlinx.android.synthetic.main.activity_send_message.*

class SendMessageActivity : AppCompatActivity() {

    private lateinit var messageRef: CollectionReference
    private var messageList = mutableListOf<ChatLine>()
    private lateinit var firebaseUserID: String
    private lateinit var friendUid: String
    private lateinit var friendUsername: String



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)
        getParameter()

        username_mchat.text = friendUsername
        //val username = intent?.getStringExtra(CURRENTUSER)
        //prepareTestData()
        initDataBase()
        initListener()
        initRecyclerView()
        realTimeUpdateMessage()
    }
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_message_chat)
    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener { val intent = Intent(this@SendMessageActivity, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()


        }



    }




    override fun onStart() {
        super.onStart()
        getChatListData()
    }

    private fun getParameter() {
        friendUid = intent.getStringExtra("FRIENDUID").toString()
        friendUsername = intent.getStringExtra("FRIENDUSERNAME").toString()
    }

    private fun initDataBase() {

        val db = Firebase.firestore
        val currentUser = FirebaseAuth.getInstance().currentUser
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

    private fun seenMessage(userId: String)
    {
        var seenListener: ValueEventListener? = null

        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                for (dataSnapshot in p0.children)
                {
                    val chat = dataSnapshot.getValue(SendMessageActivity::class.java)

                    if (chat!!.getReceiver().equals(firebaseUserID!!) && chat!!getSender(firebaseUserID!!).equals(userId))
                    {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }

            }



            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })







}





