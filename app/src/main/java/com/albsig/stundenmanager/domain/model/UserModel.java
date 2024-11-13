package com.albsig.stundenmanager.domain.model;

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
