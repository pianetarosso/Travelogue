package it.DiarioDiViaggio;

import it.Alarm.SetAlarms;
import it.Date.DateManipulation;
import it.Travel.Travel;

import java.text.ParseException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditTravel extends Activity {
	
	/**
	 */
	private EditText nameE;
	/**
	 */
	private EditText descriptionE;
	/**
	 */
	private EditText dateStartE;
	/**
	 */
	private EditText dateStopE;
	/**
	 */
	private Travel travel;
	/**
	 */
	private long id;
	/**
	 */
	private boolean startAfterStart = false;
	/**
	 */
	private boolean stopBeforeStop = false;
	
	
	// PREPARAZIONE ////////////////////////////////////////////
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_travel);
        
        Bundle b = this.getIntent().getExtras();
        id = b.getLong("id");
        
        travel = Travel.loadOnlyTravel(id, this);
        
        nameE = (EditText)findViewById(R.id.editTextTravelName);
        nameE.setText(travel.getNome());
        
        descriptionE = (EditText)findViewById(R.id.editTextDescription);
        descriptionE.setText(travel.getDescrizione());
        
        setDateStart();
        setDateStop();
	}
	
	// PARSO LA DATA DI START ///////////////////////////////////
	private void setDateStart() {
		dateStartE = (EditText)findViewById(R.id.editTextDateStart);
        dateStartE.setText(DateManipulation.parseMs(travel.getDataPartenza()));
	}
	
	
	// PARSO LA DATA DI STOP //////////////////////////////////////
	private void setDateStop() {
		dateStopE = (EditText)findViewById(R.id.editTextDateStop);
        
        if (travel.getDataFine()!=0) {
        	String data = DateManipulation.parseMs(travel.getDataFine()).split(" ")[0];
            String ora = (String) DateManipulation.parseMs(travel.getDataFine()).split(" ")[1].subSequence(0, 5);
            dateStopE.setText(data + " - " + ora);
        }
        else 
        	dateStopE.setText(R.string.missing_date);
	}
	
	// MODIFICO LA DATA DI PARTENZA DEL VIAGGIO ////////////////////////////////////////////
	public void editStartDate(View view) throws ParseException {
		
		
		Calendar start = DateManipulation.parseMsToCalendar(travel.getDataPartenza());
		
		final int myYear = start.get(Calendar.YEAR);
		final int myMonth = start.get(Calendar.MONTH);
		final int myDay = start.get(Calendar.DAY_OF_MONTH);
		final int myHour = start.get(Calendar.HOUR_OF_DAY);
		final int myMinute = start.get(Calendar.MINUTE);
		
		Calendar now = DateManipulation.getCurrentTime();
		final Calendar choosed = (Calendar) now.clone();
						
		DatePickerDialog dDialog = new DatePickerDialog(travel.getContext(), 
				new DatePickerDialog.OnDateSetListener() {
					
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						
						choosed.set(Calendar.YEAR, year);
						choosed.set(Calendar.MONTH, monthOfYear);
						choosed.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						
						TimePickerDialog tDialog = new TimePickerDialog(travel.getContext(), 
								new TimePickerDialog.OnTimeSetListener() {
									
									public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
										choosed.set(Calendar.HOUR_OF_DAY, hourOfDay);
										choosed.set(Calendar.MINUTE, minute);	
										
										if ((travel.getDataFine()!= 0) && (choosed.getTimeInMillis()>travel.getDataFine())) {
										
											AlertDialog.Builder builder = new AlertDialog.Builder(travel.getContext());
											builder.setCancelable(false);
											builder.setMessage(R.string.problem_start_travel_after_stop);
											builder.setPositiveButton(R.string.ok, 
													new DialogInterface.OnClickListener() { 
											    	   public void onClick(DialogInterface dialog, int id) {
											    		   dialog.dismiss();
											    	   }
											});		
											
											builder.show();
										}
										
										else if (choosed.getTimeInMillis()>travel.getDataPartenza()) {
											
											AlertDialog.Builder builder = new AlertDialog.Builder(travel.getContext());
											builder.setCancelable(false);
											builder.setMessage(R.string.problem_start_travel_after_start);
											builder.setPositiveButton(R.string.yes, 
													new DialogInterface.OnClickListener() { 
											    	   public void onClick(DialogInterface dialog, int id) {
											    		   travel.setdataPartenza(choosed.getTimeInMillis());
											    		   dialog.dismiss();
											    		   startAfterStart = true;
											    		   setDateStart();
											    	   }
											});
											builder.setNegativeButton(R.string.no, 
													new DialogInterface.OnClickListener() { 
										    	   public void onClick(DialogInterface dialog, int id) {
										    		   dialog.dismiss();
										    	   }
											});
											
											builder.show();
										}
										else {
											travel.setdataPartenza(choosed.getTimeInMillis());
											setDateStart();
										}
									}
								}, myHour, myMinute, true);
						tDialog.show();
						
					}
				}, myYear, myMonth, myDay);
		
		dDialog.show();
	}
	
	
	//MODIFICO LA DATA DI FINE VIAGGIO /////////////////////////////////////////
	public void saveDateStop(View view) {
	
		Calendar now = DateManipulation.getCurrentTime();
		final int myYear = now.get(Calendar.YEAR);
		final int myMonth = now.get(Calendar.MONTH);
		final int myDay = now.get(Calendar.DAY_OF_MONTH);
		final int myHour = now.get(Calendar.HOUR_OF_DAY);
		final int myMinute = now.get(Calendar.MINUTE);
		
		
		final DatePickerDialog dDialog = new DatePickerDialog(travel.getContext(), 
		
				new DatePickerDialog.OnDateSetListener(){

					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						
						final Calendar now = DateManipulation.getCurrentTime();
						final Calendar choosed = (Calendar) now.clone();

						choosed.set(Calendar.YEAR, year);
						choosed.set(Calendar.MONTH, monthOfYear);
						choosed.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						
						
						TimePickerDialog tDialog = new TimePickerDialog(travel.getContext(), 
								new TimePickerDialog.OnTimeSetListener() {
									
									public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
										choosed.set(Calendar.HOUR_OF_DAY, hourOfDay);
										choosed.set(Calendar.MINUTE, minute);	
										if (choosed.getTimeInMillis()<travel.getDataPartenza()) {
										
											AlertDialog.Builder builder = new AlertDialog.Builder(travel.getContext());
											builder.setCancelable(false);
											builder.setMessage(R.string.problem_stop_travel_before_start);
											builder.setPositiveButton(R.string.ok, 
													new DialogInterface.OnClickListener() { 
											    	   public void onClick(DialogInterface dialog, int id) {
											    		   dialog.dismiss();
											    	   }
											});	
											
											builder.show();
										}
										
										else if (choosed.before(now)) {

											AlertDialog.Builder builder = new AlertDialog.Builder(travel.getContext());
											builder.setCancelable(false);
											builder.setMessage(R.string.problem_stop_travel);
											builder.setPositiveButton(R.string.yes, 
													new DialogInterface.OnClickListener() { 
											    	   public void onClick(DialogInterface dialog, int id) {
											    		   travel.setDateStop(choosed.getTimeInMillis());
											    		   dialog.dismiss();
											    		   setDateStop();
											    		   stopBeforeStop = true;
											    	   }
											});
											builder.setNegativeButton(R.string.no, 
													new DialogInterface.OnClickListener() { 
										    	   public void onClick(DialogInterface dialog, int id) {
										    		   dialog.dismiss();
										    	   }
											});
											
											builder.show();
										}
										else {
											travel.setDateStop(choosed.getTimeInMillis());
											setDateStop();
										}
									}
								}, myHour, myMinute, true);
						
						tDialog.show();
					}
														
		}, myYear, myMonth, myDay);
				
		dDialog.show();	
	}

	
	//SALVATAGGIO ////////////////////////////////////////////////////////////
	public void saveData(View view) {
		
		travel.setNome(nameE.getText().toString());
		travel.setDescrizione(descriptionE.getText().toString());
			
		if (startAfterStart || stopBeforeStop) {
				
			if (travel.deleteTimeSelectedPointsAndContents(startAfterStart, stopBeforeStop) && travel.save()) {
				Intent intent = new Intent(travel.getContext(), TravelMap.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
				startActivity(intent);
			}
			else {
				AlertDialog.Builder builder = new AlertDialog.Builder(travel.getContext());
				builder.setCancelable(false);
				builder.setMessage(R.string.error_data_saving);
				builder.setPositiveButton(R.string.ok, 
						new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				builder.show();
			}			
		}
		else {
			travel.save();
			
			SetAlarms sa = new SetAlarms(this);
			sa.loadAlarm();
			
			finish();	
		}
	}
	
	
	// MENU ///////////////////////////////////////////////////////////////////////////////
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.travel_menu, menu);
        return true;
    } 
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.delete_travel:
        	deleteTravel();
        	break;
        }
        return true;
	}
	
	private void deleteTravel() {

		AlertDialog.Builder builder = new AlertDialog.Builder(travel.getContext());
		builder.setCancelable(false);
		builder.setMessage(R.string.are_you_sure_delete_travel);
		builder.setPositiveButton(R.string.yes, 
				new DialogInterface.OnClickListener() { 
	    	   public void onClick(DialogInterface dialog, int id) {
	    		   if (travel.deleteTravel()) {
	    			   Intent intent = new Intent(travel.getContext(), Main.class);
	    			   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
	    			   startActivity(intent);
	    		   }
	    	    	else 
	    	    		Toast.makeText(travel.getContext(), travel.getContext().getResources().getString(R.string.failed_deleting_travel), Toast.LENGTH_LONG).show();
	    		   dialog.dismiss();
	    	   }
		});
		
		builder.setNegativeButton(R.string.no, 
				new DialogInterface.OnClickListener() { 
	    	   public void onClick(DialogInterface dialog, int id) {
	    		   dialog.dismiss();
	    	   }
		});
		
		builder.show();	
	}
}
