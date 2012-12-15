package it.Travel;

import it.DB.TravelDB;
import it.Date.DateManipulation;
import it.Photos.Images;
import it.VisualMap.Points;

import java.util.ArrayList;
import java.util.List;

import Settings.ListTravelsBackup;
import Settings.MySettings;
import Settings.TimerSettings;
import Settings.TravelBackup;
import Settings.TravelSettings;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

import com.google.android.maps.GeoPoint;


public class Travel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 */
	private String nome = null;

	/**
	 */
	private String descrizione = "";
	/**
	 */
	private boolean  viaggioInCorso = false;
	/**
	 */
	private long id = -1;

	/**
	 */
	private long dataPartenza=0;

	/**
	 */
	private long dateStop=0;
	/**
	 */
	private Context context;
	
	/**
	 */
	private TravelSettings travelSettings;
	
	/**
	 */
	private TravelImages images;
	/**
	 */
	private TravelPoints points;
	/**
	 */
	private TravelMarkers markers;
	
	/**
	 */
	protected TravelBackup travelBackup = null;
		
	private static final String colID = "IDViaggio";	
	
	
	public Travel(Context context) {
		this.context = context;
		
		images = new TravelImages(context, id);
		points = new TravelPoints(context, id);
		markers = new TravelMarkers();
		
		travelSettings = new TravelSettings(context);
	}	
	
	
	
	// Metodi SET
	/**
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	/**
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
		images.setId(id);
		points.setId(id);
	}
	
	/**
	 * @param descrizione
	 */
	public void setDescrizione (String descrizione) {
		this.descrizione = descrizione;
	}
	
	public void setdataPartenza(long data) {
		Log.i("DDV", "setdatapartenza:"+data);
		this.dataPartenza = data;
	}
	
	public void setViaggioInCorso(int viaggioInCorso) {
		this.viaggioInCorso = (viaggioInCorso == 1);
	}
	
	/**
	 * @param viaggioInCorso
	 */
	public void setViaggioInCorso(boolean viaggioInCorso) {
		this.viaggioInCorso = viaggioInCorso;
	}
	
	/**
	 * @param dateStop
	 */
	public void setDateStop(long dateStop){
		this.dateStop = dateStop;
	}
	
	public void setDistance(int distance) {
		points.setDistance(distance);
	}
	
	public void setNumberOfImages(int numberOfImages) {
		images.setNumberOfImages(numberOfImages);
	}
	
	//Metodi GET
	
	// recupera il nome del viaggio dal db, o ne genera uno se non esiste
	/**
	 * @return
	 */
	public String getNome() {
		int k = 0;
		TravelDB travelDB = new TravelDB(context);
		travelDB.open();
		if (nome == null) {		
			Cursor cursor = travelDB.fetchLastTravel();
			try {	
				int columnIndex = cursor.getColumnIndex(colID);
				k = Integer.parseInt(cursor.getString(columnIndex));	
			}  
			catch (CursorIndexOutOfBoundsException e) {}
			cursor.close();	
			nome = "Travel n° " + (k+1);
		}
		travelDB.close();
		return nome;
	}
	
	public void removeImages(List<Images> images) {
		Log.i("DDV", "remove images");
		for(Images i:images) 
			this.images.getImages().remove(i);
		if (!isViaggioInCorso())
			travelBackup.removeImages(images);
	}
	
	public void removeImage(Images image) {
		images.getImages().remove(image);
		List<Images> i = new ArrayList<Images>();
		i.add(image);
		removeImages(i);
	}
	
	public List<Points> getPoints() {
		return points.getPoints();
	}
	
	public List<Images> getImages() {
		return images.getImages();
	}
	
	/**
	 * @return
	 */
	public String getDescrizione() {
		return descrizione;
	}
	
	public void removeMarker(GeoPoint g) {
		markers.removeMarker(g);
	}
	
	/**
	 * @return
	 */
	public long getDataPartenza() {
		Log.i("DDV", "datapartenza:"+dataPartenza);
		if (dataPartenza > 0)
			return dataPartenza;
		else {
			dataPartenza = DateManipulation.getCurrentTimeMs();
			return dataPartenza;
		}
	}
	
	public long getDataFine() {
		return dateStop;
	}
	
	/**
	 * @return
	 */
	public boolean isViaggioInCorso() {
		return viaggioInCorso;
	}
	
	/**
	 * @return
	 */
	public long getId() {
		return id;
	}

	public int getNumberOfImages() {
		return images.getNuberOfImages();
	}
	
	public int getDistance() {
		return points.getDistance();
	}
	
	/**
	 * @return
	 */
	public Context getContext() {
		return context;
	}
	
	public List<Travel> getAllTravels() {
		return LoadTravel.getAllTravels(context);
	}
	
	public Points getLastPointAdded() {
		return points.getLastPointAdded();
	}
	
	public GeoPoint getLastMarkerAdded() {
		return markers.getLastMarkerAdded();
	}
	
	public GeoPoint getPreviousGeoPoint(GeoPoint i) {
		return markers.getPreviousGeoPoint(i);
	}
	
	public GeoPoint getNextGeoPoint(GeoPoint i) {
		return markers.getNextGeoPoint(i);
	}
	
	// ALTRI
	
	public void addPoints(Points point, int time) {
		points.addPoints(point, time);
	}
	
	public List<Points> selectTimePoints(long start, long stop) {
		return points.selectTimePoints(start, stop);
	}
	
	public List<Points> updatePoints() {
		loadFromFile();
		return points.updatePoints(travelBackup);
	}
	
	public void addImages(Images i) {
		images.addImages(i, id);
	}
	
	public static Travel loadTravelAndImages(long id, Context context) {
		return LoadTravel.loadTravelAndImages(id, context);
	}
	
	public boolean save() {
		return SaveTravel.save(this);
	}
	
	public boolean deleteTimeSelectedPointsAndContents(boolean b, boolean a) {
		boolean t = DeleteTravel.deleteTimeSelectedPointsAndContents(b, a, this);
		points.reloadDistanceAndZoom();
		return t;
	}
	
	public void reloadImages() {
		loadFromFile();
		images.reloadImages(travelBackup);
	}
	
	public void loadPoints() {
		loadFromFile();
		points.loadPoints(travelBackup);
	}
	
	public void loadImages() {
		loadFromFile();
		images.loadImages(travelBackup);
	}
	
	private void loadFromFile() {
		if (!viaggioInCorso && (travelBackup == null)) {
			travelBackup = ListTravelsBackup.load(id);
		}
	}
		
	public boolean updateDistance(int distance) {
		points.setDistance(distance);
		return SaveTravel.save(this);
	}
	
	public Travel loadTravelAndImages(long id) {
		return LoadTravel.loadTravelAndImages(id, context);
	}
	
	public void addMarkerGeoPoint(GeoPoint i) {
		markers.addMarkerGeoPoint(i);
	}
	
	public static Travel load(long id, Context context) {
		return LoadTravel.load(id, context);
	}
	public static Travel loadOnlyTravel(long id, Context context) {
		return LoadTravel.loadOnlyTravel(id, context);
	}
	
	public boolean deleteTravel() {
		return DeleteTravel.deleteTravel(this);
	}
	
	public boolean parseSettings(TravelSettings travelSettings) {
		descrizione = travelSettings.getDescrizione();
		nome = travelSettings.getNome();
		dataPartenza = travelSettings.getDateStart();
		dateStop = travelSettings.getDateStop();
		setDistance(travelSettings.getDistance());
		viaggioInCorso = travelSettings.isInCorso();
		return save();
	}
	
	public boolean closeTravel(long dateStop) {
		
		viaggioInCorso = false;
		setDateStop(dateStop);
		
		travelSettings.saveInSP(context);
		
		MySettings s = new MySettings(context);
		s.closeTravel(getId());
		s.saveToSP(context);
		
		Intent i = new Intent(context, it.Service.GPS.MonitorService.class);
		context.stopService(i);
		
		TimerSettings ts = new TimerSettings(context);
		ts.setEnabled(false);
		ts.saveToSP(context);
			
		TravelDB tdb = new TravelDB(context);
		tdb.open();
		Log.i("DDV", "viaggioincorso:"+viaggioInCorso);
		
		ListTravelsBackup tb = ListTravelsBackup.loadAll();
		
		if (tb == null)
			tb = new ListTravelsBackup();
		
		tb.add(this);
		tb.save();
		
		if (images.getNuberOfImages()>0) 
			tdb.deleteContentByTravelId(id);
			
		if (points.getPoints().size()>0) 
			tdb.deleteGpsByTravelId(id);
		
		tdb.close();
		
		boolean test = 	SaveTravel.save(this);
		TravelSettings.reset(context);
	
		return test;
	}	
}