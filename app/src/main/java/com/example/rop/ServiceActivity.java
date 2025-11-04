package com.example.rop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ServiceActivity extends AppCompatActivity {

    // Deklarácie dátových položiek pre prvky v užívateľskom rozhraní
    private TextInputLayout dateInputLayout;
    private TextInputLayout locationInputLayout;
    private TextInputLayout costInputLayout;
    private TextInputEditText dateEditText;
    private TextInputEditText hourEditText;
    private TextInputEditText locationEditText;
    private TextInputEditText totalCostEditText;
    private ImageButton backButton;
    private ImageButton checkmarkButton;
    private Spinner serviceTypeSpinner;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        myDb = new DatabaseHelper(this);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        dateInputLayout = findViewById(R.id.DateInputLayout);
        locationInputLayout = findViewById(R.id.LocationInputLayout);
        costInputLayout = findViewById(R.id.CostInputLayout);
        dateEditText = findViewById(R.id.date);
        hourEditText = findViewById(R.id.hour);
        locationEditText = findViewById(R.id.location);
        totalCostEditText = findViewById(R.id.cost);
        backButton = findViewById(R.id.backButton);
        checkmarkButton = findViewById(R.id.CheckmarkButton);
        serviceTypeSpinner = findViewById(R.id.serviceTypeSpinner);

        // Nastavenie adaptéra pre Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.service_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceTypeSpinner.setAdapter(adapter);

        // Nastavenie poslucháčov pre tlačidlá
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Spustenie aktivity domovskej obrazovky
                NavigationUtils.openHome(ServiceActivity.this);
            }
        });

        checkmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Spracovanie kliknutia na tlačidlo zeleného začiarknutia
                handleCheckmarkButtonClick();
            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zobrazenie dialógového okna pre výber dátumu
                DatePickerUtils.showDatePickerDialog(ServiceActivity.this, dateEditText);
            }
        });

        hourEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zobrazenie dialógového okna pre výber času
                DatePickerUtils.showTimePickerDialog(ServiceActivity.this, hourEditText);
            }
        });
    }

    // Metóda pre spracovanie kliknutia na tlačidlo
    private void handleCheckmarkButtonClick() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Získanie používateľského mena z preferencií
        String username = sharedPreferences.getString("username", null);
        // Získanie carId z preferencií
        int selectedCarId = sharedPreferences.getInt("selectedCarId", -1);

        // Získanie ID používateľa z databázy na základe použivateľského mena
        int userId = myDb.getUserIdByUsername(username);

        // Získanie hodnôt z editTextov
        String date = dateEditText.getText().toString();
        String time = hourEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String totalCost = totalCostEditText.getText().toString();
        String selectedserviceType = serviceTypeSpinner.getSelectedItem().toString();

        int errorCount = 0;

        // Overenie, či nie sú prázdne editTexty
        errorCount += Error.getError(
                new TextInputEditText[]{dateEditText, locationEditText, totalCostEditText},
                new TextInputLayout[]{dateInputLayout, locationInputLayout, costInputLayout},
                ServiceActivity.this,
                R.string.empty_field
        );

        // Vloženie hodnôt do tabuľky service
        if (errorCount == 0) {
            String totalCostWithSuffix = totalCost + " " + "€";

            // Vloženie údajov do databázy
            boolean isInserted = myDb.insertServiceData(
                    userId,
                    selectedCarId,
                    date,
                    time,
                    location,
                    totalCostWithSuffix,
                    selectedserviceType
            );

            if (isInserted) {
                // Zobrazenie správy o úspešnom vložení
                NavigationUtils.openHome(this);
                Toast.makeText(ServiceActivity.this, R.string.service_inserted_successfully, Toast.LENGTH_SHORT).show();
            } else {
                // Zobrazenie správy o chybe pri vložení
                Toast.makeText(ServiceActivity.this, R.string.service_inserted_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}