package com.tincan.telepathiccaller.entities;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Contacts;
import android.provider.Contacts.People;

@SuppressWarnings("deprecation")
public class ContactAPISdk3 {
 	private Cursor cur;
 	private ContentResolver cr;
 	
 	public Cursor getCur() {
 		return cur;
 	}
 
 	public void setCur(Cursor cur) {
 		this.cur = cur;
 	}
 
 	public ContentResolver getCr() {
 		return cr;
 	}
 
 	public void setCr(ContentResolver cr) {
 		this.cr = cr;
 	}
 
 	public Intent getContactIntent() {
 		return(new Intent(Intent.ACTION_PICK, People.CONTENT_URI));
 	}
 	
 	public ContactList newContactList() {
 		ContactList contacts = new ContactList();
 		String id;
 		
 		this.cur = this.cr.query(People.CONTENT_URI, 
 				null, null, null, null);
 		if (this.cur.getCount() > 0) {
 			while (cur.moveToNext()) {
 				Contact c = new Contact();
 				id = cur.getString(cur.getColumnIndex(People._ID));
 				c.setId(id);
 				c.setDisplayName(cur.getString(cur.getColumnIndex(People.DISPLAY_NAME)));
 				if (Integer.parseInt(cur.getString(cur.getColumnIndex(People.PRIMARY_PHONE_ID))) > 0) {
 					c.setPhone(this.getPhoneNumbers(id));
 				}
 				contacts.addContact(c);
 			}
 		}
 		return(contacts);
 	}
 	
 	public ArrayList<Phone> getPhoneNumbers(String id) {
 		ArrayList<Phone> phones = new ArrayList<Phone>();
 		
 		Cursor pCur = this.cr.query(
 				Contacts.Phones.CONTENT_URI, 
 				null, 
 				Contacts.Phones.PERSON_ID +" = ?", 
 				new String[]{id}, null);
 		while (pCur.moveToNext()) {
 			phones.add(new Phone(
 					pCur.getString(pCur.getColumnIndex(Contacts.Phones.NUMBER))
 					, pCur.getString(pCur.getColumnIndex(Contacts.Phones.TYPE))
 			));
 
 		} 
 		pCur.close();
 		return(phones);
 	}
}
