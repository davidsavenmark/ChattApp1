package com.example.chattapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattapp.adapter.MessageAdapter
import com.example.chattapp.model.ChatLine
import com.google.firebase.auth.FirebaseAuth
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
        getVariables()

        username_mchat.text = friendUsername
        //val username = intent?.getStringExtra(CURRENTUSER)
        //prepareTestData()
        initDataBase()
        initListener()
        initRecyclerView()
        realTimeUpdateMessage()
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

        val db = Firebase.firestore
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
        attact_image_file_btn.setOnClickListener{

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
}