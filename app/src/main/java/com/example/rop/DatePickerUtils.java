package com.example.rop;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import java.util.Calendar;

public class DatePickerUtils {

    // Metóda pre zobrazenie dialógu pre výber dátumu
    public static void showDatePickerDialog(Activity activity, EditText editText) {
        // Získanie aktuálneho dátumu a času
        Calendar currentDate = Calendar.getInstance();

        // Vytvorenie dialógu pre výber dátumu
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                activity,
                // OnDateSetListener reaguje na výber dátumu v dialógu
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Formátovanie vybraného dátumu do textového reťazca
                        String formattedMonth = String.format("%02d", monthOfYear + 1);
                        String selectedDate = dayOfMonth + "/" + formattedMonth + "/" + year;
                        // Nastavenie vybraného dátumu do EditTextu
                        editText.setText(selectedDate);
                    }
                },
                // Nastavenie aktuálneho dátumu ako predvoleného v dialógu
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );

        // Nastavenie maximálneho dátumu, ktorý môže byť vybraný (aktuálny dátum)
        datePickerDialog.getDatePicker().setMaxDate(currentDate.getTimeInMillis());

        // Zobrazenie dialógu pre výber dátumu
        datePickerDialog.show();
    }


    // Metóda pre zobrazenie dialógu pre výber času
    public static void showTimePickerDialog(Activity activity, EditText editText) {
        // Vytvorenie dialógu pre výber času
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                activity,
                // OnTimeSetListener reaguje na výber času v dialógu
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Formátovanie vybraného času do textového reťazca
                        String formattedMinute = String.format("%02d", minute);
                        String selectedTime = hourOfDay + ":" + formattedMinute;
                        // Nastavenie vybraného času do EditTextu
                        editText.setText(selectedTime);
                    }
                },
                // Nastavenie aktuálnej hodiny a minúty ako predvolených v dialógu
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true // True pre 24-hodinový formát
        );

        // Zobrazenie dialógu pre výber času
        timePickerDialog.show();
    }
}