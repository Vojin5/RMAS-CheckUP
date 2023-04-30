package com.example.checkup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainPage extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView navigationView;

    FirebaseDatabase firebaseDatabase;

    FirebaseAuth auth;

    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        findViews();
        setListeners();
        setFirebase();
        loadFragment(new HomeFragment());
        toolbar.setOnMenuItemClickListener(this::toolbarClick);
    }

    private boolean toolbarClick(MenuItem item)
    {
        Fragment fragment = null;
        switch (item.getItemId())
        {
            case R.id.menu_logout:
                logoutClick();
                break;
            case R.id.menu_leaderboard:
                fragment = new LeaderboardFragment();
                break;
        }
        if(fragment != null)
        {
            loadFragment(fragment);
        }
        return true;
    }
    private void findViews()
    {
        navigationView = findViewById(R.id.bottom_nav);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setListeners()
    {
        navigationView.setOnNavigationItemSelectedListener(this);

    }

    private void logoutClick()
    {
        closeRemember();
        finish();
    }



    public void closeRemember()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("remember",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("remember","false");
        editor.apply();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId())
        {
            case R.id.menu_home:
                fragment = new HomeFragment();
                break;
            case R.id.menu_map:
                Intent intent = new Intent(MainPage.this,MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_profile:
                fragment = new ProfileFragment();
                break;
        }
        if(fragment != null)
        {
            loadFragment(fragment);
        }
        return  true;
    }

    private void loadFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,fragment).commit();
    }

    private void setFirebase()
    {
        firebaseDatabase = FirebaseDatabase.getInstance("https://checkup-f6ce4-default-rtdb.europe-west1.firebasedatabase.app");
    }
}