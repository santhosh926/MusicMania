package com.example.anuradha.musicmania;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class NavActivity extends AppCompatActivity {

    private BottomNavigationView navbar;
    private FrameLayout mainFrame;
    private SearchFragment searchFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        navbar = findViewById(R.id.id_navbar);
        mainFrame = findViewById(R.id.id_mainFrame);
        searchFragment = new SearchFragment();
        accountFragment = new AccountFragment();

        setFragment(accountFragment);

        navbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.id_nav_account:
                        setFragment(accountFragment);
                        return true;
                    case R.id.id_nav_search:
                        setFragment(searchFragment);
                        return true;
                        default: return false;
                }
            }
        });
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.id_mainFrame, fragment);
        fragmentTransaction.commit();
    }

}
