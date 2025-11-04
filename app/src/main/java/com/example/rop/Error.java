package com.example.rop;

import android.content.Context;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Error {

    // Metóda na spočítanie chýb v EditTexte
    public static int getError(TextInputEditText[] editTextList, TextInputLayout[] inputLayoutList, Context context, int errorMessageResId) {
        int error = 0;
        for (int i = 0; i < editTextList.length; i++) { // Prechádza cez všetky polia
            String text = editTextList[i].getText().toString().trim(); // Získanie textu z editTextov
            if (text.isEmpty()) { // Ak je text prázdny
                inputLayoutList[i].setError(context.getString(errorMessageResId)); // Nastaví error v inputLayoute
                error++;
            }
        }
        return error; // Vráti počet chýb
    }
}