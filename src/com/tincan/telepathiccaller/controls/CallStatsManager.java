package com.tincan.telepathiccaller.controls;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.content.Context;
import android.database.Cursor;

import com.tincan.telepathiccaller.entities.Call;
import com.tincan.telepathiccaller.entities.CallStats;
import com.tincan.telepathiccaller.entities.CallStatsComparator;
import com.tincan.telepathiccaller.entities.Profile;

public class CallStatsManager {
	ArrayList<CallStats> callList = new ArrayList<CallStats>();
	Calendar now;
	Context context;
	
	// Determine common time of day
	private int morningCalls = 0;
	private int lunchCalls = 0;
	private int afternoonCalls = 0;
	private int dinnerCalls = 0;
	private int nightCalls = 0;

	// Determine most common day of week
	private int mondayCalls = 0;
	private int tuesdayCalls = 0;
	private int wednesdayCalls = 0;
	private int thursdayCalls = 0;
	private int fridayCalls = 0;
	private int saturdayCalls = 0;
	private int sundayCalls = 0;

	// Determine frequency of use
	private int totalCalls = 0;

	Calendar twoMonthsAgo;
	
	public CallStatsManager(Context context) {
		now = Calendar.getInstance();
		this.context = context;

		twoMonthsAgo = Calendar.getInstance();
		twoMonthsAgo.add(Calendar.MONTH, -2);
		
		addCalls(getCallLog(context));
	}

	private void addCalls(ArrayList<Call> calls) {
		for(int i = 0; i < calls.size(); i++) {
			addCall(calls.get(i));
		}
	}
	
	private void addCall(Call newCall) {
		if(newCall.getTime().after(twoMonthsAgo)) {
			addToCallStats(newCall);
			addToGlobalStats(newCall);
		}
	}
	
	private void addToCallStats(Call newCall) {
		String shortNumber = newCall.getPhoneNumber();
		if(newCall.getPhoneNumber().length() > 8) {
			shortNumber = newCall.getPhoneNumber().substring(newCall.getPhoneNumber().length() - 8);
		}
		
		for(int i = 0; i < callList.size(); i++) {
			if(callList.get(i).getPhoneNumber().endsWith(shortNumber) ||
					(callList.get(i).getCachedName() != null && newCall.getCachedName() != null && 
							callList.get(i).getCachedName().equals(newCall.getCachedName()))) {
				callList.get(i).addTime(newCall.getTime());
				return;
			}
		}
		CallStats newCallStats = new CallStats(newCall.getPhoneNumber(), now, newCall.getCachedName(), newCall.getCachedNumberType());
		newCallStats.addTime(newCall.getTime());
		callList.add(newCallStats);
	}

	private void addToGlobalStats(Call newCall) {
		Calendar time = newCall.getTime();
		switch(time.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				mondayCalls++;
				break;
			case Calendar.TUESDAY:
				tuesdayCalls++;
				break;
			case Calendar.WEDNESDAY:
				wednesdayCalls++;
				break;
			case Calendar.THURSDAY:
				thursdayCalls++;
				break;
			case Calendar.FRIDAY:
				fridayCalls++;
				break;
			case Calendar.SATURDAY:
				saturdayCalls++;
				break;
			case Calendar.SUNDAY:
				sundayCalls++;
				break;
		}
		switch(time.get(Calendar.HOUR_OF_DAY)) {
			case 0: // Night
			case 1:
			case 2:
			case 3:
			case 4:
				nightCalls++;
				break;
			case 5: // Morning
			case 6:
			case 7:
			case 8:
			case 9:
				morningCalls++;
				break;
			case 10: // Lunch
			case 11: 
			case 12:
			case 13:
				lunchCalls++;
				break;
			case 14: // Afternoon
			case 15:
			case 16:
			case 17:
				afternoonCalls++;
				break;
			case 18: // Dinner
			case 19:
			case 20:
			case 21:
				dinnerCalls++;
				break;
			case 22: // Night
			case 23:
			case 24:
				nightCalls++;
				break;
		}
		totalCalls++;
	}
	
	public int getTimeOfDayType() {
		if(isHighestTimeOfDay(morningCalls)) {
			return Profile.MORNING_CALLER;
		} else if(isHighestTimeOfDay(lunchCalls)) {
			return Profile.LUNCH_CALLER;
		} else if(isHighestTimeOfDay(afternoonCalls)) {
			return Profile.AFTERNOON_CALLER;
		} else if(isHighestTimeOfDay(dinnerCalls)) {
			return Profile.DINNER_CALLER;
		} else {
			return Profile.NIGHT_CALLER;
		}
	}
	
