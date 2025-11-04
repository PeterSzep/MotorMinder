package com.example.rop;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.Activity;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import android.content.SharedPreferences;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


// Definícia triedy AddPage, ktorá implementuje rozhranie AdapterView.OnItemSelectedListener
public class AddPage extends Activity implements AdapterView.OnItemSelectedListener {

    // Konštanty pre používané hodnoty
    private static final int PICK_IMAGE = 101;
    public static final int lowestYear = 1900;
    public static final float lowestOdometer = 0;
    public static final float highestOdometer = 1000000;


    // Deklarácie datovych položiek pre prvky v užívateľskom rozhraní
    private Uri selectedImageUri;
    private byte[] selectedImageData;
    private DatabaseHelper myDb;
    private Button galleryButton;
    private ImageButton backButton;
    private ImageButton addCarButton;
    private TextInputEditText carNameEditText;
    private TextInputLayout brandInputLayout;
    private TextInputLayout modelInputLayout;
    private TextInputLayout yearInputLayout;
    private TextInputLayout odometernputLayout;
    private TextInputEditText carBrandEditText;
    private TextInputEditText carModelEditText;
    private TextInputEditText carYearEditText;
    private TextInputEditText carOdometerEditText;
    private Spinner fuelTypeSpinner;
    private TextInputEditText carNotesText;
    private ImageView carPicture;
    private ImageButton editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_page);

        // Inicializácia objektu pre prácu s databázou
        myDb = new DatabaseHelper(this);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        galleryButton = findViewById(R.id.galleryButton);
        backButton = findViewById(R.id.BackToHomePageButton);
        addCarButton = findViewById(R.id.CheckmarkButton);
        carNameEditText = findViewById(R.id.CarName);
        carBrandEditText = findViewById(R.id.CarBrand);
        carModelEditText = findViewById(R.id.CarModel);
        carYearEditText = findViewById(R.id.CarYear);
        brandInputLayout = findViewById(R.id.CarBrandInputLayout);
        modelInputLayout = findViewById(R.id.CarModelInputLayout);
        yearInputLayout = findViewById(R.id.CarYearInputLayout);
        odometernputLayout = findViewById(R.id.CarOdometerInputLayout);
        carOdometerEditText = findViewById(R.id.CarOdometer);
        fuelTypeSpinner = findViewById(R.id.FuelSpinner);
        carNotesText = findViewById(R.id.CarNotes);
        carPicture = findViewById(R.id.CarPicture);
        editButton = findViewById(R.id.editButton);

        editButton.setVisibility(View.INVISIBLE); // Nastavenie editButton tlačidla ako neviditeľný

        // Nastavenie adaptéra pre zvolenie typu paliva
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.fuel_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fuelTypeSpinner.setAdapter(adapter);
        fuelTypeSpinner.setOnItemSelectedListener(this); // Nastavenie poslucháča udalostí pre zvolenie typu paliva
        AddData(); // Volanie metódy pre pridanie údajov

        // Nastavenie onClickListenera na tlacidlo, ktore vrati pouzivatela na domovsku stranku
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigationUtils.openHome(AddPage.this);
            }
        });

        // Nastavenie onClickListenera na tlacidlo, ktore otvori galeriu
        galleryButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                openGallery();
            }
        });

        // Nastavenie onClickListenera na tlacidlo, ktore otvori galeriu
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openGallery();
            }
        });

    }

    // Metóda, ktorá pridá dáta o aute do databázy
    public void AddData() {
        addCarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //získanie použivatelského mena z preferencií a získanie Id použivatela podla použivatelského meno
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");
                int userId = myDb.getUserIdByUsername(username);

                // Získanie dát z textových polí
                String carNameStr = carNameEditText.getText().toString();
                String carBrandStr = carBrandEditText.getText().toString();
                String carModelStr = carModelEditText.getText().toString();
                String carYearStr = carYearEditText.getText().toString();
                String carOdometerStr = carOdometerEditText.getText().toString();
                String selectedFuelType = fuelTypeSpinner.getSelectedItem().toString();

                // Premenná pre počítanie chýb pri validácii údajov
                int errorCount = 0;

                // Validácia dát pomocou triedy Error
                errorCount += Error.getError(
                        new TextInputEditText[]{carBrandEditText, carModelEditText, carYearEditText, carOdometerEditText},
                        new TextInputLayout[]{brandInputLayout, modelInputLayout, yearInputLayout, odometernputLayout},
                        AddPage.this,
                        R.string.empty_field
                );

                // Validácia roku výroby
                try {
                    int carYear = Integer.parseInt(carYearStr);
                    if (carYear < lowestYear || carYear > getCurrentYear()) {
                        yearInputLayout.setError(getString(R.string.year_must_be) + getCurrentYear());
                        errorCount++;
                    }
                } catch (Exception e) {
                }

                // Validácia stavu odometra
                try {
                    float carOdometer = Float.parseFloat(carOdometerStr);
                    if (carOdometer < lowestOdometer || carOdometer > highestOdometer) {
                        odometernputLayout.setError(getString(R.string.odometer_must_be));
                        errorCount++;
                    }
                } catch (Exception e) {
                }

                // Ak neexistujú žiadne chyby, pokračuj s pridávaním dát do databázy
                if (errorCount == 0) {
                    boolean isInserted = myDb.insertData(
                            carNameStr,
                            carBrandStr,
                            carModelStr,
                            carYearStr,
                            carOdometerStr,
                            carNotesText.getText().toString(),
                            userId,
                            selectedFuelType,
                            (selectedImageData != null) ? selectedImageData : null
                    );

                    // Vyhodnotenie výsledku pridávania údajov do databázy
                    if (isInserted) {
                        int newCarId = myDb.getLastInsertedCarId(userId);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("selectedCarId", newCarId);
                        editor.apply();

                        // Zobrazenie správy o úspešnom pridaní vozidla a návrat na hlavnú obrazovku
                        Toast.makeText(AddPage.this, getString(R.string.car_added_successfully), Toast.LENGTH_SHORT).show();
                        NavigationUtils.openHome(AddPage.this);

                    } else {
                        // Zobrazenie správy o neúspešnom pridaní vozidla
                        Toast.makeText(AddPage.this, getString(R.string.failed_to_add_car), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Metóda pre spracovanie výberu obrázka z galérie
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            try {
                // Konverzia obrázka na bitmapu a zmena jej veľkosti
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                int targetWidth = carPicture.getWidth();
                int targetHeight = carPicture.getHeight();
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);

                // Nastavenie zmenenej bitmapy do ImageView
                carPicture.setImageBitmap(resizedBitmap);
                galleryButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.VISIBLE);

                // Konverzia bitmapy na pole bytov a uloženie do selectedImageData
                selectedImageData = getByteArrayFromBitmap(resizedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Metóda pre získanie aktuálneho roka
    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    // Metóda pre prevod bitmapy na pole bytov
    public byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // Metóda pre otvorenie galérie
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE); // Spustenie aktivity pre výber obrázka
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}