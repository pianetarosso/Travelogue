package it.DiarioDiViaggio;

import Settings.MySettings;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.SeekBar;

public class Settings extends Activity {

	private static SeekBar yourDialogSeekBar;
	private static Dialog yourDialog;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.settings);
	 }
	
	 public void gps(View view) {
		 
		 int screenWidth = view.getWidth();
		 
		 yourDialog = new Dialog(this);
		 LayoutInflater inflater =  (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		 View layout = inflater.inflate(R.layout.gps_dialog, (ViewGroup)findViewById(R.id.gps_dialog));
		 
		 int width = (screenWidth * 9) / 10 ;
		 yourDialog.setContentView(layout, new LayoutParams(width, LayoutParams.WRAP_CONTENT));

		 yourDialog.setTitle(R.string.gps_setting_info);

		 yourDialogSeekBar = (SeekBar)layout.findViewById(R.id.timeGpsSeekBar);
		 final EditText text = (EditText)layout.findViewById(R.id.timeGpsEditText);
		 
		 
		 yourDialogSeekBar.setOnSeekBarChangeListener(
				 new SeekBar.OnSeekBarChangeListener() {
					
					public void onStopTrackingTouch(SeekBar seekBar) {}
					
					public void onStartTrackingTouch(SeekBar seekBar) {}
					
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						text.setText((progress+1)+" "+ getString(R.string.minuti));
					}
				});
		 
		 yourDialogSeekBar.setEnabled(true);
		 yourDialogSeekBar.setMax(14);
		 
		 MySettings ms = new MySettings(this);
		 yourDialogSeekBar.setProgress(ms.getIntervalloGps()-1);
		 
		 yourDialog.show();
	 }
	 
	 public void reset(View view) {
		 yourDialogSeekBar.setProgress(2);
	 }
	 
	 public void save(View view) {
		 int seek = yourDialogSeekBar.getProgress()+1;
		 
		 MySettings ms = new MySettings(this);
		 ms.setIntervalloGps(seek);
		 ms.saveToSP(this);
		
		 if (ms.isGps_service()) {
			 Intent i = new Intent(this, it.Service.GPS.MonitorService.class);
			 startService(i);
		 }
		 yourDialog.dismiss();
	 }
	 
	 public void timer(View view) {
		 Intent i = new Intent(this, SetTimer.class);
		 startActivity(i);
	 }
	 
	 public void backup_restore(View view) {
		 Intent i = new Intent(this, Backup_Restore.class);
		 startActivity(i);
	 }
}
