package it.DB;


import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TravelDB {
	
	private static final String DATABASE_NAME = "DataBase dei Viaggi";
	static final int DATABASE_VERSION = 1;
	
	/**
	 */
	private final Context mCtx;
	/**
	 */
	private DatabaseHelper mDbHelper;
	/**
	 */
	private SQLiteDatabase mDb;
	
	/**
	 */
	private Travel travel;
	/**
	 */
	private Contents contents;
	/**
	 */
	private Gps gps;
	
	
	// Stringhe creazione Trigger

	// TR_colGpscolGpsTravelId -----------------------------------------------------------------------------------------------------------
	// verifica che l'id del viaggio tra le tabelle travelTable e gpsTable siano coerenti
	private static final String
	TR_colIDcolGpsTravelId = "CREATE TRIGGER TR_colIDcolGpsTravelId " +
	"BEFORE INSERT ON " +Gps.gpsTable+
	" FOR EACH ROW BEGIN" +
	" SELECT CASE WHEN ((SELECT " +Travel.colID+ " FROM " +Travel.travelTable+
	" WHERE " +Travel.colID+"=new."+Gps.colGpsTravelID+") IS NULL)"+
	" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
	" END;";

	// TR_colContentscolGpsTravelId -----------------------------------------------------------------------------------------------------------
	// verifica che l'id del viaggio tra le tabelle travelTable e ContentsTable siano coerenti
	private static final String
	TR_colContentscolGpsTravelId = "CREATE TRIGGER TR_colContentscolGpsTravelId " +
	"BEFORE INSERT ON " +Contents.contentsTable+
	" FOR EACH ROW BEGIN" +
	" SELECT CASE WHEN ((SELECT " +Travel.colID+ " FROM " +Travel.travelTable+
	" WHERE " +Travel.colID+"=new."+Contents.colContentTravelId+") IS NULL)"+
	" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
	" END;";
	
	// TR_oneTravelOn -----------------------------------------------------------------------------------------------------------
	// verifica che ci sia un solo viaggio in corso alla volta
	private static final String
	TR_oneTravelOn = "CREATE TRIGGER TR_oneTravelOn " +
	"BEFORE INSERT ON " +Travel.travelTable+
	" FOR EACH ROW BEGIN" +
	" SELECT CASE WHEN ((SELECT " +Travel.colInCorso+ " FROM " +Travel.travelTable+
	" WHERE " +Travel.colInCorso+"=new."+Travel.colInCorso+") IS NOT NULL)"+
	" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
	" END;";
	
		
	private static class DatabaseHelper extends SQLiteOpenHelper {

		
		// DATABASE HELPER --------------------------------------------------------
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		
		// CREAZIONE DATABASE E TRIGGER --------------------------------------------
		@Override
		public void onCreate(SQLiteDatabase db) {  
		
			// creazione db Viaggi
			Log.i(DATABASE_NAME, "Creating Table: " + Travel.travelTable);
			db.execSQL(Travel.Travels);
			  	   
			// creazione db Contenuti
			Log.i(DATABASE_NAME, "Creating Table: " + Contents.contentsTable);
			db.execSQL(Contents.Contents);
			
			// crezione db GPS
			Log.i(DATABASE_NAME, "Creating Table: " + Gps.gpsTable);
			db.execSQL(Gps.Gps);
			  
			// trigger controllo consistenza tra colContentTravelId tabella Contents e colID tabella viaggio
			Log.i(DATABASE_NAME, "Creating Trigger: TR_colContentscolGpsTravelId");
			db.execSQL(TR_colContentscolGpsTravelId);
			
			//trigger controllo consistenza tra colID tabella viaggio e colGpsTravelID della tabella gps
			Log.i(DATABASE_NAME, "Creating Trigger: TR_colIDcolGpsTravelId");
			db.execSQL(TR_colIDcolGpsTravelId);
			
			//trigger controllo che esista un solo viaggio in corso
			Log.i(DATABASE_NAME, "Creating Trigger: TR_oneTravelOn");
			db.execSQL(TR_oneTravelOn);
	
		} 

		
		// UPGRADE DATABASE ----------------------------------------------------------
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(DATABASE_NAME, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + Travel.travelTable);
			db.execSQL("DROP TABLE IF EXISTS " + Contents.contentsTable);
			db.execSQL("DROP TABLE IF EXISTS " + Gps.gpsTable);
			db.execSQL("DROP TRIGGER IF EXISTS TR_colContentscolGpsTravelId");
			db.execSQL("DROP TRIGGER IF EXISTS TR_colIDcolGpsTravelId");
			db.execSQL("DROP TRIGGER IF EXISTS TR_colIDcolGMapTravelId");
			onCreate(db);
		}
	}
	
	// COSTRUTTORE ----------------------------------------------------------------------
	public TravelDB(Context ctx) {
		this.mCtx = ctx;
	}
	
	// OPEN DATABASE --------------------------------------------------------------------
	public TravelDB open() throws SQLException {
		Log.i(Travel.travelTable, "OPening DataBase Connection....");
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		
		travel = new Travel(mDb);
		contents = new Contents(mDb);
		gps = new Gps(mDb);
		
		return this;
	}
	
	// CLOSE DATABASE
	public void close() {
		mDb.close();
	}
	
	
	
	//CREATE/////////////////////////////////////////////////////////////////////////////////////
	
	// Crea un viaggio
	public long createTravel(String travelName, String descrizione, long data, boolean inCorso) {
		return travel.createTravel(travelName, descrizione, data, inCorso);
	}
	
	// Crea un content
	public long createContent(String contentName, long data, String locazioneMemoria, 
			int latitudine, int longitudine, String commento, String route, long travelId) {
		return contents.createContent(contentName, data, locazioneMemoria, 
				latitudine, longitudine, commento, route, travelId);
	}
	
	// crea un punto GPS
	public long createGPS(int latitudine, int longitudine, long data, long id) {
		return gps.createGPS(latitudine, longitudine, data, id);
	}

	
	
	// DELETE /////////////////////////////////////////////////////////////////////////////////
	
	// cancella un viaggio
	public boolean deleteTravel(long travelId) {
		return travel.deleteTravel(travelId);
	}
	
	// cancella un contenuto
	public boolean deleteContent(long id) {
		return contents.deleteContent(id);
	}
	
	// cancella i contenuti tramite l'id del viaggio
	public boolean deleteContentByTravelId(long travelId) {
		return contents.deleteContentByTravelId(travelId);
	}
	
	// cancella i contenuti dopo una certa data
	public boolean deleteContentAfterDate(long travelId, long date) {
		return contents.deleteContentAfterDate(travelId, date);
	}

	// cancella i contenuti prima di una certa data
	public boolean deleteContentBeforeDate(long travelId, long date) {
		return contents.deleteContentBeforeDate(travelId, date);
	}
	
	// cancella un punto gps
	public boolean deleteGps(long gpsId) {
		return gps.deleteGps(gpsId);
	}
	
	// cancella i punti gps con lo stesso id di un viaggio
	public boolean deleteGpsByTravelId(long travelId) {
		return gps.deleteGpsByTravelId(travelId);
	}
	
	// cancella i punti GPS dopo una certa data
	public boolean deleteGpsAfterDate(long travelId, long date) {
		return gps.deleteGpsAfterDate(travelId, date);
	}
	
	// cancella i punti GPS prima di una certa data
	public boolean deleteGpsBeforeDate(long travelId, long date) {
		return gps.deleteGpsBeforeDate(travelId, date);
	}
	

	
	// FETCH//////////////////////////////////////////////////////////////////////////////////////
	
	// recupera tutti i viaggi
	public Cursor fetchAllTravels() {
		return travel.fetchAllTravels();
	}
	
	// recupera un viaggio specifico (tramite il suo id)
	public Cursor fetchTravel(long id) {
		return travel.fetchTravel(id);
	}

	// recupera l'ultimo viaggio creato
	public Cursor fetchLastTravel() throws CursorIndexOutOfBoundsException {
			return travel.fetchLastTravel();	
	}

	// recupero tutti i punti dove è stato creato un contenuto di un viaggio
	public Cursor fetchAllContentsPoints(long id) throws SQLException {
		return contents.fetchAllContentsPoints(id);
	}	
	
	// recupero tutti i punti gps tramite l'id dek viaggio
	public Cursor fetchAllGpsPoints(long id) throws SQLException {
		return gps.fetchAllGpsPoints(id);
	}
	
	// recupero tutti i punti gps compresi in un intervallo di date
	public Cursor fetchTimeSelectedGpsPoints(long id, long start, long stop) throws SQLException {
		return gps.fetchTimeSelectedGpsPoints(id, start, stop);
	}
	
	
	
	// UPDATE/////////////////////////////////////////////////////////////////
	
	// faccio l'update del viaggio
	public boolean updateTravel(long id, String name, String descrizione, 
			long start, long stop, int distance, boolean inCorso ) {
        return travel.updateTravel(id, name, descrizione, start, stop, distance, inCorso);
    }	
	
	// faccio l'update di un contenuto
	public boolean updateContent(long date, int lat, int lng, String route, String path, long id) {
        return contents.updateContent(date, lat, lng, route, path, id);       
	}
	
	// faccio l'update di un punto
	public boolean updatePoint(long date, int lat, int lng, long id) {
		return gps.updatePoint(date, lat, lng, id);  	
	}
	
	
	
	// OTHERS ////////////////////////////////////////////////////////////////
	
	// recupero il numero di immagini appartenenti ad uno stesso viaggio
	public int fetchNumberOfContents(long id) throws SQLException {
		return contents.fetchNumberOfContents(id);
	}

}


