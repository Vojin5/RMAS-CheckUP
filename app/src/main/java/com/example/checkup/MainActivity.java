package com.example.checkup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    FirebaseDatabase firebaseDatabase;
    MaterialButton login;
    MaterialButton signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        //FIREBASE SETUP
        firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
        signup.setOnClickListener(this::signupClick);

    }

    private void findViews()
    {
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
    }

    private void signupClick(View view)
    {
        Intent intent = new Intent(MainActivity.this,SignupPage.class);
        startActivity(intent);
    }
}