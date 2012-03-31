package com.tincan.telepathiccaller.controls;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Contacts;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.tincan.telepathiccaller.CallReceiver;
import com.tincan.telepathiccaller.CallerWidget;
import com.tincan.telepathiccaller.CallerWidgetLarge;
import com.tincan.telepathiccaller.R;
import com.tincan.telepathiccaller.entities.CallStats;
import com.tincan.telepathiccaller.entities.Contact;
import com.tincan.telepathiccaller.entities.ContactAPI;
import com.tincan.telepathiccaller.entities.ContactList;
import com.tincan.telepathiccaller.entities.Phone;
import com.tincan.telepathiccaller.entities.Profile;
import com.tincan.telepathiccaller.entities.Profiles;

public class ContactsProcessor {
	public static final String TAG = "TelepathicCallerProcessor";
	private CallerDatabase cdb;
	private Long updateFrequency;
	boolean displayUnknowns = false;
	boolean forceUpdate = false;
	int numberOfContacts = 10;
	Context context;
	ArrayList<Contact> callersList = new ArrayList<	Contact>();
	Profile profile;
	
	public ContactsProcessor(Context context) {
		this.context = context;
	}
	
	public Profile getProfile() {
		return profile;
	}

	public ArrayList<Contact> ProcessContactsExec(boolean forceUpdate) {
		try {
			loadPreferences();
			cdb = new CallerDatabase();
			cdb.Open(context, true);
	
			long lastUpdate = cdb.getLastUpdate();
	    	Date now = new Date();
	        long nowLong = now.getTime();
	        long updateTime = lastUpdate + updateFrequency; 
	    	if(nowLong > updateTime || forceUpdate) {
	    		updateCallers();
	    	} else {
	    		callersList = cdb.getCallers();
	    		Profiles profiles = new Profiles();
	    		profile = profiles.getProfile(cdb.getProfileNumber());
	    	}
	    	cdb.Close();
			updateWidgets();
		} catch (Exception ex) {}
		return callersList;
	}
	
	private void updateCallers() {
		cdb.clearCallers();
		CallStatsManager callStatsManager = new CallStatsManager(context);
        ArrayList<CallStats> newStats = callStatsManager.getCallStatsOrdered();

        ContactAPI contacts = ContactAPI.getAPI();
        contacts.setCr(context.getContentResolver());
        ContactList contactList = contacts.newContactList();
        ArrayList<Contact> realList = contactList.getContacts();
        int displayCount = 0;
        
        for(int i = 0; i < newStats.size() && displayCount < numberOfContacts; i++) {
        	CallStats callStats = newStats.get(i);
        	Contact contact = findContact(realList, newStats.get(i));
        	if(contact != null) {
        		addCaller(contact.getDisplayName(), callStats.getPhoneNumber(), contact.getId(), callStats.getCachedNumberType(), contact.getLookupId());
        	} else {
        		addCaller(callStats.getCachedName(), callStats.getPhoneNumber(), "", callStats.getCachedNumberType(), "");
        	}
        }
        
        Profiles profiles = new Profiles();
        profile = profiles.getMatchingProfile(
        		callStatsManager.getTimeOfDayType(), 
        		callStatsManager.getDayOfWeekType(), 
        		callStatsManager.getCallAmountType(), 
        		callStatsManager.getCallVariationType());
        
        cdb.setProfileNumber(profile.getProfileID());

    	Date now = new Date();
        long nowLong = now.getTime();
        cdb.setLastUpdate(nowLong);
	}
	
	private void updateWidgets() {
    	RemoteViews smallWidgetUpdate = new RemoteViews(context.getPackageName(), R.layout.widget_2x1);
    	RemoteViews largeWidgetUpdate = new RemoteViews(context.getPackageName(), R.layout.widget_4x1);
		int counter = 1;
		for(int i = 0; i < callersList.size(); i++) {
			if(displayUnknowns || !callersList.get(i).getId().equals("")) {
				Contact contact = callersList.get(i);
				if(counter == 1) {
					smallWidgetUpdate = updateView(smallWidgetUpdate, contact, R.id.pnlContact1, R.id.lblPhoneType1, R.id.lblContact1, R.id.imgContact1);
					largeWidgetUpdate = updateView(largeWidgetUpdate, contact, R.id.pnlContact1, R.id.lblPhoneType1, R.id.lblContact1, R.id.imgContact1);
				} else if(counter == 2) {
					smallWidgetUpdate = updateView(smallWidgetUpdate, contact, R.id.pnlContact2, R.id.lblPhoneType2, R.id.lblContact2, R.id.imgContact2);
					largeWidgetUpdate = updateView(largeWidgetUpdate, contact, R.id.pnlContact2, R.id.lblPhoneType2, R.id.lblContact2, R.id.imgContact2);
				} else if(counter == 3) {
					largeWidgetUpdate = updateView(largeWidgetUpdate, contact, R.id.pnlContact3, R.id.lblPhoneType3, R.id.lblContact3, R.id.imgContact3);
				} else if(counter == 4) {
					largeWidgetUpdate = updateView(largeWidgetUpdate, contact, R.id.pnlContact4, R.id.lblPhoneType4, R.id.lblContact4, R.id.imgContact4);
					break;
				}
				counter++;
			}
		}

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        
        ComponentName smallWidget = new ComponentName(context, CallerWidget.class);
        manager.updateAppWidget(smallWidget, smallWidgetUpdate);

        ComponentName largeWidget = new ComponentName(context, CallerWidgetLarge.class);
        manager.updateAppWidget(largeWidget, largeWidgetUpdate);
	}

