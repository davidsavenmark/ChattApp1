package com.example.chattapp.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattapp.R
import com.example.chattapp.adapter.UserAdapter
import com.example.chattapp.model.Friend
import com.example.chattapp.model.GroupName
import com.example.chattapp.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_friendslist.*
import kotlinx.android.synthetic.main.fragment_search.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var userList = mutableListOf<Users>()
    private var recyclerView: RecyclerView? = null
    private var searchEditText: EditText? = null
    private lateinit var userRef: CollectionReference
    private var userSearchResultList = mutableListOf<Users>()
    private lateinit var firebaseUserID: String
    private var tempGroup: MutableList<String> = mutableListOf<String>()
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)


        searchEditText = view.findViewById(R.id.searchUsersET)
        initUserDataBase()
        retrieveAllUsers()

        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(cs.toString().trim())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVisiblity()
        create_group.setOnClickListener {
            if (tempGroup.isEmpty()) {
                return@setOnClickListener
            }
            val groupData = mutableMapOf<String, String>()
            var groupName = ""//"GroupId-${System.currentTimeMillis()}"
            tempGroup.add(firebaseUserID)
            tempGroup.forEach { uid ->
                groupName += uid
            }
            groupName.toCharArray().sort()
            logMaker("GroupCheckkk:$groupName")
            tempGroup.forEachIndexed { index, uid ->
                groupData["memberUid-$index"] = uid
                addGroupNameToUserRef(uid, groupName)
            }

            db.collection("groups").document(groupName).set(groupData)
                .addOnSuccessListener {
                    logMaker("DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { exception ->
                    logMaker("Error updating document:${exception}")
                }


            create_group.visibility = GONE
            tempGroup.clear()
            userAdapter?.updateDataList()
        }
    }

    private fun addGroupNameToUserRef(uid: String, groupName: String) {
        val tempGroupName = GroupName(groupName)
        val it = userRef.document(uid).collection("groupCollection").document(groupName)
        it.set(tempGroupName)
            .addOnSuccessListener { logMaker("DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> logMaker("Error writing document $e") }
    }


    //First make the "create group" button Gone
    private fun initVisiblity() {
        create_group.visibility = GONE
    }

    /*Function initUserDataBase() to initial the users' information in the database,read in information from Authentication to Firestore*/
    private fun initUserDataBase() {
        val db = Firebase.firestore
        userRef = db.collection("users")
        firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
    }

    /*Function retrieveAllUsers() to get the userlist*/
    private fun retrieveAllUsers() {

        userRef.get().addOnSuccessListener { result ->
            userList.clear()
            for (document in result) {
                /*get the item's username*/
                val it = document.data["username"] as String
                /*get the item's uid as id*/
                val id = document.data["uid"] as String
                /*get the item's imageView as profile*/
                val profile = document.data["profile"] as String
                /*excluding the currentUser, do not add the current user's username on the list*/
                if (firebaseUserID != id) {
                    //Add User's two fields: username and uid
                    userList.add(Users(username = it, uid = id, profile = profile))
                    logMaker("UserName: $it")
                }

            }
            /*Sort the name list by alphabet!*/
            userList.sortBy { it.username }

            if (recyclerView!!.adapter != null) {
                val temp = recyclerView!!.adapter as UserAdapter
                temp.updateDataList(userList)
                val itemDecorator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                ContextCompat.getDrawable(requireContext(), R.drawable.divider)
                    ?.let { itemDecorator.setDrawable(it) }
                recyclerView!!.addItemDecoration(itemDecorator)
                recyclerView!!.scrollToPosition(temp.itemCount - 1)
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }

        userAdapter = UserAdapter(userList, itemUserListener, checkBoxListener)
        recyclerView!!.adapter = userAdapter


    }


    /*Search the users by the name!*/
    private fun searchForUsers(input: String) {
        if (input == "") {
            //"updateDataList" is a method in class - UserAdapter
            userAdapter?.updateDataList(userList)
            return
        }
        userSearchResultList.clear()
        userList.forEach {
            // ignore uper or lower
            if (it.username.contains(input, true)) {
                userSearchResultList.add(it)
            }
        }
        if (userSearchResultList.isEmpty()) {
            return
        }
        userAdapter?.updateDataList(userSearchResultList)
    }
    /*!!!!!!!Add a friend without his(her)permission!!!!!! If you press "OK", add him(her) directly. To be continued!*/


    //The variable "itemUserListener" is a parameter of UserAdapter
    private var itemUserListener: (Users) -> Unit = {
        //Toast.makeText(itemView.context, "Username is ${user.username}", Toast.LENGTH_SHORT).show()
        AlertDialog.Builder(context).apply {
            setTitle("Add a friend?")
            setMessage("Do you want to add ${it.username} as a friend?")
            setCancelable(false)
            setPositiveButton("OK") { _: DialogInterface, _: Int ->
                /*Add the friend to the current user's friendsCollection.*/
                val currentUsersFriendsCollection =
                    userRef.document(firebaseUserID)
                        .collection("friendsCollection")
                        .document(it.uid)
                val newFriend = Friend(uid = it.uid)
                currentUsersFriendsCollection.set(newFriend).addOnSuccessListener {
                    logMaker("successful to add user to DB")
                }
                    .addOnFailureListener {
                        logMaker("failed to add a user.$it")
                    }
            }
            setNegativeButton("No") { _: DialogInterface, _: Int ->
                //nothing to do
            }
            show()
        }
    }
    private var checkBoxListener: (Boolean, Users) -> Unit = { b: Boolean, user: Users ->
        if (b) {
            tempGroup.add(user.uid)
        } else {
            tempGroup.remove(user.uid)
        }
        if (tempGroup.isEmpty()) {
            create_group.visibility = View.GONE
        } else {
            create_group.visibility = View.VISIBLE
        }
        logMaker("tempGroup is $tempGroup")

    }
    private fun logMaker(text: String) {
        Log.d("ChatApp", text)
    }

    //To do: If the person has already been your friend, do not show him(her) in the list.To be continued!


}