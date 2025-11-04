package com.example.rop;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCarsActivity extends AppCompatActivity {

    private ImageButton addCarButton;
    private LinearLayout carContainer;
    private DatabaseHelper myDb;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private TextView nameOfUserTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycars);
        myDb = new DatabaseHelper(this);

        addCarButton = findViewById(R.id.addCarButton);
        carContainer = findViewById(R.id.carsContainer);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        nameOfUserTextView = headerView.findViewById(R.id.nameOfUser);

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
        setName();
        displayCars(getCarListForCurrentUser());


        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationUtils.openAddPage(MyCarsActivity.this);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                handleNavigationItemSelected(menuItem);
                return true;
            }
        });


    }


    private List<Map<String, Object>> getCarListForCurrentUser() {
        List<Map<String, Object>> carList = new ArrayList<>();

        // Assuming you have a DatabaseHelper class for managing the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);


        Cursor cursor = db.rawQuery("SELECT " + myDb.CAR_COL_0 + ", " + myDb.USER_COL_4 + ", " + myDb.CAR_COL_7 +
                        " FROM " + myDb.TABLE_NAME + " WHERE " + myDb.REFUEL_COL_1 + " = ?",
                new String[]{String.valueOf(myDb.getUserIdByUsername(username))});

        try {
            while (cursor.moveToNext()) {
                int carId = cursor.getInt(cursor.getColumnIndex(myDb.CAR_COL_0));
                String carName = cursor.getString(cursor.getColumnIndex(myDb.USER_COL_4));
                byte[] carImageBytes = cursor.getBlob(cursor.getColumnIndex(myDb.CAR_COL_7));

                Bitmap carImage = (carImageBytes != null) ?
                        BitmapFactory.decodeByteArray(carImageBytes, 0, carImageBytes.length) :
                        null;

                Map<String, Object> carData = new HashMap<>();
                carData.put("id", carId);
                carData.put("name", carName);
                carData.put("image", carImage);

                carList.add(carData);
            }
        } finally {
            cursor.close();
            db.close();
        }

        return carList;
    }

    private void displayCars(List<Map<String, Object>> cars) {
        // Iterate through the list of cars and dynamically create views
        for (final Map<String, Object> car : cars) {
            // Create a RelativeLayout to hold each car's views
            RelativeLayout carLayout = new RelativeLayout(this);

            // Create ImageView for the car image
            ImageView carImage = new ImageView(this);
            // Set the image from the Map or use a default image if it is null
            Bitmap carBitmap = (Bitmap) car.get("image");
            if (carBitmap != null) {
                carImage.setImageBitmap(carBitmap);
            } else {
                // Set the default car image resource and adjust layout parameters
                carImage.setImageResource(R.drawable.default_car);
            }

            // Set layout parameters with margins for both default and non-default images
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            // Add margins here (left, top, right, bottom)
            layoutParams.setMargins(16, 16, 16, 16);
            carImage.setLayoutParams(layoutParams);
            carImage.setScaleType(ImageView.ScaleType.FIT_XY);

            // Create TextView for the car name
            TextView carName = new TextView(this);
            carName.setText((String) car.get("name"));

            // Set layout parameters for the carName TextView to be below the carImage
            RelativeLayout.LayoutParams carNameLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            carNameLayoutParams.topMargin = 460;
            carNameLayoutParams.leftMargin = 20;
            carNameLayoutParams.addRule(RelativeLayout.BELOW, carImage.getId());
            carName.setLayoutParams(carNameLayoutParams);


            Typeface customTypeface = Typeface.create("fontName", Typeface.BOLD);
            carName.setTypeface(customTypeface);
            carName.setTextSize(30);

            // Set OnClickListener for the carLayout
            carLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the car ID from the Map
                    int carId = (int) car.get("id");

                    // Store the car ID in SharedPreferences
                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
                    editor.putInt("selectedCarId", carId);
                    editor.apply();

                    Log.d("carId", String.valueOf(carId));

                    NavigationUtils.openHome(MyCarsActivity.this);

                }
            });

            // Add views to the carLayout
            carLayout.addView(carImage);
            carLayout.addView(carName);

            // Add the carLayout to the main container
            carContainer.addView(carLayout);
        }
    }


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

}