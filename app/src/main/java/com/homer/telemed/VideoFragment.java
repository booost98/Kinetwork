package com.homer.telemed;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VideoFragment extends Fragment {

    int jsonExerciseTypeID, jsonExerciseNum;
    String jsonExerciseType, jsonExerciseDesc, jsonExerciseVidLink;
    Button getVitalSigns, sendVitalSigns, startExercise;
    EditText heartRateEditText, bloodPressureSystolic, bloodPressureDiastolic;
    Intent intent;
    ImageView exerciseHelpBtn;
    Spinner exerciseTypeSpinner, exerciseNumSpinner;
    TextView exerciseDescTextView, exerciseVideoLinkTextView, heartRateTitle, bloodPressureTitle;
    private List<ExerciseType> exerciseTypes;
    private List<Exercise> exercises;
    private ArrayAdapter<ExerciseType> exerciseTypeSpinnerAdapter;
    private ArrayAdapter<Exercise> exerciseNumSpinnerAdapter;
    public int exerciseTypeID;
    public String exerciseDesc, exerciseVidLink;
    private static String URL_EXERCISETYPES = "http://192.168.50.173:80/kinetwork/exercisetype.php"; //testing fetching from herokudb
    private static String URL_VITALSIGNS = "http://192.168.50.173:80/kinetwork/vitalsigns.php";
    private static String URL_EXERCISES = "http://192.168.50.173:80/kinetwork/exercise.php";
    //private static String URL_EXERCISETYPES = "https://agila.upm.edu.ph/~jhdeleon/kinetwork/exercisetype.php";
    //private static String URL_VITALSIGNS = "https://agila.upm.edu.ph/~jhdeleon/kinetwork/vitalsigns.php";
    //private static String URL_EXERCISES = "https://agila.upm.edu.ph/~jhdeleon/kinetwork/exercise.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        MainActivity.hideKeyboard(getActivity());

        ChatFragment.isInChat = false;
        intent = new Intent(getActivity(), DeviceScanActivity.class);
        getVitalSigns = view.findViewById(R.id.getVitalSigns);
        sendVitalSigns = view.findViewById(R.id.sendVitalSigns);
        startExercise = view.findViewById(R.id.startExercise);
        heartRateTitle = view.findViewById(R.id.heartRateTitle);
        bloodPressureTitle = view.findViewById(R.id.bloodPressureTitle);
        heartRateEditText = view.findViewById(R.id.heartRateEditText);
        bloodPressureSystolic = view.findViewById(R.id.bloodPressureSystolic);
        bloodPressureDiastolic = view.findViewById(R.id.bloodPressureDiastolic);
        exerciseTypeSpinner = view.findViewById(R.id.exerciseTypeSpinner);
        exerciseNumSpinner = view.findViewById(R.id.exerciseNumSpinner);
        exerciseDescTextView = view.findViewById(R.id.exerciseDescTextView);
        exerciseVideoLinkTextView = view.findViewById(R.id.exerciseVideoLinkTextView);
        exerciseHelpBtn = view.findViewById(R.id.exerciseHelpBtn);
        exerciseTypes = new ArrayList<ExerciseType>();
        exercises = new ArrayList<Exercise>();
        exerciseTypeSpinnerAdapter = new ArrayAdapter<ExerciseType>(getActivity(), R.layout.spinner_item_large, exerciseTypes);
        exerciseTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseNumSpinnerAdapter = new ArrayAdapter<Exercise>(getActivity(), R.layout.spinner_item_medium, exercises);
        exerciseNumSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseTypeSpinner.setAdapter(exerciseTypeSpinnerAdapter);
        exerciseNumSpinner.setAdapter(exerciseNumSpinnerAdapter);

        getExerciseTypes();

        heartRateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!heartRateEditText.getText().toString().trim().isEmpty()){
                    String heartRateInput = heartRateEditText.getText().toString().trim();
                    if(Integer.parseInt(heartRateInput) > 180){
                        heartRateEditText.setTextColor(Color.parseColor("#ff0000"));
                    } else if(Integer.parseInt(heartRateInput) < 180){
                        heartRateEditText.setTextColor(Color.parseColor("#000000"));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bloodPressureSystolic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!bloodPressureSystolic.getText().toString().trim().isEmpty()){
                    String bloodPressureSystolicInput = bloodPressureSystolic.getText().toString().trim();
                    if(Integer.parseInt(bloodPressureSystolicInput) > 120){
                        bloodPressureSystolic.setTextColor(Color.parseColor("#ff0000"));
                    } else if(Integer.parseInt(bloodPressureSystolicInput) < 120){
                        bloodPressureSystolic.setTextColor(Color.parseColor("#000000"));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bloodPressureDiastolic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!bloodPressureDiastolic.getText().toString().trim().isEmpty()){
                    String bloodPressureDiastolicInput = bloodPressureDiastolic.getText().toString().trim();
                    if(Integer.parseInt(bloodPressureDiastolicInput) > 80){
                        bloodPressureDiastolic.setTextColor(Color.parseColor("#ff0000"));
                    } else if(Integer.parseInt(bloodPressureDiastolicInput) < 80){
                        bloodPressureDiastolic.setTextColor(Color.parseColor("#000000"));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        exerciseTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ExerciseType exerciseType = (ExerciseType) adapterView.getSelectedItem();
                exercises.clear();
                exerciseNumSpinnerAdapter.notifyDataSetChanged();
                assignExerciseType(exerciseType);
                //getExercises();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        exerciseNumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Exercise exercise = (Exercise) adapterView.getSelectedItem();
                assignExerciseNum(exercise);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        exerciseHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openExerciseInfoFragment();
            }
        });

        startExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Please input and send your vital signs to your therapist before exercising", Toast.LENGTH_LONG).show();
                heartRateTitle.setVisibility(View.VISIBLE);
                bloodPressureTitle.setVisibility(View.VISIBLE);
                heartRateEditText.setVisibility(View.VISIBLE);
                bloodPressureSystolic.setVisibility(View.VISIBLE);
                bloodPressureDiastolic.setVisibility(View.VISIBLE);
                getVitalSigns.setVisibility(View.VISIBLE);
                sendVitalSigns.setVisibility(View.VISIBLE);
                //checkVitalSigns.setVisibility(View.VISIBLE);
                startExercise.setVisibility(View.INVISIBLE);
            }
        });

        heartRateEditText.setText(String.valueOf(BluetoothLeService.heartRate));


        getVitalSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });


        sendVitalSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVitalSigns();
            }
        });

        return view;
    }

    private void sendVitalSigns(){
        final String bloodPressure = bloodPressureSystolic.getText().toString() + "/" + bloodPressureDiastolic.getText().toString();
        final String heartRate = heartRateEditText.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VITALSIGNS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                if(Integer.parseInt(heartRate) > 180 || Integer.parseInt(bloodPressureSystolic.getText().toString().trim()) > 120 || Integer.parseInt(bloodPressureDiastolic.getText().toString().trim()) > 80){
                                    Toast.makeText(getActivity(), "Sent. Your heart rate and/or your blood pressure is too high. Please take a short rest, send your vital signs again, and start your exercise if your heart rate and/or your blood pressure improves", Toast.LENGTH_LONG).show();
                                } else{
                                    Toast.makeText(getActivity(), "Sent. You may start your exercise now.", Toast.LENGTH_LONG).show();
                                }
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
                params.put("blood_pressure", bloodPressure);
                params.put("heart_rate", heartRate);
                params.put("treatment_id", String.valueOf(LoginActivity.jsonTreatmentID));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void getExerciseTypes(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EXERCISETYPES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("getExerciseType");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    jsonExerciseType = object.getString("exercisetypename").trim();
                                    jsonExerciseTypeID = object.getInt("exercisetypeid");

                                    exerciseTypes.add(new ExerciseType(jsonExerciseTypeID, jsonExerciseType));

                                    exerciseTypeSpinnerAdapter.notifyDataSetChanged();
                                }

                            } else{
                                Toast.makeText(getActivity(), "No exercises are present", Toast.LENGTH_SHORT).show();
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
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void assignExerciseType(ExerciseType exerciseType){
        exerciseTypeID = exerciseType.getExerciseTypeID();
        //exerciseTypeName = exerciseType.getExerciseType();
        getExercises();
    }

    public void assignExerciseNum(Exercise exercise){
        exerciseDesc = exercise.getExerciseDesc();
        exerciseVidLink = exercise.getExerciseVidLink();
        exerciseDescTextView.setText(exerciseDesc);
        exerciseVideoLinkTextView.setText(exerciseVidLink);
    }

    public void getExercises(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EXERCISES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("getExercise");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    jsonExerciseDesc = object.getString("exercise_desc").trim();
                                    jsonExerciseVidLink = object.getString("exercise_vidlink").trim();
                                    jsonExerciseNum = object.getInt("exercise_num");

                                    exercises.add(new Exercise(jsonExerciseDesc, jsonExerciseVidLink, jsonExerciseNum));
                                    exerciseNumSpinnerAdapter.notifyDataSetChanged();

                                }

                            } else{
                                //Toast.makeText(getActivity(), "No exercises of this type are present", Toast.LENGTH_SHORT).show();
                                exerciseDescTextView.setText("No exercises of this type are available for you");
                                exerciseVideoLinkTextView.setText("https://www.youtube.com/yourvideolink");
                                exercises.clear();
                                exerciseNumSpinnerAdapter.notifyDataSetChanged();
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
                params.put("treatment_id", String.valueOf(LoginActivity.jsonTreatmentID));
                params.put("exercisetypeid", String.valueOf(exerciseTypeID));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void openExerciseInfoFragment(){
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ExerciseInfoFragment()).addToBackStack(null).commit();
    }

}

