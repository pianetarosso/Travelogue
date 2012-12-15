package it.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Contents {

	
	/**
	 */
	private SQLiteDatabase mDb;
	
	// Tabella per la gestione dei singoli contenuti creati
	protected static final String contentsTable="Contenuti";						// nome della tabella
	private static final String contentsId ="idContenuti";							// chiave, long id del contenuto
	private static final String colContentName="NomeContenuto";						// chiave, String (compresa l'estensione del file)
	private static final String colContentData="Date";								// data creazione contenuto, LONG millisecondi
	private static final String colContentLatitude = "Latitude";					// latitudine, INT
	private static final String colContentLongitude = "Longitude";					// longitudine, INT
	private static final String colLocalContentLocation="PosizioneLocaleContenuto";	// indica il percorso in cui si trova il contenuto localmente, string
	protected static final String colContentTravelId="TravelIdContenuto"; 			// id del viaggio del contenuto
	private static final String colContentRoute ="Address";							// indirizzo del contenuto, string
	
	// Stringa creazione Tabella
	protected static final String Contents =
			"CREATE TABLE "+contentsTable+" ( "
			+contentsId+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+colContentName+ " TEXT UNIQUE, " 
			+colContentData+ " LONG, " 
			+colLocalContentLocation+ " TEXT, " 
			+colContentLatitude+ " INT, "
			+colContentLongitude+ " INT, "
			+colContentRoute+ " TEXT, "
			+colContentTravelId+ " LONG NOT NULL, " 
			+"FOREIGN KEY ( " +colContentTravelId+ ") "
			+"REFERENCES " +Travel.travelTable+ " ( " +Travel.colID+ " )"
			+");";
	
	
	protected Contents(SQLiteDatabase mDb) {
		this.mDb = mDb;
	}
	
	// CREA CONTENUTO 
	protected long createContent(
			String contentName, 
			long data, 
			String locazioneMemoria, 
			int latitudine, 
			int longitudine, 
			String commento, 
			String route,
			long travelId
			) {
		
		Log.i(contentsTable, "Inserting record...");
		ContentValues initialValues = new ContentValues();
		initialValues.put(colContentName, contentName);
		initialValues.put(colContentData, data);
		initialValues.put(colLocalContentLocation, locazioneMemoria);
		initialValues.put(colContentLatitude, latitudine);
		initialValues.put(colContentLongitude, longitudine);
		initialValues.put(colContentTravelId, travelId);
		initialValues.put(colContentRoute, route);
		return mDb.insertOrThrow(contentsTable, null, initialValues);
	}
	
	
	// CANCELLA CONTENUTO
	// (qui elimino solo il riferimento nel database)
	
	// elimino un contenuto specifico
	protected boolean deleteContent(long id) {
		return mDb.delete(contentsTable, contentsId + "=" + id, null) > 0;
	}
	
	// elimino TUTTI i contenuti di uno stesso viaggio
	protected boolean deleteContentByTravelId(long travelId) {
		return mDb.delete(contentsTable, colContentTravelId + "=" + travelId, null) >= 0;
	}
	
	// elimino tutti i contenuti DOPO una certa data
	protected boolean deleteContentAfterDate(long travelId, long date) {
		return mDb.delete(contentsTable, colContentTravelId + "=" + travelId +
				" AND " + colContentData + ">" + date, null) >= 0;
	}
	
	// elimino tutti i contenuti PRECEDENTI ad una certa data
	protected boolean deleteContentBeforeDate(long travelId, long date) {
		return mDb.delete(contentsTable, colContentTravelId + "=" + travelId +
				" AND " + colContentData + "<" + date, null) >= 0;
	}
	
	
	// recupero tutti i punti dove è stato creato un contenuto di un viaggio
	protected Cursor fetchAllContentsPoints(long id) throws SQLException {
		Cursor mCursor = mDb.query(true, contentsTable, new String[] {colContentLatitude,colContentName,
				colContentLongitude, colContentData, colContentRoute, colLocalContentLocation, contentsId},id + "=" + colContentTravelId, null,
					null, null, colContentData+ " ASC", null);
		if (mCursor != null) 
			mCursor.moveToFirst();
		return mCursor;
	}	
	
	
	// eseguo l'update di un contenuto
	protected boolean updateContent(long date, int lat, int lng, String route, String path, long id) {
		ContentValues args = new ContentValues();
        args.put(colContentLatitude, lat);
        args.put(colContentLongitude, lng);
        args.put(colContentRoute, route);
        args.put(colLocalContentLocation, path);
        return mDb.update(contentsTable, args, id + "=" + contentsId, null) > 0;       
	}
	
	
	// CONTO LE IMMAGINI
	protected int fetchNumberOfContents(long id) throws SQLException {
		Cursor cursor = fetchAllContentsPoints(id);
		int number = cursor.getCount();
		cursor.close();
		return number;
	}
}
