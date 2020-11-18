package com.example.chattapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattapp.R
import com.example.chattapp.model.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter(
    private var userList: MutableList<Users>,
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
                Picasso.get()
                    .load(user.profile)
                    //.placeholder(R.drawable.ic_profile)
                    .into(profileImageView)
            }
            userNameTxt.setOnClickListener {
                listener(user)
            }
            //When you click the username, show a windows to ask you if you want to add a friend.
        }
    }

    //Notify the changed data.
    fun updateDataList(list: MutableList<Users>) {
        userList = list
        notifyDataSetChanged()
    }
}