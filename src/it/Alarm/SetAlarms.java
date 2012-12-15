package it.Alarm;

import java.util.Calendar;

import Settings.MySettings;
import Settings.TimerSettings;
import Settings.TravelSettings;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

	
	public class SetAlarms {
		
		/**
		 */
		private boolean settato = false;
		/**
		 */
		private long dateStartTravel;
		/**
		 */
		private long id;
		/**
		 */
		private double giorni = 0;
		
		/**
		 */
		private Context context;
		/**
		 */
		private int ore;
		/**
		 */
		private int minuti;
		/**
		 */
		private boolean armato;
		
		/**
		 */
		private Calendar alert;
		
		/**
		 */
		private TimerSettings ts;
		
		private static final double DAY_MS = 86400000; // 24*3600*1000 => numero di ms in una giornata
		private static final int DELAY = 10; 	// numero di secondi necessari per evitare che il sistema 
												// lanci 2 avvisi anzichè uno
		
		public SetAlarms(Context context) {
			this.context = context;	
			loadTimer();
		}		
		
		// carico i parametri dell'allarme
		private void loadTimer() {
			ts = new TimerSettings(context);
			ore = ts.getHour();
			minuti = ts.getMinute();
			armato = ts.isEnabled();
			
			MySettings ms = new MySettings(context);
			id = ms.getInCorso();
			
			TravelSettings trs = new TravelSettings(context);
			dateStartTravel = trs.getDateStart();
		}
		
		// calcolo il numero di giorni rimanenti
		private double calcolaGiorni(long partenza, long time) {
			long t = partenza-time;
			return t / DAY_MS;
		}
		
		// verifico se posso far partire l'allarme nello stesso giorno, o se devo rimandare al giorno dopo
		private double computaGiornaliero() {
			
			Calendar now = Calendar.getInstance();
			now.add(Calendar.SECOND, DELAY);
			
			alert = Calendar.getInstance();
			
			alert.set(Calendar.HOUR_OF_DAY, ore);
			alert.set(Calendar.MINUTE, minuti);
			alert.set(Calendar.SECOND, 0);
			 
			if (alert.before(now)) 
				alert.add(Calendar.DAY_OF_MONTH, 1);
			
			giorni = calcolaGiorni(dateStartTravel, alert.getTimeInMillis());
			
			return giorni;
		}
		
		// imposto l'allarme, e faccio tutte le verifiche del caso
		public void loadAlarm() {
			
			if ((id != -1) && armato) {
				
				Intent intent = new Intent(context, AlarmNotification.class);
				
				double giorni = computaGiornaliero();
				
				if (giorni <= 0) {
					ts.setEnabled(false);
					ts.saveToSP(context);
				}
				else {
					int giorno = (int)(giorni);
					int ore = (int)((giorni - (double)giorno) * 24);
					
					TimerSettings.setMissingTime(giorno, ore, context);
				
		            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		            // Programmo l'allarme
		            AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		            am.set(AlarmManager.RTC_WAKEUP, alert.getTimeInMillis(), sender);
		        
		            settato = true;
				}
			}
		}
		
		public boolean isSet() {
			return settato;
		}
	}
	