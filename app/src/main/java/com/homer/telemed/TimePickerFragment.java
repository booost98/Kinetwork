package com.homer.telemed;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TimePickerFragment extends DialogFragment {

    TimePickerDialog.OnTimeSetListener onTimeSet;
    private int hour, minute;

    public void TimePickerFragment(){
        //..
    }

    public void setCallBack(TimePickerDialog.OnTimeSetListener ontime){
        this.onTimeSet = ontime;
    }

    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        hour = args.getInt("hour");
        minute = args.getInt("minute");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), onTimeSet, hour, minute, false);
    }
}
