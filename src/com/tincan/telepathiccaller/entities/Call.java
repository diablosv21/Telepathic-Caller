package com.tincan.telepathiccaller.entities;

import java.util.Calendar;

public class Call {
	String phoneNumber;
	String cachedName;
	String cachedNumberType;
	Calendar time;
	
	public Call(String phoneNumber, Calendar time, String cachedName, String cachedNumberType) {
		this.phoneNumber = phoneNumber;
		this.time = time;
		this.cachedName = cachedName;
		this.cachedNumberType = cachedNumberType;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}
	public String getCachedName() {
		return this.cachedName;
	}
	public String getCachedNumberType() {
		return this.cachedNumberType;
	}
	public Calendar getTime() {
		return this.time;
	}
	public String getTimeString() {
		return this.time.getTime().toLocaleString();
	}
}
