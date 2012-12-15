package it.Service.GPS;

import it.Date.DateManipulation;
import it.DiarioDiViaggio.R;
import it.Travel.Travel;

import java.net.URISyntaxException;

import Settings.MySettings;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class MonitorService extends Service {
	
	/**
	 */
	private long id;
	/**
	 */
	private boolean firstStart = true;	
	/**
	 */
	private int minTime = 150000; //(2,5 minuti)
	/**
	 */
	private GpsListener gpsListener;
	/**
	 */
	private BroadcastReceiver mReceiver;
	/**
	 */
	private Travel travel;
	
	
	@Override
	public void onCreate() {

 	   	// recupera l'id del viaggio in corso
		MySettings mySettings = new MySettings(this);
 	   	id = mySettings.getInCorso();
 	   	
 	   	minTime = mySettings.getIntervalloGps();
 	   	minTime = minTime * 60000;
 	   	
 	   	travel = Travel.loadOnlyTravel(id, this);
 	   	
 	   	// aggiungo un ascoltatore sull'action screen_on/off
 	   	IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
 	   	filter.addAction(Intent.ACTION_SCREEN_OFF);
 	   	mReceiver = new ScreenReceiver();
 	   	registerReceiver(mReceiver, filter);
 	   
 	  
 	   	// di default attivo l'ascoltatore sul gps
 	   	gpsListener = new GpsListener(travel);
 	   	gpsListener.startListener(minTime,0);
 	   	
 	   	long dataPartenza = DateManipulation.getCurrentTimeMs();
 	   	if (travel.getDataPartenza()> dataPartenza) {
 	   		travel.setdataPartenza(dataPartenza);
 	   		travel.save();
 	   	}
 	   	
 	   	// salvo la data di inizio nell sp
 	   	mySettings.setDataScansione(dataPartenza);
 	   	mySettings.setGps_service(true);
 	   	mySettings.saveToSP(this);
 	   	
 	   	startForeground(43,displayNotification());
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		stopForeground(true);
		MySettings mySettings = new MySettings(this);
		mySettings.setGps_service(false);
		mySettings.saveToSP(this);
		if (mySettings.getInCorso()!=-1)
			travel.save();
		gpsListener.stopListener();
		unregisterReceiver(mReceiver);	
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// questo if serve per verificare se il servizio è stato riavviato dal sistema
		if ((flags & START_FLAG_RETRY) == 0) {
			startForeground(43,displayNotification());
			// TODO If it’s a restart, do something.
		}
		else {
			// TODO Alternative background process.
		}
		boolean screenOn;
		
 	   	try {	
 		   screenOn = intent.getBooleanExtra("screen_state", false); // errore lanciato -> verificare
 	   	} catch (NullPointerException e) { screenOn = false; }
 	   	
 	   	if ((!screenOn) && (!firstStart)) {
        	
        	gpsListener.stopListener();
        	gpsListener.startListener(60000, 0);
        	
        	MySettings mySettings = new MySettings(this);
        	mySettings.setDataScansione(DateManipulation.getCurrentTimeMs());
        	mySettings.saveToSP(this);
        } 
        else if (!firstStart) {
        	     	
        	gpsListener.startListener(minTime, 0);  
        	MediaScan mediaScan = new MediaScan(travel);
        	try {
				mediaScan.scanImages();
			} catch (URISyntaxException e) {e.printStackTrace();}
            
        }
        firstStart = false;
		return Service.START_STICKY;
	}
	
	private Notification displayNotification() {
    	
		Context context = travel.getContext();
		
    	int icon = R.drawable.icon;
    	
    	CharSequence contentTitle = context.getString(R.string.app_name);
    	
    	Notification notification = new Notification(icon, contentTitle, System.currentTimeMillis());
    	
    	Intent notificationIntent = new Intent(context, it.DiarioDiViaggio.Main.class);
    	
    	PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

    	notification.setLatestEventInfo(context, contentTitle, contentTitle, contentIntent);

    	notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
    	return notification;
    }
}
