package com.abhash.firebasechat.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.abhash.firebasechat.R
import com.abhash.firebasechat.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        val recyclerview_newmessage: RecyclerView = findViewById(R.id.newMessageRecyclerView)
        //val adapter = GroupAdapter<GroupieViewHolder>()

        //adapter.add(UserItem())
        //adapter.add(UserItem())
        //adapter.add(UserItem())

        //recyclerview_newmessage.adapter = adapter

        fetchUsers()
    }

    private fun fetchUsers(){
        val ref =FirebaseDatabase.getInstance("https://fir-chat-b74bb-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter=GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val user=it.getValue(User::class.java)
                    if(user!=null){
                        adapter.add(UserItem(user))
                    }
                    val recyclerview_newmessage: RecyclerView = findViewById(R.id.newMessageRecyclerView)
                    recyclerview_newmessage.adapter=adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textView_NewMessage.text=user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_new_message)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}
