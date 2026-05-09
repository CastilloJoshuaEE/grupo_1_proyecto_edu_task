package com.grupo1.edutask;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    public interface OnDateSelectedListener {
        void onDateSelected(int year, int month, int day);
    }

    private OnDateSelectedListener listener;

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    if (listener != null) {
                        listener.onDateSelected(selectedYear, selectedMonth, selectedDay);
                    }
                },
                year, month, day);
    }
}