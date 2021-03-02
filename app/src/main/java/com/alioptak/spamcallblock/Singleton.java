package com.alioptak.spamcallblock;

import java.util.ArrayList;

public class Singleton {

    private static final Singleton ourInstance = new Singleton();

    public static Singleton getInstance() {
        return ourInstance;
    }

    private Singleton() {
        contacts = new ArrayList<Contact>();
        blockedNumbers = new ArrayList<String>();
        blockedContacts = new ArrayList<>();
    }

    private ArrayList<Contact> contacts;
    private ArrayList<String> blockedNumbers;
    private ArrayList<Contact> blockedContacts;
    private ArrayList<Call> history_calls= new ArrayList<>();

    /** CONTACTS **/
    public void block(Contact c){
        blockedNumbers.add(c.getPhone_number());
        blockedContacts.add(c);
    }

    public void blockContact(Contact c){
        blockedContacts.add(c);
    }

    /**
     * @param contact : The contact you want to unblock
     * @return status  : false if the contact is not found
     */
    public Boolean unblock(Contact contact){
        boolean status = false;
        this.blockedNumbers.remove(contact.getPhone_number());
        this.blockedContacts.remove(contact);
        return status;
    }

    public void setContacts(ArrayList<Contact> listContacts){
        this.contacts = new ArrayList<Contact>();
        // We need to make a deep copy in case it is wrongly modified in the code
        for(Contact c : listContacts){
            this.contacts.add(c);
        }
    }

    public ArrayList<Contact> getListContact () {
        return this.contacts;
    }

    public ArrayList<Contact> getListContactBlocked () {
        return this.blockedContacts;
    }
    public void setListContactBlocked(ArrayList<Contact> blockedContacts){this.blockedContacts = blockedContacts; }
    public ArrayList<String> getListNumberBlocked () {
        return this.blockedNumbers;
    }
    public void setListNumberBlocked (ArrayList<String> blockedNumbers) {
        this.blockedNumbers = blockedNumbers ;
    }

    public Contact getContactAtPosition(int position){
        return this.contacts.get(position);
    }
    public Contact getContactBlockedAtPosition(int position){return this.blockedContacts.get(position); }


    public Boolean isBlocked(Contact c){
        return this.blockedNumbers.indexOf(c.getPhone_number()) != -1;
    }

    public Integer getNumberContacts(){
        return this.contacts.size();
    }

    public Integer getNumberBlocked(){
        return this.blockedNumbers.size();
    }

    public Integer getNumberContactsBlocked(){
        return this.blockedContacts.size();
    }


    // we just need to subtract.
    public Integer getNumberNotBlocked(){
        return this.getNumberContacts() - this.getNumberBlocked();
    }


    /** CALLS **/
    public void addCall (Call call) {
        history_calls.add(call);
        System.out.println("Ajout "+ call.getPhNumber() + " " + call.getDate());
    }

    public int getNumberOfCall () {
        return history_calls.size();
    }

    public Call getCallAtPosition(int position){
        return history_calls.get(position);
    }

    public Boolean isBlocked(String phoneNumber){
        return this.blockedNumbers.indexOf(phoneNumber) != -1;
    }

    public void block(String phoneNumber){
        for (Contact contact: contacts) {
            if(contact.getPhone_number().contentEquals(phoneNumber)){
                blockedContacts.add(contact);
                break;
            }
        }
        blockedNumbers.add(phoneNumber);
    }

    /**
     * @param  phoneNumber : The contact you want to unblock
     * @return status  : false if the contact is not found
     */
    public Boolean unblock(String phoneNumber){
        for (Contact contact: blockedContacts) {
            if(contact.getPhone_number().contentEquals(phoneNumber)){
                blockedContacts.remove(contact);
                break;
            }
        }
        boolean status = false;
        for(String blocked_number : blockedNumbers){
            if(blocked_number.equalsIgnoreCase(phoneNumber)){
                status = true;
                this.blockedNumbers.remove(blocked_number);
                break;
            }
        }
        return status;
    }

    public void resetCalls(){
        this.history_calls = new ArrayList<Call>();
    }
}
