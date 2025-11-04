package com.example.rop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    // Deklarácie dátových položiek pre prvky v užívateľskom rozhraní
    private TextInputLayout firstNameInputLayout;
    private TextInputLayout lastNameInputLayout;
    private TextInputLayout userNameInputLayout;
    private TextInputLayout passwordInputLayout;
    private EditText firstnameEditText;
    private EditText lastnameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private DatabaseHelper myDb;
    private CheckBox rememberMeCheckBox;

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        backButton = findViewById(R.id.backButton);
        firstnameEditText = findViewById(R.id.firstNameEditText);
        firstNameInputLayout = findViewById(R.id.firstNameInputLayout);
        lastNameInputLayout = findViewById(R.id.lastNameInputLayout);
        userNameInputLayout = findViewById(R.id.usernameInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        lastnameEditText = findViewById(R.id.lastNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        myDb = new DatabaseHelper(this);
        addRegistrationListener();

        // Nastavenie listenera pre tlačidlo späť
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.openLogin(RegisterActivity.this);
            }
        });
    }

    private void addRegistrationListener() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Získanie hodnôt z editTextov
                String firstname = firstnameEditText.getText().toString();
                String lastname = lastnameEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Automatické nastavenie veľkých písmen pre prvý znak v mene a priezvisku
                firstname = capitalizeFirstLetter(firstname);
                lastname = capitalizeFirstLetter(lastname);

                // Validácia vstupných hodnôt
                if (isValid(username) && isValid(password) && isNotEmpty(firstname) && isNotEmpty(lastname)
                        && !containsSpecialCharactersOrNumbers(firstname) && !containsSpecialCharactersOrNumbers(lastname)) {
                    if (myDb.isUsernameUnique(username)) {
                        if (myDb.insertUserData(firstname, lastname, username, password, RegisterActivity.this)) {

                            // Uloženie údajov používateľa do zdieľaných preferencií
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            // Kontrola, či je zvolená možnosť "Pamätať si ma" počas registrácie
                            if (rememberMeCheckBox.isChecked()) {
                                editor.putString("savedUsername", username);
                                editor.putString("savedPassword", password);
                                editor.putBoolean("rememberMeChecked", true);
                            }

                            // Uloženie aktuálne prihláseného používateľa
                            editor.putString("username", username);
                            editor.apply();

                            // Zobrazenie správy o úspešnej registrácii a presmerovanie na domovskú obrazovku
                            Toast.makeText(RegisterActivity.this, R.string.account_created_successfully, Toast.LENGTH_SHORT).show();
                            NavigationUtils.openHome(RegisterActivity.this);
                            finish();
                        } else {
                            // Zobrazenie chybovej správy v prípade, že registrácia zlyhala
                            Toast.makeText(RegisterActivity.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Zobrazenie chybovej správy, ak je zadané užívateľské meno už použité
                        userNameInputLayout.setError(getString(R.string.username_is_already_taken));
                    }
                } else {
                    // Validácia a zobrazenie chybových správ pre prípad neplatných alebo prázdnych vstupov
                    if (!isNotEmpty(firstname)) {
                        firstNameInputLayout.setError(getString(R.string.empty_field));
                    }

                    if (!isNotEmpty(lastname)) {
                        lastNameInputLayout.setError(getString(R.string.empty_field));
                    }

                    if (!isValid(username)) {
                        userNameInputLayout.setError(getString(R.string.invalid_username_length));
                    }

                    if (!isValid(password)) {
                        passwordInputLayout.setError(getString(R.string.invalid_password_length));
                    }

                    if (containsSpecialCharactersOrNumbers(firstname)) {
                        firstNameInputLayout.setError(getString(R.string.invalid_first_name));
                    }

                    if (containsSpecialCharactersOrNumbers(lastname)) {
                        lastNameInputLayout.setError(getString(R.string.invalid_lastname_name));
                    }
                }
            }
        });
    }

    // Metóda na kontrolu, či reťazec obsahuje špeciálne znaky alebo čísla
    public boolean containsSpecialCharactersOrNumbers(String str) {
        return !str.matches("[\\p{L}]+");
    }

    // Metóda na automatické nastavenie veľkých písmen pre prvý znak
    private String capitalizeFirstLetter(String str) {
        if (str.isEmpty()) {
            return str;
        }
        char firstChar = str.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return Character.toUpperCase(firstChar) + str.substring(1);
        }
        return str;
    }



    // Metóda na kontrolu platnosti hodnôt
    public boolean isValid(String value) {
        return value.length() >= 8;
    }

    // Metóda na kontrolu, či je hodnota prázdna
    public boolean isNotEmpty(String value){
        return value.length() != 0;
    }
}