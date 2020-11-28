package com.example.chattapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattapp.R
import com.example.chattapp.SendMessageActivity
import com.example.chattapp.adapter.FriendAdapter
import com.example.chattapp.model.ChatUser
import com.example.chattapp.model.Friend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_friendslist.*

class FriendsListFragment : Fragment() {

    private lateinit var userRef: CollectionReference
    private lateinit var friendRef: CollectionReference
    private lateinit var firebaseUserID: String
    var friendsList = mutableListOf<ChatUser>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_friendslist, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserDataBase()
        initFriendDataBase()
        getFriendListData()
        initFriendRecyclerView()
    }

    private fun initUserDataBase() {
        val db = Firebase.firestore
        userRef = db.collection("users")
        firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
    }

    private fun initFriendDataBase() {
        val db = Firebase.firestore
        friendRef = db.collection("users").document(firebaseUserID).collection("friendsCollection")
    }

    private fun getFriendListData() {
        //Get a temporary frienduidlist, just add Friend(id)
        val tempFriendUidList: MutableList<Friend> = mutableListOf()
        friendRef.get().addOnSuccessListener { result ->
            friendsList.clear()
            for (document in result) {
                val id = document.data["uid"] as String
                tempFriendUidList.add(Friend(id))
            }

            logMaker("tempUidFriendList is:$tempFriendUidList")

            logMaker("3. friendRef addOnSuccessListener finished!-----${friendsList.size}")

            tempFriendUidList.forEach {
                //Get Friend's uid
                val uid = it.uid
                // Look for a user in "users" collection whose uid is Friend's uid.Get its username and profile because we add such a ChatUser(OBS!!different members)in friendsList.
                userRef.document(uid).get().addOnSuccessListener { document ->
                    //get the username of which uid equals to "it.uid"
                    //val tempUid = document["profile"] as String
                    val username = document["username"] as String
                    val profile = document["profile"] as String
                    //set username and profile in an object from Class ChatUser, then add the object into friendsList.
                    friendsList.add(ChatUser(uid = uid, username = username, profile = profile))
                    logMaker("friendsList:${friendsList}")

                    logMaker("4. userRef addOnSuccessListener finished!-----${friendsList.size}")
                    //sort by name
                    friendsList.sortBy { it.username }
                    refreshRecyclerView()
                }
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }

        logMaker("1. getFriendListData finished!-----${friendsList.size}")
    }

    private fun initFriendRecyclerView() {
        val listener: (ChatUser) -> Unit = {
            val intent: Intent = Intent(requireContext(), SendMessageActivity::class.java)
            intent.putExtra("FRIENDUID", it.uid)

            logMaker("it.uid is ${it.uid}")

            intent.putExtra("FRIENDUSERNAME", it.username)
            startActivity(intent)
        }
        val adapter = FriendAdapter(friendsList, listener)
        friends_recyclerView.adapter = adapter
        friends_recyclerView.layoutManager = LinearLayoutManager(requireContext())

        logMaker("2. Create FriendAdapter finished!-----${friendsList.size}")

        val itemDecorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.divider)
            ?.let { itemDecorator.setDrawable(it) }
        friends_recyclerView.addItemDecoration(itemDecorator)
        friends_recyclerView.scrollToPosition(adapter.itemCount - 1)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    private fun refreshRecyclerView() {
        if (friends_recyclerView.adapter != null) {
            val temp = friends_recyclerView.adapter as FriendAdapter
            temp.updateDataList()
            //friends_recyclerView.scrollToPosition(temp.itemCount - 1)
        }
    }

    private fun logMaker(text: String) {
        Log.v("ChatAppKF", text)
    }
}