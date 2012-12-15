package it.DiarioDiViaggio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DisplayNotification extends Activity {

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);       
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_notification);

		finish();
	}
}
