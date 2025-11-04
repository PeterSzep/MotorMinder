package com.example.rop;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class MyCar extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int PICK_IMAGE = 101;

    // Deklarácie dátových položiek pre prvky v užívateľskom rozhraní
    private ImageButton confirmButton;
    private ImageButton backButton;
    private ImageButton deleteButton;
    private ImageButton editButton;
    private TextInputEditText carNameEditText;
    private TextInputEditText carBrandEditText;
    private TextInputEditText carModelEditText;
    private TextInputEditText carYearEditText;
    private TextInputEditText carOdometerEditText;
    private TextView fuelType;
    private EditText notesEditText;
    private Spinner fuelSpinner;
    private TextInputLayout brandInputLayout;
    private TextInputLayout modelInputLayout;
    private TextInputLayout yearInputLayout;
    private TextInputLayout odometernputLayout;
    private DatabaseHelper myDb;
    private ImageView carPicture;
    private ImageButton galleryButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);
        myDb = new DatabaseHelper(this);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        confirmButton = findViewById(R.id.confirmButton);
        backButton = findViewById(R.id.BackToHomePageButton);
        editButton = findViewById(R.id.EditButton);
        deleteButton = findViewById(R.id.deleteButton);
        galleryButton = findViewById(R.id.galleryButton);
        carNameEditText = findViewById(R.id.CarName);
        carBrandEditText = findViewById(R.id.CarBrand);
        carModelEditText = findViewById(R.id.CarModel);
        carYearEditText = findViewById(R.id.CarYear);
        carOdometerEditText = findViewById(R.id.CarOdometer);
        notesEditText = findViewById(R.id.CarNotes);
        brandInputLayout = findViewById(R.id.CarBrandInputLayout);
        modelInputLayout = findViewById(R.id.CarModelInputLayout);
        yearInputLayout = findViewById(R.id.CarYearInputLayout);
        odometernputLayout = findViewById(R.id.CarOdometerInputLayout);
        carOdometerEditText = findViewById(R.id.CarOdometer);
        fuelSpinner = findViewById(R.id.FuelTextview);
        carPicture = findViewById(R.id.CarPicture);
        fuelType = findViewById(R.id.ConsumptionText);

        confirmButton.setVisibility(View.INVISIBLE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.fuel_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fuelSpinner.setAdapter(adapter);
        fuelSpinner.setOnItemSelectedListener(this);
        fuelSpinner.setEnabled(false);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        int carId = sharedPreferences.getInt("selectedCarId", -1);




        if (username != null) {
            int userId = myDb.getUserIdByUsername(username);
            if (userId != -1) {
                galleryButton.setVisibility(View.INVISIBLE);
                displayCarInfoForUser(userId, carId);
            }
        }

        // Nastavenie onClickListenera na button
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editInfo();
            }
        });

        // Nastavenie onClickListenera na button
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NavigationUtils.openHome(MyCar.this);

            }
        });

        // Nastavenie onClickListenera na button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateCarData();
            }
        });

        // Nastavenie onClickListenera na button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", null);
                int carId = sharedPreferences.getInt("selectedCarId", -1);

                if (username != null) {
                    int userId = myDb.getUserIdByUsername(username);
                    if (userId != -1) {
                        showDeleteDialog(userId, carId);
                    }
                }
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                openGallery();
            }
        });


    }

    // Metóda pre zobrazenie informácií o aute pre používateľa
    public void displayCarInfoForUser(int userId, int carId) {
        Cursor cursor = myDb.getCarDataByUserIdAndCarId(userId, carId);

        if (cursor != null && cursor.moveToFirst()) {
            // Získa údaje o aute z databázy
            @SuppressLint("Range") String carName = cursor.getString(cursor.getColumnIndex("NAME"));
            @SuppressLint("Range") String carBrand = cursor.getString(cursor.getColumnIndex("BRAND"));
            @SuppressLint("Range") String carModel = cursor.getString(cursor.getColumnIndex("MODEL"));
            @SuppressLint("Range") int carYear = cursor.getInt(cursor.getColumnIndex("YEAR"));
            @SuppressLint("Range") float carOdometer = cursor.getFloat(cursor.getColumnIndex("ODOMETER"));
            @SuppressLint("Range") String notes = cursor.getString(cursor.getColumnIndex("NOTES"));
            @SuppressLint("Range") String selectedFuelType = cursor.getString(cursor.getColumnIndex("FUEL"));
            @SuppressLint("Range") byte[] imageData = cursor.getBlob(cursor.getColumnIndex("IMAGE"));

            // Nastaví získané údaje do príslušných polí a komponentov
            carNameEditText.setText(carName);
            carBrandEditText.setText(carBrand);
            carModelEditText.setText(carModel);
            carYearEditText.setText("" + carYear);
            carOdometerEditText.setText("" + carOdometer);
            notesEditText.setText(notes);
            String[] fuelTypes = getResources().getStringArray(R.array.fuel_types);

            int selectedFuelPosition = Arrays.asList(fuelTypes).indexOf(selectedFuelType);

            fuelSpinner.setSelection(selectedFuelPosition);

            //keď imageData neni null nastaví obrázok do imageView
            if (imageData != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                carPicture.setImageBitmap(imageBitmap);
                galleryButton.setVisibility(View.INVISIBLE);
            } else {
                //ak je imagedata null tak sa nastaví defaultný obrázok
                galleryButton.setVisibility(View.INVISIBLE);
                carPicture.setImageResource(R.drawable.default_car);


                carPicture.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        int targetWidth = carPicture.getWidth();
                        int targetHeight = carPicture.getHeight();

                        BitmapDrawable drawable = (BitmapDrawable) carPicture.getDrawable();
                        Bitmap defaultImage = drawable.getBitmap();
                        if (targetWidth > 0 && targetHeight > 0) {
                            Bitmap resizedDefaultImage = Bitmap.createScaledBitmap(defaultImage, targetWidth, targetHeight, false);
                            carPicture.setImageBitmap(resizedDefaultImage);
                            carPicture.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });

                galleryButton.setVisibility(View.VISIBLE);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    // Metóda pre úpravu informácií o aute
    //zmení komponenty
    public void editInfo(){
        galleryButton.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);
        carNameEditText.setEnabled(true);
        carNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        carBrandEditText.setEnabled(true);
        carBrandEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        carModelEditText.setEnabled(true);
        carModelEditText.setInputType(InputType.TYPE_CLASS_TEXT);;
        carYearEditText.setEnabled(true);
        carYearEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        carOdometerEditText.setEnabled(true);
        carOdometerEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        fuelSpinner.setEnabled(true);
        notesEditText.setEnabled(true);
        fuelType.setTextColor(Color.BLACK);
    }


    // Metóda pre otvorenie galérie pre výber obrázka auta
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Spracovanie výsledku z aktivity galérie
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                int targetWidth = carPicture.getWidth();
                int targetHeight = carPicture.getHeight();

                // Načítanie obrázka z galérie
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                // Zmenšenie obrázka na požadovanú veľkosť
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);

                carPicture.setImageBitmap(resizedBitmap); // Nastavenie zmenšeného obrázka do ImageView

            } catch (IOException e) {
                e.printStackTrace();
                // V prípade chyby pri načítaní obrázka zobrazí sa upozornenie
                Toast.makeText(this, getString(R.string.failed_to_load_image), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Metóda pre zobrazenie dialogu pre potvrdenie zmazania auta
    private void showDeleteDialog(final int userId, final  int carId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.are_you_sure_delete_car)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Volanie metódy pre zmazanie údajov o aute z databázy
                        boolean deleted = myDb.deleteCarDataByUserId(userId, carId);
                        NavigationUtils.openHome(MyCar.this); // Návrat na domovskú obrazovku
                        if (deleted) {
                            // Zobrazenie správy o úspešnom zmazaní auta
                            Toast.makeText(MyCar.this, getString(R.string.car_deleted_successfully), Toast.LENGTH_SHORT).show();
                        } else {
                            // Zobrazenie správy o neúspešnom zmazaní auta
                            Toast.makeText(MyCar.this, getString(R.string.failed_to_delete_car), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss(); // Zavretie dialogu bez zmazania auta
                    }
                });

        builder.create().show(); // Zobrazenie dialogu
    }

    // Metóda pre aktualizáciu údajov o aute
    public void updateCarData(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        int carId = sharedPreferences.getInt("selectedCarId", -1);

        if (username != null) {
            int userId = myDb.getUserIdByUsername(username);
            if (userId != -1) {
                // Získanie dát o aute z editTextov
                String carName = carNameEditText.getText().toString();
                String carBrand = carBrandEditText.getText().toString();
                String carModel = carModelEditText.getText().toString();
                String carYearStr = carYearEditText.getText().toString();
                String carOdometerStr = carOdometerEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                String fuelType = fuelSpinner.getSelectedItem().toString();

                int errorCount = 0;

                // Validácia dát
                errorCount += Error.getError(
                        new TextInputEditText[]{carBrandEditText, carModelEditText, carYearEditText, carOdometerEditText},
                        new TextInputLayout[]{brandInputLayout, modelInputLayout, yearInputLayout, odometernputLayout},
                        MyCar.this,
                        R.string.empty_field
                );

                try {
                    int carYear = Integer.parseInt(carYearStr);
                    // Kontrola platnosti roku výroby auta
                    if (carYear < AddPage.lowestYear || carYear > AddPage.getCurrentYear()) {
                        yearInputLayout.setError(getString(R.string.year_must_be) + AddPage.getCurrentYear());
                        errorCount++;
                    }
                } catch (Exception e) {
                }

                try {
                    float carOdometer = Float.parseFloat(carOdometerStr);
                    // Kontrola platnosti stavu odometra auta
                    if (carOdometer < AddPage.lowestOdometer || carOdometer > AddPage.highestOdometer) {
                        odometernputLayout.setError(getString(R.string.odometer_must_be));
                        errorCount++;
                    }
                } catch (Exception e) {
                }

                BitmapDrawable drawable = (BitmapDrawable) carPicture.getDrawable();
                Bitmap newImage = drawable.getBitmap();
                byte[] newImageData = getByteArrayFromBitmap(newImage);

                boolean updated = false;

                // Aktualizácia údajov o aute v databáze, ak neexistujú žiadne chyby
                if (errorCount == 0) {
                    updated = myDb.updateCarDataByUserId(userId, carId, carName, carBrand, carModel, carYearStr, carOdometerStr, notes, fuelType, newImageData);
                }
                if (updated) {
                    // Zobrazenie správy o úspešnej aktualizácii údajov o aute
                    Toast.makeText(MyCar.this, R.string.car_upadted_successfully, Toast.LENGTH_SHORT).show();
                    NavigationUtils.openHome(MyCar.this); // Návrat na domovskú obrazovku
                } else {
                    // Zobrazenie správy o neúspešnej aktualizácii údajov o aute
                    Toast.makeText(MyCar.this, R.string.car_updated_error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Metóda pre získanie byte array z bitmapy
    private byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        // Prevod bitmapy na byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
