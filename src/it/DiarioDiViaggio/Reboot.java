package it.DiarioDiViaggio;

import it.Alarm.SetAlarms;
import Settings.MySettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

// questa classe si occupa di settare nuovamente i timer e di riavviare 
// il servizio di ascolto e registrazione del gps, nel caso che il cellulare
// si spenga

public class Reboot extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		MySettings s = new MySettings(context);
		long id = s.getInCorso();
		
		if (id > 0) {
			SetAlarms setAlarms = new SetAlarms(context);
			setAlarms.loadAlarm();
			
			MySettings ms = new MySettings(context);
			
			if (ms.isGps_service()) {
				Intent i = new Intent(context, it.Service.GPS.MonitorService.class);
				if (context.startService(i) == null) 
					Toast.makeText(context, R.string.error_start_service, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
