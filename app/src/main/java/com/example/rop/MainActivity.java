package com.example.rop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Deklarácie dátových položiek pre prvky v užívateľskom rozhraní
    private com.google.android.material.floatingactionbutton.FloatingActionButton plusButton;
    private com.google.android.material.floatingactionbutton.FloatingActionButton infoButton;
    private FloatingActionButton refuelButton, serviceButton, expenseButton, odometerButton;
    private ImageView imageViewCar;
    private TextView carNameTextView;
    private TextView usernameTextView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView nameOfUserTextView;
    private ActionBarDrawerToggle drawerToggle;
    private BottomNavigationView bottomNavigation;
    private DatabaseHelper myDb;
    private boolean isMenuOpen = false;
    private LinearLayout cardContainer;
    private Float translationY = 100f;
    private List<CardView> cardList = new ArrayList<>();
    private List<String> refuelColumnNames = Arrays.asList("DATE", "TIME", "LOCATION", "AMOUNT", "PRICE_PER_LITER", "TOTAL_COST");
    private List<String> expensesColumnNames = Arrays.asList("DATE", "TIME", "LOCATION", "TYPE", "COST");
    private List<String> odometerColumnNames = Arrays.asList("DATE", "KILOMETERS");
    private List<String> serviceColumnNames = Arrays.asList("DATE", "TIME", "LOCATION", "TYPE", "COST");
    private Spinner typeSpinner;
    OvershootInterpolator interpolator = new OvershootInterpolator();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        odometerButton = findViewById(R.id.odometer_button);
        expenseButton = findViewById(R.id.expense_button);
        refuelButton = findViewById(R.id.refuelButton);
        serviceButton = findViewById(R.id.service_button);
        plusButton = findViewById(R.id.plusButton);
        infoButton = findViewById(R.id.infoButton);
        imageViewCar = findViewById(R.id.CarImageView);
        carNameTextView = findViewById(R.id.CarNameTextView);
        usernameTextView = findViewById(R.id.NameOfApp);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        cardContainer = findViewById(R.id.cardContainer);
        typeSpinner = findViewById(R.id.typeSpinner);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);


        infoButton.setVisibility(View.INVISIBLE);
        typeSpinner.setVisibility(View.INVISIBLE);

        // Inicializácia navigačného panela a toolbaru
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        nameOfUserTextView = headerView.findViewById(R.id.nameOfUser);
        initFabMenu();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.open
        );

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        setNameAndCar();


        if (cardList.isEmpty()) {
            typeSpinner.setVisibility(View.INVISIBLE);
        } else {
            typeSpinner.setVisibility(View.VISIBLE);
        }

        // Pridanie onClickListenera pre tlačidlo infoButton
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.openMyCarPage(MainActivity.this);
            }
        });

        // Pridanie setNavigationItemSelectedListener pre  položky navigačného menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                handleNavigationItemSelected(menuItem);
                return true;
            }
        });

        // Pridanie setNavigationItemSelectedListener pre  položky dolného navigačného menu
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == bottomNavigation.getSelectedItemId()) {
                    return false;
                }

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_statistics:
                        NavigationUtils.openStatistics(MainActivity.this);
                        return true;
                    case R.id.navigation_notifications:
                        NavigationUtils.openNotifications(MainActivity.this);
                        return true;
                }
                return false;
            }
        });


        // Pridanie setOnItemSelectedListener pre Spinner typeSpinner
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                updateCardContainerForFilter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });



    }

    // Metóda pre otvorenie menu
    private void openMenu(){
        isMenuOpen = !isMenuOpen;

        // Animácia otvorenia menu
        plusButton.animate().setInterpolator(interpolator).rotation(45F).setDuration(300).start();

        // Nastavenie tlačidiel menu na klikateľné
        odometerButton.setClickable(true);
        expenseButton.setClickable(true);
        refuelButton.setClickable(true);
        serviceButton.setClickable(true);

        // Animácia zobrazenia tlačidiel menu
        odometerButton.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        expenseButton.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        refuelButton.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        serviceButton.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();

    }

    // Metóda pre zatvorenie menu
    private void closeMenu(){
        isMenuOpen = !isMenuOpen;

        // Animácia zatvorenia menu
        plusButton.animate().setInterpolator(interpolator).rotation(0F).setDuration(300).start();

        // Nastavenie tlačidiel menu na neklikateľné
        odometerButton.setClickable(false);
        expenseButton.setClickable(false);
        refuelButton.setClickable(false);
        serviceButton.setClickable(false);

        // Animácia skrytia tlačidiel menu
        odometerButton.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        expenseButton.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        refuelButton.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        serviceButton.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }

    // Metóda pre inicializáciu menu s tlačidlami
    private void initFabMenu(){
        // Nastavenie alfa kanálu tlačidiel menu na 0
        odometerButton.setAlpha(0f);
        expenseButton.setAlpha(0f);
        refuelButton.setAlpha(0f);
        serviceButton.setAlpha(0f);

        // Nastavenie pohybu tlačidla plusButton o veľkosť translationY smerom nadol
        plusButton.setTranslationY(translationY);
        refuelButton.setTranslationY(translationY);
        expenseButton.setTranslationY(translationY);
        serviceButton.setTranslationY(translationY);
        odometerButton.setTranslationY(translationY);

        // Pridanie poslucháča pre kliknutie na tlačidlá menu
        plusButton.setOnClickListener(this);
        refuelButton.setOnClickListener(this);
        expenseButton.setOnClickListener(this);
        serviceButton.setOnClickListener(this);
        odometerButton.setOnClickListener(this);

        // Nastavenie tlačidiel menu na klikateľné
        plusButton.setClickable(true);
        refuelButton.setClickable(false);
        expenseButton.setClickable(false);
        serviceButton.setClickable(false);
        odometerButton.setClickable(false);
    }

    // Metóda pre spracovanie kliknutia na tlačidlo
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plusButton:
                ImageView carImageView = findViewById(R.id.CarImageView);
                if (carImageView.getDrawable() == null) {
                    NavigationUtils.openAddPage(this);
                } else {
                    if (isMenuOpen) {
                        closeMenu();
                    } else {
                        openMenu();
                    }
                }
                break;
            case R.id.refuelButton:
                NavigationUtils.openFuelPage(MainActivity.this);
                break;
            case R.id.expense_button:
                NavigationUtils.openExpensePage(MainActivity.this);
                break;
            case R.id.service_button:
                NavigationUtils.openServicePage(MainActivity.this);
                break;
            case R.id.odometer_button:
                NavigationUtils.openOdometerPage(MainActivity.this);
                break;
        }
    }
    // Metóda pre zobrazenie informácií o aute pre používateľa
    private void displayCarInfoForUser(int userId, int carId) {
        // Získanie údajov o aute z databázy
        Cursor cursor = myDb.getCarDataByUserIdAndCarId(userId, carId);

        // Spracovanie údajov, ak sú k dispozícii
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String carName = cursor.getString(cursor.getColumnIndex("NAME"));
            @SuppressLint("Range") byte[] imageData = cursor.getBlob(cursor.getColumnIndex("IMAGE"));

            // Zobrazenie názvu auta
            infoButton.setVisibility(View.VISIBLE);
            typeSpinner.setVisibility(View.VISIBLE);
            carNameTextView.bringToFront();
            carNameTextView.setText(carName);

            // Zobrazenie obrázka auta
            if (imageData != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                imageViewCar.setImageBitmap(imageBitmap);
            } else {
                // Ak nie je k dispozícii žiadny obrázok, použije sa predvolený obrázok
                imageViewCar.setImageResource(R.drawable.default_car);

                // Nastavenie veľkosti predvoleného obrázka
                imageViewCar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        int targetWidth = imageViewCar.getWidth();
                        int targetHeight = imageViewCar.getHeight();

                        BitmapDrawable drawable = (BitmapDrawable) imageViewCar.getDrawable();
                        Bitmap defaultImage = drawable.getBitmap();
                        if (targetWidth > 0 && targetHeight > 0) {
                            Bitmap resizedDefaultImage = Bitmap.createScaledBitmap(defaultImage, targetWidth, targetHeight, false);
                            imageViewCar.setImageBitmap(resizedDefaultImage);

                            imageViewCar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
            }
        }

        // Uzavretie kurzora
        if (cursor != null) {
            cursor.close();
        }
    }


    // Metóda na spracovanie výberu položiek z bočného navigačného menu
    public void handleNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                NavigationUtils.openHome(this);
                break;
            case R.id.about:
                NavigationUtils.openAbout(this);
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.contact:
                NavigationUtils.openContact(this);
                break;
            case R.id.cars:
                NavigationUtils.openMyCars(this);
                break;
            default:
                break;
        }
    }

    // Metóda na nastavenie mena a informácií o aute pre používateľa
    public void setNameAndCar() {
        // Získanie mena používateľa z preferencií
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        // Ak je meno používateľa k dispozícii
        if (username != null) {
            // Získanie informácií o používateľovi z databázy
            String[] nameInfo = myDb.getFirstNameAndLastNameByUsername(username);
            if (nameInfo != null) {
                String firstName = nameInfo[0];
                String lastName = nameInfo[1];
                if (firstName != null && lastName != null) {
                    // Určenie vhodného pozdravu na základe aktuálneho času
                    Calendar calendar = Calendar.getInstance();
                    int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                    String greeting;
                    if (hourOfDay >= 0 && hourOfDay < 11) {
                        greeting = getString(R.string.good_morning);
                    } else if (hourOfDay >= 11 && hourOfDay < 18) {
                        greeting = getString(R.string.good_afternoon);
                    } else {
                        greeting = getString(R.string.good_evening);
                    }
                    // Nastavenie textu pozdravu a mena používateľa
                    usernameTextView.setText(greeting + ", " + firstName);
                    nameOfUserTextView.setText(firstName + " " + lastName);
                }
            }
        }
        // Získanie informácií o aute a zobrazenie auta
        if (username != null) {
            int userId = myDb.getUserIdByUsername(username);
            int carId = sharedPreferences.getInt("selectedCarId", -1);
            if (userId != -1) {
                displayCarInfoForUser(userId, carId);
            }
        }
    }

    // Metóda na aktualizáciu zobrazenia kariet pre daný typ záznamov
    private void updateCardContainerForType(String tableName, List<String> columnNames, int iconResource, int position) {
        // Získanie informácií o používateľovi a aute
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        int userId = myDb.getUserIdByUsername(username);
        int carId = sharedPreferences.getInt("selectedCarId", -1);
        final int currentUserId = userId;
        String currentColumnName = null;
        String currentColumnValue = null;
        List<TextView> currentCardTextViews = new ArrayList<>();

        // Ak nie sú karty k dispozícii, nastavíme spinner ako neviditeľný
        if (cardList.isEmpty()) {
            typeSpinner.setVisibility(View.INVISIBLE);
        } else {
            typeSpinner.setVisibility(View.VISIBLE);
        }

        // Získanie všetkých záznamov pre daný typ
        Cursor cursor = myDb.getAllEntriesByTypeAndUser(tableName, userId, carId);

        if (cursor != null) {
            try {
                // Ak sú záznamy k dispozícii, prechádzame cez ne
                if (cursor.moveToFirst()) {
                    do {
                        // Získana hodnôty stĺpcov
                        List<String> columnValues = new ArrayList<>();
                        for (String columnName : columnNames) {
                            int columnIndex = cursor.getColumnIndex(columnName);
                            String columnValue = cursor.getString(columnIndex);
                            columnValues.add(columnValue);

                            currentColumnName = columnName;
                            currentColumnValue = columnValue;
                        }

                        // Vytvorenie novej karty
                        CardView cardView = new CardView(this);
                        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                                CardView.LayoutParams.MATCH_PARENT,
                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 155, getResources().getDisplayMetrics())
                        );

                        // Nastavenie okrajov karty
                        cardParams.setMargins(20, 20, 20, 20);
                        cardView.setLayoutParams(cardParams);
                        cardView.setCardElevation(4);
                        cardView.setRadius(8);
                        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.cardview_border));

                        // Vytvorenie RelativeLayout pre titulok a obrázok
                        RelativeLayout relativeLayout = new RelativeLayout(this);
                        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));

                        relativeLayout.setId(View.generateViewId());

                        final String finalCurrentColumnName = currentColumnName;
                        final String finalCurrentColumnValue = currentColumnValue;

                        // Vytvorenie tlačidla pre odstránenie záznamu
                        ImageButton deleteButton = new ImageButton(this);
                        deleteButton.setId(View.generateViewId());
                        deleteButton.setImageResource(R.drawable.baseline_delete_24);
                        deleteButton.setBackgroundColor(Color.TRANSPARENT);

                        // Nastavenie parametrov tlačidla pre odstránenie
                        RelativeLayout.LayoutParams deleteParams = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        deleteParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        deleteParams.setMargins(0, 8, 16, 0);

                        // Nastavenie onClickListenera pre tlačidlo pre odstránenie
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Zobrazenie dialógového okna na potvrdenie odstránenia záznamu
                                showDeleteConfirmationDialog(cardView, tableName, currentUserId, carId, finalCurrentColumnName, finalCurrentColumnValue);
                            }
                        });

                        // Pridanie tlačidla pre odstránenie do RelativeLayout
                        relativeLayout.addView(deleteButton, deleteParams);

                        // Vytvorenie TextView pre titulok
                        TextView titleTextView = new TextView(this);
                        titleTextView.setText(getTitleForTable(tableName));
                        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        titleTextView.setTextColor(Color.BLACK);

                        // Nastavenie parametrov pre titulok (vľavo)
                        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        titleParams.setMargins(16, 0, 0, 0);

                        // Nastavenie obrázka pre titulok
                        titleTextView.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, 0, 0);
                        titleTextView.setCompoundDrawablePadding(8);

                        // Pridanie TextView pre titulok do RelativeLayout
                        relativeLayout.addView(titleTextView, titleParams);

                        // Vytvorenie LinearLayout pre hodnoty stĺpcov
                        LinearLayout linearLayout = new LinearLayout(this);
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        linearLayout.setOrientation(LinearLayout.VERTICAL);

                        // Pre každú hodnotu stĺpca vytvorí TextView
                        for (int i = 0; i < columnValues.size(); i++) {
                            String columnName = columnNames.get(i);
                            String columnValue = columnValues.get(i);

                            TextView textView = new TextView(this);
                            textView.setText(columnName + ": " + columnValue);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );

                            currentCardTextViews.add(textView);
                            layoutParams.setMargins(15, 1, 0, 2);
                            textView.setLayoutParams(layoutParams);

                            linearLayout.addView(textView);
                        }

                        // Pridanie LinearLayout do RelativeLayout
                        RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        linearParams.addRule(RelativeLayout.BELOW, relativeLayout.getId());
                        linearParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()), 0, 0);
                        relativeLayout.addView(linearLayout, linearParams);

                        // Pridanie RelativeLayout do CardView
                        cardView.addView(relativeLayout);

                        cardList.add(0, cardView);

                        cardContainer.addView(cardView, 0);

                    } while (cursor.moveToNext());
                } else {
                    Log.d("MainActivity", "No rows found for " + tableName);
                }
            } finally {
                cursor.close();
            }
        }
    }

    // Pomocná metóda pre získanie názvu pre danú tabuľku záznamov
    private String getTitleForTable(String tableName) {
        switch (tableName) {
            case "refuel_table":
                return getString(R.string.refuel);
            case "odometer_table":
                return getString(R.string.odometer);
            case "expenses_table":
                return getString(R.string.expense);
            case "services_table":
                return getString(R.string.service);
            default:
                return "Unknown";
        }
    }

    // Metóda pre odhlásenie používateľa
    public void logout() {
        // Odstránenie uložených údajov o používateľovi
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("username");
        editor.remove("savedUsername");
        editor.remove("savedPassword");
        editor.remove("rememberMeChecked");

        editor.apply();

        // Presmerovanie používateľa na prihlasovaciu obrazovku
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Metóda pre aktualizáciu zobrazenia všetkých kariet
    private void updateAllCardContainers() {
        hideAllCards();
        updateCardContainerForType("refuel_table", refuelColumnNames, R.drawable.baseline_local_gas_station_24, 0);
        updateCardContainerForType("odometer_table", odometerColumnNames, R.drawable.baseline_speed_24, 0);
        updateCardContainerForType("expenses_table", expensesColumnNames, R.drawable.baseline_attach_money_24, 0);
        updateCardContainerForType("services_table", serviceColumnNames, R.drawable.baseline_car_repair_24, 0);
    }

    // Metóda pre skrytie všetkých kariet
    private void hideAllCards() {
        for (CardView cardView : cardList) {
            cardView.setVisibility(View.GONE);
        }
    }

    // Metóda pre aktualizáciu zobrazenia kariet na základe filtra
    private void updateCardContainerForFilter(int position) {
        switch (position) {
            case 0: // Všetky záznamy
                updateAllCardContainers();
                break;
            case 1: // Filter na tankovanie
                hideAllCards();
                updateCardContainerForType("refuel_table", refuelColumnNames, R.drawable.baseline_local_gas_station_24, 0);
                break;
            case 2: // Filter na odometer
                hideAllCards();
                updateCardContainerForType("odometer_table", odometerColumnNames, R.drawable.baseline_speed_24, 0);
                break;
            case 3: // Filter na náklady
                hideAllCards();
                updateCardContainerForType("expenses_table", expensesColumnNames, R.drawable.baseline_attach_money_24, 0);
                break;
            case 4: // Filter na servis
                hideAllCards();
                updateCardContainerForType("services_table", serviceColumnNames, R.drawable.baseline_car_repair_24, 0);
                break;
        }
    }

    // Metóda pre zobrazenie dialógového okna na potvrdenie odstránenia záznamu
    private void showDeleteConfirmationDialog(CardView cardView, String tableName, int userId, int carId, String columnName, String columnValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete_title))
                .setMessage(getString(R.string.are_you_sure_delete_entry))
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Odstránenie karty zobrazenia záznamu
                    cardContainer.removeView(cardView);

                    // Odstránenie záznamu z databázy
                    myDb.deleteEntry(tableName, userId, carId, columnName, columnValue);

                    // Odstránenie karty z listu kariet
                    cardList.remove(cardView);

                    // Ak je list kariet prázdny, skryje spinner
                    if (cardList.isEmpty()) {
                        typeSpinner.setVisibility(View.INVISIBLE);
                    }
                    // Zobrazenie oznámenia o úspešnom odstránení záznamu
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

}


