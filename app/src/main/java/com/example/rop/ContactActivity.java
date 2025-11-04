package com.example.rop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ContactActivity extends AppCompatActivity {

    // Deklarácie datovych poloziek pre prvky v užívateľskom rozhraní
    private TextInputLayout phoneNumberInputLayout;
    private TextInputEditText phoneNumberEditText;
    private TextInputLayout appVersionInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputEditText emailEditText;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        appVersionInputLayout = findViewById(R.id.appVersionInputLayout);
        phoneNumberInputLayout = findViewById(R.id.phoneNumberInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        backButton = findViewById(R.id.backButton);

        // Nastavenie niektorých prvkov z uživatelského rozhrania ako neaktívne
        phoneNumberInputLayout.setFocusable(false);
        emailInputLayout.setFocusable(false);

        // Nastavenie textu pre telefónne číslo a zablokovanie jeho editovateľnosti
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setText("0949 067 989");
        phoneNumberEditText.setFocusable(false);

        backButton.bringToFront();

        // Pridanie odkazov na telefónne číslo a nastavenie udalosti pre kliknutie
        Linkify.addLinks(phoneNumberEditText, Patterns.PHONE, "tel:");
        phoneNumberInputLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEditText.getText().toString().replaceAll("[^0-9]", "");
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(dialIntent);
            }
        });

        // Nastavenie e-mailovej adresy ako neaktívnej a vytvorenie odkazu
        emailEditText = findViewById(R.id.emailEditText);
        emailEditText.setFocusable(false);
        String email = "<a href=\"mailto:peter.szepesi@student.spseke.sk\">peter.szepesi@student.spseke.sk</a>";
        emailEditText.setText(Html.fromHtml(email));
        emailEditText.setMovementMethod(LinkMovementMethod.getInstance());

        // Nastavenie onClickListenera na inputLayout, ktorý otvori emailovu aplikaciu
        emailInputLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "peter.szepesi@student.spseke.sk";
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                startActivity(emailIntent);
            }
        });

        // Nastavenie onClickListenera na tlacidlo, ktorý vráti použivatela na domovskú stránku
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.openHome(ContactActivity.this);
            }
        });
    }
}