package com.example.chattapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattapp.R
import com.example.chattapp.model.Users
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter(
    private var userList: MutableList<Users>,
    //listener as a variable
    private val listener: (Users) -> Unit,
    private var checkBoxListener: (Boolean, Users) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTxt: TextView = itemView.findViewById(R.id.username)
        var profileImageView: CircleImageView = itemView.findViewById(R.id.profile_image)
        //var offlineImageView: CircleImageView = itemView.findViewById(R.id.image_offline)
        var checkBox: CheckBox = itemView.findViewById(R.id.member_checkbox)
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
            checkBox.visibility = View.VISIBLE
            checkBox.isChecked = false
            checkBox.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                checkBoxListener(b, user)
            }

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
    fun updateDataList() {
        notifyDataSetChanged()
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