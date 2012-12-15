package it.Date;

import it.DiarioDiViaggio.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

public class DateManipulation {

	private static Date date = new Date();
	private static Calendar calendar;
	
	// funzione che restituisce il tempo corrente
	public static Calendar getCurrentTime() {
		return Calendar.getInstance();
	}
	
	// funzione che restituisce il tempo attuale in millisecondi
	public static long getCurrentTimeMs() {
		long milliseconds;
		date = getCurrentTime().getTime();
		milliseconds = date.getTime();
		return milliseconds;
	}
	
	// funzione che restituisce il tempo attuale in millisecondi sotto forma di calendar
	public static long getCurrentTimeMs(Calendar calendar) {
		long milliseconds;
		date = calendar.getTime();
		milliseconds = date.getTime();
		return milliseconds;
	}
	
	// funzione che calcola la "distanza" tra due istanti di tempo
	public static String getPeriod(long start, long stop, Context context) {
		
		String minutiS = context.getString(R.string.minuti);
		String oreS = context.getString(R.string.ore);
		String giorniS = context.getString(R.string.giorni);
		int minuti = (int)Math.abs(start-stop)/60000;
		int ore = (int)minuti/60;
		int giorni = (int) ore/24;
		
		ore = ore - (giorni * 24);
		minuti = minuti - (ore * 60) - (giorni * 24 * 60); 
		
		String text = giorni+" "+giorniS+", "+ore+" "+oreS+", "+minuti+" "+minutiS;
	
		return text;
	}
	
	// funzione che converte un tempo dato in ms
	public static long parseTimeMs(int day, int month, int year) {
		
		date.setYear(year-1900);
		date.setMonth(month);
		date.setDate(day);
		date.setHours(0);
		date.setMinutes(1);
		date.setSeconds(0);
		return date.getTime();
	}
	
	// converte i millisecondi in una data leggibile
	public static String parseMs(long input) {
		date.setTime(input);
		return DateFormat.getDateTimeInstance().format(date);
	}
	
	// converto i ms in calendar
	public static Calendar parseMsToCalendar(long input) throws ParseException {	
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(input);
		return calendar;
	}
	
	
	// metodo per il parsing della data in formato ISO8601
	public static Calendar parseISO8601(String input) throws ParseException {
		SimpleDateFormat iso8601formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	    date = iso8601formatter.parse(input);
	    calendar.setTime(date); 
	    return calendar;
	}
	
	// metodo per il parsing inverso della data in formato ISO8601
	public static String parseISO8601 (Date input) throws ParseException {
		SimpleDateFormat iso8601formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	    String result = iso8601formatter.format(input);
	    return result;
	}
	
	// metodo per il parsing della data in formato locale
	public static Calendar localParse(String input) throws ParseException {
		DateFormat localFormatter = SimpleDateFormat.getDateTimeInstance();
		date = localFormatter.parse(input);
		calendar.setTime(date);
		return calendar;
	}
}
