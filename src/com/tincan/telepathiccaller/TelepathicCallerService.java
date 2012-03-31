package com.tincan.telepathiccaller;

import java.util.ArrayList;

import com.tincan.telepathiccaller.controls.ContactsProcessor;
import com.tincan.telepathiccaller.entities.Contact;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class TelepathicCallerService extends Service {
	public static final String TAG = "TelepathicCallerService";
    public static final String COMPLETE_ACTION = "com.tincan.telepathiccaller.service.ProcessingCompleteEvent";
	boolean displayUnknowns = false;
	boolean forceUpdate = false;
	int numberOfContacts = 10;
	ArrayList<Contact> callersList = new ArrayList<	Contact>();
    private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

    public class LocalBinder extends Binder {
    	TelepathicCallerService getService() {
    		return TelepathicCallerService.this;
    	}
	}

	private void sendCompleteBroadcast() {
	    Intent broadcast = new Intent(COMPLETE_ACTION);
		sendBroadcast(broadcast);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		Bundle extras = intent.getExtras();
		if(extras != null && extras.containsKey("force_update")) {
			forceUpdate = extras.getBoolean("force_update");
		}
		
		ContactsProcessor cp = new ContactsProcessor(this);
		cp.ProcessContactsExec(forceUpdate);
		sendCompleteBroadcast();
        stopSelf();
	}
}
