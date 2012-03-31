package com.tincan.telepathiccaller.controls;

import java.util.ArrayList;

import com.tincan.telepathiccaller.entities.Contact;
import com.tincan.telepathiccaller.entities.Phone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CallerDatabase {
	
	SQLiteDatabase db;
	
	public void Open(Context context, boolean writable) {
		android.util.Log.w("Telepathic Caller", "Opening DB Connection");
		if(writable) {
			db = (new DatabaseHelper(context)).getWritableDatabase();
		} else {
			db = (new DatabaseHelper(context)).getReadableDatabase();
		}
	}
	
	public void Close() {
		if(db != null && db.isOpen()) {
			db.close();
		}
	}

	public int getProfileNumber() {
		return Integer.parseInt(getMiscValue("Profile", "0"));
	}
	
	public void setProfileNumber(int profileNumber) {
		setMiscValue("Profile", String.valueOf(profileNumber));
	}

	public long getLastUpdate() {
		return Long.parseLong(getMiscValue("LastUpdate", "0"));
	}
	
	public void setLastUpdate(long lastUpdate) {
		setMiscValue("LastUpdate", String.valueOf(lastUpdate));
	}
	
	public boolean showAboutScreen() {
		return getMiscValue("AboutShown", "").equals("");
	}
	
	public void setAboutScreenShown() {
		setMiscValue("AboutShown", "Yes");
	}
	
	public void clearCallers() {
		db.execSQL("DELETE FROM callers");
	}
	
	public void addCaller(String displayName, String phoneNumber, String contactId, String numberType, String lookupId) {
		ContentValues values=new ContentValues(4);
		values.put("display_name", displayName);
		values.put("phone_number", phoneNumber);
		values.put("contact_id", contactId);
		values.put("lookup_id", lookupId);
		values.put("number_type", numberType);
		db.insert("callers", "display_name", values);
	}

	public ArrayList<Contact> getCallers() {
		ArrayList<Contact> callers = new ArrayList<Contact>();
		
		Cursor result= db.rawQuery("SELECT display_name, phone_number, number_type, contact_id, lookup_id FROM callers", null);
		result.moveToFirst();
		
		while (!result.isAfterLast()) {
			Contact newContact = new Contact();
			newContact.setDisplayName(result.getString(0));
			newContact.addPhone(new Phone(result.getString(1), result.getString(2)));
			newContact.setId(result.getString(3));
			newContact.setLookupId(result.getString(4));
			callers.add(newContact);
			result.moveToNext();
		}
		result.close();
		
		return callers;
	}

    private String getMiscValue(String key, String defaultValue) {
		Cursor result= db.rawQuery("SELECT value FROM data WHERE key='" + key + "'", null);
		String value = defaultValue;
		result.moveToFirst();
		if (!result.isAfterLast()) {
		  value=result.getString(0);
		}
		result.close();
		return value;
    }

    private void setMiscValue(String key, String value) {
		Cursor result= db.rawQuery("SELECT value FROM data WHERE key='" + key + "'", null);
		if (result.getCount() == 1) {
			ContentValues values=new ContentValues(2);
			values.put("value", value);
			db.update("data", values, "key='" + key + "'", null);
		} else {
			ContentValues values=new ContentValues(2);
			values.put("key", key);
			values.put("value", value);
			db.insert("data", "key", values);
		}
		result.close();
	}

	public void beginTransaction() {
		db.beginTransaction();
	}
	
	public void endTransaction(boolean success) {
		if(db.inTransaction()){
			if(success) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
		}
	}

	class DatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME="TelepathicCallerDB";
		private static final int DATABASE_VERSION = 2;

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE data (key TEXT PRIMARY KEY, value TEXT);");
			db.execSQL("CREATE TABLE callers (display_name TEXT, phone_number TEXT, contact_id TEXT, number_type TEXT, lookup_id TEXT);");
			setVersion(db);
		}
		
		private void setVersion(SQLiteDatabase db) {
			ContentValues values=new ContentValues(2);
			values.put("key", "version");
			values.put("value", DATABASE_VERSION);
			db.insert("data", "key", values);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			android.util.Log.w("Telepathic Caller", "Upgrading database");

			if(oldVersion < 2) {
				db.execSQL("DELETE FROM data;");
			    db.execSQL("DROP TABLE IF EXISTS callers;");
				db.execSQL("CREATE TABLE callers (display_name TEXT, phone_number TEXT, contact_id TEXT, number_type TEXT, lookup_id TEXT);");
			}
		}
	}
}
