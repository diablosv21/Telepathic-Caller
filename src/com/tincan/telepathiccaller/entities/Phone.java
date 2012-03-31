package com.tincan.telepathiccaller.entities;

public class Phone {
 	private String number;
 	private String type;
 	
 	public String getNumber() {
 		return number;
 	}
 
 	public void setNumber(String number) {
 		this.number = number;
 	}
 
 	public String getType() {
 		return type;
 	}
 
 	public void setType(String type) {
 		this.type = type;
 	}
 
 	public Phone(String n, String t) {
 		this.number = n;
 		this.type = t;
 	}
}
