package it.DiarioDiViaggio;

import it.Alarm.SetAlarms;
import Settings.TimerSettings;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetTimer extends Activity {
	
	/**
	 */
	private CheckBox abilita_timer;
	/**
	 */
	private TimePicker timePicker;
	
	/**
	 */
	private TimerSettings tm;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_timer);	
		tm = new TimerSettings(this);
		loadSelected();		
	}
	
		
	private void loadSelected() {	
		abilita_timer = (CheckBox)findViewById(R.id.checkBox_abilita_timer);
		timePicker = (TimePicker)findViewById(R.id.timePicker);
		
		abilita_timer.setChecked(tm.isEnabled());
		timePicker.setEnabled(tm.isEnabled());
		
		if (tm.isSet()) {
			timePicker.setCurrentHour(tm.getHour());
			timePicker.setCurrentMinute(tm.getMinute());
		}
		
		enableMasterListeners();	
	}
	
	private void enableMasterListeners() {
		
		abilita_timer.setOnCheckedChangeListener(
				new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						timePicker.setEnabled(isChecked);						
					}
				});
	}
	
	public void save(View view) {
		
		tm.setEnabled(abilita_timer.isChecked());
		if (abilita_timer.isChecked()) {
			tm.setHour(timePicker.getCurrentHour());
			tm.setMinute(timePicker.getCurrentMinute());
		}
		tm.saveToSP(this);
		
		Toast.makeText(this, getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
		
		SetAlarms setAlarms = new SetAlarms(this);
		setAlarms.loadAlarm();
	}
}
