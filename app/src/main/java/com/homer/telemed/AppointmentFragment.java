package com.homer.telemed;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
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

    //int jsonTreatmentID;
    private List<String> appointmentDates;
    private ArrayAdapter<String> arrayAdapter;
    String pickedDate, pickedTime, jsonSchedule;
    String concatAppointment;
    int jsonIsApproved;
    private static String URL_APPOINTMENTSEND = "http://192.168.50.173:80/kinetwork/appointmentsend.php";
    private static String URL_APPOINTMENTGET = "http://192.168.50.173:80/kinetwork/appointmentget.php";
    //private static String URL_APPOINTMENTSEND = "https://agila.upm.edu.ph/~jhdeleon/kinetwork/appointmentsend.php";
    //private static String URL_APPOINTMENTGET = "https://agila.upm.edu.ph/~jhdeleon/kinetwork/appointmentget.php";


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.hideKeyboard(getActivity());
        ChatFragment.isInChat = false;



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
            pickedDate = year + "-" + (monthOfYear+1) + "-" + dayOfMonth; //(monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
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
        final String schedule = pickedDate + " " + pickedTime;

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
                            Toast.makeText(getActivity(), "Error! Please check your connection" + e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("schedule", schedule);
                Log.i("treatmentid test @ sendappointment()", String.valueOf(LoginActivity.jsonTreatmentID));
                params.put("treatment_id", String.valueOf(LoginActivity.jsonTreatmentID));
                params.put("label", "Appointment for patient_id " + LoginActivity.jsonID);
                params.put("note", "Appointment for patient_id " + LoginActivity.jsonID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void getAppointment(){ //gets all appointments of current user from database
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
                                    jsonSchedule = object.getString("schedule").trim();
                                    jsonIsApproved = object.getInt("is_approved");
                                    //Log.i("jsonSchedule and jsonisApproved check @ getappointment", jsonSchedule + String.valueOf(jsonIsApproved));

                                    if(jsonIsApproved == 0){
                                        concatAppointment = "Schedule: " + jsonSchedule + " Status: Pending";
                                    } else if(jsonIsApproved == 1){
                                        concatAppointment = "Schedule: " + jsonSchedule + " Status: Approved";
                                    } else if(jsonIsApproved == 2){
                                        concatAppointment = "Schedule: " + jsonSchedule + " Status: Rejected";
                                    } else{
                                        concatAppointment = "Schedule: " + jsonSchedule + " Status: Unknown";
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
                Log.i("treatmentid from login check @ getappointments()", String.valueOf(LoginActivity.jsonTreatmentID));
                params.put("treatment_id", String.valueOf(LoginActivity.jsonTreatmentID)); //put treatment_id of current user to get only HIS/HER appointments
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

}
