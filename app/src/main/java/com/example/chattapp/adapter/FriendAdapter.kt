package com.example.chattapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattapp.R
import com.example.chattapp.model.ChatUser
//Adapter's classical example

class FriendAdapter(//members: a list is a must,listner, it depends on.inheritage from RecyclerView.Adapter<FriendAdapter.FriendsViewHolder>
    private var friendsList: MutableList<ChatUser>,
    private var listener: (ChatUser) -> Unit,
    private var showCheckBoxOrNot:Boolean,

) : RecyclerView.Adapter<FriendAdapter.FriendsViewHolder>() {
//class "FriendsViewHolder" is a must, see up,FriendsViewHolder.
    inner class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var oneFriendViewName: TextView = view.findViewById(R.id.username)
        var oneFriendViewImage: ImageView = view.findViewById(R.id.profile_image_settings)
        var checkBox: CheckBox = view.findViewById(R.id.member_checkbox)
    }
//Three methods to implement.
    /*Using item's layout to show item.*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_search_item_layout, parent, false)
        return FriendsViewHolder(view)
    }
/*Connect an item with an element of the list, username or porfileDefault. If there is listener to send item out, just check the RecyclerView where they use Adapter */
    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val friend = friendsList[position]

        holder.apply {

            checkBox.visibility = View.GONE


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
/*Size of the list*/
    override fun getItemCount(): Int = friendsList.size
/*update list if they have changed something.*/
    fun updateDataList() {
        notifyDataSetChanged()
    }
/*Glide to show image(uri address in Storage) in the right place - imageView. */
    private fun showImage(uri: Uri, imageView: ImageView) {
        Glide.with(imageView.context)
            .asBitmap()
            .load(uri)
            .placeholder(R.drawable.ic_profile)
            .into(imageView)
    }
}
