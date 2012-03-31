package com.tincan.telepathiccaller;

import java.util.ArrayList;

import com.tincan.telepathiccaller.controls.CallerDatabase;
import com.tincan.telepathiccaller.entities.Contact;
import com.tincan.telepathiccaller.entities.Phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.QuickContact;

public class CallReceiver extends BroadcastReceiver {
	boolean useContactBadge;
	int contactBadgeSize;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		loadPreferences(context);

		if(useContactBadge && Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.ECLAIR_MR1) {
			String phoneNumber = intent.getData().getSchemeSpecificPart();
			
			CallerDatabase cdb = new CallerDatabase();
			cdb.Open(context, false);
			ArrayList<Contact> contacts = cdb.getCallers();
			cdb.Close();
			boolean userFound = false;
			
			for(int i = 0; i < contacts.size() && !userFound; i++) {
				Contact contact = contacts.get(i);
				ArrayList<Phone> phones = contact.getPhone();
				for(int j = 0; j < phones.size() && !userFound; j++) {
					Phone phone = phones.get(j);
					if(phone.getNumber().equals(phoneNumber)) {
						if(!contact.getLookupId().equals("")) {
							Uri uri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, contact.getLookupId());
							QuickContact.showQuickContact(context, intent.getSourceBounds(), uri, contactBadgeSize, null);
							return;
						}
						userFound = true;
					}
				}
			}
		}
		
		// Call the person directly
		Intent call1 = new Intent(Intent.ACTION_CALL);
    	call1.setData(intent.getData());
    	call1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(call1);
	}

    private void loadPreferences(Context context)
    {
    	SharedPreferences prefs=PreferenceManager
			.getDefaultSharedPreferences(context);
    	useContactBadge = prefs.getBoolean("useContactBadge", false);
    	contactBadgeSize = Integer.parseInt(prefs.getString("contactBadgeSize", "1"));
    }
}
