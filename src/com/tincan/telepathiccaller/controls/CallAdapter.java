package com.tincan.telepathiccaller.controls;

import java.util.List;

import com.tincan.telepathiccaller.R;
import com.tincan.telepathiccaller.entities.Contact;
import com.tincan.telepathiccaller.entities.Phone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CallAdapter extends ArrayAdapter<Contact> {
	Context context;
	
	public CallAdapter(Context context, int textViewResourceId, List<Contact> items) {
		super(context, textViewResourceId, items);
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		if(row == null) {
			LayoutInflater inflater = LayoutInflater.from(this.getContext());
			row=inflater.inflate(R.layout.call_row, parent, false);
		}

		Contact contact = this.getItem(position);
		TextView lblDisplayName = (TextView)row.findViewById(R.id.lblDisplayName);
		TextView lblPhoneNumber = (TextView)row.findViewById(R.id.lblPhoneNumber);
		TextView lblPhoneType = (TextView)row.findViewById(R.id.lblPhoneType);
		ImageView imgContact = (ImageView)row.findViewById(R.id.imgContact);
		
        Phone phone = contact.getPhone().get(0);
		lblPhoneNumber.setText(phone.getNumber());

    	if(phone.getType().equals("")) {
    		lblPhoneType.setVisibility(TextView.GONE);
    	} else {
    		lblPhoneType.setVisibility(TextView.VISIBLE);
    		lblPhoneType.setText(phone.getType());
    	}
    	
		if(contact.getDisplayName() != null && !contact.getDisplayName().equals("")) {
			lblDisplayName.setText(contact.getDisplayName());
		} else {
			lblDisplayName.setText(phone.getNumber());
		}

		if(contact.getContactImage() == null) {
			imgContact.setImageResource(R.drawable.contact_icon);
		} else {
			imgContact.setImageBitmap(contact.getContactImage());
		}
		
		return row;
	}
}
