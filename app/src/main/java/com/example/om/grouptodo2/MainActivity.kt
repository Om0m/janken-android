package com.example.om.grouptodo2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

import java.util.HashMap

class MainActivity : AppCompatActivity() {
    //DatabaseReference todosRef = FirebaseDatabase.getInstance().getReference("todos");
    internal var TAG = "aaa"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Write a message to the database

        var todosRef = FirebaseDatabase.getInstance().getReference("todos")

        val map = HashMap<String?, Any>()

        val btnMake :Button= findViewById(R.id.btmake)
        val btnEnter :Button= findViewById(R.id.btenter)


        //todosRef.child("01").child("title").setValue("はみがき");
        //todosRef.setValue("04");
        //todosRef.child("04").setValue("title");
        //todosRef.push({"01",{"title","おかいもの"}});


        btnMake.setOnClickListener{
            Log.d(TAG, "btnmake pressed")
            val intent = Intent(this,MakeroomActivity::class.java)
            intent.putExtra(KEY,editname.text.toString())
            startActivity(intent)
        }

        btnEnter.setOnClickListener{
            val intent = Intent(this,EnterroomActivity::class.java)
            intent.putExtra(KEY2,editname.text.toString())
            startActivity(intent)
        }

//        // Read from the database
//        todosRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//
//                for (dataSnapshot in snapshot.children) {
//                    val key = dataSnapshot.key
//                    val timestamp = dataSnapshot.child("timestamp").value as Long?
//                    val title = dataSnapshot.child("title").value as String?
//                    val isDone = dataSnapshot.child("isDone").value as Boolean?
//                    val tv3 = findViewById<TextView>(R.id.tv3)
//                    val tv4 = findViewById<TextView>(R.id.tv4)
//                    tv3.setText(title)
//                    tv4.setText(isDone.toString())
//                    Log.d(TAG, "title is: " + title)
//                    Log.d(TAG, "isdone is: " + isDone)
//                    val todo = Todo(timestamp,title, isDone)
//                    map[key] = todo.toMap()
//                    Log.d(TAG, "map is: " + map)
//                    // このforループで、Todoごとのkey, title, isDoneが取得できているので、
//                    // Todoクラスを利用し、Hashmapに追加するなどして保存する。
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//
//
//        })
    }

    inner class Todo(val timestamp: Long?,val title: String?, isDone: Boolean?) {
        var isDone: Boolean? = null
            private set

        init {
            this.isDone = isDone
        }

        fun setDone() {
            this.isDone = true
        }

        @Exclude
        fun toMap(): Map<String, Any?> {
            val hashmap = HashMap<String, Any?>()
            hashmap["timestamp"] = timestamp
            hashmap["title"] = title
            hashmap["isDone"] = this.isDone
            return hashmap
        }
    }

    companion object {
        private val FIREBASE_URL = "https://grouptodo-4234b.firebaseio.com"
        const val KEY = "com.example.om.grouptodo2-toMakeroomActivity"
        const val KEY2 = "com.example.om.grouptodo2-toEnterroomActivity"
    }


}

