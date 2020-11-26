package com.example.chattapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattapp.R
import com.example.chattapp.model.ChatUser

class FriendAdapter(
    private var friendsList: MutableList<ChatUser>,
    private var listener: (ChatUser) -> Unit,
) : RecyclerView.Adapter<FriendAdapter.FriendsViewHolder>() {

    inner class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var oneFriendViewName: TextView = view.findViewById(R.id.username)
        var oneFriendViewImage: ImageView = view.findViewById(R.id.profile_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_search_item_layout, parent, false)
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val friend = friendsList[position]

        holder.apply {
            oneFriendViewName.text = friend.username
            if (friend.profile != ChatUser.profileDefault) {
                showImage(friend.profile.toUri(), oneFriendViewImage)
            } else {
                showImage(ChatUser.profileDefault.toUri(), oneFriendViewImage)
            }
            oneFriendViewImage.setOnClickListener {
                listener(friend)
            }
            oneFriendViewName.setOnClickListener {
                listener(friend)
            }
        }
    }

    override fun getItemCount(): Int = friendsList.size

    fun updateDataList() {
        notifyDataSetChanged()
    }

    private fun showImage(uri: Uri, imageView: ImageView) {
        Glide.with(imageView.context)
            .asBitmap()
            .load(uri)
            .placeholder(R.drawable.ic_profile)
            .into(imageView)
    }
}
