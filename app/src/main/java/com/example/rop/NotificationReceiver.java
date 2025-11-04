package com.example.rop;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    // Metóda volaná po prijatí intentu
    public void onReceive(Context context, Intent intent) {
        try {
            // Získanie ID notifikácie z intentu
            int notificationId = intent.getIntExtra("notificationId", 0);

            // Získanie ďalších informácií z intentu
            int userId = intent.getIntExtra("userId", 0);
            int carId = intent.getIntExtra("carId", -1);

            // Získanie detailov z  intentu
            String description = intent.getStringExtra("DESCRIPTION");
            String serviceType = intent.getStringExtra("SERVICE_TYPE");
            String location = intent.getStringExtra("LOCATION");
            String recurring = intent.getStringExtra("RECURRING");
            String deadline = intent.getStringExtra("DEADLINE");

            // Vytvorenie objektu DatabaseHelper
            DatabaseHelper databaseHelper = new DatabaseHelper(context);

            // Získanie názvu auta podľa jeho ID
            String carName = databaseHelper.getCarNameByCarId(userId, carId);

            // Vytvorenie notifikačného kanálu
            createNotificationChannel(context);

            // Vytvorenie obsahu notifikácie
            String notificationContent = context.getString(R.string.car) + carName + '\n'
                    + context.getString(R.string.service_type) + serviceType + '\n'
                    + context.getString(R.string.description_1) + description + '\n'
                    + context.getString(R.string.location_1) + location;

            // Vytvorenie notifikačného builderu
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "YourChannelId")
                    .setSmallIcon(R.drawable.baseline_notifications_24)
                    .setContentTitle(context.getString(R.string.notification))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationContent))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX);

            // Získanie inštancie NotificationManagerCompat

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            // Zobrazenie notifikácie
            notificationManager.notify(notificationId, builder.build());

            // Ak je notifikácia jednorazová, odstráni ju z databázy a aktualizuje zobrazenie v aktivite
            if (recurring.trim().isEmpty()) {
                deleteNotificationFromDatabase(context, userId, carId,"DESCRIPTION", description);
                Intent updateIntent = new Intent(NotificationsActivity.ACTION_UPDATE_CARD_CONTAINER);
                context.sendBroadcast(updateIntent);
            } else {
                // Ak je notifikácia opakujúca sa, naplánuje ju v databáze a aktualizuje zobrazenie v aktivite
                databaseHelper.scheduleRecurringNotification(context, userId, carId, description, serviceType, location, notificationId, recurring, deadline);
                Intent updateIntent = new Intent(NotificationsActivity.ACTION_UPDATE_CARD_CONTAINER);
                context.sendBroadcast(updateIntent);
            }
        } catch (Exception e) {
            // V prípade chyby zaznamenáva chybový log
            Log.e("NotificationError", "Error in onReceive: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Metóda pre vytvorenie notifikačného kanálu
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            NotificationChannel channel = new NotificationChannel(
                    "YourChannelId",
                    "Your Channel Name",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationManager.createNotificationChannel(channel);
        }
    }

    // Metóda pre odstránenie notifikácie z databázy
    private void deleteNotificationFromDatabase(Context context, int userId, int carId, String columnName, String columnValue) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        databaseHelper.deleteEntry("notifications_table", userId, carId, columnName, columnValue);
    }
}