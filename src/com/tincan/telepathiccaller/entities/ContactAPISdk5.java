package com.tincan.telepathiccaller.entities;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.content.ContentResolver;

public class ContactAPISdk5 extends ContactAPI {
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
 		return(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI));
 	}
 	
 	public ContactList newContactList() {
 		ContactList contacts = new ContactList();
 		String id;
 		String lookupId;
 		
 		this.cur = this.cr.query(ContactsContract.Contacts.CONTENT_URI,
                 null, null, null, null);
 		if (this.cur.getCount() > 0) {
 			while (cur.moveToNext()) {
 				Contact c = new Contact();
 				id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
 				lookupId = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
 				c.setId(id);
 				c.setLookupId(lookupId);
 				c.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
 				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
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
 				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
 				null, 
 				ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
 				new String[]{id}, null);
 		while (pCur.moveToNext()) {
 			phones.add(new Phone(
 					pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
 					, pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
 			));
 
 		} 
 		pCur.close();
 		return(phones);
 	}
}
