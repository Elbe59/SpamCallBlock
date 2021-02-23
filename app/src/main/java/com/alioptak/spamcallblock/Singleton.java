package com.alioptak.spamcallblock;

import java.util.ArrayList;

public class Singleton {

    private static final Singleton ourInstance = new Singleton();

    public static Singleton getInstance() {
        return ourInstance;
    }

    private Singleton() {
         contacts = new ArrayList<Contact>();
         contacts.add(new Contact(0, "Lucie", "Dolle", "0678787899"));
         blockedNumbers = new ArrayList<String>();
    }

    private ArrayList<Contact> contacts;
    private ArrayList<String> blockedNumbers;
    private ArrayList<Call> historic_calls;

    /** CONTACTS **/
    public void block(Contact c){
        blockedNumbers.add(c.getPhone_number());
    }

    /**
     * @param  contact : The contact you want to unblock
     * @return status  : false if the contact is not found
     */
    public Boolean unblock(Contact contact){
        boolean status = false;
        for(String blocked_number : blockedNumbers){
            if(blocked_number.equalsIgnoreCase(contact.getPhone_number())){
                status = true;
                this.blockedNumbers.remove(blocked_number);
            }
        }
        return status;
    }

    public void setContacts(ArrayList<Contact> listContacts){
        // We need to make a deep copy in case it is wrongly modified in the code
        for(Contact c : listContacts){
            this.contacts.add(c);
        }
    }

    public ArrayList<Contact> getListContact () {
        return this.contacts;
    }

    public Contact getContactAtPosition(int position){
        return this.contacts.get(position);
    }

    public Boolean isBlocked(Contact c){
        return this.blockedNumbers.indexOf(c.getPhone_number()) != -1;
    }

    public Integer getNumberContacts(){
        return this.contacts.size();
    }

    public Integer getNumberBlocked(){
        return this.blockedNumbers.size();
    }

    // we just need to subtract.
    public Integer getNumberNotBlocked(){
        return this.getNumberContacts() - this.getNumberBlocked();
    }


    /** CALLS **/
    public void addCall (Call call) {
        historic_calls.add(call);
    }

    public int getNumberOfCall () {
        return historic_calls.size();
    }

    public Call getCallAtPosition(int position){
        return historic_calls.get(position);
    }

    public Boolean isBlocked(String phoneNumber){
        return this.blockedNumbers.indexOf(phoneNumber) != -1;
    }

    public void block(String phoneNumber){
        blockedNumbers.add(phoneNumber);
    }

    /**
     * @param  phoneNumber : The contact you want to unblock
     * @return status  : false if the contact is not found
     */
    public Boolean unblock(String phoneNumber){
        boolean status = false;
        for(String blocked_number : blockedNumbers){
            if(blocked_number.equalsIgnoreCase(phoneNumber)){
                status = true;
                this.blockedNumbers.remove(blocked_number);
            }
        }
        return status;
    }
}
