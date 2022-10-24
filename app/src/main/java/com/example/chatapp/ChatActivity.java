package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapter.MessagesAdapter;
import com.example.chatapp.ModelClass.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String ReceiverImage,RecieverUID,RecevierName,SenderUID;
    CircleImageView profileImage;
    TextView receiverName;
    CardView sendbtn;
    EditText typemsg;

    String senderRoom,receiverRoom;
    RecyclerView messagerAdapter;

    MessagesAdapter adapter;


    public static String sImage;
    public static String rImage;

    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;

    ArrayList<Messages> messagesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        RecevierName=getIntent().getStringExtra("name");
        ReceiverImage=getIntent().getStringExtra("ReceiverImage");
        RecieverUID=getIntent().getStringExtra("uid");

        messagesArrayList=new ArrayList<>();

        profileImage=findViewById(R.id.profile_Image);
       receiverName=findViewById(R.id.ReceieverName);
       sendbtn=findViewById(R.id.sendBtn);
       typemsg=findViewById(R.id.typemsg);

       messagerAdapter=findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        messagerAdapter.setLayoutManager(linearLayoutManager);

        adapter=new MessagesAdapter(ChatActivity.this,messagesArrayList);
        messagerAdapter.setAdapter(adapter);




        Picasso.get().load(ReceiverImage).into(profileImage);
        receiverName.setText(""+RecevierName);
        SenderUID=firebaseAuth.getUid();

        senderRoom=SenderUID+RecieverUID;
        receiverRoom=RecieverUID+SenderUID;

       DatabaseReference reference= database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference ChatsRerence= database.getReference().child("chats").child(senderRoom).child("messages");
        ChatsRerence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Messages messages=dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
              sImage=snapshot.child("imageUri").getValue().toString();
              rImage=ReceiverImage;

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

       sendbtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String message=typemsg.getText().toString();
               if (message.isEmpty())
               {
                   Toast.makeText(ChatActivity.this, "please enter valid text", Toast.LENGTH_SHORT).show();
                   return;
               }
               typemsg.setText("");
               Date date= new Date();

               Messages messages= new Messages(message,SenderUID,date.getTime() );
               database.getReference().child("chats")
                       .child(senderRoom)
                       .child("messages")
                       .push()
                       .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               database.getReference().child("chats")
                                       .child(receiverRoom)
                                       .child("messages")
                                       .push()
                                       .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {

                                           }
                                       });

                           }
                       });


           }
       });

    }
}