package com.homer.telemed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    TextView headerName;
    TextView headerEmail;
    TextView headerTherapist;
    String name, email, therapist;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        headerName = header.findViewById(R.id.headerName);
        headerEmail = header.findViewById(R.id.headerEmail);
        headerTherapist = header.findViewById(R.id.headerTherapist);
        int key = getIntent().getIntExtra("key", 0);
        int keyLogin = getIntent().getIntExtra("keyLogin", 1);
        therapist = getIntent().getStringExtra("therapist");

        headerName.setText(LoginActivity.jsonName);
        headerEmail.setText(LoginActivity.jsonEmail);

        if(keyLogin == 1) {
            headerTherapist.setText("Your therapist: " + LoginActivity.jsonTherapistName);
        } else{
            headerTherapist.setText("Your therapist: " + therapist);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(key == (Integer) 3){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VideoFragment()).commit();
        }

        if(savedInstanceState == null && key != 3){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AppointmentFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_appointment);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).commit();
                break;

            case R.id.nav_videochat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VideoChatFragment()).commit();
                break;

            case R.id.nav_exercise:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VideoFragment()).commit();
                break;

            case R.id.nav_appointment:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AppointmentFragment()).commit();
                break;

            /*case R.id.nav_video:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VideoFragment()).commit();
                break;*/

            case R.id.nav_files:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FileFragment()).commit();
                break;

            case R.id.nav_logout:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else if(doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back button again to logout", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}