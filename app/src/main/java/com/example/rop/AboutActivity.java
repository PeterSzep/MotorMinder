package com.example.rop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class AboutActivity extends AppCompatActivity {

    // Deklarácie datovych poloziek pre prvky v užívateľskom rozhraní
    private ImageButton backButton;
    private TextInputEditText firstNameEditText;
    private TextInputEditText lastNameEditText;
    private TextInputEditText usernameEditText;
    private TextInputLayout firstNameInputLayout;
    private TextInputLayout lastNameInputLayout;
    private TextInputLayout usernameInputLayout;
    private Button deleteAccountButton;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Inicializácia objektu pre prácu s databázou
        myDb = new DatabaseHelper(this);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        firstNameInputLayout = findViewById(R.id.firstNameInputLayout);
        lastNameInputLayout = findViewById(R.id.lastNameInputLayout);
        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        backButton = findViewById(R.id.backButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);

        backButton.bringToFront();

        // Získanie uloženého používateľského mena z preferencií
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        // Naplnenie polí pre osobné údaje, ak je používateľ prihlásený
        if (username != null) {
            String[] userInfo = myDb.getFirstNameAndLastNameByUsername(username);
            if (userInfo != null && userInfo.length == 2) {
                String firstName = userInfo[0];
                String lastName = userInfo[1];

                firstNameInputLayout.setHint(getString(R.string.first_name));
                lastNameInputLayout.setHint(getString(R.string.last_name));
                usernameInputLayout.setHint(getString(R.string.username));

                firstNameEditText.setText(firstName);
                lastNameEditText.setText(lastName);
                usernameEditText.setText(username);

                firstNameEditText.setFocusable(false);
                lastNameEditText.setFocusable(false);
                usernameEditText.setFocusable(false);

                firstNameInputLayout.setFocusable(false);
                lastNameInputLayout.setFocusable(false);
                usernameInputLayout.setFocusable(false);
            }
        }

        // Nastavenie onClickListenera pre tlačidlo späť
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.openHome(AboutActivity.this);
            }
        });

        // Nastavenie onClickListenera pre tlačidlo vymazať účet
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
    }

    // Metóda pre zobrazenie dialógového okna
    public void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.confirmation_dialog, null);
        builder.setView(dialogView);

        final EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);

        // Nastavenie pozitívneho tlačidla
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredPassword = passwordEditText.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", null);

                // Overenie hesla a volanie metódy na vymazanie účtu
                if (myDb.loginUser(username,enteredPassword)) {
                    deleteAccount();
                } else {
                    Toast.makeText(AboutActivity.this, getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Nastavenie negatívneho tlačidla
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Zobrazenie dialógového okna
        builder.show();
    }

    // Metóda pre vymazanie účtu
    public void deleteAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Ak je používateľ prihlásený, vymaže sa jeho účet a odstránia sa údaje z preferencií
        if (username != null) {
            myDb.deleteUser(username);

            editor.remove("username");
            editor.remove("savedUsername");
            editor.remove("savedPassword");
            editor.remove("rememberMeChecked");

            editor.apply();

            NavigationUtils.openLogin(AboutActivity.this);
        }
    }
}