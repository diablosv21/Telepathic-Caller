package com.tincan.telepathiccaller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import com.tincan.telepathiccaller.controls.*;
import com.tincan.telepathiccaller.entities.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.QuickContact;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class ContactsList extends Activity {
	TelephonyManager telManager;
	PhoneStateListener listener;

	TextView lblProfileDescription;
	TextView lblProfileSpacer;
	Button btnSlideHandle;
	ImageButton btnRefresh;
	ImageButton btnSettings;
	ImageView imgProfile;
	SlidingDrawer sldProfile;
	LinearLayout pnlProfile;
	LinearLayout pnlLoading;
	TextView lblLoading;
	ProgressBar prgLoading;
	
	ArrayList<Contact> latestContacts = new ArrayList<Contact>();
	ListView lstCalls;
	CallAdapter callAdapter;
	
	AsyncTask<Boolean, Contact, Profile> showContacts;
	
	Profile myProfile;
	boolean displayUnknowns;
	boolean useContactBadge;
	boolean showProfile;
	int numberOfContacts = 10;
	int contactBadgeSize = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.call_monitor);
        loadPreferences();

        lstCalls = (ListView)findViewById(R.id.lstCalls);
        lstCalls.setOnItemClickListener(new ContactClickListener());
        
        lblProfileDescription = (TextView)findViewById(R.id.lblProfileDescription);
        lblProfileSpacer = (TextView)findViewById(R.id.lblProfileSpacer);
        btnSlideHandle = (Button)findViewById(R.id.btnSlideHandle);
        btnRefresh = (ImageButton)findViewById(R.id.btnRefresh);
        btnSettings = (ImageButton)findViewById(R.id.btnSettings);
        imgProfile = (ImageView)findViewById(R.id.imgProfile);
        sldProfile = (SlidingDrawer)findViewById(R.id.sldProfile);
        pnlProfile = (LinearLayout)findViewById(R.id.pnlProfile);
        pnlLoading = (LinearLayout)findViewById(R.id.pnlLoading);
        lblLoading = (TextView)findViewById(R.id.lblLoading);
        prgLoading = (ProgressBar)findViewById(R.id.prgLoading);
        
        callAdapter = new CallAdapter(this, R.layout.call_row, latestContacts);
        lstCalls.setAdapter(callAdapter);

        btnRefresh.setOnClickListener(new RefreshClickListener());
        btnSettings.setOnClickListener(new SettingsClickListener());
        pnlProfile.setOnClickListener(new ProfileClickListener());
        
        restoreMe();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	try {
			if(showContacts != null && showContacts.getStatus() == AsyncTask.Status.RUNNING) {
				showContacts.cancel(true);
			}
			unregisterReceiver(receiver);
    	} catch (Exception ex) {}
    }
    
    private void startTelepathicService(boolean forceUpdate) {
		if(showContacts == null || showContacts.getStatus() == AsyncTask.Status.FINISHED) {
			CallerDatabase cdb = new CallerDatabase();
			boolean showAboutScreen = true;
			try {
				cdb.Open(ContactsList.this, true);
		        showAboutScreen = cdb.showAboutScreen();
	        	cdb.setAboutScreenShown();
	        	cdb.Close();
			} catch (Exception ex) {}

	        if(showAboutScreen) {
				startActivityForResult(new Intent(ContactsList.this, AboutScreenOne.class), 0);
	        }
	        else {
	    	    try {
	            	registerReceiver(receiver, new IntentFilter(TelepathicCallerService.COMPLETE_ACTION));
	        	} catch (Exception ex) {}
        	
    	        showContacts = new ShowContactsTask();
    	        showContacts.execute(forceUpdate);
	        }
		}
    }

    private BroadcastReceiver receiver=new BroadcastReceiver() {
    	public void onReceive(Context context, Intent intent) {
    		if(intent.getAction().equals(TelepathicCallerService.COMPLETE_ACTION)) {
    		}
    	}
    };
    
    public class ProfileClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
	        // Do nothing, this is to avoid click-throughs
		}
    }

    public class RefreshClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			startTelepathicService(true);
			//if(showContacts == null || showContacts.getStatus() == AsyncTask.Status.FINISHED) {
		    //    showContacts = new ShowContactsTask();
		    //    showContacts.execute();
			//}
		}
    }

    public class SettingsClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			startActivity(new Intent(ContactsList.this, EditPreferences.class));
		}
    }
    
    public class ContactClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			Contact caller = latestContacts.get(position);
			
			if(useContactBadge && caller.getDisplayName() != null && !caller.getDisplayName().equals("")) {
				View imageView = v.findViewById(R.id.imgContact);
				Uri uri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, caller.getLookupId());
				QuickContact.showQuickContact(ContactsList.this, imageView, uri, contactBadgeSize, null);
			} else {
		    	Intent call = new Intent(Intent.ACTION_CALL);
		    	call.setData(Uri.parse("tel:" + caller.getPhone().get(0).getNumber()));
				startActivity(call);
			}
		}
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	boolean displayUnknownsOld = Boolean.valueOf(displayUnknowns);
    	boolean showProfileOld = Boolean.valueOf(showProfile);
    	int numberOfContactsOld = Integer.valueOf(numberOfContacts);
    	
    	loadPreferences();
    	if(displayUnknownsOld != displayUnknowns || 
    			numberOfContactsOld != numberOfContacts) {
	        startTelepathicService(true);
    	}

    	if(showProfileOld != showProfile) {
    		displayProfile(myProfile);
    	}
    	
    	if(useContactBadge && Integer.parseInt(Build.VERSION.SDK) <= Build.VERSION_CODES.ECLAIR_MR1) {
    		new AlertDialog.Builder(this)
				.setTitle(R.string.alert)
				.setMessage(R.string.badge_disabled)
				.setPositiveButton(R.string.OK, null)
				.show();

        	SharedPreferences prefs=PreferenceManager
    			.getDefaultSharedPreferences(this);
        	Editor editor = prefs.edit();
        	editor.putBoolean("useContactBadge", false);
        	editor.commit();
        	useContactBadge = false;
    	}
    }

    @Override
    public Object onRetainNonConfigurationInstance()
    {
    	return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startTelepathicService(false);
    }
    
	private void restoreMe() {
		if(getLastNonConfigurationInstance() != null) {
			ContactsList cl = (ContactsList)getLastNonConfigurationInstance();
			displayProfile(cl.myProfile);
			latestContacts.addAll(cl.latestContacts);
			callAdapter.notifyDataSetChanged();
			
			if(cl.showContacts != null && cl.showContacts.getStatus() == AsyncTask.Status.RUNNING) {
	        	startTelepathicService(false);
			} else {
	    		prgLoading.setVisibility(ProgressBar.GONE);
	    		btnRefresh.setVisibility(Button.VISIBLE);
	        	if(latestContacts.size() > 0) {
	        		pnlLoading.setVisibility(LinearLayout.GONE);
	        		lstCalls.setVisibility(ListView.VISIBLE);
	        	} else {
	        		pnlLoading.setVisibility(LinearLayout.VISIBLE);
	        		lstCalls.setVisibility(ListView.GONE);
	        		lblLoading.setText(R.string.no_predictions);
	        	}
			}

		} else {
        	startTelepathicService(false);
		}
	}

    private void loadPreferences()
    {
    	SharedPreferences prefs=PreferenceManager
			.getDefaultSharedPreferences(this);
    	displayUnknowns = prefs.getBoolean("displayUnknowns", false);
    	numberOfContacts = Integer.parseInt(prefs.getString("numberOfContacts", "10"));
    	useContactBadge = prefs.getBoolean("useContactBadge", false);
    	showProfile = prefs.getBoolean("showProfile", true);
    	contactBadgeSize = Integer.parseInt(prefs.getString("contactBadgeSize", "1"));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && sldProfile != null &&
        		sldProfile.isOpened()) {
        	sldProfile.animateClose();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.menu.contact_list, menu);
		return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      return(applyMenuChoice(item) ||
              super.onOptionsItemSelected(item));
    }

    private boolean applyMenuChoice(MenuItem item) {
        switch (item.getItemId()) {
    	case R.id.mnuPreferences:
			startActivity(new Intent(this, EditPreferences.class));
			return true;
    	case R.id.mnuAbout:
			startActivity(new Intent(this, AboutScreenOne.class));
			return true;
        }
        return(false);
    }
    
    public Bitmap getPhoto(ContentResolver contentResolver, Long contactId) {
        Uri contactPhotoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        // contactPhotoUri --> content://com.android.contacts/contacts/1557
        InputStream photoDataStream = Contacts.openContactPhotoInputStream(contentResolver,contactPhotoUri); // <-- always null
        Bitmap photo = BitmapFactory.decodeStream(photoDataStream);
        return photo;
    }
    
    private void displayProfile(Profile profile) {
    	if(profile != null) {
    		if(showProfile) {
				sldProfile.setVisibility(SlidingDrawer.VISIBLE);
				lblProfileSpacer.setVisibility(TextView.VISIBLE);
    		} else {
				sldProfile.setVisibility(SlidingDrawer.GONE);
				lblProfileSpacer.setVisibility(TextView.GONE);
    		}
			btnSlideHandle.setText(profile.getName());
			lblProfileDescription.setText(profile.getDescription());
			myProfile = profile;
		}
    }
    
    private String getLoadingText() {
    	CharSequence[] strings;
    	Resources res = getResources();
    	strings = res.getTextArray(R.array.loadingText);
    	Random rand = new Random();
    	return strings[rand.nextInt(strings.length - 1)].toString();
    }
    
    class ShowContactsTask extends AsyncTask<Boolean, Contact, Profile> {
    	@Override
    	protected void onPreExecute()
    	{
    		pnlLoading.setVisibility(LinearLayout.VISIBLE);
    		lstCalls.setVisibility(ListView.GONE);
    		prgLoading.setVisibility(ProgressBar.VISIBLE);
    		lblLoading.setText(getLoadingText());
    		btnRefresh.setVisibility(Button.GONE);

    		latestContacts.clear();
    	}
    	
		@Override
		protected Profile doInBackground(Boolean... forceUpdate) {
			ContactsProcessor cp = new ContactsProcessor(ContactsList.this);
			ArrayList<Contact> callersList;
			
			if(forceUpdate[0] != null) {
				callersList = cp.ProcessContactsExec(forceUpdate[0]);
			} else {
				callersList = cp.ProcessContactsExec(false);
			}

			int displayCount = 0;
	        
	        for(int i = 0; i < callersList.size() && displayCount < numberOfContacts; i++) {
	        	Contact contact = callersList.get(i);
	        	if(!contact.getId().equals("")) {
	        		Bitmap contactImage = getPhoto(getContentResolver(), Long.parseLong(contact.getId()));
	        		if(contactImage != null) {
	        			contact.setContactImage(contactImage);
	        		}
	        	}
        		if(displayUnknowns || !contact.getId().equals("")) {
		        	this.publishProgress(contact);
		        	displayCount++;
        		}
	        }
	        
	        return cp.getProfile();
		}

        public Bitmap getPhoto(ContentResolver contentResolver, Long contactId) {
            Uri contactPhotoUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
            InputStream photoDataStream = Contacts.openContactPhotoInputStream(contentResolver,contactPhotoUri);
            if(photoDataStream == null) {
            	return null;
            }
            Bitmap photo = BitmapFactory.decodeStream(photoDataStream);
            return photo;
        }

		@Override
        protected void onProgressUpdate(Contact... contacts) {
			for(int i = 0; i < contacts.length; i++) {
				latestContacts.add(contacts[i]);
			}
			callAdapter.notifyDataSetChanged();
		}
		
		@Override
		protected void onCancelled() {
    		super.onCancelled();
    	}

    	@Override
        protected void onPostExecute(Profile profile) {
    		displayProfile(profile);
    		
    		prgLoading.setVisibility(ProgressBar.GONE);
    		btnRefresh.setVisibility(Button.VISIBLE);
        	if(latestContacts.size() > 0) {
        		pnlLoading.setVisibility(LinearLayout.GONE);
        		lstCalls.setVisibility(ListView.VISIBLE);
    			callAdapter.notifyDataSetChanged();
        	} else {
        		pnlLoading.setVisibility(LinearLayout.VISIBLE);
        		lstCalls.setVisibility(ListView.GONE);
        		lblLoading.setText(R.string.no_predictions);
        	}
    	}
    }
}