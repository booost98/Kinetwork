package com.homer.telemed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TherapistInfoActivity extends AppCompatActivity {

    Button returnChooser;
    TextView therapistField, therapistPhone, therapistAddress, therapistSpecialtyList, therapistWebsite;
    String URL_THERAPISTINFO = "http://192.168.50.173:80/kinetwork/therapistinfo.php";
    String jsonTherapistField, jsonTherapistPhone, jsonTherapistAddress, jsonTherapistSpecialtyList, jsonTherapistWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapistinfo);
        final Intent intent = new Intent(TherapistInfoActivity.this, TherapistActivity.class);
        returnChooser = findViewById(R.id.returnToChooser);
        therapistField = findViewById(R.id.therapistField);
        therapistPhone = findViewById(R.id.therapistPhone);
        therapistAddress = findViewById(R.id.therapistAddress);
        therapistSpecialtyList = findViewById(R.id.therapistSpecialtyList);
        therapistWebsite = findViewById(R.id.therapistWebsite);

        getTherapistInfo();

        returnChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
    }

    private void getTherapistInfo(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_THERAPISTINFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("getTherapistInfo");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    jsonTherapistField = object.getString("field").trim();
                                    jsonTherapistPhone = object.getString("phonenumber").trim();
                                    jsonTherapistAddress = object.getString("address").trim();
                                    jsonTherapistSpecialtyList = object.getString("specialties").trim();
                                    jsonTherapistWebsite = object.getString("websites").trim();

                                    therapistField.setText(jsonTherapistField);
                                    therapistPhone.setText(jsonTherapistPhone);
                                    therapistAddress.setText(jsonTherapistAddress);
                                    therapistSpecialtyList.setText(jsonTherapistSpecialtyList);
                                    therapistWebsite.setText(jsonTherapistWebsite);
                                }

                            } else{
                                Toast.makeText(TherapistInfoActivity.this, "Therapist Info for therapist: " + TherapistActivity.selectedNameForInfo + "cannot be fetched", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TherapistInfoActivity.this, "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TherapistInfoActivity.this, "Error!" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("selectedNameForInfo", TherapistActivity.selectedNameForInfo);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(TherapistInfoActivity.this);
        requestQueue.add(stringRequest);
    }

}
