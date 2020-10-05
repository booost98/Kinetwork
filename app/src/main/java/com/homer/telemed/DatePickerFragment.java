package com.homer.telemed;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class DatePickerFragment extends DialogFragment {

    DatePickerDialog.OnDateSetListener ondateSet;
    private int year,month,day;

    public void DataPickerFragment(){
        //..
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        this.ondateSet = ondate;
    }

    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
    }
}