	private boolean isHighestTimeOfDay(int timeOfDayCount) {
		return (timeOfDayCount >= morningCalls &&
				timeOfDayCount >= lunchCalls &&
				timeOfDayCount >= afternoonCalls &&
				timeOfDayCount >= dinnerCalls &&
				timeOfDayCount >= nightCalls);
	}

	public int getDayOfWeekType() {
		if(mondayCalls + tuesdayCalls + wednesdayCalls + thursdayCalls + fridayCalls > saturdayCalls + sundayCalls) {
			return Profile.WEEKDAY_CALLER;
		} else {
			return Profile.WEEKEND_CALLER;
		} 
	}
	
	public int getCallAmountType() {
		if(totalCalls < 10) {
			return Profile.RARE_CALLER;
		} else if (totalCalls < 50) {
			return Profile.OCCASIONAL_CALLER;
		} else if (totalCalls < 200) {
			return Profile.AVERAGE_CALLER;
		} else if (totalCalls < 600) {
			return Profile.FREQUENT_CALLER;
		} else {
			return Profile.INTENSE_CALLER;
		}
	}
	
	public int getCallVariationType() {
		double variation = Double.longBitsToDouble(callList.size()) / Double.longBitsToDouble(totalCalls);
		if(variation < 0.1) {
			return Profile.FOCUSSED_CALLER;
		} else if(variation < 0.6) {
			return Profile.EVEN_CALLER;
		} else {
			return Profile.BROAD_CALLER;
		}
	}
	
	public ArrayList<CallStats> getCallStatsOrdered() {
		double callCountWeight = 0.25d;
		double recentCountWeight = 0.50d;
		double timeOfDayWeight = 4.0d;
		double dayOfWeekWeight = 2.0d;

		switch (getCallVariationType())
		{
		case Profile.FOCUSSED_CALLER:
			callCountWeight = 0.5d;
			recentCountWeight = 0.6d;
			break;
		case Profile.BROAD_CALLER:
			callCountWeight = 0.1d;
			recentCountWeight = 0.4d;
			break;
		}
		CallStatsComparator comparator = new CallStatsComparator(callCountWeight, recentCountWeight, timeOfDayWeight, dayOfWeekWeight);
		Collections.sort(callList, comparator);
		return callList;
	}

    private ArrayList<Call> getCallLog(Context context) {
		// Querying for a cursor is like querying for any SQL-Database
		Cursor c = context.getContentResolver().query(
				android.provider.CallLog.Calls.CONTENT_URI,
				null, null, null, 
				android.provider.CallLog.Calls.DATE + " DESC");
		
		// Retrieve the column-indixes of phoneNumber, date and calltype
		int numberColumn = c.getColumnIndex(
				android.provider.CallLog.Calls.NUMBER);
		int dateColumn = c.getColumnIndex(
				android.provider.CallLog.Calls.DATE);
		// type can be: Incoming, Outgoing or Missed
		int typeColumn = c.getColumnIndex(
				android.provider.CallLog.Calls.TYPE);
		// Cached Contact ID
		int nameColumn = c.getColumnIndex(
				android.provider.CallLog.Calls.CACHED_NAME);
		// Cached Contact ID
		int numberTypeColumn = c.getColumnIndex(
				android.provider.CallLog.Calls.CACHED_NUMBER_TYPE);
		// Cached Contact ID
		int durationColumn = c.getColumnIndex(
				android.provider.CallLog.Calls.DURATION);
		
		// Will hold the calls, available to the cursor
		ArrayList<Call> callList = new ArrayList<Call>();
		
		
		// Loop through all entries the cursor provides to us.
		if(c.moveToFirst()){
			do{
				int callType = c.getInt(typeColumn);
				
				if(callType == android.provider.CallLog.Calls.OUTGOING_TYPE) {
					Calendar cal = Calendar.getInstance();
					String callerPhoneNumber = c.getString(numberColumn);
					String cachedName = c.getString(nameColumn);
					int duration = c.getInt(durationColumn);
					if(duration < 10) {
						continue;
					}
					String cachedNumberType = "";
					int cachedNumberTypeInt = c.getInt(numberTypeColumn);
					if(cachedNumberTypeInt == 1 || cachedNumberTypeInt == 4) {
						cachedNumberType = "H";
					} else if(cachedNumberTypeInt == 2 || cachedNumberTypeInt > 5) {
						cachedNumberType = "M";
					} else if(cachedNumberTypeInt == 3 || cachedNumberTypeInt == 5) {
						cachedNumberType = "W";
					}
					
					long callDate = c.getLong(dateColumn);
					cal.setTimeInMillis(callDate);
					callList.add(new Call(callerPhoneNumber, cal, cachedName, cachedNumberType ));
				}
			}while(c.moveToNext());
		}
		
		c.close();
		
		return callList;
    }
}
