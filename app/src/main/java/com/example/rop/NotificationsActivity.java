package com.example.rop;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private TextView nameOfUserTextView;
    private TextView nameOfApp;
    private ImageButton plusButton;
    private LinearLayout cardContainer;
    private List<CardView> cardList = new ArrayList<>();
    private List<String> notificationCollumnList = Arrays.asList("DESCRIPTION", "SERVICE_TYPE", "LOCATION", "RECURRING", "DEADLINE");
    private DatabaseHelper myDb;
    public static final String ACTION_UPDATE_CARD_CONTAINER = "com.rop.UPDATE_CARD_CONTAINER";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        myDb = new DatabaseHelper(this);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.navigation_notifications);
        nameOfApp = findViewById(R.id.NameOfApp);
        plusButton = findViewById(R.id.plusButton);
        cardContainer = findViewById(R.id.notificationContainer);


        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        nameOfUserTextView = headerView.findViewById(R.id.nameOfUser);

        // Nastavenie toolbaru
        nameOfApp.bringToFront();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Aktualizácia kartičiek
        updateCardContainerForType("notifications_table", notificationCollumnList, R.drawable.baseline_notifications_24, 0);

        // Nastavenie toggle pre posúvanie navigačnej lišty
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.open
        );

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        setName();

        // Listener pre výber položky v dolnej navigácií
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        NavigationUtils.openHome(NotificationsActivity.this);
                        return true;
                    case R.id.navigation_statistics:
                        NavigationUtils.openStatistics(NotificationsActivity.this);
                        return true;
                    case R.id.navigation_notifications:
                        return true;
                }
                return false;
            }
        });

        // Listener pre výber položky v navigačnom menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                handleNavigationItemSelected(menuItem);
                return true;
            }
        });

        // Listener pre kliknutie na tlačidlo plus
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.openAddNotificationsPage(NotificationsActivity.this);
            }
        });

    }
    protected void onResume() {
        super.onResume();
        // Skrytie všetkých kariet a aktualizácia kartičiek
        hideAllCards();
        updateCardContainerForType("notifications_table", notificationCollumnList, R.drawable.baseline_notifications_24, 0);
        registerReceiver(updateCardContainerReceiver, new IntentFilter(ACTION_UPDATE_CARD_CONTAINER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateCardContainerReceiver);
    }

    // Aktualizuje kartičky na základe záznamov v databáze
    private void updateCardContainerForType(String tableName, List<String> columnNames, int iconResource, int position) {
        // Vymazanie existujúceho zoznamu kariet
        cardList.clear();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        int carId = sharedPreferences.getInt("selectedCarId", -1);
        int userId = myDb.getUserIdByUsername(username);
        final int currentUserId = userId;
        String currentColumnName = null;
        String currentColumnValue = null;
        List<TextView> currentCardTextViews = new ArrayList<>();


        // Získanie záznamov z databázy
        Cursor cursor = myDb.getAllEntriesByTypeAndUser(tableName, userId, carId);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        List<String> columnValues = new ArrayList<>();
                        for (String columnName : columnNames) {
                            int columnIndex = cursor.getColumnIndex(columnName);
                            String columnValue = cursor.getString(columnIndex);
                            columnValues.add(columnValue);

                            currentColumnName = columnName;
                            currentColumnValue = columnValue;
                        }

                        // Vytvorenie novej karty (CardView)
                        CardView cardView = new CardView(this);
                        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                                CardView.LayoutParams.MATCH_PARENT,
                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 155, getResources().getDisplayMetrics())
                        );

                        // Pridanie okrajov karte
                        cardParams.setMargins(20, 20, 20, 20); // Adjust margin values as needed

                        cardView.setLayoutParams(cardParams);
                        cardView.setCardElevation(4);
                        cardView.setRadius(8);
                        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.cardview_border));

                        // Vytvorenie RelativeLayout pre názov a obrázok
                        RelativeLayout relativeLayout = new RelativeLayout(this);
                        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));

                        relativeLayout.setId(View.generateViewId());

                        final String finalCurrentColumnName = currentColumnName;
                        final String finalCurrentColumnValue = currentColumnValue;


                        // Vytvorenie tlačidla Delete
                        ImageButton deleteButton = new ImageButton(this);
                        deleteButton.setId(View.generateViewId());
                        deleteButton.setImageResource(R.drawable.baseline_delete_24);
                        deleteButton.setBackgroundColor(Color.TRANSPARENT);

                        // Layout parametre pre tlačidlo Delete
                        RelativeLayout.LayoutParams deleteParams = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        deleteParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        deleteParams.setMargins(0, 8, 16, 0); // margins

                        // Listener pre tlačidlo Delete
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDeleteConfirmationDialog(cardView, tableName, currentUserId, carId, finalCurrentColumnName, finalCurrentColumnValue);
                            }
                        });

                        // Pridanie tlačidla Delete do RelativeLayout
                        relativeLayout.addView(deleteButton, deleteParams);

                        // Vytvorenie TextView pre názov
                        TextView titleTextView = new TextView(this);
                        titleTextView.setText(R.string.notification);
                        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        titleTextView.setTextColor(Color.BLACK);

                        // Layout parametre pre názov
                        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        titleParams.setMargins(16, 0, 0, 0); // Adjust margins as needed

                        // Nastavenie ikony pre názov
                        titleTextView.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, 0, 0);
                        titleTextView.setCompoundDrawablePadding(8);

                        // Pridanie názvu do RelativeLayout
                        relativeLayout.addView(titleTextView, titleParams);

                        // Vytvorenie LinearLayout pre hodnoty stĺpcov
                        LinearLayout linearLayout = new LinearLayout(this);
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        linearLayout.setOrientation(LinearLayout.VERTICAL);

                        for (int i = 0; i < columnValues.size(); i++) {
                            String columnName = columnNames.get(i);
                            String columnValue = columnValues.get(i);

                            // Vytvorenie TextView pre každú hodnotu stĺpca
                            TextView textView = new TextView(this);

                            // Nastavenie vlastností TextView (text, vzhľad)
                            textView.setText(columnName + ": " + columnValue);

                            // Layout parametre pre okraje TextView
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );

                            currentCardTextViews.add(textView);

                            layoutParams.setMargins(15, 1, 0, 2);

                            textView.setLayoutParams(layoutParams);

                            // Pridanie TextView do LinearLayout
                            linearLayout.addView(textView);
                        }

                        // Pridanie LinearLayout do RelativeLayout
                        RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        linearParams.addRule(RelativeLayout.BELOW, relativeLayout.getId());
                        linearParams.setMargins(0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()), 0, 0); // Add 20dp margin from the top
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
        } else {
            Log.d("MainActivity", "Cursor is null for " + tableName);
            Log.d("MainActivity", "User ID: " + userId);
            Log.d("MainActivity", "Table Name: " + tableName);
        }
    }

    // Zobrazenie dialógového okna pre potvrdenie zmazania záznamu
    private void showDeleteConfirmationDialog(CardView cardView, String tableName, int userId, int carId, String columnName, String columnValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_title)
                .setMessage(R.string.are_you_sure_delete_entry)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    // Používateľ klikol na tlačidlo Delete
                    // Odstránenie karty z aktivity
                    cardContainer.removeView(cardView);

                    Log.d("tablename", tableName);
                    Log.d("columnName", columnName);
                    Log.d("columnValue", columnValue);


                    // Vymazanie záznamu z databázy
                    myDb.deleteEntry(tableName, userId, carId, columnName, columnValue);

                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Používateľ klikol na tlačidlo Cancel dialog zmizol
                    dialog.dismiss();
                })
                .show();
    }

    // Nastavenie mena a priezviska používateľa
    public void setName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username != null) {
            String[] nameInfo = myDb.getFirstNameAndLastNameByUsername(username);
            if (nameInfo != null) {
                String firstName = nameInfo[0];
                String lastName = nameInfo[1];
                nameOfUserTextView.setText(firstName + " " + lastName);
            }
        }
    }

    // Odhlásenie používateľa
    public void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("username");
        editor.remove("savedUsername");
        editor.remove("savedPassword");
        editor.remove("rememberMeChecked");

        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Spracovanie výberu položky v navigačnom menu
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


    private void hideAllCards() {
        // Skryje všetky karty
        for (CardView cardView : cardList) {
            cardView.setVisibility(View.GONE);
        }
    }

    // BroadcastReceiver pre aktualizáciu kartičky
    private BroadcastReceiver updateCardContainerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Aktualizácia kartičky
            hideAllCards();
            updateCardContainerForType("notifications_table", notificationCollumnList, R.drawable.baseline_notifications_24, 0);

        }
    };



}