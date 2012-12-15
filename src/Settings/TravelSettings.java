package Settings;

import it.VisualMap.Points;

import java.io.Serializable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class TravelSettings implements Serializable {
	
	// Variabili diverse per ogni viaggio, necessarie per la navigazione sulla mappa e la 
	// visualizzazione delle statistiche
	
	private static final long serialVersionUID = 1L;
	/**
	 */
	private int maxLat;
	/**
	 */
	private int maxLng;
	/**
	 */
	private int minLat;
	/**
	 */
	private int minLng;
	/**
	 */
	private long dateStart;
	/**
	 */
	private long dateStop;
	/**
	 */
	int distance;
	/**
	 */
	private long id;
	/**
	 */
	private boolean inCorso = false;
	/**
	 */
	private String descrizione;
	/**
	 */
	private String nome;
	/**
	 */
	private int numberOfPhotos;
	/**
	 */
	private long last_point_parsed = 0;
	
	private static final String travelSettings ="Viaggio";
	private static final String maxLatS ="latitudine max";
	private static final String maxLngS ="longitudine max";
	private static final String minLatS ="latitudine min";
	private static final String minLngS ="longitudine min";
	private static final String nomeS ="nome del viaggio";
	private static final String descrizioneS =" descrizione del viaggio";
	private static final String dateStartS ="data partenza viaggio";
	private static final String dateStopS ="data fine viaggio";
	private static final String distanceS ="distanza percorsa";
	private static final String numberOfPhotosS ="numero di foto";
	private static final String idS ="id del viaggio";
	private static final String last_point_parsedS = "ultimo punto analizzato";
	
	private static final int DEF_VALUE = 0;
	
	public TravelSettings(Context context) {
		loadFromSP(context);
	}
	
	private void loadFromSP(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(travelSettings, Context.MODE_PRIVATE);
		
		maxLat = (int)sp.getLong(maxLatS, Integer.MIN_VALUE);
		maxLng = (int)sp.getLong(maxLngS, Integer.MIN_VALUE);
		minLat = (int)sp.getLong(minLatS, Integer.MAX_VALUE);
		minLng = (int)sp.getLong(minLngS, Integer.MAX_VALUE);
		
		dateStart = sp.getLong(dateStartS, DEF_VALUE);
		dateStop = sp.getLong(dateStopS, DEF_VALUE);
		distance = sp.getInt(distanceS, DEF_VALUE);
		id = sp.getLong(idS, DEF_VALUE);
		inCorso = true;
		nome = sp.getString(nomeS, "");
		descrizione = sp.getString(descrizioneS, "");
		numberOfPhotos = (int)sp.getLong(numberOfPhotosS, DEF_VALUE);
		last_point_parsed = sp.getLong(last_point_parsedS, DEF_VALUE);
	}
	
	public static boolean deleteTravel(Context context) {
		Editor editor = context.getSharedPreferences(travelSettings, Context.MODE_PRIVATE).edit();
		editor.remove(dateStartS);
		editor.remove(dateStopS);
		editor.remove(descrizioneS);
		editor.remove(distanceS);
		editor.remove(idS);
		editor.remove(last_point_parsedS);
		editor.remove(maxLatS);
		editor.remove(maxLngS);
		editor.remove(minLatS);
		editor.remove(minLngS);
		editor.remove(nomeS);
		editor.remove(numberOfPhotosS);
		return editor.commit();
	}
	
	public void resetZoom() {
		maxLat = Integer.MIN_VALUE;
		maxLng = Integer.MIN_VALUE;
		minLat = Integer.MAX_VALUE;
		minLng = Integer.MAX_VALUE;
	}

	public void addPoint(Points point) {
		
		Log.i("DDV", point.getLatitude()+"");
		maxLat = Math.max(point.getLatitude(), maxLat);
		minLat = Math.min(point.getLatitude(), minLat);
		maxLng = Math.max(point.getLongitude(), maxLng);
		minLng = Math.min(point.getLongitude(), minLng);
		Log.i("DDV", "added: "+maxLat+" "+minLat+" "+maxLng+" "+minLng);
		Log.i("DDV", point.toString());
		setLastPointParsed(point.getDataRilevamento());
	}
	
	public long getTravelId() {
		return id;
	}
	
	public long getLastPointParsed() {
		return last_point_parsed;
	}
	
	public void setLastPointParsed(long last_point_parsed) {
		this.last_point_parsed = last_point_parsed;
	}
	
	public void setTravelId(long id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return
	 */
	public long getDateStart() {
		return dateStart;
	}

	/**
	 * @return
	 */
	public long getDateStop() {
		return dateStop;
	}

	/**
	 * @return
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * @return
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * @return
	 */
	public int getNumberOfPhotos() {
		return numberOfPhotos;
	}

	public int getZoomSpanLat() {
		return (int)(Math.abs(maxLat - minLat));
	}

	public int getZoomSpanLng() {
		return (int)(Math.abs(maxLng - minLng));
	}

	public int getCenterLat() {
		return (int)(maxLat + minLat)/2;
	}

	public int getCenterLng() {
		return (int)(maxLng + minLng)/2;
	}

	/**
	 * @return
	 */
	public boolean isInCorso() {
		return inCorso;
	}

	/**
	 * @param inCorso
	 */
	public void setInCorso(boolean inCorso) {
		this.inCorso = inCorso;
	}
	
	/**
	 * @param dateStart
	 */
	public void setDateStart(long dateStart) {
		this.dateStart = dateStart;
	}
	
	/**
	 * @param dateStop
	 */
	public void setDateStop(long dateStop) {
		this.dateStop = dateStop;
	}
	
	public void setDistance(float d) {
		this.distance = (int)d;
	}
	
	/**
	 * @param nof
	 */
	public void setNumberOfPhotos(int nof) {
		this.numberOfPhotos = nof;
	}
	
	/**
	 * @param descrizione
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public void setMaxCoord(int maxLat, int maxLng) {
		this.maxLat = maxLat;
		this.maxLng = maxLng;
	}
	
	public void setMinCoord(int minLat, int minLng) {
		this.minLat = minLat;
		this.minLng = minLng;
	}
	
	/**
	 * @return
	 */
	public int getMaxLat() {
		return maxLat;
	}

	/**
	 * @return
	 */
	public int getMaxLng() {
		return maxLng;
	}

	/**
	 * @return
	 */
	public int getMinLat() {
		return minLat;
	}

	/**
	 * @return
	 */
	public int getMinLng() {
		return minLng;
	}

	public boolean saveInSP(Context context) {
		if (inCorso) {
			
			Editor editor = context.getSharedPreferences(travelSettings, Context.MODE_PRIVATE).edit();
			editor.putLong(minLatS, minLat);
			editor.putLong(minLngS, minLng);
			editor.putLong(maxLatS, maxLat);
			editor.putLong(maxLngS, maxLng);
			editor.putLong(dateStartS, dateStart);
			editor.putLong(dateStopS, dateStop);
			editor.putInt(distanceS, distance);
			editor.putLong(numberOfPhotosS, numberOfPhotos);
			editor.putLong(idS, id);
			editor.putLong(last_point_parsedS, last_point_parsed);
			editor.putString(nomeS, nome);
			return editor.commit();
		}
		return false;
	}
	
	public static void reset(Context context) {
		Editor editor = context.getSharedPreferences(travelSettings, Context.MODE_PRIVATE).edit();
		editor.remove(travelSettings);
		editor.commit();
	}
}