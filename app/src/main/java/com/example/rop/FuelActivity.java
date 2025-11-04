package com.example.rop;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class FuelActivity extends AppCompatActivity {

    // Deklarácie dátových položiek pre prvky v užívateľskom rozhraní
    private TextInputLayout dateInputLayout;
    private TextInputLayout amountInputLayout;
    private TextInputLayout priceInputLayout;
    private TextInputEditText dateEditText;
    private TextInputEditText hourEditText;
    private TextInputEditText locationEditText;
    private TextInputEditText amountEditText;
    private TextInputEditText priceEditText;
    private TextInputEditText totalCostEditText;
    private ImageButton backButton;
    private ImageButton checkmarkButton;
    private Spinner fuelTypeSpinner;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel);

        // Inicializácia objektu pre prácu s databázou
        myDb = new DatabaseHelper(this);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        dateInputLayout = findViewById(R.id.DateInputLayout);
        amountInputLayout = findViewById(R.id.AmountInputLayout);
        priceInputLayout = findViewById(R.id.PriceInputLayout);
        dateEditText = findViewById(R.id.date);
        hourEditText = findViewById(R.id.hour);
        locationEditText = findViewById(R.id.location);
        amountEditText = findViewById(R.id.amount);
        priceEditText = findViewById(R.id.price);
        totalCostEditText = findViewById(R.id.total);
        backButton = findViewById(R.id.backButton);
        checkmarkButton = findViewById(R.id.CheckmarkButton);
        fuelTypeSpinner = findViewById(R.id.fuelTypeSpinner);

        // Nastavenie onClickListenera na editText
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerUtils.showDatePickerDialog(FuelActivity.this, dateEditText);
            }
        });

        // Nastavenie onClickListenera na editText
        hourEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerUtils.showTimePickerDialog(FuelActivity.this, hourEditText);
            }
        });

        // Nastavenie onClickListenera na button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.openHome(FuelActivity.this);
            }
        });

        // Pridanie TextWatcher na amountEditText
        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateTotalCost();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Pridanie TextWatcher na priceEditText
        priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateTotalCost();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Nastavenie onClickListenera na button
        checkmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckmarkButtonClick();
            }
        });

        // Inicializácia používateľa a typu paliva
        initializeUserAndFuelType();
    }

    // Metóda na získanie typu paliva z databázy
    private String getFuelTypeFromDatabase(int userId, int carId) {
        Cursor cursor = myDb.getCarDataByUserIdAndCarId(userId, carId);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String selectedFuelType = cursor.getString(cursor.getColumnIndex("FUEL"));
            cursor.close();
            return selectedFuelType;
        } else {
            return null;
        }
    }

    // Metóda na nastavenie spinnera s typmi paliva
    private void setupFuelTypeSpinner(String selectedFuelType) {
        // Rozdelenie vybraného typu paliva na jednotlivé typy paliva
        String[] individualFuelTypes = selectedFuelType.split("/");

        // Vytvorenie ArrayAdapteru s jednotlivými typmi paliva
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                individualFuelTypes
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fuelTypeSpinner.setAdapter(adapter);

        // Nastavenie OnItemSelectedListener pre spinner
        fuelTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String suffix;
            String priceFormat;

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Aktualizácia prípony a formátu ceny na základe vybranej položky
                String selectedSpinnerItem = individualFuelTypes[position].trim();
                if (selectedSpinnerItem.equalsIgnoreCase(getString(R.string.diesel)) || selectedSpinnerItem.equalsIgnoreCase(getString(R.string.gasoline))) {
                    suffix = "L";
                    priceFormat = "€/L";
                } else if (selectedSpinnerItem.equalsIgnoreCase(getString(R.string.electric))) {
                    suffix = "kWh";
                    priceFormat = "€/kWh";
                } else if (selectedSpinnerItem.equalsIgnoreCase(getString(R.string.lpg))) {
                    suffix = "kg";
                    priceFormat = "€/kg";
                } else {
                    // Predvolené hodnoty, ak typ paliva nie je rozpoznaný
                    suffix = "";
                    priceFormat = "";
                }
                // Volanie metódy alebo aktualizácia ovládacích prvkov použivateľského rozhrania na základe novej prípony a formátu ceny
                updateUIBasedOnSuffixAndPriceFormat(suffix, priceFormat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Nič nerobí
            }
        });
    }

    // Metóda na výpočet celkových nákladov
    private void calculateTotalCost() {
        String amount = amountEditText.getText().toString();
        String price = priceEditText.getText().toString();

        try {
            double amountValue = Double.parseDouble(amount);
            double priceValue = Double.parseDouble(price);
            double totalCostValue = amountValue * priceValue;
            totalCostEditText.setText(String.valueOf(totalCostValue));
        } catch (NumberFormatException e) {
            // Spracovanie prípadu, ak vstup nie je platné číslo
            totalCostEditText.setText("");
        }
    }

    // Metóda pre spracovanie kliknutia na tlačidlo potvrdenia
    private void handleCheckmarkButtonClick() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        int selectedCarId = sharedPreferences.getInt("selectedCarId", -1);

        int userId = myDb.getUserIdByUsername(username);

        //získanie textu z editTextov
        String date = dateEditText.getText().toString();
        String time = hourEditText.getText().toString();
        String amount = amountEditText.getText().toString();
        String price = priceEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String totalCost = totalCostEditText.getText().toString();

        // Získanie prípony textu
        String amountSuffix = amountInputLayout.getSuffixText().toString();
        String priceSuffix = priceInputLayout.getSuffixText().toString();

        int errorCount = 0;

        // Kontrola prítomnosti chýb v editTextoch
        errorCount += Error.getError(
                new TextInputEditText[]{dateEditText, amountEditText, priceEditText},
                new TextInputLayout[]{dateInputLayout, amountInputLayout, priceInputLayout},
                FuelActivity.this,
                R.string.empty_field
        );

        // Vloženie hodnôt do tabuľky refuel
        if (errorCount == 0) {
            String amountWithSuffix = amount + " " + amountSuffix;

            String priceWithSuffix = price + " " + priceSuffix;

            String totalCostWithSuffix = totalCost + " " + "€";

            boolean isInserted = myDb.insertRefuelData(
                    userId,
                    selectedCarId,
                    date,
                    time,
                    location,
                    amountWithSuffix,
                    priceWithSuffix,
                    totalCostWithSuffix
            );

            if (isInserted) {
                // Zobrazenie správy o úspešnom vložení údajov
                NavigationUtils.openHome(this);
                Toast.makeText(FuelActivity.this, getString(R.string.refuel_data_inserted_successfully), Toast.LENGTH_SHORT).show();
            } else {
                // Zobrazenie správy o chybe
                Toast.makeText(FuelActivity.this, getString(R.string.refuel_data_inserted_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Metóda na inicializáciu používateľa a typu paliva
    private void initializeUserAndFuelType() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        int userId = -1;

        if (username != null) {
            userId = myDb.getUserIdByUsername(username);
            int carId = sharedPreferences.getInt("selectedCarId", -1);
            if (userId != -1) {
                String selectedFuelType = getFuelTypeFromDatabase(userId, carId);
                if (selectedFuelType != null) {
                    updateUIForFuelType(selectedFuelType);
                }
            }
        }
    }

    // Metóda na aktualizáciu použivateľského rozhrania pre typ paliva
    private void updateUIForFuelType(String selectedFuelType) {
        String suffix;
        String priceFormat;

        if (selectedFuelType != null && selectedFuelType.contains("/")) {
            // Zobraziť spinner
            setupFuelTypeSpinner(selectedFuelType);
            fuelTypeSpinner.setVisibility(View.VISIBLE);
        } else {
            // Skryť spinner
            fuelTypeSpinner.setVisibility(View.GONE);

            // Aktualizácia prípony a formátu ceny pre  typ paliva
            if (selectedFuelType.equalsIgnoreCase(getString(R.string.diesel)) || selectedFuelType.equalsIgnoreCase(getString(R.string.gasoline))) {
                suffix = "L";
                priceFormat = "€/L";
            } else if (selectedFuelType.equalsIgnoreCase(getString(R.string.electric))) {
                suffix = "kWh";
                priceFormat = "€/kWh";
            } else if (selectedFuelType.equalsIgnoreCase(getString(R.string.lpg))) {
                suffix = "kg";
                priceFormat = "€/kg";
            } else {
                suffix = "L";
                priceFormat = "€/L";
            }

            // Volanie metódy na aktualizáciu  použivateľského rozhrania  na základe typu paliva
            updateUIBasedOnSuffixAndPriceFormat(suffix, priceFormat);
        }
    }

    // Metóda na aktualizáciu použivateľského rozhrania na základe prípony
    private void updateUIBasedOnSuffixAndPriceFormat(String suffix, String priceFormat) {
        if (suffix != null && priceFormat != null) {
            // Aktualizácia prípony textu pre oba TextInputLayouty
            amountInputLayout.setSuffixText(suffix);
            priceInputLayout.setSuffixText(priceFormat);
        }
    }

}