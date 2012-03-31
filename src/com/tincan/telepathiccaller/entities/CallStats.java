package com.tincan.telepathiccaller.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.graphics.Bitmap;

public class CallStats {
	String phoneNumber;
	String cachedName;
	String cachedNumberType;
	List<Calendar> times;
	Calendar now;
	Contact contact;
	Bitmap contactImage;
	
	Calendar firstCall;
	Calendar lastCall;

	int weekDayCount = 0;
	int recentCount = 0;
	int timeOfDayCount = 0;
	
	public CallStats(String phoneNumber, Calendar now, String cachedName, String cachedNumberType) {
		this.phoneNumber = phoneNumber;
		this.now = now;
		this.cachedName = cachedName;
		this.cachedNumberType = cachedNumberType;
		times = new ArrayList<Calendar>();
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
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public Bitmap getContactImage() {
		return contactImage;
	}
	public void setContactImage(Bitmap contactImage) {
		this.contactImage = contactImage;
	}
	public void addTime(Calendar time) {
		times.add(time);
		
		if(firstCall == null || time.before(firstCall)) {
			firstCall = time;
		}
		if(lastCall == null || time.after(lastCall)) {
			lastCall = time;
		}
		
		if(time.get(Calendar.DAY_OF_WEEK) == now.get(Calendar.DAY_OF_WEEK)) {
			weekDayCount++;
		}
		if(time.get(Calendar.HOUR_OF_DAY) > now.get(Calendar.HOUR_OF_DAY) - 1 &&
				time.get(Calendar.HOUR_OF_DAY) < now.get(Calendar.HOUR_OF_DAY) + 1) {
			recentCount++;
		}
		if(time.getTimeInMillis() > (now.getTimeInMillis() - 2592000000l)) {
			recentCount++;
		}
	}

	public int getWeekDayCount() {
		return this.weekDayCount;
	}
	public int getRecentCount() {
		return this.recentCount;
	}
	public int getTimeOfDayCount() {
		return this.timeOfDayCount;
	}
	public int getCallCount() {
		return this.times.size();
	}
	public double getCallFrequencyScale() {
		double sinceLast = this.now.getTimeInMillis() - this.lastCall.getTimeInMillis();
		double period = sinceLast / 2;
		
		if(times.size() > 1) {
			long difference = this.lastCall.getTimeInMillis() - this.firstCall.getTimeInMillis();
			period = difference / times.size() - 1;
		}
		
		if(sinceLast < period) {
			return sinceLast / period;
		} else {
			return period / sinceLast;
		}
	}

	public double getScore(double callCountWeight, double recentCountWeight, double timeOfDayWeight, double dayOfWeekWeight) {
		double score = 0;
		score += getCallCount() * callCountWeight;
		score += getRecentCount() * recentCountWeight;
		score += getTimeOfDayCount() * timeOfDayWeight;
		score += getWeekDayCount() * dayOfWeekWeight;
		return (score * getCallFrequencyScale());
	}
}
