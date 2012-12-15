package it.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Travel {
	
	
	/**
	 */
	private SQLiteDatabase mDb;
		
	// Tabella principale gestione generica viaggi
	protected static final String travelTable="Viaggi";								// nome della tabella
	protected static final String colID="IDViaggio";								// chiave, autoincrementale, int
	private static final String colName="NomeViaggio";								// nome del viaggio, string
	private static final String colDescrizione="DescrizioneViaggio";				// descrizion del viaggio, string
	private static final String colDataStart="DataPartenzaViaggio";					// data inizio viaggio, string
	private static final String colDataStop="DataFineViaggio";						// data fine viaggio, string
	protected static final String colInCorso = "ViaggioInCorso";					// indica se il viaggio è in corso, int
	private static final String colDistanza = "MetriPercorsi";						// indica i metri percorsi durante il viaggio	
	
	// Stringa creazione Tabella
	protected static final String Travels =
			"CREATE TABLE "+travelTable+" ( "
			+colID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+colName+ " TEXT, " 
			+colDescrizione+ " TEXT, " 
			+colDataStart+ " LONG, "
			+colDataStop+ " LONG, " 
			+colDistanza+ " INTEGER, " 
			+colInCorso+ " INTEGER );";
	
	protected Travel (SQLiteDatabase mDb) {
		this.mDb = mDb;
	}
	
	// CREA VIAGGIO 
	protected long createTravel(
			String travelName, 
			String descrizione, 
			long data,  
			boolean inCorso 
			) {
		int t = 0;
		if (inCorso) t = 1;
		Log.i(travelTable, "Inserting record...");
		ContentValues initialValues = new ContentValues();
		initialValues.put(colName, travelName);
		initialValues.put(colDescrizione, descrizione);
		initialValues.put(colDataStart, data);
		initialValues.put(colDataStop, 0);
		initialValues.put(colInCorso, t); 
		initialValues.put(colDistanza, 0);
		return mDb.insert(travelTable, null, initialValues);
	}
	
	// CANCELLA VIAGGIO 
	protected boolean deleteTravel(long travelId) {
		return mDb.delete(travelTable, colID + "=" + travelId, null) > 0;
	}
	
	// recupero tutti i viaggi dalla tabella travelTable
	protected Cursor fetchAllTravels() {
		return mDb.query(true, travelTable, new String[] {colID}, 
				null, null, null, null, colID+" ASC", null);
	}
	
	// recupero un viaggio dalla tabella travelTable in base all'id
	protected Cursor fetchTravel(long id) {
		return mDb.query(true, travelTable, new String[] {colName,
				colDescrizione, colDataStart, colDataStop, colInCorso, colDistanza}, 
				id +"="+colID, null, null, null, null, null);
	}

	// restituisco l'ultimo viaggio creato
	protected Cursor fetchLastTravel() throws CursorIndexOutOfBoundsException {
			Cursor cursor = fetchAllTravels();
			cursor.moveToLast();
			return cursor;		
	}
	
	// eseguo l'update di un viaggio
	protected boolean updateTravel(long id, String name, String descrizione, 
			long start, long stop, int distance, boolean inCorso ) {
		
		int t = 0;
		if (inCorso) t = 1;
		
		Log.i("DDV", "t:"+t+" incorso:"+inCorso);
        ContentValues args = new ContentValues();
        args.put(colName, name);
        args.put(colDescrizione, descrizione);
        args.put(colDataStart, start);
        args.put(colDataStop, stop);
        args.put(colDistanza, distance);
        args.put(colInCorso, t);
        return mDb.update(travelTable, args, id + "=" + colID, null) > 0;
    }	
}
