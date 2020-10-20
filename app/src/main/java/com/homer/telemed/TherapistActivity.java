package com.homer.telemed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TherapistActivity extends AppCompatActivity implements TherapistAdapter.OnItemClickListener {

    public static String selectedNameForInfo;
    public static int therapistID;
    //public static String jsonTField, jsonTLocation, jsonTSpecialties, jsonTClinic;
    private FirebaseStorage storage;
    private DatabaseReference databaseReference;
    private ValueEventListener dBListener;
    private List<Therapist> therapists;
    private RecyclerView recyclerView2;
    private TherapistAdapter therapistAdapter;
    public static String URL_THERAPIST = "http://192.168.50.173:80/kinetwork/therapist.php"; //herokudbtest
    public static String URL_THERAPISTCHOOSERINFO = "http://192.168.50.173:80/kinetwork/therapistchooserinfo.php"; //herokudbtest
    public static String URL_THERAPISTIDGET = "http://192.168.50.173:80/kinetwork/therapistidget.php";
    public static String URL_TREATMENTSEND = "http://192.168.50.173:80/kinetwork/treatmentsend.php";
    //public static String URL_THERAPIST = "https://agila.upm.edu.ph/~jhdeleon/kinetwork/therapist.php";
    //public static String URL_THERAPISTCHOOSERINFO = "https://agila.upm.edu.ph/~jhdeleon/kinetwork/therapistchooserinfo.php";


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
                    final Therapist therapist = postSnapshot.getValue(Therapist.class);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_THERAPISTCHOOSERINFO,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String success = jsonObject.getString("success");
                                        JSONArray jsonArray = jsonObject.getJSONArray("getChooserTherapistInfo");

                                        if(success.equals("1")){
                                            for(int i = 0; i < jsonArray.length(); i++){
                                                JSONObject object = jsonArray.getJSONObject(i);
                                                therapist.setField(object.getString("field"));
                                                therapist.setLocation(object.getString("address"));
                                                therapist.setSpecialties(object.getString("specialties"));
                                                therapist.setClinic(object.getString("clinic"));
                                                therapists.add(therapist);
                                                therapistAdapter.notifyDataSetChanged();
                                            }

                                        } else{
                                            Toast.makeText(TherapistActivity.this, "Additional Info Cannot be Fetched", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(TherapistActivity.this, "Additional Info Cannot be Fetched" + e.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(TherapistActivity.this, "Error!" + error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("name", therapist.getName());
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(TherapistActivity.this);
                    requestQueue.add(stringRequest);
                }
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

        selectTherapist(selectedName); //update patient data on user table
        //Toast.makeText(this, "Need to login again after choosing your therapist", Toast.LENGTH_SHORT).show();

        //Intent intent = new Intent(TherapistActivity.this, MainActivity.class);
        //intent.putExtra("therapist", selectedName);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(intent);
        //finish();
    }

    @Override
    public void onViewClick(int position) {
        Therapist selectedTherapist = therapists.get(position);
        selectedNameForInfo = selectedTherapist.getName();
        Intent intent = new Intent(TherapistActivity.this, TherapistInfoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(dBListener);
    }

    private void getTherapistID(final String selectedName){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_THERAPISTIDGET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("getTherapistID");
                            Log.i("b4 loop", String.valueOf(therapistID));
                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    therapistID = object.getInt("user_id");
                                    sendTreatment(LoginActivity.jsonID, therapistID);
                                    Log.i("inside loop", String.valueOf(therapistID)); //acquires it ok
                                }
                                Log.i("after loop", String.valueOf(therapistID));
                            } else{
                                Toast.makeText(TherapistActivity.this, "TherapistID cannot be fetched", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TherapistActivity.this, "TherapistID cannot be fetched" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TherapistActivity.this, "Error!" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("selectedName", selectedName);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(TherapistActivity.this);
        requestQueue.add(stringRequest);
    }

    private void selectTherapist(final String selectedName){
        StringRequest stringRequest =new StringRequest(Request.Method.POST, URL_THERAPIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(TherapistActivity.this, "Therapist has been selected and sent to server", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TherapistActivity.this, "Error! Please check your connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TherapistActivity.this, "Error! Please check your connection", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("therapist_name", selectedName);
                params.put("email", LoginActivity.jsonEmail);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        getTherapistID(selectedName);
    }

    private void sendTreatment(final int patientID, final int therapistID){
        StringRequest stringRequest =new StringRequest(Request.Method.POST, URL_TREATMENTSEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(TherapistActivity.this, "Need to login again after choosing your therapist", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(TherapistActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                //Toast.makeText(TherapistActivity.this, "Treat has been selected and sent to server", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TherapistActivity.this, "Error! Please check your connection" + e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TherapistActivity.this, "Error! Please check your connection", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("patient_id", String.valueOf(patientID));
                params.put("physiotherapist_id", String.valueOf(therapistID));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}