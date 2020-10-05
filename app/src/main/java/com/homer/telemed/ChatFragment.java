package com.homer.telemed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatFragment extends Fragment {
    private EditText myMessage;
    private ImageButton sendButton;
    private ListView messages_view;
    private static String URL_MESSAGESEND = "http://192.168.50.173:80/kinetwork/messagesend.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        MainActivity.hideKeyboard(getActivity());


        myMessage = view.findViewById(R.id.myMessage);
        sendButton = view.findViewById(R.id.sendButton);
        messages_view = (ListView) view.findViewById(R.id.messages_view);
        final List<String> messages = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.my_message, messages);
        messages_view.setAdapter(arrayAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int sentByPatient = 1;
                final String message = myMessage.getText().toString();
                if(message.length()>0){
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MESSAGESEND,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String success = jsonObject.getString("success");

                                        if (success.equals("1")) {
                                            //Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), "Error! Please check your connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getActivity(), "Error! Please check your connection", Toast.LENGTH_SHORT).show();
                                }
                            })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("message", message);
                            params.put("id", String.valueOf(LoginActivity.jsonID)); //send parameters to database for proper identification of which user sent the data
                            params.put("sentByPatient", String.valueOf(sentByPatient));
                            if(MainActivity.keyLogin == 1){
                                params.put("therapistName", LoginActivity.jsonTherapistName);
                            } else{
                                params.put("therapistName", MainActivity.therapist);
                            }
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    requestQueue.add(stringRequest);

                    messages.add(message);
                    arrayAdapter.notifyDataSetChanged();
                    myMessage.getText().clear();
                }
            }
        });

        return view;
    }


}
