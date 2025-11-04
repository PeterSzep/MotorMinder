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

public class ExpenseActivity extends AppCompatActivity {

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
    private Spinner expenseTypeSpinner;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        // Inicializácia objektu pre prácu s databázou
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
        expenseTypeSpinner = findViewById(R.id.expenseTypeSpinner);

        // Nastavenie adaptéra pre spinner s typmi nákladov
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.expense_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseTypeSpinner.setAdapter(adapter);

        // Nastavenie onClickListenera
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.openHome(ExpenseActivity.this);
            }
        });

        // Nastavenie onClickListenera
        checkmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckmarkButtonClick();
            }
        });

        // Nastavenie onClickListenera
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zobrazenie dialógu pre výber dátumu
                DatePickerUtils.showDatePickerDialog(ExpenseActivity.this, dateEditText);
            }
        });

        // Nastavenie onClickListenera
        hourEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zobrazenie dialógu pre výber času
                DatePickerUtils.showTimePickerDialog(ExpenseActivity.this, hourEditText);
            }
        });
    }

    // Metóda pre spracovanie kliknutia na tlačidlo checkmarck
    private void handleCheckmarkButtonClick() {
        // Získanie uloženého používateľského mena a ID vybraného auta z preferencií
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        int selectedCarId = sharedPreferences.getInt("selectedCarId", -1);

        // Získanie ID používateľa na základe používateľského mena
        int userId = myDb.getUserIdByUsername(username);

        //získanie textov z editTextov
        String date = dateEditText.getText().toString();
        String time = hourEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String totalCost = totalCostEditText.getText().toString();
        String selectedExpenseType = expenseTypeSpinner.getSelectedItem().toString();

        int errorCount = 0;

        // Kontrola prítomnosti chýb vo editTextoch
        errorCount += Error.getError(
                new TextInputEditText[]{dateEditText, locationEditText, totalCostEditText},
                new TextInputLayout[]{dateInputLayout, locationInputLayout, costInputLayout},
                ExpenseActivity.this,
                R.string.empty_field
        );

        // Vloženie hodnôt do databázy, ak neexistujú žiadne chyby vo editTextoch
        if (errorCount == 0) {

            String totalCostWithSuffix = totalCost + " " + "€";

            // Vloženie údajov do tabuľky expenses
            boolean isInserted = myDb.insertExpenseData(
                    userId,
                    selectedCarId,
                    date,
                    time,
                    location,
                    totalCostWithSuffix,
                    selectedExpenseType
            );

            // Zobrazenie správy o úspešnom vložení údajov alebo o chybe
            if (isInserted) {
                NavigationUtils.openHome(this);
                Toast.makeText(ExpenseActivity.this, getString(R.string.expense_data_inserted_successfully), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ExpenseActivity.this, getString(R.string.expense_data_inserted_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}