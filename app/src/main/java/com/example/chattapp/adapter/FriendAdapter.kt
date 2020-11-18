package com.example.chattapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattapp.R
import com.example.chattapp.model.ChatUser

class FriendAdapter(
    private var friendsList: MutableList<ChatUser>,
    private var listener: (ChatUser) -> Unit,
) : RecyclerView.Adapter<FriendAdapter.FriendsViewHolder>() {

    inner class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var onefriend: TextView = view.findViewById(R.id.username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_search_item_layout, parent, false)
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val friend = friendsList[position]

        holder.apply {
            onefriend.text = friend.username
            onefriend.setOnClickListener {
                listener(friend)
            }
        }
    }

    override fun getItemCount(): Int = friendsList.size

    fun updateDataList() {
        notifyDataSetChanged()
    }
}
