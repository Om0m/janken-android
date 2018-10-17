package com.sample.janken

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.*
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // to get dp unit
        val dp = resources.displayMetrics.density
        val sp = resources.displayMetrics.scaledDensity

        // Instantiation of linearLayout
        val layout = LinearLayout(this)

        val mParent =  LayoutParams.MATCH_PARENT
        val wContent =  LayoutParams.WRAP_CONTENT

        layout.layoutParams = LayoutParams(mParent, mParent)

        layout.orientation = VERTICAL

        layout.gravity = Gravity.CENTER

        setContentView(layout)
        //setContentView(R.layout.activity_result)

        // Instantiation of TextView
        val tv_result = TextView(this)
        tv_result.textSize = 8 * sp
        tv_result.layoutParams = LayoutParams(wContent, wContent)
        //layout.addView(tv_result)

        val btn_Select = Button(this@ResultActivity)
        val btn_Main = Button(this@ResultActivity)


        val state = intent.getSerializableExtra(SelectActivity.KEY_SELECT)

        var name =""
        var roomname =""
        var isOwner = false

        if(state is DataState){
            name = state.name
            roomname = state.roomname
            isOwner = state.isOwner
        }

        val ownerRef = FirebaseDatabase.getInstance().getReference("room/"+roomname+"/owner")


        val ResultRef = FirebaseDatabase.getInstance().getReference("room/"+roomname+"/result/"+name)
        ResultRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                layout.removeAllViews()

                val result = snapshot.value
                val name_snapshot = snapshot.key
                tv_result.text = name_snapshot + "さんの結果は" + result
                layout.addView(tv_result)
                Log.w("value", "result=" + result +" roomname = "+roomname)

                // Instantiation of Button
                btn_Select.text = "やり直す"
                val btnSelectLayoutParams = LayoutParams(
                        (250 * dp).toInt(), LayoutParams.WRAP_CONTENT)
                btnSelectLayoutParams.topMargin = (40 * dp).toInt()
                btn_Select.layoutParams = btnSelectLayoutParams
                layout.addView(btn_Select)

                btn_Main.text = "ホームに戻る"
                val btnMainLayoutParams = LayoutParams(
                        (300 * dp).toInt(), LayoutParams.WRAP_CONTENT)
                btnMainLayoutParams.topMargin = (40 * dp).toInt()
                btn_Main.layoutParams = btnMainLayoutParams
                layout.addView(btn_Main)




            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("error", "Failed to read value.", error.toException())
            }
        })

        btn_Select.setOnClickListener{
            // やり直すのでデータベースの一部分をクリア ownerのみ
            if(isOwner) {
                val isStartRef = FirebaseDatabase.getInstance().getReference("room/" + roomname + "/owner/isStart")
                val handRef = FirebaseDatabase.getInstance().getReference("room/" + roomname + "/hand")
                handRef.setValue("hand")
                isStartRef.setValue(false)
            }

            // アクティビティに遷移
            val intent = Intent(this@ResultActivity, SelectActivity::class.java)
            val state = DataState(roomname, name, isOwner)      //DataStateの記述はMakeroomActivityに記述
            intent.putExtra(KEY_RESULT,state)
            startActivity(intent)


        }

        btn_Main.setOnClickListener{

            //ルームを削除 ownerのみ
            if(isOwner) {
                val ResultRef = FirebaseDatabase.getInstance().getReference("room/" + roomname)
                ResultRef.removeValue()
            }
            // アクティビティに遷移
            val intent = Intent(this@ResultActivity, MainActivity::class.java)
            val state = DataState(roomname, name, isOwner)      //DataStateの記述はMakeroomActivityに記述
            intent.putExtra(KEY_RESULT_MAIN,state)
            startActivity(intent)

        }
        ownerRef.removeEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                AlertDialog.Builder(this@ResultActivity).apply {
                    setTitle("お知らせ")
                    setMessage("部屋が削除されました。")
                    setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        // OKをタップしたときの処理

                        // アクティビティに遷移
                        val intent = Intent(this@ResultActivity, MainActivity::class.java)
                        val state = DataState(roomname, name, isOwner)      //DataStateの記述はMakeroomActivityに記述
                        intent.putExtra(KEY_RESULT_MAIN,state)
                        startActivity(intent)
                    })
                    //setNegativeButton("Cancel", null)
                    show()
                }

            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("error", "Failed to read value.", error.toException())
            }
        })

    }


    companion object {
        private val FIREBASE_URL = "https://grouptodo-4234b.firebaseio.com"
        const val KEY_RESULT = "com.example.om.grouptodo2-SelectFromResult"
        const val KEY_RESULT_MAIN  = "com.example.om.grouptodo2-MainFromResult"
    }
}
