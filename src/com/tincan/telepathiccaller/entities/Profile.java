package com.tincan.telepathiccaller.entities;

public class Profile {

	public static final int MORNING_CALLER = 0;
	public static final int LUNCH_CALLER = 1;
	public static final int AFTERNOON_CALLER = 2;
	public static final int DINNER_CALLER = 3;
	public static final int NIGHT_CALLER = 4;

	public static final int WEEKDAY_CALLER = 5;
	public static final int WEEKEND_CALLER = 6;

	public static final int RARE_CALLER = 7;
	public static final int OCCASIONAL_CALLER = 8;
	public static final int AVERAGE_CALLER = 9;
	public static final int FREQUENT_CALLER = 10;
	public static final int INTENSE_CALLER = 11;

	public static final int FOCUSSED_CALLER = 12;
	public static final int EVEN_CALLER = 13;
	public static final int BROAD_CALLER = 14;
	
	
	private String name;
	private String description;
	private int profileID;
	private int imageID;
	
	private int timeOfDayType;
	private int dayOfWeekType;
	private int callAmountType;
	private int callVariationType;
	
	public Profile(int profileID, String name, String description, int imageID, 
			int timeOfDayType, int dayOfWeekType, int callAmountType, int callVariationType) {
		this.profileID = profileID;
		this.name = name;
		this.description = description;
		this.imageID = imageID;
		
		this.timeOfDayType = timeOfDayType;
		this.dayOfWeekType = dayOfWeekType;
		this.callAmountType = callAmountType;
		this.callVariationType = callVariationType;
	}
	
	public int getProfileID() {
		return this.profileID;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public int getImageID() {
		return this.imageID;
	}
	
	public int getTimeOfDayType() {
		return this.timeOfDayType;
	}
	
	public int getDayOfWeekType() {
		return this.dayOfWeekType;
	}
	
	public int getCallAmountType() {
		return this.callAmountType;
	}
	
	public int getCallVariationType() {
		return this.callVariationType;
	}
}
