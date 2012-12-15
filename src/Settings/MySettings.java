package Settings;

import it.GestioneFile.MyFile;

import java.io.Serializable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MySettings implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 */
	private long inCorso = -1;
	/**
	 */
	private long ultimoVisualizzato = -1;
	/**
	 */
	private boolean gps_service = false;
	/**
	 */
	private long dataScansione = 0;
	/**
	 */
	private int intervalloGps = 3;
	/**
	 */
	private int idOffset = 0;
	
	private static final String settings = "Settings";
	private static final String inCorsoS = "viaggio in corso";
	private static final String ultimoVisualizzatoS = "ultimo viaggio caricato";
	private static final String gps_serviceS = "servizio gps";
	private static final String dataScansioneS = "data iniziale scansione";
	private static final String intervalloGpsS = "intervallo gps"; // minuti
	private static final String idOffsetS = "offset travel id"; // variabile necessaria per evitare conflitti tra 
																// gli id dei viaggi in caso di ripristino da backup
	
	private static final long TRAVEL_DEF_VALUE = -1;
	private static final long DEF_VALUE = 0;
	private static final int GPS_DEF_VALUE = 3;
	
	public MySettings(Context context) {	
		loadFromSP(context);
	}
	
	public void closeTravel(long id) {
		inCorso = TRAVEL_DEF_VALUE;
		ultimoVisualizzato = id;
		gps_service = false;
		dataScansione = DEF_VALUE;
	}
	
	public void loadFromSP(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(settings, Context.MODE_PRIVATE);
		
		inCorso = sp.getLong(inCorsoS, TRAVEL_DEF_VALUE);
		ultimoVisualizzato = sp.getLong(ultimoVisualizzatoS, TRAVEL_DEF_VALUE);
		gps_service = sp.getBoolean(gps_serviceS, false);
		dataScansione = sp.getLong(dataScansioneS, DEF_VALUE);
		intervalloGps = sp.getInt(intervalloGpsS, GPS_DEF_VALUE);
		idOffset = sp.getInt(idOffsetS, idOffset);
	}
	
	public static boolean deleteTravel(Context context, long id) {
		SharedPreferences sp = context.getSharedPreferences(settings, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		
		if (sp.getLong(inCorsoS, TRAVEL_DEF_VALUE) == id)
			editor.putLong(inCorsoS, TRAVEL_DEF_VALUE);
		if (sp.getLong(ultimoVisualizzatoS, TRAVEL_DEF_VALUE) == id)
			editor.putLong(ultimoVisualizzatoS, TRAVEL_DEF_VALUE);
		editor.putLong(dataScansioneS, DEF_VALUE);
		editor.putBoolean(gps_serviceS, false);
		return editor.commit();
	}
	
	public boolean saveToSP(Context context) {
		
		Editor editor = context.getSharedPreferences(settings, Context.MODE_PRIVATE).edit();
		
		editor.putLong(inCorsoS, inCorso);
		editor.putLong(dataScansioneS, dataScansione);
		editor.putLong(ultimoVisualizzatoS, ultimoVisualizzato);
		editor.putBoolean(gps_serviceS, gps_service);
		editor.putInt(intervalloGpsS, intervalloGps);
		editor.putInt(idOffsetS, idOffset);
		return editor.commit();
	}
	
	public void saveToSD() {
		MyFile mf = new MyFile();
		mf.saveSettings(this);
	}
	
	public static MySettings loadFromSD() {
		MyFile mf = new MyFile();
		return mf.loadSettings();
	}
	
	public boolean restore(Context context) {
		Editor editor = context.getSharedPreferences(settings, Context.MODE_PRIVATE).edit();
		
		editor.putBoolean(gps_serviceS, false);
		editor.putInt(intervalloGpsS, intervalloGps);
		editor.putInt(idOffsetS, idOffset);
		return editor.commit();
	}
	
	public void setOffset(int idOffset) {
		this.idOffset = idOffset;
	}
	public int getOffset() {
		return idOffset;
	}
	/**
	 * @return
	 */
	public int getIntervalloGps() {
		return intervalloGps;
	}
	/**
	 * @param intervalloGps
	 */
	public void setIntervalloGps(int intervalloGps) {
		this.intervalloGps = intervalloGps;
	}
	/**
	 * @return
	 */
	public long getInCorso() {
		return inCorso;
	}
	/**
	 * @param inCorso
	 */
	public void setInCorso(long inCorso) {
		this.inCorso = inCorso;
	}
	/**
	 * @return
	 */
	public long getUltimoVisualizzato() {
		return ultimoVisualizzato;
	}
	/**
	 * @param ultimoVisualizzato
	 */
	public void setUltimoVisualizzato(long ultimoVisualizzato) {
		this.ultimoVisualizzato = ultimoVisualizzato;
	}
	/**
	 * @return
	 */
	public boolean isGps_service() {
		return gps_service;
	}
	/**
	 * @param gpsService
	 */
	public void setGps_service(boolean gpsService) {
		gps_service = gpsService;
	}
	/**
	 * @return
	 */
	public long getDataScansione() {
		return dataScansione;
	}
	/**
	 * @param dataScansione
	 */
	public void setDataScansione(long dataScansione) {
		this.dataScansione = dataScansione;
	}
}
