package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth;
    RecyclerView UserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView imglogout,setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth= FirebaseAuth.getInstance();






        usersArrayList=new ArrayList<>();


        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        DatabaseReference reference= database.getReference().child("user");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users=dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imglogout=findViewById(R.id.logOut);
        setting=findViewById(R.id.setting);
        UserRecyclerView=findViewById(R.id.recyclerview1);
        UserRecyclerView.setLayoutManager( new LinearLayoutManager(this));
        adapter=new UserAdapter(HomeActivity.this,usersArrayList);

        UserRecyclerView.setAdapter(adapter);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this,SettingActivity.class));


            }
        });

        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(HomeActivity.this,R.style.Dialoge);
                dialog.setContentView(R.layout.dialogue_layout);
                TextView yesbtn,Nobtn;
                yesbtn=dialog.findViewById(R.id.yes_btn);
                Nobtn=dialog.findViewById(R.id.no_btn);

                yesbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this,Sign_up.class));

                    }
                });

                Nobtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });


        if (auth.getCurrentUser()==null){
            startActivity(new Intent(HomeActivity.this,Sign_up.class));

        }



    }
}