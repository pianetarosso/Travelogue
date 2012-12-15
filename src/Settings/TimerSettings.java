package Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TimerSettings {

	private static final long serialVersionUID = 1L;
	/**
	 */
	private int hour = -1;
	/**
	 */
	private int minute = -1;
	/**
	 */
	private boolean enabled = false;
	
	private static final String timer = "Timer";
	private static final String hourS = "ora_avviso";
	private static final String minuteS = "minuto_avviso";
	private static final String enabledS = "timer_avviato";
	private static final String oreMancantiS = "ore_mancanti";
	private static final String giorniMancantiS = "minuti_mancanti";
	
	private static final int DEF_VALUE = -1;
	
	public TimerSettings(Context context) {	
		loadFromSP(context);
	}	
	
	public boolean isSet() {
		return (hour != -1) && (minute != -1);
	}
	/**
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public static boolean removeTimer(Context context) {
		Editor editor = context.getSharedPreferences(timer, Context.MODE_PRIVATE).edit();
		editor.putBoolean(enabledS, false);
		return editor.commit();
	}

	/**
	 * @return
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * @param hour
	 */
	public void setHour(int hour) {
		this.hour = hour;
	}

	/**
	 * @return
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * @param minute
	 */
	public void setMinute(int minute) {
		this.minute = minute;
	}

	public void loadFromSP(Context context) {
		SharedPreferences sp = context.getSharedPreferences(timer, Context.MODE_PRIVATE);
		hour = sp.getInt(hourS, DEF_VALUE);
		minute = sp.getInt(minuteS, DEF_VALUE);
		enabled = sp.getBoolean(enabledS, false);
	}
	
	public static String[] getMissingTime(Context context) {
		SharedPreferences sp = context.getSharedPreferences(timer, Context.MODE_PRIVATE);
		String[] time = new String[2];
		time[1] = sp.getString(oreMancantiS, "0");
		time[0] = sp.getString(giorniMancantiS, "0");
		Editor editor = sp.edit();
		editor.remove(giorniMancantiS);
		editor.remove(oreMancantiS);
		editor.commit();
		return time;
	}
	
	public static boolean setMissingTime(int g, int h, Context context) {
		Editor editor = context.getSharedPreferences(timer, Context.MODE_PRIVATE).edit();
		editor.putString(oreMancantiS, h+"");
		editor.putString(giorniMancantiS, g+"");
		return editor.commit();
	}
	
	public boolean saveToSP(Context context) {
		
		Editor editor = context.getSharedPreferences(timer, Context.MODE_PRIVATE).edit();
		editor.putInt(hourS, hour);
		editor.putInt(minuteS, minute);
		editor.putBoolean(enabledS, enabled);
		return editor.commit();
	}	
}
