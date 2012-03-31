package com.tincan.telepathiccaller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class AboutScreenTwo extends Activity {
	Button btnBack;
	Button btnFinish;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_two);

        btnFinish = (Button)findViewById(R.id.btnFinish);
        btnBack = (Button)findViewById(R.id.btnBack);

        btnFinish.setOnClickListener(new FinishClickListener());
        btnBack.setOnClickListener(new BackClickListener());
    }

    class BackClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
    }
    
    class FinishClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			setResult(Activity.RESULT_OK);
			finish();
		}
    }
}
