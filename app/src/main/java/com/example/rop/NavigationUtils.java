package com.example.rop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import maes.tech.intentanim.CustomIntent;

public class NavigationUtils {

    //metoda na otvorenie aktivity AddPage
    public static void openAddPage(Context context) {
        Intent intent = new Intent(context, AddPage.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie aktivity MyCar
    public static void openMyCarPage(Context context) {
        Intent intent = new Intent(context, MyCar.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie aktivity About
    public static void openAbout(Context context){
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie aktivity MyCars
    public static void openMyCars(Context context){
        Intent intent = new Intent(context, MyCarsActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie aktivity Contact
    public static  void openContact(Context context){
        Intent intent = new Intent(context, ContactActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie MainActivity
    public static void openHome(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"left-to-right" );

    }

    //metoda na otvorenie Statistics aktivity
    public static void openStatistics(Context context){
        Intent intent = new Intent(context, StatisticsActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"left-to-right" );

    }

    //metoda na otvorenie Notifications aktivity
    public static void openNotifications(Context context){
        Intent intent = new Intent(context, NotificationsActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"left-to-right" );

    }

    //metoda na otvorenie Fuel aktivity
    public static void openFuelPage(Context context){
        Intent intent = new Intent(context, FuelActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie Expense aktivity
    public static void openExpensePage(Context context){
        Intent intent = new Intent(context, ExpenseActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie Service aktivity
    public static void openServicePage(Context context){
        Intent intent = new Intent(context, ServiceActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie Odometer aktivity
    public static void openOdometerPage(Context context){
        Intent intent = new Intent(context, OdometerActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie AddNotifications aktivity
    public static void openAddNotificationsPage(Context context){
        Intent intent = new Intent(context, AddNotificationsActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie Login aktivity
    public static void openLogin(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie Password aktivity
    public static void openPassword(Context context){
        Intent intent = new Intent(context, PasswordActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }

    //metoda na otvorenie Register aktivity
    public static void openRegister(Context context){
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);

        ((Activity)context).finish();

        CustomIntent.customType(context,"bottom-to-up" );

    }


}