package it.VisualMap;

import it.DB.TravelDB;

import java.util.ArrayList;
import java.util.List;

import Settings.ListTravelsBackup;
import android.content.Context;
import android.database.Cursor;

public class BuildPointsList {
	
	/**
	 */
	private Context context;
	/**
	 */
	private List<Points> pointsList;
	/**
	 */
	private long travelId;
	
	private static final String colGpsId = "IDGps";									// chiave, autoincrementale, int
	private static final String colGpsLatitude = "Latitudine";						// latitudine, INT
	private static final String colGpsLongitude = "Longitudine";					// longitudine, INT
	private static final String colGpsDate = "DataRilevamento";						// data del rilevamento, LONG
	

	public BuildPointsList(Context context, long travelId) {
		this.context=context;
		this.travelId = travelId;
		initializePointList();
	}
	
	private void initializePointList() {
		pointsList = new ArrayList<Points>();
	}
	
	public List<Points> fromDB() {
		TravelDB traveldb = new TravelDB(context);
		traveldb.open();
		Cursor cursor = traveldb.fetchAllGpsPoints(travelId);
		
		loadPoints(cursor);
		cursor.close();
		traveldb.close();
		return pointsList;
	}
	
	public List<Points> fromFile() {
		return ListTravelsBackup.loadPoints(travelId, context);
	}
	
	public List<Points> loadSelectedTimePoints(long start, long stop) {
		TravelDB traveldb = new TravelDB(context);
		traveldb.open();
		Cursor cursor = traveldb.fetchTimeSelectedGpsPoints(travelId, start, stop);
		loadPoints(cursor);
		cursor.close();
		traveldb.close();
		return pointsList;
	}
	
	protected void loadPoints(Cursor cursor) {
	
		initializePointList();
		cursor.moveToFirst();
		int gpsIndex = cursor.getColumnIndex(colGpsId);
		int latitudeIndex = cursor.getColumnIndex(colGpsLatitude);
		int longitudeIndex = cursor.getColumnIndex(colGpsLongitude);
		int dateIndex = cursor.getColumnIndex(colGpsDate);
	
		for (int progress=0; progress<cursor.getCount(); progress++) {
			Points points = new Points();
			points.setLatitude(cursor.getInt(latitudeIndex));
			points.setLongitude(cursor.getInt(longitudeIndex));
			points.setDataRilevamento(cursor.getLong(dateIndex));
			points.setGpsId(cursor.getInt(gpsIndex));

			pointsList.add(points);
			
			cursor.moveToNext();
		} 
	}
	
	public String toString() {
		String text = "PointList:";
		for(int i=0; i<pointsList.size(); i++)
			text = text+"point number:"+i+", value:("+pointsList.get(i).toString()+"); ";
		return text;
	}
}
