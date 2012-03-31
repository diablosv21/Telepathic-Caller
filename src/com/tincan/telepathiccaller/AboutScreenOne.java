package com.tincan.telepathiccaller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class AboutScreenOne extends Activity {
	Button btnNext;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_one);
        
        
        btnNext = (Button)findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new NextClickListener());
    }
    
    class NextClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			startActivityForResult(new Intent(AboutScreenOne.this, AboutScreenTwo.class), 0);
		}
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
        	finish();
        }
    }
}
