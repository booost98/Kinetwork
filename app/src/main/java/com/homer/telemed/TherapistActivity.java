package com.homer.telemed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TherapistActivity extends AppCompatActivity implements TherapistAdapter.OnItemClickListener {

    public static String selectedNameForInfo;
    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private ValueEventListener dBListener;
    private List<Therapist> therapists;
    private RecyclerView recyclerView2;
    private TherapistAdapter therapistAdapter;
    String name, email;
    String URL_THERAPIST = "http://192.168.50.173:80/kinetwork/therapist.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist);

        recyclerView2 = findViewById(R.id.recycler_view2);
        therapists = new ArrayList<Therapist>();
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        therapistAdapter = new TherapistAdapter(this, therapists);
        recyclerView2.setAdapter(therapistAdapter);

        therapistAdapter.setOnItemClickListener(TherapistActivity.this);

        storage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("therapists");

        dBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Therapist therapist = postSnapshot.getValue(Therapist.class);
                    therapists.add(therapist);
                }
                therapistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TherapistActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(this, "Normal click at position " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelectClick(int position) {
        Therapist selectedItem = therapists.get(position);
        final String selectedName = selectedItem.getName();
        final int hasTherapist = 1;
        //Toast.makeText(this, "Therapist " + selectedName + " has been selected", Toast.LENGTH_SHORT).show();

        StringRequest stringRequest =new StringRequest(Request.Method.POST, URL_THERAPIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(TherapistActivity.this, "Therapist sent to db", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TherapistActivity.this, "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TherapistActivity.this, "Error! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("hasTherapist", String.valueOf(hasTherapist));
                params.put("therapistName", selectedName);
                params.put("email", LoginActivity.jsonEmail);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        Intent intent = new Intent(TherapistActivity.this, MainActivity.class);
        intent.putExtra("therapist", selectedName);
        startActivity(intent);
    }

    @Override
    public void onViewClick(int position) {
        Therapist selectedTherapist = therapists.get(position);
        selectedNameForInfo = selectedTherapist.getName();
        //Toast.makeText(this, "Viewing Therapist Info...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TherapistActivity.this, TherapistInfoActivity.class);
        //intent.putExtra("therapist", selectedNameForInfo);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(dBListener);
    }
}