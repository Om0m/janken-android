package com.sample.janken

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_enterroom.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class EnterroomActivity : AppCompatActivity() {
    internal var mHandler = Handler()
    var job: Deferred<Unit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enterroom)

        val username = intent.getStringExtra(MainActivity.KEY2)
        tv_username.setText(username)
        var roomname = ed_roomname_enter.text.toString()

        var roomRef = FirebaseDatabase.getInstance().getReference("room/" + roomname + "/owner")       //URLが格納される。つまり文字列
        var roomexists = false
        Log.w("error", "button pressed "+roomRef)

//        btn_enter.setOnClickListener {
//            roomname = ed_roomname_enter.text.toString()
//            roomRef = FirebaseDatabase.getInstance().getReference("room/" + roomname + "/owner")       //URLが格納される。つまり文字列
//            tv_username.setText(roomname)
//            Log.w("error", "button pressed "+roomname+"exists"+roomexists)
//
//            if (roomname.isNotEmpty()) {
//                roomRef.addValueEventListener(object: ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        roomexists = snapshot.exists()
//                        Log.w("exists", "exists= "+roomexists)
//
//                    }
//                    override fun onCancelled(error: DatabaseError) {
//                        // Failed to read value
//                        Log.w("error", "Failed to read value.", error.toException())
//                    }
//                })
//
//
//            }
//        }

        btn_enter.setOnClickListener {
            roomname = ed_roomname_enter.text.toString()
            roomRef = FirebaseDatabase.getInstance().getReference("room/" + roomname + "/owner")       //URLが格納される。つまり文字列
            tv_username.setText(roomname)
            Log.w("error", "button pressed "+roomname+"exists"+roomexists)
            // async関数の戻り（Deferred型）を受け取る
            job = async {
                // myTaskメソッドの呼び出し　非同期処理
                myTask(username,roomname,roomRef)
            }

        }


    }
    private suspend fun myTask(username:String,roomname:String,roomRef: DatabaseReference) {
        // onPreExecuteと同等の処理
        var roomexists = false

        async(UI) {
            Log.w("before exists", "exists= "+roomexists)
            roomRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    roomexists = snapshot.exists()
                    Log.w("exists", "exists= "+roomexists)

                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("error", "Failed to read value.", error.toException())
                }
            })
            Log.w("before exists", "exists= "+roomexists)

        }

        // doInBackgroundメソッドとonProgressUpdateメソッドと
        // 同等の処理
        Thread.sleep(800)

        // onPostExecuteメソッドと同等の処理
        async(UI) {
            if(roomexists) {
                // ダイアログを作成して表示
                AlertDialog.Builder(this@EnterroomActivity).apply {
                    setTitle("お知らせ")
                    setMessage("部屋がみつかりました。入りますか？")
                    setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        // OKをタップしたときの処理
                        Toast.makeText(context, "Dialog OK", Toast.LENGTH_LONG).show()

                        val memberRef = FirebaseDatabase.getInstance().getReference("room/"+roomname+"/member")
                        //メンバーリストに自分を追加
                        memberRef.child(username).setValue(username)

                        // アクティビティに遷移
                        val intent = Intent(this@EnterroomActivity, SelectActivity::class.java)
                        val state = DataState(roomname, username, false)      //DataStateの記述はMakeroomActivityに記述
                        intent.putExtra(KEY_ENTER,state)
                        startActivity(intent)
                    })
                    setNegativeButton("Cancel", null)
                    show()
                }
            }else{
                Toast.makeText(applicationContext, "部屋が見つかりません。", Toast.LENGTH_LONG).show()

            }
        }

    }
    companion object {
        private val FIREBASE_URL = "https://grouptodo-4234b.firebaseio.com"
        const val KEY_ENTER = "com.example.om.grouptodo2-SelectFromEnter"
    }
}
