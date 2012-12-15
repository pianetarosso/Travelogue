package it.DiarioDiViaggio;

import it.Alarm.SetAlarms;
import it.Date.DateManipulation;
import it.Travel.Travel;

import java.util.Calendar;

import Settings.TimerSettings;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TravelWizard extends Activity {
	
	/**
	 */
	private DatePicker datePicker;
	/**
	 */
	private TimePicker timePicker;
	/**
	 */
	private CheckBox getDate;
	/**
	 */
	private CheckBox setTimer;
	/**
	 */
	private EditText nomeViaggio;
	/**
	 */
	private TimerSettings timerSettings;
	/**
	 */
	private Travel travel;
	/**
	 */
	private int mYear;
	/**
	 */
	private int mMonth;
	/**
	 */
	private int mDay;
	/**
	 */
	private boolean test=false;

	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travelwizard);
        
        travel = new Travel(this);
        nomeViaggio = (EditText)findViewById(R.id.editTextNomeViaggio);
		nomeViaggio.setText(travel.getNome());
		
		datePicker = (DatePicker)findViewById(R.id.datePicker1);
		timePicker = (TimePicker)findViewById(R.id.timePicker1);
		
		getDate = (CheckBox)findViewById(R.id.checkBox_enable_start);
		setTimer = (CheckBox)findViewById(R.id.checkBox_enable_timer);
		
		datePicker.setEnabled(false);
		timePicker.setEnabled(false);
		setTimer.setEnabled(false);
		
		timerSettings = new TimerSettings(this);
		
		setTimer.setChecked(timerSettings.isSet());
		
		if (timerSettings.isSet()) {
			timePicker.setCurrentHour(timerSettings.getHour());
			timePicker.setCurrentMinute(timerSettings.getMinute());
		}
		
		setListeners();
	}
	
	private void setListeners() {
		getDate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				datePicker.setEnabled(isChecked);
				setTimer.setEnabled(isChecked);
				timePicker.setEnabled(isChecked && setTimer.isChecked());
			}
		});
		
		setTimer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				timePicker.setEnabled(isChecked && getDate.isChecked());
			}
		});
	}
	
	private long returnData() { // funzione per recuperare e convertire la data dal datepicker
		mYear = datePicker.getYear();
		mMonth = datePicker.getMonth();
		mDay = datePicker.getDayOfMonth();
		return DateManipulation.parseTimeMs(mDay, mMonth, mYear);	
	}

	
	public void saveData (View view) { // Salvataggio

		Travel travel = new Travel(this);
		travel.setNome(nomeViaggio.getText().toString());
		EditText descrizioneET = (EditText)findViewById(R.id.editTextDescrizioneViaggio);
		travel.setDescrizione(descrizioneET.getText().toString());
		
		if (getDate.isChecked()) {
			travel.setdataPartenza(returnData());
			
			if (setTimer.isChecked()) {
				timerSettings.setEnabled(setTimer.isChecked());
				timerSettings.setHour(timePicker.getCurrentHour());
				timerSettings.setMinute(timePicker.getCurrentMinute());
				timerSettings.saveToSP(this);
				
				SetAlarms setAlarms = new SetAlarms(this);
				setAlarms.loadAlarm();
			}
		}
		Calendar c = Calendar.getInstance();
		SaveNewTravel.saveTravel(travel, ((c.get(Calendar.DAY_OF_MONTH) == mDay) && (c.get(Calendar.MONTH) == mMonth) && (c.get(Calendar.YEAR) == mYear)));
	}
	
	// salvo la variabile test nel caso in cui debba attivare il gps
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  savedInstanceState.putBoolean("test", test);
	  super.onSaveInstanceState(savedInstanceState);
	}
	
	// ripristino la variabile test
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  test = savedInstanceState.getBoolean("test");
	}
	
	// se la variabile test è true, vuol dire che ero uscito per attivare il gps,
	// quindi indipendentemente dal valore attivo il servizio di monitoraggio
	@Override
	public void onResume() {
		super.onResume();
		
		if (test) {
			if (SaveNewTravel.testGps(this)) {
				SaveNewTravel.startGps(this);
				SaveNewTravel.returnToMain(this);
			}
			else 
				Toast.makeText(this, R.string.no_start_gps, Toast.LENGTH_SHORT).show();	
		}
	}
}