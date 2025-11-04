package com.example.rop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // Deklarácie dátových položiek pre prvky v užívateľskom rozhraní
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private CheckBox rememberMeCheckBox;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        myDb = new DatabaseHelper(this);

        // Ak je používateľ prihlásený, prejde na domovskú aktivitu; inak pridá listener pre prihlásenie a registráciu
        if (isUserLoggedIn()) {
            NavigationUtils.openHome(this);
        } else {
            addLoginListener();
            addRegistrationListener();
        }
    }

    // Metóda pre pridanie listenera pre tlačidlo prihlásenia
    private void addLoginListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Overenie, či sú zadané platné údaje
                if (isValid(username) && isValid(password)) {
                    if (myDb.loginUser(username, password)) {
                        // Uloženie prihlasovacích údajov, ak je začiarknuté políčko "Pamätať si ma"
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (rememberMeCheckBox.isChecked()) {
                            editor.putString("savedUsername", username);
                            editor.putString("savedPassword", password);
                            editor.putBoolean("rememberMeChecked", true);
                        } else {
                            editor.remove("savedUsername");
                            editor.remove("savedPassword");
                            editor.putBoolean("rememberMeChecked", false);
                        }

                        editor.putString("username", username);
                        editor.apply();

                        // Prechod na domovskú aktivitu
                        NavigationUtils.openHome(LoginActivity.this);

                    } else {
                        // Upozornenie na neplatné prihlasovacie údaje
                        Toast.makeText(LoginActivity.this, getString(R.string.invalid_username_password), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Upozornenie na neplatnú dĺžku používateľského mena alebo hesla
                    Toast.makeText(LoginActivity.this, getString(R.string.invalid_username_password_lenght), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Metóda pre pridanie poslucháča pre tlačidlo registrácie
    public void addRegistrationListener() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Prechod na aktivitu registrácie
                NavigationUtils.openRegister(LoginActivity.this);
            }
        });
    }

    // Metóda pre overenie platnosti reťazca
    public boolean isValid(String value) {
        return value.length() >= 8;
    }

    // Metóda pre kliknutie na tlačidlo zabudnuté heslo
    public void onForgotPasswordClick(View view) {
        // Prechod na aktivitu obnovenia hesla
        NavigationUtils.openPassword(LoginActivity.this);
    }

    // Metóda pre overenie, či je používateľ prihlásený
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("rememberMeChecked", false);
    }
}