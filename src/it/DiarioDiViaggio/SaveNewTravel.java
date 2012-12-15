package it.DiarioDiViaggio;

import it.Alarm.SetAlarms;
import it.Travel.Travel;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.Toast;

public class SaveNewTravel {
	
	protected static void saveTravel(Travel travel, boolean test) {
		
		ProgressDialog pd = createProgressDialog(travel.getContext());
		pd.setTitle(R.string.saving);
		pd.show();
		saveData sd = new saveData();
		sd.initialize(pd, test);
		sd.execute(travel);
	}
	
	private static ProgressDialog createProgressDialog(Context context) {
		ProgressDialog pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		return pd;
	}
	
	private static class saveData extends AsyncTask<Travel,Void,Boolean> {

		private ProgressDialog pd;
		private Context context;
		private boolean test;
		
		protected void initialize(ProgressDialog pd, boolean test) {
			this.test = test;
			this.pd = pd;
		}
		
		@Override
		protected Boolean doInBackground(Travel... travel) {
			this.context = travel[0].getContext();
			boolean t = travel[0].save();
			return t;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			pd.dismiss();
			if (result) 
				Toast.makeText(context, R.string.data_saved, Toast.LENGTH_SHORT).show();
			else 
				Toast.makeText(context, R.string.error_data_saving, Toast.LENGTH_SHORT).show();
			
				SetAlarms sa = new SetAlarms(context);
				sa.loadAlarm();
				if (test)
					gpsDialog(context);
				returnToMain(context);
		}
	}	
	
	protected static void startGps(Context context) {
		Intent i = new Intent(context, it.Service.GPS.MonitorService.class);
		
		if (context.startService(i) == null) 
			Toast.makeText(context, R.string.error_start_service, Toast.LENGTH_SHORT).show();
	}
	
	public static boolean testGps(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean isGPS = locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
		return (isGPS);
	}
	
	protected static void returnToMain(Context context) {
		Intent intent = new Intent(context, Main.class);
		// facendo partire l'intent in questo modo, termino tutti gli
		// intent precedenti, rendendo impossibile tornare indietro
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		context.startActivity(intent);
	}
	
	// verifico se il gps è attivo o meno, nel secondo caso chiedo all'utente se lo vuole attivare
	// e lo porto nelle settings corrette
	private static void gpsDialog(final Context context) {
		if (!testGps(context)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
    		builder.setMessage(R.string.start_gps)
    	       .setCancelable(true)
    	       .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() { 
    	    	   public void onClick(DialogInterface dialog, int id) {
    	    		   context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    	           }
    	       })
    	       .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() { 
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   dialog.cancel();
    	           }
    	       });
    		
    		AlertDialog alert = builder.create();	// creo l'alert dialog
    		alert.show();							// lo mostro
		}
		else
			startGps(context);
	}	
	
}
