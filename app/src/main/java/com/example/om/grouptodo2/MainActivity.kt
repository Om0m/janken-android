package com.example.om.grouptodo2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*

import java.util.HashMap

class MainActivity : AppCompatActivity() {
    //DatabaseReference todosRef = FirebaseDatabase.getInstance().getReference("todos");
    internal var TAG = "aaa"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Write a message to the database

        val todosRef = FirebaseDatabase.getInstance().getReference("todos")

        val map = HashMap<String?, Any>()

        val btn1 :Button= findViewById(R.id.bt1)



        //todosRef.child("01").child("title").setValue("はみがき");
        //todosRef.setValue("04");
        //todosRef.child("04").setValue("title");
        //todosRef.push({"01":{"title","おかいもの"}});


        btn1.setOnClickListener{
            val tv1 = findViewById<EditText>(R.id.tv1) as EditText?
            val tv2 = findViewById<EditText>(R.id.tv2) as EditText?
            val str = tv1?.text.toString()
            val bool = tv2?.text.toString()
            val key = todosRef.child("title").push().key
            val todo = Todo(str, bool.toBoolean())
            map[key] = todo.toMap()
            todosRef.updateChildren(map)
            Log.d(TAG, "map is: " + map)
        }

        // Read from the database
        todosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (dataSnapshot in snapshot.children) {
                    val key = dataSnapshot.key
                    val title = dataSnapshot.child("title").value as String?
                    val isDone = dataSnapshot.child("isDone").value as Boolean?
                    Log.d(TAG, "title is: " + title)
                    Log.d(TAG, "isdone is: " + isDone)

                    // このforループで、Todoごとのkey, title, isDoneが取得できているので、
                    // Todoクラスを利用し、Hashmapに追加するなどして保存する。
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    inner class Todo(val title: String, isDone: Boolean?) {
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
            hashmap["title"] = title
            hashmap["isDone"] = this.isDone
            return hashmap
        }
    }

    companion object {
        private val FIREBASE_URL = "https://grouptodo-4234b.firebaseio.com"
    }


}

