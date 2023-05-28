package com.example.checkup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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

    // TODO: Leaderboard
    // TODO: Add Photo
    // TODO: Add comment
    // TODO: Profile point read
    // TODO: Reserve Logic
    // TODO: Home filter
    private static final String TAG = "MainActivity";
    //checkup-385309
    //AIzaSyB8CLKSAhaamioA1U62or5CLQbUqxs5YyM
    //AIzaSyCQIixnPlJGSN7LYFaK3oekf7SOx12ATtM

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth auth;

    CheckBox rememberMeBox;

    TextInputEditText emailTxt;
    TextInputEditText passwordTxt;
    MaterialButton loginBtn;
    MaterialButton signupBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setListeners();
        setFirebase();
        checkRememberMe();

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
                            if(rememberMeBox.isChecked())
                            {
                                addRemember();
                            }
                            Toast.makeText(MainActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent(MainActivity.this,MainPage.class);
                            startActivity(intent);
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
        rememberMeBox = findViewById(R.id.rememberMe);
    }

    private void signupClick(View view)
    {
        Intent intent = new Intent(MainActivity.this,SignupPage.class);
        startActivity(intent);
    }

    public void addRemember()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("remember",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("remember","true");
        editor.apply();
    }

    public void setFirebase()
    {
        firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
    }

    private void setListeners()
    {
        signupBtn.setOnClickListener(this::signupClick);
        loginBtn.setOnClickListener(this::loginClick);
    }



    public void checkRememberMe()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("remember",MODE_PRIVATE);
        String checked = sharedPreferences.getString("remember","");
        if(checked.equals("true"))
        {
            Intent intent = new Intent(MainActivity.this,MainPage.class);
            startActivity(intent);
        }

    }
}