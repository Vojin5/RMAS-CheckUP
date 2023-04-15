package com.example.checkup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.BoringLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupPage extends AppCompatActivity{

    FirebaseDatabase firebaseDatabase;
    TextInputEditText email;
    TextInputEditText password;
    TextInputEditText username;

    String usernameStr;
    String emailStr;
    String passwordStr;

    MaterialButton create;
    MaterialButton cancel;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        findViews();
        firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
        create.setOnClickListener(this::createClick);
    }

    private void createClick(View view)
    {
        this.emailStr = email.getText().toString();
        this.passwordStr = password.getText().toString();
        this.usernameStr = username.getText().toString();

        //Check if there is empty fields
        if(isEmpty(emailStr) || isEmpty(passwordStr) || isEmpty(usernameStr))
        {
            Toast.makeText(this, "Some field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        //User is ready to be added if username and mail are free
        user = new User(emailStr,passwordStr);
        checkUsernameAndEmail();

    }

    private void checkUsernameAndEmail()
    {
        firebaseDatabase.getReference()
                .child("users")
                .orderByKey()
                .equalTo(usernameStr)
                .get()
                .addOnSuccessListener(this::usersOnSuccess);
    }

    private void usersOnSuccess(DataSnapshot snapshot)
    {
        if(snapshot.exists())
        {
            Toast.makeText(SignupPage.this, "Username already exists", Toast.LENGTH_SHORT).show();
        }
        else{
            checkEmail();
        }
    }

    private void checkEmail()
    {
        firebaseDatabase.getReference()
                .child("users")
                .get()
                .addOnSuccessListener(this::emailOnSuccess);

    }

    private void emailOnSuccess(DataSnapshot snapshot)
    {
        boolean used = false;
        for(DataSnapshot dataSnapshot : snapshot.getChildren())
        {
            User tmp = dataSnapshot.getValue(User.class);
            if(tmp.getEmail().equals(emailStr))
            {
                Toast.makeText(SignupPage.this, "Mail is already in use", Toast.LENGTH_SHORT).show();
                used = true;
                break;
            }
        }
        if(used == false)
        {
            addUser();
        }
    }

    private void addUser()
    {
        firebaseDatabase.getReference().child("users").child(usernameStr).setValue(user);
        Toast.makeText(this, "User created", Toast.LENGTH_SHORT).show();
    }

    public Boolean isEmpty(String str)
    {
        return TextUtils.isEmpty(str);
    }

    private void findViews()
    {
        create = findViewById(R.id.signup_create);
        cancel = findViewById(R.id.signup_cancel);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        username = findViewById(R.id.signup_username);
    }

}