package com.example.rop;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.jakewharton.threetenabp.AndroidThreeTen;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class StatisticsActivity extends AppCompatActivity {
    // Deklarácie dátových položiek pre prvky v užívateľskom rozhraní
    private BottomNavigationView bottomNavigation;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private TextView nameOfUserTextView;
    private DatabaseHelper myDb;
    private BarChart barChart1;
    private BarChart barChart2;
    private BarChart barChart3;
    private TextView nameOfApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        // Inicializácia objektu pre prácu s databázou
        myDb = new DatabaseHelper(this);

        AndroidThreeTen.init(this);

        // Pripojenie prvkov z užívateľského rozhrania k datovym polozkam
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.navigation_statistics);
        barChart1 = findViewById(R.id.barChart1);
        barChart2 = findViewById(R.id.barChart2);
        barChart3 = findViewById(R.id.barChart3);
        nameOfApp = findViewById(R.id.NameOfApp);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        nameOfUserTextView = headerView.findViewById(R.id.nameOfUser);

        nameOfApp.bringToFront();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Vytvorenie ActionBarDrawerToggle a jeho pridanie do DrawerLayout
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.open
        );

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Nastavenie mena používateľa v navigačnom menu
        setName();


        // Nastavenie setNavigationItemSelectedListener na BottomNavigationView a NavigationView
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        NavigationUtils.openHome(StatisticsActivity.this);
                        return true;
                    case R.id.navigation_statistics:
                        return true;
                    case R.id.navigation_notifications:
                        NavigationUtils.openNotifications(StatisticsActivity.this);
                        return true;
                }
                return false;
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                handleNavigationItemSelected(menuItem);
                return true;
            }
        });

        displayMonthlyData();

    }

    // Metóda pre zobrazenie mesačných štatistík
    private void displayMonthlyData() {
        displayMonthlyExpensesRefuel();
        displayMonthlyExpensesAndServices();
        displayMonthlyKilometers();
    }

    // Metóda pre zobrazenie mesačných nákladov na služby a výdavky
    private void displayMonthlyExpensesAndServices() {
        // SQL dotaz na získanie dátumov a súhrnných hodnôt nákladov na služby a výdavky
        String query = "WITH monthly_expenses AS (" +
                "  SELECT " +
                "         SUBSTR(DATE, 4, 7) || '/' || SUBSTR(DATE, 1, 2) AS month, " +
                "         SUM(COST) AS totalValue " +
                "  FROM " + myDb.TABLE_NAME_SERVICE +
                "  WHERE IDuser = ? AND IDcar = ? AND DATE IS NOT NULL " +
                "  GROUP BY month " +
                "  UNION ALL " +
                "  SELECT " +
                "         SUBSTR(DATE, 4, 7) || '/' || SUBSTR(DATE, 1, 2) AS month, " +
                "         SUM(COST) AS totalValue " +
                "  FROM " + myDb.TABLE_NAME_EXPENSES +
                "  WHERE IDuser = ? AND IDcar = ? AND DATE IS NOT NULL " +
                "  GROUP BY month" +
                ") " +
                "SELECT month, SUM(totalValue) AS totalValue " +
                "FROM monthly_expenses " +
                "GROUP BY month " +
                "ORDER BY month DESC;";


        SQLiteDatabase db = myDb.getReadableDatabase();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        int userId = myDb.getUserIdByUsername(username);
        int carId = sharedPreferences.getInt("selectedCarId", -1);

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(carId), String.valueOf(userId), String.valueOf(carId)});


        try {
            if (cursor != null && cursor.moveToFirst()) {
                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                do {
                    @SuppressLint("Range") String rawMonth = cursor.getString(cursor.getColumnIndex("month"));

                    // Rozdelenie rawMonth podľa "/"
                    String[] parts = rawMonth.split("/");

                    // Formátovanie mesiaca a roku
                    String formattedMonth = String.format("%s/%s", Integer.parseInt(parts[0]), parts[1]);

                    @SuppressLint("Range") float totalValue = cursor.getFloat(cursor.getColumnIndex("totalValue"));

                    // Kontrola, či je formattedMonth už obsiahnutý v labels
                    int existingIndex = labels.indexOf(formattedMonth);
                    if (existingIndex != -1) {
                        // Ak áno, pridaj totalValue k existujúcej položke
                        entries.get(existingIndex).setY(entries.get(existingIndex).getY() + totalValue);
                    } else {
                        // Ak nie, pridaj novú položku
                        labels.add(formattedMonth);
                        entries.add(new BarEntry(labels.size() - 1, totalValue));
                    }
                } while (cursor.moveToNext());

                // Zobrazenie grafu s načítanými dátami
                displayBarChart(barChart1, entries, labels, "Monthly Expenses for Service and Expenses");
            } else {
                Log.e("ChartData", "Cursor is null or empty");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    // Metóda pre zobrazenie mesačných nákladov na tankovanie
    private void displayMonthlyExpensesRefuel() {
        createBarChart(barChart2, myDb.TABLE_NAME_REFUEL, "Monthly Expenses for Refuel", "DATE", "TOTAL_COST");
    }

    // Metóda pre zobrazenie mesačných kilometrov
    private void displayMonthlyKilometers() {
        createBarChart(barChart3, myDb.TABLE_NAME_ODOMETER, "Monthly Kilometers", "DATE", "KILOMETERS");
    }

    // Metóda pre vytvorenie grafu
    private void createBarChart(BarChart barChart, String tableName, String chartTitle, String dateColumn, String valueColumn) {
        SQLiteDatabase db = myDb.getReadableDatabase();

        //SQL dotaz na ziskanie dat z tabuľky
        String query = "SELECT " + dateColumn + ", " +
                "SUM(" + valueColumn + ") AS totalValue " +
                "FROM " + tableName +
                " WHERE IDuser = ? AND IDcar = ? AND DATE IS NOT NULL " +
                "GROUP BY " + dateColumn + " " +
                "ORDER BY " + dateColumn + " DESC;";

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        int userId = myDb.getUserIdByUsername(username);
        int carId = sharedPreferences.getInt("selectedCarId", -1);


        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(carId)});

        try {
            if (cursor != null && cursor.moveToFirst()) {
                List<BarEntry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                do {
                    @SuppressLint("Range") String dateString = cursor.getString(cursor.getColumnIndex(dateColumn));

                    // Parsovanie dátumu pomocou SimpleDateFormat s formátom deň, mesiac a rok
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date date = dateFormat.parse(dateString);

                    // Formátovanie dátumu
                    String formattedDate = new SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(date);

                    @SuppressLint("Range") float totalValue = cursor.getFloat(cursor.getColumnIndex("totalValue"));

                    // Kontrola, či je formattedDate už obsiahnutý v labels
                    int existingIndex = labels.indexOf(formattedDate);
                    if (existingIndex != -1) {
                        // Ak áno, pridaj totalValue k existujúcej položke
                        entries.get(existingIndex).setY(entries.get(existingIndex).getY() + totalValue);
                    } else {
                        // Ak nie, pridaj novú položku
                        labels.add(formattedDate);
                        entries.add(new BarEntry(labels.size() - 1, totalValue));
                    }
                } while (cursor.moveToNext());

                // Triedenie záznamov podľa dátumu
                Collections.sort(entries, new Comparator<BarEntry>() {
                    @Override
                    public int compare(BarEntry entry1, BarEntry entry2) {
                        int index1 = (int) entry1.getX();
                        int index2 = (int) entry2.getX();

                        String date1 = labels.get(index1);
                        String date2 = labels.get(index2);

                        // Použitie SimpleDateFormat s formátom deň, mesiac a rok
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                        try {
                            Date dateObj1 = dateFormat.parse(date1);
                            Date dateObj2 = dateFormat.parse(date2);

                            // Porovnanie parsovaných dátumov
                            return dateObj2.compareTo(dateObj1); // Obrátený poradie pre zostupné triedenie
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0; // Vrátenie 0 v prípade, že sa nepodarilo parsovať dátumy
                        }
                    }
                });
                // Zobrazenie grafu s načítanými dátami
                displayBarChart(barChart, entries, labels, chartTitle);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }


    // Metóda pre zobrazenie grafu
    private void displayBarChart(BarChart barChart, List<BarEntry> entries, List<String> labels, String chartTitle) {
        BarDataSet dataSet = new BarDataSet(entries, "Values");
        int barColor = Color.parseColor("#4a5f79");
        dataSet.setColor(barColor);

        BarData data = new BarData(dataSet);

        // Nastavenie X-osových popisov
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // Spodná pozícia pre popisy
        barChart.getXAxis().setValueFormatter(new XAxisValueFormatter(labels));
        barChart.getXAxis().setLabelCount(labels.size());
        barChart.getXAxis().setGranularity(1f);

        // Formátovanie hodnôt na Y-osi
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (chartTitle.contains("Kilometers")) {
                    return value + " km";
                } else {
                    return value + "€";
                }
            }
        });

        barChart.setData(data);

        barChart.getBarData().setValueTextSize(15f);

        barChart.setTouchEnabled(false);
        barChart.getDescription().setEnabled(false);

        barChart.invalidate();
    }



    // Metóda pre získanie mena používateľa a jeho zobrazenie v navigačnom menu
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

    // Metóda pre odhlásenie používateľa
    public void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Odstránenie uložených údajov pri odhlásení
        editor.remove("username");
        editor.remove("savedUsername");
        editor.remove("savedPassword");
        editor.remove("rememberMeChecked");

        editor.apply();

      NavigationUtils.openLogin(StatisticsActivity.this);
    }

    // Metóda pre spracovanie vybranej položky v navigačnom menu
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


    // Vlastný formáter popisov na X-osi
    public class XAxisValueFormatter extends IndexAxisValueFormatter {
        private List<String> labels;

        public XAxisValueFormatter(List<String> labels) {
            this.labels = labels;
        }

        @Override
        public String getFormattedValue(float value) {
            int index = (int) value;
            if (index >= 0 && index < labels.size()) {
                return labels.get(index);
            }
            return ""; //Riešenie prípadu, ak index vyjde mimo rozsahu
        }
    }

}



