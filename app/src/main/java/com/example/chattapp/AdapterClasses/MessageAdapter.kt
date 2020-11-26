package com.example.chattapp.AdapterClasses


import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattapp.R
import com.example.chattapp.ModelClasses.ChatLine


class MessageAdapter(
    private var messageList: MutableList<ChatLine>,
    private var currentUid:String
) : RecyclerView.Adapter<MessageAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var oneMessage: TextView = view.findViewById(R.id.chat_line_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.send_message_chat_line_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val messages = messageList[position]

        holder.apply {
            oneMessage.text = messages.text_message

            if(messages.senderUid==currentUid){
                oneMessage.gravity = Gravity.END
            }else{
                oneMessage.gravity =Gravity.START
            }

        }
    }

    override fun getItemCount(): Int = messageList.size

    fun updateDataList() {
        notifyDataSetChanged()
    }
}