package com.example.checkup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class MainPage extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView navigationView;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        findViews();
        setListeners();
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
                //IMPLEMENT
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
}