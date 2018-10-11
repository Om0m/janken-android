package com.example.om.grouptodo2

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_select.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class SelectActivity : AppCompatActivity() {
    var job: Deferred<Unit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        //このアクティビティはMakeroomとEnterroomの2つから遷移するため、stateの参照場所を変える
        var state = intent.getSerializableExtra(MakeroomActivity.KEY)
        if(state==null) {
            state = intent.getSerializableExtra(EnterroomActivity.KEY_ENTER)
            if(state==null)state = intent.getSerializableExtra(ResultActivity.KEY_RESULT
            )

        }

        val items = Array(20, { i -> "Title-$i" })
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)
        var hand = ""
        var count = 0

        var name =""
        var roomname =""
        var isOwner = false

        if(state is DataState){
            name = state.name
            roomname = state.roomname
            isOwner = state.isOwner
        }

        userList.adapter = adapter      //リストビューの初期表示
        Log.w("state", "name = " + name + "roomname = " +roomname + "isOwner="+isOwner )

        val memberRef = FirebaseDatabase.getInstance().getReference("room/"+roomname+"/member")
        val roomCountRef = FirebaseDatabase.getInstance().getReference("room/"+roomname+"/owner/count")
        val handCountRef = FirebaseDatabase.getInstance().getReference("room/"+roomname+"/hand")
        radio_group.setOnCheckedChangeListener{_,handId : Int ->
            when (handId){
                R.id.radio_Rock -> hand="rock"
                R.id.radio_Scissors -> hand="scissors"
                R.id.radio_Paper -> hand="paper"
            }
        }
        bt_Confirm.setOnClickListener{
            if(hand.isNotEmpty()){
                if(state is DataState){         //stateにアクセスするために必要
                    val roomRef= FirebaseDatabase.getInstance().getReference("room/"+state.roomname+"/hand/"+state.name)
                    roomRef.setValue(hand)
                }

            }else{
                Toast.makeText(applicationContext, "手を選択してください", Toast.LENGTH_LONG).show()

            }
        }
        memberRef.addValueEventListener(object: ValueEventListener {     //部屋のメンバーの変更を検知
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value
                val childnum = dataSnapshot.childrenCount.toInt()
                var memberArray: ArrayList<String> = arrayListOf("参加メンバー")
                for (snapshot in dataSnapshot.children){
                    memberArray.add(snapshot.value.toString())

                }
                val memberadapter = ArrayAdapter<String>(this@SelectActivity, android.R.layout.simple_list_item_1, memberArray)
                userList.adapter = memberadapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("onCancelled", "error:", error.toException())
            }
        })
        roomCountRef.addValueEventListener(object: ValueEventListener {     //部屋の人数を取得する
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value
                try {
                    count = value.toString().toInt()
                } catch (e: Exception){
                    //
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("onCancelled", "error:", error.toException())
            }
        })

        //        // 参加者すべてが手を出したらisStartをtrueにする  ownerのみ
        if(isOwner) {
            handCountRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    if (snapshot.childrenCount.toInt() == count) {
                        val isStartRef = FirebaseDatabase.getInstance().getReference("room/"+roomname+"/owner/isStart")
                        // ダイアログを作成して表示
                        AlertDialog.Builder(this@SelectActivity).apply {
                            setTitle("お知らせ")
                            setMessage("みんな手を出しました！結果を見に行きますか？")
                            setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                                // OKをタップしたときの処理
                                // async関数の戻り（Deferred型）を受け取る
                                job = async {
                                    // myTaskメソッドの呼び出し　非同期処理
                                    myTask(roomname,name,isOwner,isStartRef)
                                }
                            })
                            setNegativeButton("Cancel", null)
                            show()
                        }
                    }
                    Log.w("handnum", "handnum = " + snapshot.childrenCount.toInt())

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("error", "Failed to read value.", error.toException())
                }


            })
        }else{      //部屋主でない人
            val isStartRef = FirebaseDatabase.getInstance().getReference("room/"+roomname+"/owner/isStart")
            isStartRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value == true){
                        AlertDialog.Builder(this@SelectActivity).apply {
                            setTitle("お知らせ")
                            setMessage("結果が出ました！見に行きますか？")
                            setPositiveButton("はい", DialogInterface.OnClickListener { _, _ ->
                                // OKをタップしたときの処理


                                val intent = Intent(this@SelectActivity,ResultActivity::class.java)
                                val state = DataState(roomname,name,isOwner)      //DataStateの記述はMakeroomActivityに記述
                                intent.putExtra(SelectActivity.KEY_SELECT,state)
                                startActivity(intent)
                            })
                            setNegativeButton("いいえ", null)
                            show()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("error", "Failed to read value.", error.toException())
                }
            })

        }
    }
    private suspend fun myTask(roomname:String,name:String,isOwner:Boolean,isStartRef: DatabaseReference) {

        async(UI) {
            isStartRef.setValue(true)
        }

        Thread.sleep(1000)

        async(UI) {
            val intent = Intent(this@SelectActivity,ResultActivity::class.java)
            val state = DataState(roomname,name,isOwner)      //DataStateの記述はMakeroomActivityに記述
            intent.putExtra(SelectActivity.KEY_SELECT,state)
            startActivity(intent)
        }


    }
    companion object {
        private val FIREBASE_URL = "https://grouptodo-4234b.firebaseio.com"
        const val KEY_SELECT = "com.example.om.grouptodo2-ResultFromSelect"
    }
}
