package com.homer.telemed;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AppointmentFragment extends Fragment {

    private List<String> appointmentDates;
    private ArrayAdapter<String> arrayAdapter;
    String pickedDate, pickedTime, jsonDate, jsonTime;
    String concatAppointment;
    int jsonIsApproved;
    private static String URL_APPOINTMENTSEND = "http://192.168.50.173:80/kinetwork/appointmentsend.php";
    private static String URL_APPOINTMENTGET = "http://192.168.50.173:80/kinetwork/appointmentget.php";

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.hideKeyboard(getActivity());
        View view = inflater.inflate(R.layout.fragment_appointment, container, false); //display appointment layout
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView appointments_view = view.findViewById(R.id.appointments_view);
        appointmentDates = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.my_appointment, appointmentDates);
        appointments_view.setAdapter(arrayAdapter);
        FloatingActionButton addAppointment = view.findViewById(R.id.addAppointment);
        Button getAppointmentsBtn = view.findViewById(R.id.getAppointmentsBtn);

        addAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDatePicker();
            }
        });

        getAppointmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appointmentDates.clear();
                getAppointment();
            }
        });

    }

    private void showDatePicker(){
        DialogFragment date = new DatePickerFragment();

        Calendar calendar = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calendar.get(Calendar.YEAR));
        args.putInt("month", calendar.get(Calendar.MONTH));
        args.putInt("day", calendar.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

         // Set Call back to capture selected date

        ((DatePickerFragment) date).setCallBack(ondate);
        date.show(getFragmentManager(), "date picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            pickedDate = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
            showTimePicker(); //show time picker after datepicker
        }
    };

    private void showTimePicker(){
        DialogFragment time = new TimePickerFragment();

        Calendar calendar = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("hour", calendar.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", calendar.get(Calendar.MINUTE));
        time.setArguments(args);

        // Set Call back to capture selected time

        ((TimePickerFragment) time).setCallBack(ontime);
        time.show(getFragmentManager(), "date picker");
    }

    TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hour, int minute) {
            if(minute == 0){
                pickedTime = hour + ":00";
            } else{
            pickedTime = hour + ":" + minute;
            }
            sendAppointment();
        }
    };

    private void sendAppointment(){ //sends the set appointment to databse
        final String date = pickedDate;
        final String time = pickedTime;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_APPOINTMENTSEND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(getActivity(), "Appointment sent to your therapist", Toast.LENGTH_SHORT).show();
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
                params.put("date", date);
                params.put("time", time);
                params.put("id", String.valueOf(LoginActivity.jsonID)); //send parameters to database for proper identification of which user sent the data
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void getAppointment(){ //gets all appointments of current user from database
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_APPOINTMENTGET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("getAppointment");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    jsonDate = object.getString("date").trim();
                                    jsonTime = object.getString("time").trim();
                                    jsonIsApproved = object.getInt("isApproved");

                                    if(jsonIsApproved == 0){
                                        concatAppointment = "Date: " + jsonDate + " Time: " + jsonTime + " Status: Pending";
                                    } else{
                                        concatAppointment = "Date: " + jsonDate + " Time: " + jsonTime + " Status: Confirmed";
                                    }
                                    appointmentDates.add(concatAppointment);
                                    arrayAdapter.notifyDataSetChanged();
                                }

                            } else{
                                Toast.makeText(getActivity(), "You have no appointments", Toast.LENGTH_SHORT).show();
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
                params.put("id", String.valueOf(LoginActivity.jsonID)); //put id of current user to get only HIS/HER appointments
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

}
