package com.alioptak.spamcallblock;

import java.util.ArrayList;

public class Singleton {

    private static final Singleton ourInstance = new Singleton();

    public static Singleton getInstance() {
        return ourInstance;
    }

    private Singleton() {
         contacts = new ArrayList<Contact>();
         blockedContacts = new ArrayList<Contact>();
    }

    private ArrayList<Contact> contacts;
    private ArrayList<Contact> blockedContacts;

    public void block(Contact c){
        blockedContacts.add(c);
    }

    /**
     * @param  contact : The contact you want to unblock
     * @return status  : false if the contact is not found
     */
    public Boolean unblock(Contact contact){
        boolean status = false;
        for(Contact blocked_contact : blockedContacts){
            if(blocked_contact.getPhone_number().equalsIgnoreCase(contact.getPhone_number())){
                status = true;
                this.blockedContacts.remove(blocked_contact);
            }
        }
        return status;
    }

    public void setContacts(ArrayList<Contact> listContacts){
        // We need to make a deep copy in case it is wrongly modified in the code
        this.contacts = new ArrayList<Contact>();
        for(Contact c : listContacts){
            this.contacts.add(c);
        }
    }
}
