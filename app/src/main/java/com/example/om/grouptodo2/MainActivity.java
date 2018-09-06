package com.example.om.grouptodo2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String FIREBASE_URL = "https://grouptodo-4234b.firebaseio.com";
    //DatabaseReference todosRef = FirebaseDatabase.getInstance().getReference("todos");
    String TAG = "aaa";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Write a message to the database

        DatabaseReference todosRef = FirebaseDatabase.getInstance().getReference("todos");
        String key = todosRef.child("title").push().getKey();
        Todo todo = new Todo("歌を歌うを歌う",true);
        Map<String, Object> map = new HashMap<>();
        map.put(key, todo.toMap());
        todosRef.updateChildren(map);

        //todosRef.child("01").child("title").setValue("はみがき");
        //todosRef.setValue("04");
        //todosRef.child("04").setValue("title");
        //todosRef.push({"01":{"title","おかいもの"}});
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Read from the database
        todosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    String title = (String) dataSnapshot.child("title").getValue();
                    Boolean isDone = (Boolean) dataSnapshot.child("isDone").getValue();
                    Log.d(TAG, "title is: " + title);
                    Log.d(TAG, "isdone is: " + isDone);

                    // このforループで、Todoごとのkey, title, isDoneが取得できているので、
                    // Todoクラスを利用し、Hashmapに追加するなどして保存する。
                }
        }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public class Todo {
        private String title;
        private Boolean isDone;

        public Todo(String title, Boolean isDone){
            this.title = title;
            this.isDone = isDone;
        }
        public String getTitle(){
            return title;
        }
        public Boolean isDone(){
            return isDone;
        }
        public void setDone(){
            this.isDone = true;
        }

        @Exclude
        public Map<String, Object> toMap(){
            HashMap<String, Object> hashmap = new HashMap<>();
            hashmap.put("title", title);
            hashmap.put("isDone", isDone);
            return hashmap;
        }
    }



}

