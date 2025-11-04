package com.example.rop;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class OdometerActivity extends AppCompatActivity {

    // Deklarácie dátových položiek pre prvky v užívateľskom rozhraní
    private TextInputLayout dateInputLayout;
    private TextInputLayout kilometerInputLayout;
    private TextInputEditText dateEditText;
    private TextInputEditText kilometerEditText;
    private DatabaseHelper myDb;
    private ImageButton backButton;
    private ImageButton checkmarkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odometer);

        myDb = new DatabaseHelper(this);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        dateInputLayout = findViewById(R.id.DateInputLayout);
        kilometerInputLayout = findViewById(R.id.KilometerInputLayout);
        dateEditText = findViewById(R.id.date);
        kilometerEditText = findViewById(R.id.kilometer);
        backButton = findViewById(R.id.backButton);
        checkmarkButton = findViewById(R.id.CheckmarkButton);

        // Nastavenie listenera pre tlačidlo späť
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.openHome(OdometerActivity.this);
            }
        });

        // Nastavenie listenera pre textové pole dátumu
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zobrazenie dialógového okna pre výber dátumu
                DatePickerUtils.showDatePickerDialog(OdometerActivity.this, dateEditText);
            }
        });

        // Nastavenie listenera pre tlačidlo
        checkmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckmarkButtonClick();
            }
        });
    }

    // Metóda na spracovanie kliknutia na tlačidlo zaškrtnutia
    private void handleCheckmarkButtonClick() {
        // Načítanie údajov z preferencií
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        int selectedCarId = sharedPreferences.getInt("selectedCarId", -1);

        // Získanie ID používateľa z databázy
        int userId = myDb.getUserIdByUsername(username);
        String date = dateEditText.getText().toString();
        String kilometer = kilometerEditText.getText().toString();

        int errorCount = 0;

        // Validácia editTextov
        errorCount += Error.getError(
                new TextInputEditText[]{dateEditText, kilometerEditText},
                new TextInputLayout[]{dateInputLayout, kilometerInputLayout},
                OdometerActivity.this,
                R.string.empty_field
        );

        // Vloženie hodnôt do tabuľky odometer_table
        if (errorCount == 0) {
            // Spojenie hodnoty s jej jednotkou
            String totalKilometerWithSuffix = kilometer + " " + "km";

            // Vloženie údajov do databázy
            boolean isInserted = myDb.insertOdometerData(
                    userId,
                    selectedCarId,
                    date,
                    totalKilometerWithSuffix
            );

            if (isInserted) {
                // Aktualizácia hodnoty odometer v databáze
                boolean isUpdated = myDb.updateOdometerValue(userId, selectedCarId, kilometer);

                if (isUpdated) {
                    // Zobrazenie správy o úspešnom vložení
                    NavigationUtils.openHome(this);
                    Toast.makeText(OdometerActivity.this, R.string.odometer_inserted_successfully, Toast.LENGTH_SHORT).show();
                } else {
                    // Zobrazenie chybovej správy o aktualizácii hodnoty odometer
                    Toast.makeText(OdometerActivity.this, R.string.odometer_updating_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Zobrazenie chybovej správy o vložení údajov do tabuľky odometer_table
                Toast.makeText(OdometerActivity.this, R.string.odometer_inserted_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
