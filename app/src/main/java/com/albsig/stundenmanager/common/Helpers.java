package com.albsig.stundenmanager.common;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Helpers {

    public static String checkLoginMail(String email) {
        if (email == null)  return "Email is null";
        if(email.isEmpty())  return "Email cannot be empty";
        return "";
    }

    public static String checkLoginPassword(String password) {
        if (password == null)  return "Password is null";
        if(password.isEmpty())  return "Password cannot be empty";
        return "";
    }

    public static String FSTimestampToDateString(Timestamp timestamp) {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(timestamp.toDate());
    }
}
