package com.albsig.stundenmanager.domain.model;

import com.albsig.stundenmanager.common.Constants;

public class WorkerModel {
    private final String userReference;
    private final String name;
    private final String surname;
    private int shift;
    private boolean isSelected;

    public WorkerModel(String docId, String name, String surname) {
        this.userReference = "/" + Constants.USERS_COLLECTION + "/" + docId;
        this.name = name;
        this.surname = surname;
        this.shift = Constants.NO_SHIFT_SELECTED;
    }

    public String getUserReference() {
        return userReference;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }
}
