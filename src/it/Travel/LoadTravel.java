package it.Travel;

import it.DB.TravelDB;

import java.util.ArrayList;
import java.util.List;

import Settings.TravelSettings;
import android.content.Context;
import android.database.Cursor;

public class LoadTravel {
	
	private static final String colName="NomeViaggio";								// nome del viaggio, string
	private static final String colDescrizione="DescrizioneViaggio";				// descrizion del viaggio, string
	private static final String colDataStart="DataPartenzaViaggio";					// data inizio viaggio, string
	private static final String colDataStop="DataFineViaggio";						// data fine viaggio, string
	private static final String colInCorso = "ViaggioInCorso";						// indica se il viaggio è in corso, int
	private static final String colDistanza = "MetriPercorsi";						// indica i metri percorsi durante il viaggio	
	private static final String colID = "IDViaggio";	
	
	
	protected static Travel load (long id, Context context) {
		Travel travel = loadOnlyTravel(id, context);	
		travel.loadPoints();
		travel.loadImages();				
		return travel;		
	}
	
	protected static Travel loadTravelAndImages(long id, Context context) {
		Travel travel = loadOnlyTravel(id, context);	
		travel.loadPoints();
		return travel;
	}
	
	protected static Travel loadOnlyTravel(long id, Context context) {
		
		Travel travel = new Travel(context);
		
		TravelDB travelDB = new TravelDB(context);
		travelDB.open();
		
		Cursor cursor = travelDB.fetchTravel(id);
		
		cursor.moveToFirst();
		
		try {
			int nameIndex = cursor.getColumnIndex(colName);
			int descrizioneIndex = cursor.getColumnIndex(colDescrizione);
			int dataPartenzaIndex = cursor.getColumnIndex(colDataStart);
			int inCorsoIndex = cursor.getColumnIndex(colInCorso);
			int dateStopIndex = cursor.getColumnIndex(colDataStop);
			int distanceIndex = cursor.getColumnIndex(colDistanza);
			
			travel.setNome(cursor.getString(nameIndex));
			travel.setDescrizione(cursor.getString(descrizioneIndex));
			travel.setdataPartenza(cursor.getLong(dataPartenzaIndex));
			travel.setViaggioInCorso(cursor.getInt(inCorsoIndex));
			travel.setDateStop(cursor.getLong(dateStopIndex));
			travel.setDistance(cursor.getInt(distanceIndex));
			travel.setNumberOfImages(travelDB.fetchNumberOfContents(id));
			travel.setId(id);
			
			cursor.close();
			travelDB.close();
			
			if (travel.isViaggioInCorso()) {
				TravelSettings ts = new TravelSettings(context);
				int distance = ts.getDistance();
				if (travel.getDistance()<distance)
					travel.setDistance(distance);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			travel = null;
			cursor.close();
			travelDB.close();
		}

		return travel;
	}
	
	protected static List<Travel> getAllTravels(Context context) {
		TravelDB traveldb = new TravelDB(context);
		traveldb.open();
		Cursor cursor = traveldb.fetchAllTravels();
		cursor.moveToFirst();
		int size = cursor.getCount();
		List<Travel> travels = new ArrayList<Travel>();
		
		int idIndex = cursor.getColumnIndex(colID);
		
		for (int i=0; i<size; i++) {
			long id = cursor.getLong(idIndex);
			travels.add(loadOnlyTravel(id, context));
			cursor.moveToNext();
		}
		
		cursor.close();
		traveldb.close();
		
		return travels;	
	}
}
