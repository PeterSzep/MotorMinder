package com.example.rop;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNotificationsActivity extends AppCompatActivity {

    // Deklarácie datovych poloziek pre prvky v užívateľskom rozhraní
    private Spinner newSpinner;
    private Spinner serviceTypeSpinner;
    private ImageButton backButton;
    private ImageButton checkmarkButton;
    private Switch recurringSwitch;
    private TextInputLayout deadlineInputLayout;
    private TextInputEditText descriptionEditText;
    private TextInputLayout descriptionInputLayout;
    private TextInputEditText locationEditText;
    private TextInputEditText deadlineEditText;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notifications);

        // Inicializácia objektu pre prácu s databázou
        myDb = new DatabaseHelper(this);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        deadlineEditText = findViewById(R.id.deadline);
        deadlineInputLayout = findViewById(R.id.deadlineInputLayout);
        descriptionInputLayout = findViewById(R.id.descriptionInputLayout);
        locationEditText = findViewById(R.id.location);
        descriptionEditText = findViewById(R.id.description);
        recurringSwitch = findViewById(R.id.recurringSwitch);
        serviceTypeSpinner = findViewById(R.id.serviceTypeSpinner);
        backButton = findViewById(R.id.backButton);
        checkmarkButton = findViewById(R.id.CheckmarkButton);
        newSpinner = new Spinner(this);

        newSpinner.setLayoutParams(deadlineInputLayout.getLayoutParams());

        // Nastaviť obmedzenia rozloženia pre newSpinner
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) newSpinner.getLayoutParams();
        params.startToStart = R.id.deadlineInputLayout;
        params.endToEnd = R.id.deadlineInputLayout;
        params.topToBottom = R.id.recurringSwitch;

        // Pridanie newSpinner do rozloženia
        ((ConstraintLayout) findViewById(R.id.constraintLayout)).addView(newSpinner, params);

        // Vytvorenie adaptéra pre zobrazenie možností upozornení v spinneri
        ArrayAdapter<CharSequence> serviceTypeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.notifications_options, // Definované možnosti upozornení, pole v subore string
                android.R.layout.simple_spinner_item
        );

        // Nastavenie layoutu pre zobrazenie možností upozornení v spinneri
        serviceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

         // Priradenie adaptéra k spinneru pre zobrazenie možností upozornení
        serviceTypeSpinner.setAdapter(serviceTypeAdapter);

        // Vytvorenie adaptéra pre zobrazenie možností opakovania v novom spinneri
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.recurring_options, // Definované možnosti opakovania v zdrojovom súbore (resources)
                android.R.layout.simple_spinner_item // Jednoduchý layout pre zobrazenie položiek vo výbere
        );

        // Nastavenie layoutu pre zobrazenie možností opakovania v novom spinneri
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Priradenie adaptéra k novému  spinneru pre zobrazenie možností opakovania
        newSpinner.setAdapter(adapter);

        // Aktualizácia viditeľnosti polí pre deadline v závislosti od stavu switchu
        updateDeadlineVisibility();

        // Nastavenie onClickListenera na switch, ktorý reaguje na zmenu stavu
        recurringSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateDeadlineVisibility();
            }
        });

        // Nastavenie onClickListenera pre tlačidlo späť, ktoré otvorí obrazovku s upozorneniami
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.openNotifications(AddNotificationsActivity.this);
            }
        });

        // Nastavenie onClickListenera pre tlačidlo začiarknutia (checkmark), ktoré vykoná akciu po kliknutí
        checkmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckmarkButtonClick(); // Zavolanie metódy na spracovanie kliknutia na tlačidlo začiarknutia
            }
        });

        // Nastavenie onClickListenera na kliknutie na textové pole pre deadline, ktoré otvorí dialógové okno s dátumovým výberom
        deadlineEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    //metoda, ktorá ukladá dáta do databázy ak podmienky su splnené
    public void handleCheckmarkButtonClick() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        int selectedCarId = sharedPreferences.getInt("selectedCarId", -1);

        int userId = myDb.getUserIdByUsername(username);
        String serviceType = serviceTypeSpinner.getSelectedItem().toString();
        String description = descriptionEditText.getText().toString();
        String location = locationEditText.getText().toString();

        // Získanie stavu prepínača
        boolean isRecurring = recurringSwitch.isChecked();

        // Nastavenie hodnôt pre opakujúce sa a deadline podľa stavu prepínača
        String recurring;
        String deadline;

        if (isRecurring) {
            recurring = newSpinner.getSelectedItem().toString();
            // Nastavenie deadline na aktuálny dátum
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = sdf.format(new Date());
            deadline = currentDate;
        } else {
            recurring = "";
            deadline = deadlineEditText.getText().toString();
        }

        int errorCount = 0;

        errorCount += Error.getError(
                new TextInputEditText[]{deadlineEditText, descriptionEditText},
                new TextInputLayout[]{deadlineInputLayout, descriptionInputLayout},
                this,
                R.string.empty_field
        );

        if (errorCount == 0) {
            Log.d("deadline", deadline);
            boolean isInserted = myDb.insertNotificationData(this, userId, selectedCarId, description, serviceType, location, recurring, deadline);

            // Vloženie údajov do databázy
            if (isInserted) {
                // Vloženie úspešné
                NavigationUtils.openNotifications(this);
                Toast.makeText(this, getText(R.string.notifcation_inserted_succesfully), Toast.LENGTH_SHORT).show();
            } else {
                // Vloženie zlyhalo
                Toast.makeText(this, R.string.failed_to_insert_notification, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateDeadlineVisibility() {
        if (recurringSwitch.isChecked()) {
            // Ak je switch zapnutý, skryje deadline a pridá Spinner
            deadlineInputLayout.setVisibility(View.GONE);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            deadlineEditText.setText(currentDate);

            // Overenie, či už newSpinner nie je pridaný pred pridaním
            if (newSpinner.getParent() == null) {
                ((ConstraintLayout) findViewById(R.id.constraintLayout)).addView(newSpinner);
            }
        } else {
            // Ak je switch vypnutý, zobrazí deadline a odstráni Spinner
            deadlineInputLayout.setVisibility(View.VISIBLE);

            // Overenie, či je newSpinner pridaný pred jeho odstránením
            if (newSpinner.getParent() != null) {
                ((ConstraintLayout) findViewById(R.id.constraintLayout)).removeView(newSpinner);
            }
        }
    }

    public void showDatePickerDialog() {
        // Získanie aktuálneho dátumu
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Vytvorenie dialogu pre výber dátumu
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Spracovanie zvoleného dátumu
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        deadlineEditText.setText(selectedDate);
                    }
                },
                year, month, day);

        // Nastavenie minimálneho dátumu na dnešný deň
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        // Zobrazenie dialogu pre výber dátumu
        datePickerDialog.show();
    }
}