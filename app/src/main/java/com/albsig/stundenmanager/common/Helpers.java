package com.albsig.stundenmanager.common;

import android.widget.Toast;

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
}
