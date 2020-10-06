package com.homer.telemed;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText name, emailRegister, passwordRegister, confirmPassword;
    Button registerBtn;
    //private static String URL_REGIST = "http://192.168.50.173:80/kinetwork/register.php";
    private static String URL_REGIST = "https://agila.upm.edu.ph/~jhdeleon/kinetwork/register.php";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        confirmPassword = findViewById(R.id.confirmPassword);
        name.addTextChangedListener(registerTextWatcher);
        emailRegister.addTextChangedListener(registerTextWatcher);
        passwordRegister.addTextChangedListener(registerTextWatcher);
        confirmPassword.addTextChangedListener(registerTextWatcher);
        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordRegister.getText().toString().trim().equals(confirmPassword.getText().toString().trim())){
                    register();
                } else{
                    Toast.makeText(RegisterActivity.this, "Password input is not equal to Confirm Password input", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String nameInput = name.getText().toString().trim();
            String emailInput = emailRegister.getText().toString().trim();
            String passwordInput = passwordRegister.getText().toString().trim();
            String confirmPasswordInput = confirmPassword.getText().toString().trim();

            registerBtn.setEnabled(!nameInput.isEmpty() && !emailInput.isEmpty() && !passwordInput.isEmpty() && !confirmPasswordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    private void register(){
        final String name = this.name.getText().toString().trim();
        final String email = this.emailRegister.getText().toString().trim();
        final String password = this.passwordRegister.getText().toString().trim();

        StringRequest stringRequest =new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(RegisterActivity.this, "You have been registered", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Error! Please check your connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Error! Please check your connection", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void goLogin(View view){
        Intent intent = new Intent(view.getContext(), LoginActivity.class);
        view.getContext().startActivity(intent);
    }

}
