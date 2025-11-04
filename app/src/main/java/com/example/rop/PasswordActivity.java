package com.example.rop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PasswordActivity extends AppCompatActivity {

    // Deklarácie dátových položiek pre prvky v užívateľskom rozhraní
    private ImageButton backButton;
    private Button resetButton;
    private TextInputEditText usernameEditText;
    private TextInputLayout usernameInputLayout, newPasswordInputLayout, retypePasswordInputLayout;
    private TextInputEditText newPasswordEditText;
    private TextInputEditText retypePasswordEditText;
    private DatabaseHelper myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        // Inicializácia databázového pomocníka
        myDb = new DatabaseHelper(this);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        backButton = findViewById(R.id.backButton);
        usernameEditText = findViewById(R.id.usernameEditText);
        newPasswordEditText = findViewById(R.id.passwordEditText);
        retypePasswordEditText = findViewById(R.id.retypePasswordEditText);
        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        newPasswordInputLayout = findViewById(R.id.passwordInputLayout);
        retypePasswordInputLayout = findViewById(R.id.retypePasswordInputLayout);
        resetButton = findViewById(R.id.resetButton);
        backButton.bringToFront();

        // Nastavenie listenera pre tlačidlo
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToLogin();
            }
        });

        // Nastavenie listenera pre tlačidlo resetovania hesla
        resetPassword();
    }

    // Metóda na resetovanie hesla
    public void resetPassword() {
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Získanie zadaných hodnôt
                String username = usernameEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String retypePassword = retypePasswordEditText.getText().toString();

                // Validácia zadaných hodnôt
                if (!isValid(username)) {
                    usernameInputLayout.setError(getString(R.string.invalid_username));
                    return;
                }

                if (!isValid(newPassword)) {
                    newPasswordInputLayout.setError(getString(R.string.password_must_be));
                    return;
                }

                if (!isValid(retypePassword)) {
                    retypePasswordInputLayout.setError(getString(R.string.password_must_be));
                    return;
                }

                if (!newPassword.equals(retypePassword)) {
                    retypePasswordInputLayout.setError(getString(R.string.passwords_do_not_match));
                    return;
                }

                // Aktualizácia hesla v databáze
                if (myDb.updatePassword(username, newPassword)) {
                    // Zobrazenie úspešnej správy a presmerovanie na prihlasovaciu obrazovku
                    Toast.makeText(PasswordActivity.this, getString(R.string.password_reset_successfully), Toast.LENGTH_SHORT).show();
                    NavigationUtils.openLogin(PasswordActivity.this);
                } else {
                    // Zobrazenie chybovej správy v prípade, že používateľ neexistuje
                    usernameEditText.setError(getString(R.string.username_does_not_exists));
                }
            }
        });
    }

    // Metóda na návrat na prihlasovaciu obrazovku
    public void backToLogin(){
        NavigationUtils.openLogin(PasswordActivity.this);
    }

    // Metóda na kontrolu platnosti hodnoty
    public boolean isValid(String value) {
        return value.length() >= 8;
    }

}