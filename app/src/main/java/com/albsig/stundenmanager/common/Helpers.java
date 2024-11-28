package com.albsig.stundenmanager.common;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Helpers {

    public static String checkLoginMail(String email) {
        if (email == null) return "Email is null";
        if (email.isEmpty()) return "Email cannot be empty";
        return "";
    }

    public static String checkLoginPassword(String password) {
        if (password == null) return "Password is null";
        if (password.isEmpty()) return "Password cannot be empty";
        return "";
    }

    public static String FSTimestampToDateString(Timestamp timestamp) {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(timestamp.toDate());
    }

    public static Timestamp createCustomTimestamp(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTime());
    }
}
