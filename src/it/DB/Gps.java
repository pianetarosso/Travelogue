package it.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Gps {

	
	/**
	 */
	private SQLiteDatabase mDb;
	
	// Tabella per la gestione dei percorsi col gps
	protected static final String gpsTable = "GPS";									// nome della tabella
	private static final String colGpsId = "IDGps";									// chiave, autoincrementale, int
	private static final String colGpsLatitude = "Latitudine";						// latitudine, INT
	private static final String colGpsLongitude = "Longitudine";					// longitudine, INT
	private static final String colGpsDate = "DataRilevamento";						// data del rilevamento, LONG
	protected static final String colGpsTravelID = "IdViaggiodiRiferimento";		// chiave esterna, travelTable -> colID
	
	// Stringa creazione Tabella
	protected static final String Gps =
			"CREATE TABLE " +gpsTable+" ( " 
			+colGpsId+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+colGpsLatitude+ " INT, " 
			+colGpsLongitude+ " INT, "
			+colGpsDate+ " LONG, "
			+colGpsTravelID+ " INTEGER NOT NULL, "
			+"FOREIGN KEY ( "+colGpsTravelID+" ) "
			+"REFERENCES " +Travel.travelTable+ " ( " +Travel.colID+ " )"
			+");";
	
	
	protected Gps(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}
	
	
	// CREA DATO GPS 
	protected long createGPS(
			int latitudine, 
			int longitudine, 
			long data, 
			long id
			) {
		
		Log.i(gpsTable, "Inserting record...");
		ContentValues initialValues = new ContentValues();
		initialValues.put(colGpsLatitude, latitudine);
		initialValues.put(colGpsLongitude, longitudine);
		initialValues.put(colGpsDate, data);
		initialValues.put(colGpsTravelID, id);
		return mDb.insert(gpsTable, null, initialValues);
	}
	
	
	// CANCELLA DATO GPS
	
	// per id
	protected boolean deleteGps(long gpsId) {
		return mDb.delete(gpsTable, colGpsId + "=" + gpsId, null) > 0;
	}
	
	// per id del viaggio (tutti)
	protected boolean deleteGpsByTravelId(long travelId) {
		return mDb.delete(gpsTable, colGpsTravelID + "=" + travelId, null) >= 0;
	}
	
	// tutti quelli DOPO una data precisa
	protected boolean deleteGpsAfterDate(long travelId, long date) {
		return mDb.delete(gpsTable, colGpsTravelID + "=" + travelId +
				" AND " + colGpsDate + ">" + date, null) >= 0;
	}
	
	// tutti quelli PRIMA di una data precisa
	protected boolean deleteGpsBeforeDate(long travelId, long date) {
		return mDb.delete(gpsTable, colGpsTravelID + "=" + travelId +
				" AND " + colGpsDate + "<" + date, null) >= 0;
	}
	
	
	// recupero tutti i punti gps dalla tabella gpsTable basandomi sull'id del viaggio
	protected Cursor fetchAllGpsPoints(long id) throws SQLException {

		Cursor mCursor = mDb.query(true, gpsTable, new String[] {colGpsLatitude,
				colGpsLongitude, colGpsDate, colGpsId}, id + "=" + colGpsTravelID, null,
					null, null, colGpsId+" ASC", null);
		if (mCursor != null) 
			mCursor.moveToFirst();
		return mCursor;
	}
	
	// recupero tutti i punti gps tra due precisi istanti temporali
	protected Cursor fetchTimeSelectedGpsPoints(long id, long start, long stop) throws SQLException {
		Cursor mCursor = mDb.query(true, gpsTable, new String[] {colGpsLatitude,
				colGpsLongitude, colGpsDate, colGpsId},id+ "=" +colGpsTravelID+" AND "+colGpsDate+" BETWEEN " + start + " AND " + stop, null,
					null, null, colGpsDate+ " ASC", null);// funzione che restituisce il tempo attuale in millisecondi
		if (mCursor != null) 
			mCursor.moveToFirst();
		return mCursor;
	}
	
	// eseguo l'update dei parametri di un punto
	protected boolean updatePoint(long date, int lat, int lng, long id) {
		ContentValues args = new ContentValues();
        args.put(colGpsLatitude, lat);
        args.put(colGpsLongitude, lng);
        args.put(colGpsDate, date);
        return mDb.update(gpsTable, args, id + "=" + colGpsId, null) > 0;  	
	}
}
