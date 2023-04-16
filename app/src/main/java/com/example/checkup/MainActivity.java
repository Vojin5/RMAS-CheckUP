package com.example.checkup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;

    TextInputEditText emailTxt;
    TextInputEditText passwordTxt;
    MaterialButton loginBtn;
    MaterialButton signupBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        //FIREBASE SETUP
        firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
        signupBtn.setOnClickListener(this::signupClick);
        loginBtn.setOnClickListener(this::loginClick);
    }

    public void loginClick(View view)
    {
        auth = FirebaseAuth.getInstance();
        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void findViews()
    {
        loginBtn = findViewById(R.id.login);
        signupBtn = findViewById(R.id.signup);
        emailTxt = findViewById(R.id.email);
        passwordTxt = findViewById(R.id.password);
    }

    private void signupClick(View view)
    {
        Intent intent = new Intent(MainActivity.this,SignupPage.class);
        startActivity(intent);
    }
}