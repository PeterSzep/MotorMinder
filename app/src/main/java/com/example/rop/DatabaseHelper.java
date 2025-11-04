package com.example.rop;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationManagerCompat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;

    // Názov databázy
    public static final String DATABASE_NAME = "Database.db";

    // Konštanty pre tabuľku USERS
    public static final String TABLE_NAME_USERS = "users_table";
    public static final String USER_COL_0 = "FIRSTNAME";
    public static final String USERS_COL_1 = "LASTNAME";
    public static final String USER_COL_2 = "USERNAME";
    public static final String USERS_COL_3 = "PASSWORD";
    public static final String USER_COL_4 = "NAME";

    // Konštanty pre tabuľku CAR
    public static final String TABLE_NAME = "cars_table";
    public static final String CAR_COL_0 = "IDcar";
    public static final String CAR_COL_1 = "BRAND";
    public static final String CAR_COL_2 = "MODEL";
    public static final String CAR_COL3 = "YEAR";
    public static final String CAR_COL_4 = "ODOMETER";
    public static final String CAR_COL_5 = "NOTES";
    public static final String CAR_COL_6 = "FUEL";
    public static final String CAR_COL_7 = "IMAGE";
    public static final String CAR_COL_8 = "NAME";

    // Konštanty pre tabuľku EXPENSES
    public static final String TABLE_NAME_EXPENSES = "expenses_table";
    public static final String EXPENSES_COL_0 = "IDexpense";
    public static final String EXPENSES_COL_1 = "IDuser";
    public static final String EXPENSES_COL_2 = "LOCATION";
    public static final String EXPENSES_COL_3 = "TIME";
    public static final String EXPENSES_COL_4 = "DATE";
    public static final String EXPENSES_COL_5 = "COST";
    public static final String EXPENSES_COL_6 = "TYPE";

    // Konštanty pre tabuľku SERVICE
    public static final String TABLE_NAME_SERVICE = "services_table";
    public static final String SERVICE_COL_0 = "IDservice";
    public static final String SERVICE_COL_1 = "IDexpense";
    public static final String SERVICE_COL_2 = "IDuser";
    public static final String SERVICE_COL_3 = "LOCATION";
    public static final String SERVICE_COL_4 = "TIME";
    public static final String SERVICE_COL_5 = "DATE";
    public static final String SERVICE_COL_6 = "COST";
    public static final String SERVICE_COL_7 = "TYPE";

    // Konštanty pre tabuľku NOTIFICATIONS
    public static final String TABLE_NAME_NOTIFICATIONS = "notifications_table";
    public static final String NOTIFICATIONS_COL_0 = "IDnotification";
    public static final String NOTIFICATIONS_COL_1 = "IDuser";
    public static final String NOTIFICATIONS_COL_2 = "IDcar";
    public static final String NOTIFICATIONS_COL_3 = "DESCRIPTION";
    public static final String NOTIFICATIONS_COL_4 = "SERVICE_TYPE";
    public static final String NOTIFICATIONS_COL_5 = "LOCATION";
    public static final String NOTIFICATIONS_COL_6 = "RECURRING";
    public static final String NOTIFICATIONS_COL_7 = "DEADLINE";

    // Konštanty pre tabuľku REFUEL
    public static final String TABLE_NAME_REFUEL = "refuel_table";
    public static final String REFUEL_COL_1 = "IDuser";
    public static final String REFUEL_COL_2 = "DATE";
    public static final String REFUEL_COL_3 = "TIME";
    public static final String REFUEL_COL_4 = "AMOUNT";
    public static final String REFUEL_COL_5 = "PRICE_PER_LITER";
    public static final String REFUEL_COL_6 = "TOTAL_COST";
    public static final String REFUEL_COL_7 = "LOCATION";

    // Konštanty pre tabuľku ODOMETER
    public static final String TABLE_NAME_ODOMETER = "odometer_table";
    public static final String ODOMETER_COL_1 = "IDuser";
    public static final String ODOMETER_COL_2 = "DATE";
    public static final String ODOMETER_COL_3 = "KILOMETERS";

    // Konštruktor triedy
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 32);
        mContext = context;
    }

    // Metóda onCreate pre vytvorenie databázy
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Vytvorenie tabulky pre autá
        db.execSQL("create table " + TABLE_NAME + " (" +
                "IDcar INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "IDuser INTEGER, " +
                "NAME  VARCHAR(30) NOT NULL," +
                "BRAND  VARCHAR(30) NOT NULL," +
                "MODEL  VARCHAR(30) NOT NULL," +
                "YEAR INTEGER NOT NULL," +
                "ODOMETER FLOAT NOT NULL," +
                "NOTES TEXT NOT NULL," +
                "FUEL  VARCHAR(20) NOT NULL, " +
                "IMAGE BLOB, " +
                "FOREIGN KEY (IDuser) REFERENCES " + TABLE_NAME_USERS + "(IDuser)" +
                ");");

        // Vytvorenie tabuľky pre používateľov
        db.execSQL("create table " + TABLE_NAME_USERS + " (" +
                "IDuser INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "USERNAME VARCHAR(30) NOT NULL UNIQUE," +
                "FIRSTNAME VARCHAR(30) NOT NULL," +
                "LASTNAME VARCHAR(30) NOT NULL," +
                "PASSWORD TEXT NOT NULL" +
                ");");

        // Vytvorenie tabuľky pre tankovanie
        db.execSQL("CREATE TABLE " + TABLE_NAME_REFUEL + " (" +
                "IDrefuel INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "IDuser INTEGER, " +
                "IDcar INTEGER, " +
                "DATE TEXT NOT NULL," +
                "TIME TEXT NOT NULL," +
                "AMOUNT FLOAT NOT NULL," +
                "LOCATION TEXT NOT NULL," +
                "PRICE_PER_LITER FLOAT NOT NULL," +
                "TOTAL_COST FLOAT NOT NULL," +
                "FOREIGN KEY (IDuser) REFERENCES " + TABLE_NAME_USERS + "(IDuser)," +
                "FOREIGN KEY (IDcar) REFERENCES " + TABLE_NAME + "(IDcar)" +
                ");");


        // Vytvorenie tabuľky pre tachometer
        db.execSQL("CREATE TABLE " + TABLE_NAME_ODOMETER + " (" +
                "IDodometer INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "IDuser INTEGER, " +
                "IDcar INTEGER, " +
                "DATE TEXT NOT NULL," +
                "KILOMETERS FLOAT NOT NULL," +
                "FOREIGN KEY (IDuser) REFERENCES " + TABLE_NAME_USERS + "(IDuser)," +
                "FOREIGN KEY (IDcar) REFERENCES " + TABLE_NAME + "(IDcar)" +
                ");");

        // Vytvorenie tabuľky pre výdavky
        db.execSQL("CREATE TABLE " + TABLE_NAME_EXPENSES + " (" +
                "IDexpense INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "IDuser INTEGER, " +
                "IDcar INTEGER, " +
                "DATE TEXT NOT NULL," +
                "TIME TEXT NOT NULL," +
                "LOCATION TEXT NOT NULL,"+
                "COST FLOAT NOT NULL," +
                "TYPE FLOAT NOT NULL," +
                "FOREIGN KEY (IDuser) REFERENCES " + TABLE_NAME_USERS + "(IDuser)," +
                "FOREIGN KEY (IDcar) REFERENCES " + TABLE_NAME + "(IDcar)" +
                ");");


        // Vytvorenie tabuľky pre servis
        db.execSQL("CREATE TABLE " + TABLE_NAME_SERVICE + " (" +
                "IDservice INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "IDuser INTEGER, " +
                "IDcar INTEGER, " +
                "DATE TEXT NOT NULL," +
                "TIME TEXT NOT NULL," +
                "LOCATION TEXT NOT NULL,"+
                "COST FLOAT NOT NULL," +
                "TYPE FLOAT NOT NULL," +
                "FOREIGN KEY (IDuser) REFERENCES " + TABLE_NAME_USERS + "(IDuser)," +
                "FOREIGN KEY (IDcar) REFERENCES " + TABLE_NAME + "(IDcar)" +
                ");");

        // Vytvorenie tabuľky pre upozornenia
        db.execSQL("CREATE TABLE " + TABLE_NAME_NOTIFICATIONS + " (" +
                "IDnotification INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "IDuser INTEGER, " +
                "IDcar INTEGER, " +
                "DESCRIPTION TEXT NOT NULL," +
                "SERVICE_TYPE TEXT NOT NULL," +
                "LOCATION TEXT NOT NULL," +
                "RECURRING TEXT NOT NULL,"+
                "DEADLINE TEXT NOT NULL,"+
                "FOREIGN KEY (IDuser) REFERENCES " + TABLE_NAME_USERS + "(IDuser)," +
                "FOREIGN KEY (IDcar) REFERENCES " + TABLE_NAME + "(IDcar)" +
                ");");


    }

    // Metóda onUpgrade pre aktualizáciu databázy
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Odstránenie existujúcich tabuliek
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SERVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_REFUEL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ODOMETER);

        // Vytvorenie nových tabuliek
        onCreate(db);
    }


    // Metóda pre vkladanie údajov do tabuľky pre autá
    public boolean insertData(String name, String brand, String model, String year, String odometer, String notes, int userId, String selectedFuelType, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_4, name);
        contentValues.put(CAR_COL_1, brand);
        contentValues.put(CAR_COL_2, model);
        contentValues.put(CAR_COL3, year);
        contentValues.put(CAR_COL_4, odometer);
        contentValues.put(CAR_COL_5, notes);
        contentValues.put(REFUEL_COL_1, userId);
        contentValues.put(CAR_COL_6, selectedFuelType);
        contentValues.put(CAR_COL_7, image);

        try {
            long result = db.insert(TABLE_NAME, null, contentValues);
            Log.d("InsertQuery", "Query: " + result);
            return result != -1;
        } catch (SQLException e) {
            Log.e("InsertQuery", "SQLException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // Metóda pre získanie ID používateľa na základe používateľského mena
    @SuppressLint("Range")
    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1;

        String[] columns = {"IDuser"};
        String selection = "USERNAME = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_NAME_USERS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex("IDuser"));
        }

        cursor.close();
        return userId;
    }

    //metoda, ktorá vloží dáta o použivateľovi do databázy
    public boolean insertUserData( String firstName, String lastName,String username, String password, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Overí, či je používateľské meno jedinečné
        if (isUsernameUnique(username)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(USER_COL_0, firstName);
            contentValues.put(USERS_COL_1, lastName);
            contentValues.put(USER_COL_2, username);
            contentValues.put(USERS_COL_3, password);

            long result = db.insert(TABLE_NAME_USERS, null, contentValues);

            if (result != -1) {
                // Úspešne vložené
                return true;
            } else {
                // Vloženie neúspešné
                return false;
            }
        } else {
            // Používateľské meno už existuje, zobraziť oznámenie
            Toast.makeText(context, mContext.getString(R.string.username_is_already_taken), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //metoda, ktorá vloží dáta o tankovaní do tabuľky refuel
    public boolean insertRefuelData(int userId, int carId, String date, String time, String location, String amount, String pricePerLiter, String totalCost) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(REFUEL_COL_1, userId);
        contentValues.put(REFUEL_COL_2, date);
        contentValues.put(REFUEL_COL_3, time);
        contentValues.put(REFUEL_COL_4, amount);
        contentValues.put(REFUEL_COL_5, pricePerLiter);
        contentValues.put(REFUEL_COL_6, totalCost);
        contentValues.put(REFUEL_COL_7, location);
        contentValues.put(CAR_COL_0, carId);

        try {
            // Úspešne vložené
            long result = db.insert(TABLE_NAME_REFUEL, null, contentValues);
            Log.d("InsertQuery", "Query: " + result);
            return result != -1;
        } catch (SQLException e) {
            // Vloženie neúspešné
            Log.e("InsertQuery", "SQLException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //metoda, ktorá vloží dáta o výdavkoch do tabuľky expenses
    public boolean insertExpenseData(int userId,int carId, String date, String time, String location, String totalCost, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXPENSES_COL_1, userId);
        contentValues.put(EXPENSES_COL_3, time);
        contentValues.put(EXPENSES_COL_2, location);
        contentValues.put(EXPENSES_COL_4, date);
        contentValues.put(EXPENSES_COL_5, totalCost);
        contentValues.put(EXPENSES_COL_6, type);
        contentValues.put(CAR_COL_0, carId);


        try {
            //vloženie je úspešné
            long result = db.insert(TABLE_NAME_EXPENSES, null, contentValues);
            Log.d("InsertQuery", "Query: " + result);
            return result != -1;
        } catch (SQLException e) {
            //vloženie je neúspešné
            Log.e("InsertQuery", "SQLException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //metoda, ktorá vloží dáta o servise do tabuľky services
    public boolean insertServiceData(int userId, int carId, String date, String time, String location, String totalCost, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SERVICE_COL_2, userId);
        contentValues.put(SERVICE_COL_4, time);
        contentValues.put(SERVICE_COL_3, location);
        contentValues.put(SERVICE_COL_5, date);
        contentValues.put(SERVICE_COL_6, totalCost);
        contentValues.put(SERVICE_COL_7, type);
        contentValues.put(CAR_COL_0, carId);


        try {
            long result = db.insert(TABLE_NAME_SERVICE, null, contentValues);
            Log.d("InsertQuery", "Query: " + result);
            //vloženie je úspešné
            return result != -1;
        } catch (SQLException e) {
            //vloženie je neúspešné
            Log.e("InsertQuery", "SQLException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //metoda, ktorá vloží dáta o tachometri do tabuľky odometer
    public boolean insertOdometerData(int userId, int carId, String date, String kilometer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ODOMETER_COL_1, userId);
        contentValues.put(ODOMETER_COL_2, date);
        contentValues.put(ODOMETER_COL_3, kilometer);
        contentValues.put(CAR_COL_0, carId);


        try {
            //vloženie je úspešné
            long result = db.insert(TABLE_NAME_ODOMETER, null, contentValues);
            Log.d("InsertQuery", "Query: " + result);
            return result != -1;
        } catch (SQLException e) {
            //vloženie je neúspešné
            Log.e("InsertQuery", "SQLException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //metoda, ktorá vloží dáta o upozorneniach do tabuľky notifications
    public boolean insertNotificationData(Context context, int userId, int carId, String description, String serviceType, String location, String recurring, String deadline) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATIONS_COL_1, userId);
        contentValues.put(NOTIFICATIONS_COL_2, carId);
        contentValues.put(NOTIFICATIONS_COL_3, description);
        contentValues.put(NOTIFICATIONS_COL_4, serviceType);
        contentValues.put(NOTIFICATIONS_COL_5, location);
        contentValues.put(NOTIFICATIONS_COL_7, deadline);
        contentValues.put(NOTIFICATIONS_COL_6,recurring);
        contentValues.put(CAR_COL_0, carId);

        Log.d("InsertQuery", "Recurring value: " + recurring);

        try {
            long result = db.insert(TABLE_NAME_NOTIFICATIONS, null, contentValues);

            if (result != -1) {
                // vloženie je úspešné, naplánuj upozornenie
                if (!TextUtils.isEmpty(recurring)) {
                    //ked upozornenie je reccuring, tak len naplánuj na interval ktorý zadal použivateľ
                    scheduleRecurringNotification(context, userId, carId, description, serviceType, location, (int) result, recurring, deadline);
                } else {
                    // ked upozornenie nie je reccuring, tak len naplánuj na dátum, ktorý zadal použivateľ
                    long deadlineMillis = convertDeadlineToMillis(deadline);
                    String initialDeadline = "";
                    scheduleNotification(context, userId, carId, description, serviceType, location, deadlineMillis, (int) result, recurring, initialDeadline);
                }
            }

            Log.d("InsertQuery", "Query: " + result);
            return result != -1;
        } catch (SQLException e) {
            Log.e("InsertQuery", "SQLException: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // zatvoj databázu
            db.close();
        }
    }

    //metoda, ktorá aktualizuje termím spustenia upozornenia
    public boolean updateNotificationDeadline(int notificationId, String newDeadline) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATIONS_COL_6, newDeadline);

        String selection = NOTIFICATIONS_COL_0 + "=?";
        String[] selectionArgs = {String.valueOf(notificationId)};

        // Aktualizujte termín upozornenia v databáze
        int rowsUpdated = db.update(TABLE_NAME_NOTIFICATIONS, contentValues, selection, selectionArgs);
        db.close();

        return rowsUpdated > 0;
    }

    //metoda, ktorá slúži na nastavenia upozornenia, ktoré sa bude opakovať
    public void scheduleRecurringNotification(Context context, int userId, int carId, String description, String serviceType, String location, int previousNotificationId, String recurring, String initialDeadline) {
        try {
            // Vypočíta opakujúci sa interval v milisekundách
            long recurringMillis = calculateRecurringMillis(recurring, mContext);

            // Vypočíta ďalší termín na základe opakovaného intervalu
            long initialDeadlineMillis = convertDeadlineToMillis(initialDeadline);
            long currentMillis = System.currentTimeMillis();

            // Skontroluje, či je počiatočný termín v minulosti
            if (initialDeadlineMillis <= currentMillis) {
                // Ak áno, vypočítajte ďalší termín od aktuálneho času
                initialDeadlineMillis = currentMillis;
            }

            long nextDeadlineMillis = initialDeadlineMillis + recurringMillis;

            // Konvertujte ďalší termín na čitateľný dátum
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String nextDeadline = sdf.format(new Date(nextDeadlineMillis));

            Log.d("RecurringNotification", "Recurring: " + recurring);
            Log.d("RecurringNotification", "Initial Deadline: " + initialDeadline);
            Log.d("RecurringNotification", "Next Deadline: " + nextDeadline);

            // Aktualizuje ďalší termín v databáze
            updateNotificationDeadline(previousNotificationId, nextDeadline);

            // Naplánuje ďalšie opakujúce sa upozornenie s rovnakými parametrami
            scheduleNotification(context, userId, carId, description, serviceType, location, nextDeadlineMillis, previousNotificationId, recurring, initialDeadline);
        } catch (Exception e) {
            //error pri nastavení opakujúceho sa upozornenia
            Log.e("RecurringNotification", "Error in scheduleRecurringNotification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //metoda, ktorá konvertuje termín upozornenia na milisekundy
    public long convertDeadlineToMillis(String deadline) {
        // Ak je termín null, vráť 0
        if (deadline == null) {
            return 0;
        }

        // Parsuje dátum termínu
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date deadlineDate = sdf.parse(deadline);

            // Ak je dátum termínu platný, nastaví čas na  dátum termínu a vráti hodnotu v milisekundách
            if (deadlineDate != null) {
                Calendar deadlineCalendar = Calendar.getInstance();
                deadlineCalendar.setTime(deadlineDate);
                deadlineCalendar.set(Calendar.HOUR_OF_DAY, 7); // Nastaví hodinu na 7:00 ráno
                deadlineCalendar.set(Calendar.MINUTE, 0);
                deadlineCalendar.set(Calendar.SECOND, 0);
                deadlineCalendar.set(Calendar.MILLISECOND, 0);

                long deadlineMillis = deadlineCalendar.getTimeInMillis();

                Log.d("ConvertMillis", "Deadline Millis: " + deadlineMillis);

                return deadlineMillis;
            } else {
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //metoda, ktorá získa meno auta podľa jeho ID
    @SuppressLint("Range")
    public String getCarNameByCarId(int userId, int carId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String carName = "";

        // Query pre získanie názvu auta podľa jeho ID a ID používateľa
        String query = "SELECT " + CAR_COL_8 + " FROM " + TABLE_NAME +
                " WHERE " + CAR_COL_0 + " = ? AND " + REFUEL_COL_1 + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(carId), String.valueOf(userId)});

        try {
            // Ak kurzor nie je null a má aspoň jeden záznam, nastavíme názov auta na hodnotu z databázy
            if (cursor != null && cursor.moveToFirst()) {
                carName = cursor.getString(cursor.getColumnIndex(CAR_COL_8));
            }
        } finally {
            // Uzavrieme kurzor a databázu
            cursor.close();
            db.close();
        }

        return carName; // Vrátime názov auta
    }

    // Metóda na naplánovanie notifikácie
    @SuppressLint("ScheduleExactAlarm")
    public void scheduleNotification(Context context, int userId, int carId, String description, String serviceType, String location, long deadlineMillis, int notificationId, String recurring, String deadline) {
        try {
            createNotificationChannel(context); // Vytvorenie notifikačného kanálu

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // Vytvorenie intentu pre notifikačný receiver s potrebnými parametrami
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("notificationId", notificationId);
            intent.putExtra("userId", userId);
            intent.putExtra("carId", carId);
            intent.putExtra("DESCRIPTION", description);
            intent.putExtra("SERVICE_TYPE", serviceType);
            intent.putExtra("LOCATION", location);
            intent.putExtra("RECURRING", recurring);
            intent.putExtra("DEADLINE", deadline);

            // Vytvorenie PendingIntent pre notifikáciu
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    notificationId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );


            // Kontrola, či aplikácia môže naplánovať presné alarmy
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(deadlineMillis, pendingIntent);
                try {
                    // Pokus o nastavenie presného alarmu
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                    Log.d("NotificationTest", "Scheduled exact alarm");
                } catch (SecurityException e) {
                    // výpis výnimky SecurityException
                    Log.e("NotificationTest", "SecurityException: " + e.getMessage());
                }
            } else {
                // Pre staršie verzie Androidu použijeme setExact
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, deadlineMillis, pendingIntent);
                Log.d("NotificationTest", "Scheduled non-exact alarm");
            }
        } catch (Exception e) {
            //výpis erroru, keď zlyhá naplánovanie upozornenia
            e.printStackTrace();
        }
    }

    // Metóda na vytvorenie notifikačného kanálu
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // Vytvorenie notifikačného kanálu s danými parametrami
            NotificationChannel channel = new NotificationChannel(
                    "ChannelId",
                    "Channel Name",
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Vytvorenie notifikačného kanálu
            notificationManager.createNotificationChannel(channel);

            // Logovanie úspechu alebo chyby, debugovacie účely
            if (notificationManager.getNotificationChannel("ChannelId") != null) {
                Log.d("NotificationChannel", "Notification channel created successfully");
            } else {
                Log.e("NotificationChannel", "Error creating notification channel");
            }
        }
    }


    // Metóda na výpočet intervalu opakovaných notifikácií v milisekundách
    private long calculateRecurringMillis(String recurring, Context context) {
        // Definovanie premenných pre časové jednotky
        long WEEK_MILLIS = 7 * 24 * 60 * 60 * 1000L;  // 1 týždeň v milisekundách
        long MONTH_MILLIS = 30 * 24 * 60 * 60 * 1000L;  // 1 mesiac v milisekundách

        /*
        switch (recurring) {
            case weekString:
                return WEEK_MILLIS;
            case "1 month":
                return MONTH_MILLIS;
            case "2 months":
                return 2 * MONTH_MILLIS;
            case "3 months":
                return 3 * MONTH_MILLIS;
            case "4 months":
                return 4 * MONTH_MILLIS;
            case "5 months":
                return 5 * MONTH_MILLIS;
            case "6 months":
                return 6 * MONTH_MILLIS;
            case "7 months":
                return 7 * MONTH_MILLIS;
            case "8 months":
                return 8 * MONTH_MILLIS;
            case "9 months":
                return 9 * MONTH_MILLIS;
            case "10 months":
                return 10 * MONTH_MILLIS;
            case "11 months":
                return 11 * MONTH_MILLIS;
            case "12 months":
                return 12 * MONTH_MILLIS;
            default:
                return 0;
        }
        */

        // Zisti, aký je interval opakovania na základe reťazca reprezentujúceho opakovanie
        if (recurring.equals(mContext.getString(R.string.week_1))) {
            return WEEK_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_1))) {
            return MONTH_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_2))) {
            return 2 * MONTH_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_3))) {
            return 3 * MONTH_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_4))) {
            return 4 * MONTH_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_5))) {
            return 5 * MONTH_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_6))) {
            return 6 * MONTH_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_7))) {
            return 7 * MONTH_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_8))) {
            return 8 * MONTH_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_9))) {
            return 9 * MONTH_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_10))) {
            return 10 * MONTH_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_11))) {
            return 11 * MONTH_MILLIS;
        } else if (recurring.equals(mContext.getString(R.string.month_12))) {
            return 12 * MONTH_MILLIS;
        } else {
            return 0;
        }

    }



    // Metóda na overenie, či je používateľské meno jedinečné
    boolean isUsernameUnique(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_USERS + " WHERE " + USER_COL_2 + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean isUnique = cursor.getCount() == 0;

        cursor.close();
        return isUnique;
    }

    // Metóda na prihlásenie používateľa.
    public boolean loginUser(String username, String password) {
        // Získanie databázového objektu pre čítanie
        SQLiteDatabase db = this.getReadableDatabase();

        // Definovanie stĺpcov, ktoré sa majú vyhľadávať
        String[] columns = {USER_COL_2};

        // Podmienka pre vyhľadávanie - používateľské meno musí byť rovnaké ako zadané meno (username)
        String selection = USER_COL_2 + " = ?" + " AND " + USERS_COL_3 + " = ?";
        String[] selectionArgs = {username, password};

        // Vykonanie dotazu do databázy
        Cursor cursor = db.query(TABLE_NAME_USERS, columns, selection, selectionArgs, null, null, null);

        // Kontrola, či sa výsledok dotazu presunul na prvý riadok, čo znamená, že používateľ s daným menom a heslom existuje
        boolean userExists = cursor.moveToFirst();

        // Uzavretie kurzora
        cursor.close();

        // Návratová hodnota je true, ak používateľ existuje, inak false
        return userExists;
    }


    // Metóda na získanie údajov o aute podľa ID používateľa a ID auta
    public Cursor getCarDataByUserIdAndCarId(int userId, int carId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Definovanie stĺpcov, ktoré sa majú vyhľadávať
        String[] columns = {CAR_COL_8,CAR_COL_1, CAR_COL_2, CAR_COL3, CAR_COL_4, CAR_COL_5, CAR_COL_6, CAR_COL_7};

        //podmienky na vyhladávanie
        String selection = "IDuser = ? AND IDcar = ?";

        String[] selectionArgs = {String.valueOf(userId), String.valueOf(carId)};

        return db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
    }

    // Metóda na získanie ID posledne vloženého auta podľa ID používateľa
    public int getLastInsertedCarId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int lastInsertedCarId = -1;

        //dotaz na vyzhladávanie
        String query = "SELECT MAX(" + CAR_COL_0 + ") FROM " + TABLE_NAME +
                " WHERE " + REFUEL_COL_1 + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        try {
            if (cursor.moveToFirst()) {
                lastInsertedCarId = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }

        return lastInsertedCarId;
    }

    // Metóda pre získanie mena a priezviska používateľa na základe používateľského mena.
    @SuppressLint("Range")
    public String[] getFirstNameAndLastNameByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] nameInfo = new String[2]; // Pole na uchovanie mena a priezviska

        String tableName = TABLE_NAME_USERS;
        String usernameColumn = USER_COL_2;
        String firstNameColumn = USER_COL_0;
        String lastNameColumn = USERS_COL_1;

        // vykonaný dotaz do databázy na získanie mena a priezviska používateľa na základe zadaného používateľského mena.
        String selection = usernameColumn + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                tableName,
                new String[]{firstNameColumn, lastNameColumn},
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            nameInfo[0] = cursor.getString(cursor.getColumnIndex(firstNameColumn)); // Prvé meno
            nameInfo[1] = cursor.getString(cursor.getColumnIndex(lastNameColumn)); // Priezvisko
            cursor.close();
        }
        //vrati pole s meno a priezviskom
        return nameInfo;
    }

    // Metóda pre aktualizáciu hesla používateľa.
    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PASSWORD", newPassword);

        //  vykonaný dotaz do databázy na aktualizáciu hesla používateľa na základe zadaného používateľského mena.
        int rowsAffected = db.update(TABLE_NAME_USERS, contentValues, "USERNAME=?", new String[]{username});

        // Návratová hodnota je true, ak bol aspoň jeden riadok úspešne aktualizovaný, inak false.
        return rowsAffected > 0;
    }

    // Metóda na odstránenie údajov o aute pomocou identifikátorov používateľa a auta.
    public boolean deleteCarDataByUserId(int userId, int carId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            // Odstránenie súvisiacich záznamov
            db.delete(TABLE_NAME_REFUEL, "IDuser=? AND IDcar=?", new String[]{String.valueOf(userId), String.valueOf(carId)});
            db.delete(TABLE_NAME_SERVICE, "IDuser=? AND IDcar=?", new String[]{String.valueOf(userId), String.valueOf(carId)});
            db.delete(TABLE_NAME_EXPENSES, "IDuser=? AND IDcar=?", new String[]{String.valueOf(userId), String.valueOf(carId)});
            db.delete(TABLE_NAME_ODOMETER, "IDuser=? AND IDcar=?", new String[]{String.valueOf(userId), String.valueOf(carId)});
            db.delete(TABLE_NAME_NOTIFICATIONS, "IDuser=? AND IDcar=?", new String[]{String.valueOf(userId), String.valueOf(carId)});

            // Odstránenie auta
            int deletedRows = db.delete(TABLE_NAME, "IDuser=? AND IDcar=?", new String[]{String.valueOf(userId), String.valueOf(carId)});

            db.setTransactionSuccessful();

            // Návratová hodnota je true, ak bol aspoň jeden riadok úspešne odstránený, inak false.
            return deletedRows > 0;
        } finally {
            db.endTransaction();
        }
    }

    // Metóda na aktualizáciu údajov o aute na základe ID používateľa a IDauta.
    public boolean updateCarDataByUserId(int userId, int carId, String carName, String carBrand, String carModel, String carYear, String carOdometer, String notes, String fuelType, byte[] imageData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("NAME", carName);
        values.put("BRAND", carBrand);
        values.put("MODEL", carModel);
        values.put("YEAR", carYear);
        values.put("ODOMETER", carOdometer);
        values.put("NOTES", notes);
        values.put("FUEL", fuelType);
        values.put("IMAGE", imageData);

        String whereClause = "IDuser=? AND IDcar=?";
        String[] whereArgs = {String.valueOf(userId), String.valueOf(carId)};

        // Návratová hodnota je true, ak bol aspoň jeden riadok úspešne aktualizovaný, inak false.
        return db.update(TABLE_NAME, values, whereClause, whereArgs) > 0;
    }

    // Metóda na odstránenie používateľa na základe používateľského mena.
    public void deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        //  vykonaný dotaz do databázy na odstránenie záznamov používateľa podľa zadaného používateľského mena.
        db.delete(TABLE_NAME_USERS, USER_COL_2 + " = ?", new String[]{username});
        db.close();
    }

    // Metóda na získanie všetkých záznamov danej tabuľky podľa typu a ID používateľa a IDauta.
    // Je vykonaný dotaz do databázy na získanie všetkých záznamov danej tabuľky pre daného používateľa a auto.
    public Cursor getAllEntriesByTypeAndUser(String entryType, int userId, int carId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query;
        String tableName;

        switch (entryType) {
            case "odometer_table":
                tableName = TABLE_NAME_ODOMETER;
                break;
            case "expenses_table":
                tableName = TABLE_NAME_EXPENSES;
                break;
            case "services_table":
                tableName = TABLE_NAME_SERVICE;
                break;
            case "notifications_table":
                tableName = TABLE_NAME_NOTIFICATIONS;
                break;
            case "refuel_table":
                tableName = TABLE_NAME_REFUEL;
                break;
            default:
                // Spracovanie neznámeho typu záznamu
                return null;
        }

        try {
            // Pridanie podmienok pre ID používateľa a auta
            query = "SELECT * FROM " + tableName + " WHERE IDuser = " + userId + " AND IDcar = " + carId;
            Log.d("MainActivity", "Query: " + query);

            return db.rawQuery(query, null);
        } catch (Exception e) {
            Log.e("DatabaseError", "Error in getAllEntriesByTypeAndUser: " + e.getMessage());
            return null;
        }
    }

    // Metóda na odstránenie záznamu z danej tabuľky
    public void deleteEntry(String tableName, int userId, int carId, String columnName, String columnValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "IDuser = ? AND IDcar = ? AND " + columnName + " = ?";
        String[] whereArgs = {String.valueOf(userId), String.valueOf(carId), columnValue};


        int deletedRows = db.delete(tableName, whereClause, whereArgs);

        Log.d("DeleteEntry", "Deleted rows: " + deletedRows);

        db.close();
    }

    // Metóda na aktualizáciu hodnoty tachometra v databáze.
    public boolean updateOdometerValue(int userId, int carId, String newOdometerValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Získanie aktuálnej hodnoty tachometra z databázy
        String currentOdometer = getCurrentOdometerValue(userId, carId);

        // Vypočítanie súčtu aktuálnej a novej hodnoty tachometra
        float sumOdometer = Float.parseFloat(currentOdometer) + Float.parseFloat(newOdometerValue);
        String updatedOdometerValue = String.valueOf(sumOdometer) + " km";

        ContentValues values = new ContentValues();
        values.put(CAR_COL_4, updatedOdometerValue);

        Log.d("updatedOdometerValue", updatedOdometerValue);

        String whereClause = "IDuser=? AND IDcar=?";
        String[] whereArgs = {String.valueOf(userId), String.valueOf(carId)};

        // Aktualizácia riadku
        int rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);

        // Zatvorenie databázy
        db.close();

        // vráti true, ak bol aspoň jeden riadok aktualizovaný
        return rowsUpdated > 0;
    }

    // Metóda na aktualizáciu hodnoty tachometra v databáze s odlišným prístupom.
    public boolean updateOdometerValue2(int userId, int carId, String newOdometerValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Získanie aktuálnej hodnoty tachometra z databázy
        String currentOdometer = getCurrentOdometerValue(userId, carId);

        // Vypočítanie rozdielu aktuálnej a novej hodnoty tachometra
        float difference = Float.parseFloat(currentOdometer) - Float.parseFloat(newOdometerValue);

        // Zabezpečenie, aby bol výsledok nezáporný
        float sumOdometer = Math.max(0, difference);

        String updatedOdometerValue = String.valueOf(sumOdometer) + " km";

        Log.d("updatedOdometerValue", updatedOdometerValue);

        ContentValues values = new ContentValues();
        values.put(CAR_COL_4, updatedOdometerValue);

        String whereClause = "IDuser=? AND IDcar=?";
        String[] whereArgs = {String.valueOf(userId), String.valueOf(carId)};

        // Aktualizácia riadku
        int rowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);

        // Zatvorenie databázy
        db.close();

        // vráti true, ak bol aspoň jeden riadok aktualizovaný
        return rowsUpdated > 0;
    }

    // Metóda na získanie aktuálnej hodnoty tachometra z databázy.
    @SuppressLint("Range")
    public String getCurrentOdometerValue(int userId, int carId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // dotaz do databázy na získanie aktuálnej hodnoty tachometra pre používateľa a auta.
        String query = "SELECT " + CAR_COL_4 + " FROM " + TABLE_NAME +
                " WHERE IDuser = ? AND IDcar = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(carId)});

        String currentOdometer = "";

        try {
            if (cursor != null && cursor.moveToFirst()) {
                currentOdometer = cursor.getString(cursor.getColumnIndex(CAR_COL_4));

                // Extrahovanie číselnej časti bez " km"
                currentOdometer = extractNumericPart(currentOdometer);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // vrati hodnotu z tachometra ako retazec
        return currentOdometer;
    }

    // Metóda na extrahovanie číselnej časti z reťazca tachometra.
    // Extrahuje číselnú časť tachometra bez " km".
    private String extractNumericPart(String odometerWithUnit) {
        String numericPart = odometerWithUnit.replaceAll("[^\\d.]", "");
        return numericPart;
    }


}

