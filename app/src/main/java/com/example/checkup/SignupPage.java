package com.example.checkup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.BoringLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignupPage extends AppCompatActivity{
    private static final String TAG = "SignupPage";

    private static final String PROFILE_IMG_STORAGE = "ProfileImages";

    public static final int PICK_IMAGE = 1;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    Uri imageURI;
    FirebaseAuth auth;

    ImageView image;
    TextInputEditText emailTxt;
    TextInputEditText passwordTxt;
    TextInputEditText usernameTxt;

    String username;
    String email;
    String password;

    MaterialButton createBtn;
    MaterialButton cancelBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        findViews();
        setListeners();
        setFirebase();
    }

    private void cancelClick(View view)
    {
        finish();
    }

    private void imageClick(View view)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageURI = data.getData();
            image.setImageURI(imageURI);
        }
    }

    private void createClick(View view)
    {
        username = usernameTxt.getText().toString();
        email = emailTxt.getText().toString();
        password = passwordTxt.getText().toString();

        if(isEmpty(username,"username"))
        {
            return;
        }
        if(isEmpty(email,"email"))
        {
            return;
        }
        if(isEmpty(password,"password"))
        {
            return;
        }

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = auth.getCurrentUser();
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            user.updateProfile(request);
                            if(imageURI != null)
                            {
                                StorageReference photoRef = storage.getReference();
                                photoRef.child(PROFILE_IMG_STORAGE)
                                        .child(username+".jpg")
                                        .putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                Toast.makeText(SignupPage.this, "Photo added", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignupPage.this, "Failed photo upload", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            Toast.makeText(SignupPage.this, "Account created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupPage.this,MainActivity.class);
                            startActivity(intent);

                        }
                        else{
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignupPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private boolean isEmpty(String str,String name)
    {
        if(TextUtils.isEmpty(str))
        {
            Toast.makeText(this, "Enter " + name, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    private void setFirebase()
    {
        firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }
    private void findViews()
    {
        createBtn = findViewById(R.id.signup_create);
        cancelBtn = findViewById(R.id.signup_cancel);
        emailTxt = findViewById(R.id.signup_email);
        passwordTxt = findViewById(R.id.signup_password);
        usernameTxt = findViewById(R.id.signup_username);
        image = findViewById(R.id.userImage);
    }

    public void setListeners()
    {
        createBtn.setOnClickListener(this::createClick);
        image.setOnClickListener(this::imageClick);
        cancelBtn.setOnClickListener(this::cancelClick);
    }



}