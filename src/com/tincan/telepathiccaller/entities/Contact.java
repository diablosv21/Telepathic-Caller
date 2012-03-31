package com.tincan.telepathiccaller.entities;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class Contact {
 	private String id = "";
 	private String lookupId = "";
 	private String displayName = "";
 	private ArrayList<Phone> phone = new ArrayList<Phone>();
	Bitmap contactImage;

 	public String getId() {
 		return id;
 	}
 	public void setId(String id) {
  		this.id = id;
 	}
 	public String getLookupId() {
 		return lookupId;
 	}
 	public void setLookupId(String lookupId) {
  		this.lookupId = lookupId;
 	}
 	public String getDisplayName() {
 		return displayName;
 	}
 	public void setDisplayName(String dName) {
 		this.displayName = dName;
 	}
 	public ArrayList<Phone> getPhone() {
 		return phone;
 	}
 	public void setPhone(ArrayList<Phone> phone) {
 		this.phone = phone;
 	}
 	public void addPhone(Phone phone) {
 		this.phone.add(phone);
 	}
	public Bitmap getContactImage() {
		return contactImage;
	}
	public void setContactImage(Bitmap contactImage) {
		this.contactImage = contactImage;
	}
}
