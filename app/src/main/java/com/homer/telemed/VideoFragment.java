package com.homer.telemed;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    Button getVitalSigns, sendVitalSigns;
    EditText heartRateEditText, bloodPressureSystolic, bloodPressureDiastolic;
    Intent intent;
    Spinner exerciseTypeSpinner, exerciseNumSpinner;
    TextView exerciseDescTextView, exerciseVideoLinkTextView;
    private List<ExerciseType> exerciseTypes;
    private List<Exercise> exercises;
    private ArrayAdapter<ExerciseType> exerciseTypeSpinnerAdapter;
    private ArrayAdapter<Exercise> exerciseNumSpinnerAdapter;
    public int exerciseTypeID;
    public String exerciseDesc, exerciseVidLink;
    private static String URL_VITALSIGNS = "http://192.168.50.173:80/kinetwork/vitalsigns.php";
    private static String URL_EXERCISETYPES = "http://192.168.50.173:80/kinetwork/exercisetype.php";
    private static String URL_EXERCISES = "http://192.168.50.173:80/kinetwork/exercise.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        MainActivity.hideKeyboard(getActivity());

        intent = new Intent(getActivity(), DeviceScanActivity.class);
        getVitalSigns = view.findViewById(R.id.getVitalSigns);
        sendVitalSigns = view.findViewById(R.id.sendVitalSigns);
        heartRateEditText = view.findViewById(R.id.heartRateEditText);
        bloodPressureSystolic = view.findViewById(R.id.bloodPressureSystolic);
        bloodPressureDiastolic = view.findViewById(R.id.bloodPressureDiastolic);
        exerciseTypeSpinner = view.findViewById(R.id.exerciseTypeSpinner);
        exerciseNumSpinner = view.findViewById(R.id.exerciseNumSpinner);
        exerciseDescTextView = view.findViewById(R.id.exerciseDescTextView);
        exerciseVideoLinkTextView = view.findViewById(R.id.exerciseVideoLinkTextView);
        exerciseTypes = new ArrayList<ExerciseType>();
        exercises = new ArrayList<Exercise>();
        exerciseTypeSpinnerAdapter = new ArrayAdapter<ExerciseType>(getActivity(), R.layout.spinner_item_large, exerciseTypes);
        exerciseTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseNumSpinnerAdapter = new ArrayAdapter<Exercise>(getActivity(), R.layout.spinner_item_medium, exercises);
        exerciseNumSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseTypeSpinner.setAdapter(exerciseTypeSpinnerAdapter);
        exerciseNumSpinner.setAdapter(exerciseNumSpinnerAdapter);

        getExerciseTypes();

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



        getVitalSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
        heartRateEditText.setText(String.valueOf(BluetoothLeService.heartRate));

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
                                Toast.makeText(getActivity(), "Vital signs sent to database", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bloodpressure", bloodPressure);
                params.put("heartrate", heartRate);
                params.put("id", String.valueOf(LoginActivity.jsonID));
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
                                    jsonExerciseTypeID = object.getInt("exercisetypeid"); //could be wrong, needs to be assigned onchoose @ spinner

                                    exerciseTypes.add(new ExerciseType(jsonExerciseTypeID, jsonExerciseType));

                                    exerciseTypeSpinnerAdapter.notifyDataSetChanged();
                                }

                            } else{
                                Toast.makeText(getActivity(), "Your exercises cannot be fetched", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error!" + error.toString(), Toast.LENGTH_SHORT).show();
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
                                    jsonExerciseDesc = object.getString("exercisedesc").trim();
                                    jsonExerciseVidLink = object.getString("exercisevidlink").trim();
                                    jsonExerciseNum = object.getInt("exercisenum");

                                    exercises.add(new Exercise(jsonExerciseDesc, jsonExerciseVidLink, jsonExerciseNum));
                                    exerciseNumSpinnerAdapter.notifyDataSetChanged();

                                }

                            } else{
                                Toast.makeText(getActivity(), "No exercises of this type are present", Toast.LENGTH_SHORT).show();
                                exerciseDescTextView.setText("Exercise Description");
                                exerciseVideoLinkTextView.setText("https://www.youtube.com/yourvideolink");
                                exercises.clear();
                                exerciseNumSpinnerAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error!" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(LoginActivity.jsonID));
                params.put("exercisetypeid", String.valueOf(exerciseTypeID));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

}

