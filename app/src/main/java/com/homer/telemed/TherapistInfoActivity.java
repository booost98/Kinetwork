package com.homer.telemed;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class TherapistInfoActivity extends AppCompatActivity {

    Button returnChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapistinfo);

        final Intent intent = new Intent(TherapistInfoActivity.this, TherapistActivity.class);
        returnChooser = findViewById(R.id.returnToChooser);

        returnChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

    }
}
