package it.Alarm;

import it.DiarioDiViaggio.DisplayNotification;
import it.DiarioDiViaggio.R;
import Settings.TimerSettings;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmNotification extends BroadcastReceiver {
	
	/**
	 */
	private Context context;
	/**
	 */
	private boolean led = true;
	/**
	 */
	private String notifica = "";
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	this.context = context;
    	
    	String[] time = TimerSettings.getMissingTime(context);
    	String giorni = time[0];
    	String ore = time[1];
    	Log.i("DDV", "giorni:"+giorni+" ore:"+ore+" time:"+time.toString());
    	
    	// costruisco la stringa da visualizzare nell'alert
    	notifica = context.getString(R.string.giorni_mancanti);
    	notifica = notifica.replaceFirst("gg", giorni);
    	notifica = notifica.replaceFirst("hh", ore);
    	
    	TimerSettings ts = new TimerSettings(context);
    	if (ts.isEnabled()) {
	    	displayNotification();
	    	
	    	SetAlarms setAlarms = new SetAlarms(context);
			setAlarms.loadAlarm();
    	}
    }    
    
    private void displayNotification() {
    	
    	String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
    	
    	int icon = R.drawable.statusbar_size_hdpi;
    	
    	Notification notification = new Notification(icon, notifica, System.currentTimeMillis());
    	
    	CharSequence contentTitle = context.getString(R.string.app_name);
    	
    	Intent notificationIntent = new Intent(context, DisplayNotification.class);
    	
    	PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

    	notification.setLatestEventInfo(context, contentTitle, notifica, contentIntent);

    	// attivo il led del cellulare per la notifica
    	if(led) {
    		notification.ledARGB = 0xff00ff00;
    		notification.ledOnMS = 300;
    		notification.ledOffMS = 1000;
    		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
    		led = false;
    	}
    	
    	// altri tipi di notifica (per sviluppi futuri)
 /*   	if(vibrazione) {
    		long[] vibrate = {0,100,200,300};
    		notification.vibrate = vibrate;
    	}
    	
    	if(suoneria) {
    		notification.defaults |= Notification.DEFAULT_SOUND;	
    		// notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");
    		//notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6"); 
    	}
 */   	
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	
    	mNotificationManager.notify(42, notification);
    }
}
