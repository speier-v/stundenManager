package com.albsig.stundenmanager.domain.model;

import com.albsig.stundenmanager.common.Constants;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserModel {
    private String uid;
    private String email;
    private String name;
    private String surname;
    private String date;
    private String street;
    private String zipCode;
    private String city;

    public UserModel() {}

    public UserModel(String uid, String email, String name, String surname, String date, String street, String zipCode, String city) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.date = date;
        this.street = street;
        this.zipCode = zipCode;
        this.city = city;
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
}
