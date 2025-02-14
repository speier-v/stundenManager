package com.albsig.stundenmanager.domain.model;

import com.albsig.stundenmanager.common.Constants;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class UserModel {
    private String uid;
    private String email;
    private String name;
    private String surname;
    private String date;
    private String street;
    private String zipCode;
    private String city;
    private String role;

    public UserModel() {
    }

    public UserModel(String uid, String email, String name, String surname, String date, String street, String zipCode, String city, String role) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.date = date;
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;
        this.role = role;
    }

    public UserModel(String uid, String email, DocumentSnapshot documentSnapshot) {
        this.uid = uid;
        this.email = email;
        this.name = documentSnapshot.getString(Constants.USER_MODEL_NAME);
        this.surname = documentSnapshot.getString(Constants.USER_MODEL_SURNAME);
        this.date = documentSnapshot.getString(Constants.USER_MODEL_BIRTHDAY);
        this.street = documentSnapshot.getString(Constants.USER_MODEL_STREET);
        this.zipCode = documentSnapshot.getString(Constants.USER_MODEL_ZIP_CODE);
        this.city = documentSnapshot.getString(Constants.USER_MODEL_CITY);
        this.role = documentSnapshot.getString(Constants.USER_MODEL_ROLE);
    }

    public UserModel(Map<String, Object> userMap) {
        this.uid = (String) userMap.get(Constants.USER_MODEL_ID);
        this.email = (String) userMap.get(Constants.USER_MODEL_EMAIL);
        this.name = (String) userMap.get(Constants.USER_MODEL_NAME);
        this.surname = (String) userMap.get(Constants.USER_MODEL_SURNAME);
        this.date = (String) userMap.get(Constants.USER_MODEL_BIRTHDAY);
        this.street = (String) userMap.get(Constants.USER_MODEL_STREET);
        this.zipCode = (String) userMap.get(Constants.USER_MODEL_ZIP_CODE);
        this.city = (String) userMap.get(Constants.USER_MODEL_CITY);
        this.role = (String) userMap.get(Constants.USER_MODEL_ROLE);
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getRole() {
        return role;
    }
}
