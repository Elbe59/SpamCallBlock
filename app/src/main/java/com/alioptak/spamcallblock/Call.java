package com.alioptak.spamcallblock;

import java.util.Date;

public class Call {

    private String phNumber;
    private String date;

    public Call (String phNumber, String date) {
        this.phNumber = phNumber;
        this.date = date;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public void setPhNumber(String contact) {
        this.phNumber = phNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
