package com.homer.telemed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


public class LoginActivity extends AppCompatActivity {

    EditText emailLogin, passwordLogin;
    private static String URL_LOGIN = "http://192.168.50.173:80/kinetwork/login.php"; //herokudbtest
    //private static String URL_LOGIN = "https://agila.upm.edu.ph/~jhdeleon/kinetwork/login.php";
    public static String jsonName, jsonEmail, jsonTherapistName;
    int jsonHasTherapist;
    public static int jsonID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);


        Button loginBtn = findViewById(R.id.loginBtn);

        /*loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });*/
        //comment out for faster login muna
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailLogin.getText().toString().trim();
                final String password = passwordLogin.getText().toString().trim();

                if(!email.isEmpty() || !password.isEmpty()){
                    login(email, password);
                }
                else{
                    emailLogin.setError("Please enter your email address");
                    passwordLogin.setError("Please enter your password");
                }

            }
        });

    }

    private void login(final String email, final String password){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    jsonName = object.getString("name").trim();
                                    jsonEmail = object.getString("email").trim();
                                    jsonID = object.getInt("user_id");
                                    jsonHasTherapist = object.getInt("has_therapist");
                                    jsonTherapistName = object.getString("therapist_name");
                                }

                                Intent intent2 = new Intent(LoginActivity.this, MainActivity.class);
                                Intent intent = new Intent(LoginActivity.this, TherapistActivity.class);
                                if(jsonHasTherapist == 0){
                                    startActivity(intent);
                                } else{
                                    intent2.putExtra("therapist", jsonTherapistName);
                                    intent2.putExtra("keyLogin", 1);
                                    startActivity(intent2);
                                }


                            } else{
                                Toast.makeText(LoginActivity.this, "Your email and/or password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Your email and/or password is incorrect" + e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Error! Pleae check your connection", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void goSignUp(View view){
        Intent intent = new Intent(view.getContext(), RegisterActivity.class);
        view.getContext().startActivity(intent);
    }
}