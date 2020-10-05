package com.homer.telemed;

//CURRENTLY UNUSED CODE. TO BE DELETED

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;


public class ExerciseFragment extends Fragment{

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mSignInClient;
    TextView stepCount;
    Button stopExerciseBtn;
    String[] permissions;
    int currCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity.hideKeyboard(getActivity());
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);


        Button exerciseBtn = view.findViewById(R.id.exerciseBtn);
        stepCount = view.findViewById(R.id.stepCount);
        stopExerciseBtn = view.findViewById(R.id.stopExerciseBtn);
        Button testBle = view.findViewById(R.id.testBle);
        Button releaseBle = view.findViewById(R.id.releaseBle);
        permissions = new String[]{Manifest.permission.ACTIVITY_RECOGNITION};


        GoogleSignInOptions options =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Fitness.SCOPE_BODY_READ)
                        .build();

        mSignInClient = GoogleSignIn.getClient(getActivity(), options);
        signIn();


        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        FitnessOptions fitnessOptions1 = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .build();

        final GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(getActivity(), fitnessOptions1);

        final OnDataPointListener myStepCountListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (final Field field : dataPoint.getDataType().getFields()) {
                    final Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);
                    //Toast.makeText(getActivity(), field.getName() + " Step Count:" + val, Toast.LENGTH_LONG).show();
                    //int currValue = val.asInt();
                    //stepCounter(currValue);
                }
            }
        };

        BleScanCallback bleScanCallbacks = new BleScanCallback() {
            @Override
            public void onDeviceFound(BleDevice device) {
                // A device that provides the requested data types is available
                Fitness.getBleClient(getActivity(),
                        googleSignInAccount).claimBleDevice(device);
                Log.i(TAG, "Device found");
            }
            @Override
            public void onScanStopped() {
                // The scan timed out or was interrupted
                Log.i(TAG, "Scan timed out");
            }
        };

        Fitness.getBleClient(getActivity(), googleSignInAccount).startBleScan(Arrays.asList(DataType.TYPE_HEART_RATE_BPM),1000,bleScanCallbacks);


        testBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fitness.getSensorsClient(getActivity(), googleSignInAccount)
                        .findDataSources(
                                new DataSourcesRequest.Builder()
                                        .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                                        .setDataSourceTypes(DataSource.TYPE_RAW)
                                        .build())
                        .addOnSuccessListener(
                                new OnSuccessListener<List<DataSource>>() {
                                    @Override
                                    public void onSuccess(List<DataSource> dataSources) {
                                        for (DataSource dataSource : dataSources) {
                                            Log.i(TAG, "Data source found: " + dataSource.toString());
                                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                                            // Let's register a listener to receive Activity data!
                                            /*if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)
                                                    && mListener == null) {
                                                Log.i(TAG, "Data source for LOCATION_SAMPLE found!  Registering.");
                                                registerFitnessDataListener(dataSource, DataType.TYPE_LOCATION_SAMPLE);
                                            }*/
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "failed", e);
                                    }
                                });
            }
        });

        /*releaseBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fitness.getBleClient(getActivity(),
                        googleSignInAccount).unclaimBleDevice(device);
            }
        });*/





        exerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission is not granted, requesting permissions", Toast.LENGTH_SHORT).show();
                    requestPermissions(permissions, 1);
                } else{*/
                    //stepCounter(currValue);
                    //stepCount.setText("Step Count: " + currCount);
                    Fitness.getSensorsClient(getActivity(), googleSignInAccount)
                            .add(
                                    new SensorRequest.Builder()
                                            .setDataType(DataType.TYPE_HEART_RATE_BPM)
                                            .setSamplingRate(1, TimeUnit.SECONDS)  // sample once per second
                                            .build(),
                                    myStepCountListener
                            )
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i(TAG, "Listener registered!");
                                    } else {
                                        Log.e(TAG, "Listener not registered.", task.getException());
                                    }
                                }
                            });
                    Toast.makeText(getActivity(), "Exercise Started. Walk now and steps will be counted", Toast.LENGTH_LONG).show();
                //}

            }
        });

        stopExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stop listener
                  Fitness.getSensorsClient(getActivity(), googleSignInAccount)
                        .remove(myStepCountListener)
                        .addOnCompleteListener(
                                new OnCompleteListener<Boolean>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Boolean> task) {
                                        if (task.isSuccessful() && task.getResult()) {
                                            Log.i(TAG, "Listener was removed!");
                                        } else {
                                            Log.i(TAG, "Listener was not removed.");
                                        }
                                    }
                                });
                Toast.makeText(getActivity(), "Exercise Stopped", Toast.LENGTH_SHORT).show();
                currCount = 0;
                stepCount.setText("Step Count");
            }
        });

        return view;
    }

    private void signIn() {
        // Launches the sign in flow, the result is returned in onActivityResult
        Intent intent = mSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                // Sign in succeeded, proceed with account
                GoogleSignInAccount acct = task.getResult();
            } else {
                // Sign in failed, handle failure and update UI
                Toast.makeText(getActivity(), "Sign-In Failed. Please sign-in for features to work.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void stepCounter(int currValue){
        currCount = currCount + currValue;
        stepCount.setText("BPM " + currCount);
    }
}
