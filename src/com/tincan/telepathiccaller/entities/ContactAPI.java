package com.tincan.telepathiccaller.entities;

import android.os.Build;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;

public abstract class ContactAPI {
 	private static ContactAPI api;
 	
 	@SuppressWarnings("unused")
	private Cursor cur;
 	@SuppressWarnings("unused")
 	private ContentResolver cr;
 	
 	public static ContactAPI getAPI() {
 		if (api == null) {
 			String apiClass;
 			if (Integer.parseInt(Build.VERSION.SDK) >= Build.VERSION_CODES.ECLAIR) {
 				apiClass = "com.tincan.telepathiccaller.entities.ContactAPISdk5";
 			} else {
 				apiClass = "com.tincan.telepathiccaller.entities.ContactAPISdk3";
 			}
 			
 			try {
 				Class<? extends ContactAPI> realClass = Class.forName(apiClass).asSubclass(ContactAPI.class);
 				api = realClass.newInstance();
 			} catch (Exception e) {
 				throw new IllegalStateException(e);
 			}
 			
 		}
 		return api;
 	}
 	
 	public abstract Intent getContactIntent();
 	
 	public abstract ContactList newContactList();
 	
 	public abstract Cursor getCur();
 	public abstract void setCur(Cursor cur);
 	
 	public abstract ContentResolver getCr();
 	public abstract void setCr(ContentResolver cr);
}
