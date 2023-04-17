package com.example.checkup;

public class PhoneNumbers {
    private String phone;
    private String userID;

    public PhoneNumbers(String phone, String userID) {
        this.phone = phone;
        this.userID = userID;
    }

    public PhoneNumbers()
    {

    }

    public String getPhone() {
        return phone;
    }

    public String getUserID() {
        return userID;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