    public Bitmap getPhoto(ContentResolver contentResolver, String contactId) {
    	try {
	        Uri contactPhotoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.parseLong(contactId));
	        // contactPhotoUri --> content://com.android.contacts/contacts/1557
	        InputStream photoDataStream = Contacts.openContactPhotoInputStream(contentResolver,contactPhotoUri); // <-- always null
	        if(photoDataStream == null) {
	        	return null;
	        }
	        Bitmap photo = BitmapFactory.decodeStream(photoDataStream);
	        return photo;
    	} catch (Exception ex) {
    		return null;
    	}
    }

	private RemoteViews updateView(RemoteViews updateViews, Contact contact, 
			int contactPanel, int phoneTypePanel, int phoneNumberPanel, int imagePanel) {
		Phone phone = contact.getPhone().get(0);
		if(contact.getDisplayName() == null || contact.getDisplayName().equals("")) {
			updateViews.setTextViewText(phoneNumberPanel, phone.getNumber());
		} else {
			updateViews.setTextViewText(phoneNumberPanel, contact.getDisplayName());
		}
    	Bitmap photo1 = getPhoto(context.getContentResolver(), contact.getId());
    	if(photo1 != null) {
    		updateViews.setImageViewBitmap(imagePanel, photo1);
    	} else {
    		updateViews.setImageViewResource(imagePanel, R.drawable.contact_icon);
    	}
		
		Intent call1 = new Intent(context, CallReceiver.class);
    	call1.setData(Uri.parse("tel:" + phone.getNumber()));
    	updateViews.setOnClickPendingIntent(contactPanel, PendingIntent.getBroadcast(context, 0, call1, 0));

    	if(phone.getType().equals("")) {
    		updateViews.setTextViewText(phoneTypePanel, "?");
    	} else {
    		updateViews.setTextViewText(phoneTypePanel, phone.getType());
    	}
    	updateViews.setViewVisibility(phoneTypePanel, TextView.VISIBLE);
    	
    	return updateViews;
	}
	
	private void addCaller(String displayName, String phoneNumber, String contactId, String numberType, String lookupId) {
		Contact newContact = new Contact();
		newContact.setDisplayName(displayName);
		newContact.addPhone(new Phone(phoneNumber, numberType));
		newContact.setId(contactId);
		newContact.setLookupId(lookupId);
		cdb.addCaller(displayName, phoneNumber, contactId, numberType, lookupId);
		callersList.add(newContact);
	}

    private void loadPreferences()
    {
    	SharedPreferences prefs=PreferenceManager
			.getDefaultSharedPreferences(context);
    	displayUnknowns = prefs.getBoolean("displayUnknowns", false);
    	numberOfContacts = Integer.parseInt(prefs.getString("numberOfContacts", "10"));
    	updateFrequency = Long.parseLong(prefs.getString("updateFrequency", "14")) * 60000;
    }

	private Contact findContact(ArrayList<Contact> contactList, CallStats caller) {
		if(caller == null || caller.getPhoneNumber() == null) {
			return null;
		}
		for(int i = 0; contactList != null && i < contactList.size(); i++) {
			Contact contact = contactList.get(i);
			if(contact != null) {
    			for(int j = 0; contact.getPhone() != null && j < contact.getPhone().size(); j++) {
    				if(contact.getPhone().get(j).getNumber() != null &&
    					contact.getPhone().get(j).getNumber().equals(caller.getPhoneNumber())) {
        				return contact;
    				}
    			}
    			if(caller.getCachedName() != null && contact.getDisplayName() != null &&
    					contact.getDisplayName().equals(caller.getCachedName())) {
    				return contact;
    			}
			}
		}
		return null;
	}
}
