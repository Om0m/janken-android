package com.sample.janken

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_make.*
import java.io.Serializable
import java.util.HashMap

class MakeroomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make)

        val ownername = intent.getStringExtra(MainActivity.KEY)
        val owner = findViewById<TextView>(R.id.owner_name) as TextView
        owner.text = ownername
        val map = HashMap<String?, Any>()


        btn_make.setOnClickListener{

            val numroom = ed_numroom.text
            val roomname = ed_roomname_make.text.toString()
            if(numroom.isNotEmpty() || roomname.isNotEmpty()){      //部屋名と部屋の数がどちらとも入力されたとき
                val roomRef= FirebaseDatabase.getInstance().getReference("room/"+roomname)
                val memberRef = FirebaseDatabase.getInstance().getReference("room/"+roomname+"/member")
                //部屋作成前に一旦クリア
                roomRef.setValue("hand")

                Log.d("a", "numroom is: " + numroom +"nameroom is:"+roomname)
                //roomRef.setValue(roomname.toString())


                // owner情報をデータベースに追加
                val key = roomRef.child("owner").key
                val roomstatus = RoomStatus(numroom.toString().toInt(),roomname,ownername)
                map[key] = roomstatus.toMap()
                roomRef.updateChildren(map)

                //メンバーリストに自分を追加
                memberRef.child(ownername).setValue(ownername)

                //部屋を作成して手を選択するアクティビティに遷移
                val intent = Intent(this, SelectActivity::class.java)
                val state = DataState(roomname, ownername, true)
                intent.putExtra(KEY,state)
                startActivity(intent)

            }else{
                Log.d("a", "kara")

            }

        }

    }
    inner class RoomStatus(val numroom: Int,val roomname: String, val ownername: String) {
        @Exclude
        fun toMap(): Map<String, Any?> {
            val hashmap = HashMap<String, Any?>()
            hashmap["count"] = numroom
            hashmap["name"] = ownername
            hashmap["isStart"] = false
            return hashmap
        }
    }

    companion object {
        private val FIREBASE_URL = "https://grouptodo-4234b.firebaseio.com"
        const val KEY = "com.example.om.grouptodo2-toSelectActivity"
    }
}
data class DataState(
        val roomname: String,
        val name: String,
        val isOwner :Boolean
): Serializable
