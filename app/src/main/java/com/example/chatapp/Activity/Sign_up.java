package com.example.chatapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.HomeActivity;
import com.example.chatapp.R;
import com.example.chatapp.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Sign_up extends AppCompatActivity {

    TextView register;
    Button signUP;
    CircleImageView prfileImage;
    EditText name,password,cpassword,email;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageuri;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String imageURI;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("please wait..");
        progressDialog.setCancelable(false);



        prfileImage=findViewById(R.id.profile_Image);
        name=findViewById(R.id.regis_name1);
        password=findViewById(R.id.regis_password);
        cpassword=findViewById(R.id.regis_confpass22);
        email=findViewById(R.id.regis_email);
        String status="Hey there im using ChatApp";


       auth= FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();



        signUP=findViewById(R.id.btn_signUp);
        signUP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressDialog.show();

                String nam= name.getText().toString();
                String em=email.getText().toString();
                String pass=password.getText().toString();
                String cpass= cpassword.getText().toString();

                if (TextUtils.isEmpty(nam)||TextUtils.isEmpty(em)||TextUtils.isEmpty(pass)||
                        TextUtils.isEmpty(cpass))

                {
                    progressDialog.dismiss();
                    Toast.makeText(Sign_up.this, "Enter valid Data ", Toast.LENGTH_SHORT).show();
                }else if (!em.matches(emailPattern))
                {
                    progressDialog.dismiss();
                    email.setError("Enter valid email");
                    Toast.makeText(Sign_up.this, "please enter valid Email", Toast.LENGTH_SHORT).show();
                }else if (!pass.equals(cpass))
                {
                    progressDialog.dismiss();
                    Toast.makeText(Sign_up.this, "Password Does Not Matches ", Toast.LENGTH_SHORT).show();
                }else if (pass.length()<6)
                {
                    progressDialog.dismiss();
                    Toast.makeText(Sign_up.this, "Enter more than 6 char for pass", Toast.LENGTH_SHORT).show();
                }else{

                    auth.createUserWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // here em=email and pass = password which we get

                            if (task.isSuccessful()){
                                DatabaseReference reference= database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());


                                if (imageuri!=null){
                                    storageReference.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            if (task.isSuccessful())
                                            {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI=uri.toString();
                                                        Users users=new Users(auth.getUid(),nam,em,imageURI,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful())
                                                                {
                                                                    startActivity(new Intent(Sign_up.this, HomeActivity.class));
                                                                }else{
                                                                    Toast.makeText(Sign_up.this, "Error ", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });

                                                    }
                                                });
                                            }


                                        }
                                    });

                                }
                                else{


                                    {
                                        String status="Hey there im using ChatApp";
                                        imageURI="https://firebasestorage.googleapis.com/v0/b/chatapp-dd1c7.appspot.com/o/profile.png?alt=media&token=3fa6a278-83a8-4202-bf8c-2456abff9709";
                                        Users users=new Users(auth.getUid(),nam,em,imageURI,status);
                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful())
                                                {
                                                    startActivity(new Intent(Sign_up.this,HomeActivity.class));
                                                }else{
                                                    Toast.makeText(Sign_up.this, "Error ", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

                                    }





                                }








                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(Sign_up.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }










            }
        });

        prfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);

            }
        });








        register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Sign_up.this,HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10){
            if (data!=null){

                imageuri=data.getData();
                prfileImage.setImageURI(imageuri);
            }
        }
    }
}
