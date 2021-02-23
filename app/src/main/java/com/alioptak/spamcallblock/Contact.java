package com.alioptak.spamcallblock;

public class Contact {
    private int id;
    private String firstname;
    private String lastname;
    private String phone_number;
    private boolean isBlock;

    public Contact( int id, String firstname,String lastname,String phone_number, boolean isBlock) {
    this.id = id;
    this.firstname = firstname;
    this.lastname = lastname;
    this.phone_number = phone_number;
    this.isBlock = isBlock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }
}
