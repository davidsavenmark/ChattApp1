package com.example.chattapp.AdapterClasses

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
import com.example.chattapp.ModelClasses.Users
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter(
    private var userList: MutableList<Users>,
    //listener as a variable
    private val listener: (Users) -> Unit,
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTxt: TextView = itemView.findViewById(R.id.username)
        var profileImageView: CircleImageView = itemView.findViewById(R.id.profile_image)
        var onlineImageView: CircleImageView = itemView.findViewById(R.id.image_online)
        var offlineImageView: CircleImageView = itemView.findViewById(R.id.image_offline)
        var lastMessageTxt: TextView = itemView.findViewById(R.id.message_last)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.user_search_item_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {

        val user: Users = userList[i]

        holder.apply {
            userNameTxt.text = user.username
            if (user.profile != "") {
                showImage(user.profile.toUri(), profileImageView)
            }
            //just to send the listener out to SearchFragment.kt
            userNameTxt.setOnClickListener {
                listener(user)
            }

        }
    }

    //Notify the changes of data.
    fun updateDataList(list: MutableList<Users>) {
        userList = list
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