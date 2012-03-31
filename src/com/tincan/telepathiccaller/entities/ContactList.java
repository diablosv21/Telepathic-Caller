package com.tincan.telepathiccaller.entities;

import java.util.ArrayList;

public class ContactList {
 	private ArrayList<Contact> contacts = new ArrayList<Contact>();
  	
 	public ContactList() {
 		
 	}
 	 
 	public ArrayList<Contact> getContacts() {
 		return contacts;
 	}
 
 	public void setContacts(ArrayList<Contact> contacts) {
 		this.contacts = contacts;
 	}
 	
 	public void addContact(Contact contact) {
 		this.contacts.add(contact);
 	}
 	
 	public Contact getContactByPhoneNumber(String phoneNumber) {
 		for(int i = 0; i < this.contacts.size(); i++) {
 			Contact contact = contacts.get(i);
 			ArrayList<Phone> phones = contact.getPhone();
 			for(int j = 0; j < phones.size(); j++) {
 				Phone phone = phones.get(j);
 				if(phone.getNumber().equals(phoneNumber)) {
 					return contact;
 				}
 			}
 		}
 		return null;
 	}
 	
 	public Contact getContactByID(String contactID) {
 		for(int i = 0; i < this.contacts.size(); i++) {
 			Contact contact = contacts.get(i);
			if(contact.getId().equals(contactID)) {
				return contact;
			}
 		}
 		return null;
 	}
}
