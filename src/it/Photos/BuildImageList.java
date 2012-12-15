package it.Photos;

import it.DB.TravelDB;
import it.Date.DateManipulation;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import Settings.ListTravelsBackup;
import Settings.MySettings;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class BuildImageList {
	
	/**
	 */
	private Context context;
	/**
	 */
	private long start;
	/**
	 */
	private long stop;
	/**
	 */
	private long travelID;
	/**
	 */
	private List<Images> images;
	/**
	 */
	private Uri imagesUri;
	
	
	public BuildImageList(Context context, long travelID) {
		this.context=context;
		this.travelID=travelID;
		images = new ArrayList<Images>();

		// Recupero l'uri delle immagini.
	    imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	}
	
	public long getDateStart() {
		return start;
	}
	
	public long getDateStop() {
		return stop;
	}
	
	public List<Images> fromFile() { // Caricamento delle immagini da un file di backup della SD
		return ListTravelsBackup.loadImages(travelID, context);
	}
	
	public List<Images> fromDataBase() { // Caricamento della lista delle immagini dal DB
		String colContentName="NomeContenuto";						// chiave, String (compresa l'estensione del file)
		String colContentData="Date";								// data creazione contenuto, LONG millisecondi
		String colContentLatitude = "Latitude";						// latitudine, INT
		String colContentLongitude = "Longitude";					// longitudine, INT
		String colLocalContentLocation="PosizioneLocaleContenuto";	// indica il percorso in cui si trova il contenuto localmente, string
		String colContentRoute ="Address";							// indirizzo del contenuto, string
		String contentsId ="idContenuti";							// chiave, long id del contenuto
		
		// apro la connessione col database 
		TravelDB traveldb = new TravelDB(context);
		traveldb.open();
		
		// recupero tutti i contenuti
		Cursor cursor = traveldb.fetchAllContentsPoints(travelID); 
		
		// Recupero gli indici delle colonne della tabella
		int nameIndex = cursor.getColumnIndex(colContentName);
		int latitudeIndex = cursor.getColumnIndex(colContentLatitude);
		int longitudeIndex = cursor.getColumnIndex(colContentLongitude);
		int addressIndex = cursor.getColumnIndex(colContentRoute);
		int dateIndex = cursor.getColumnIndex(colContentData);
		int pathIndex = cursor.getColumnIndex(colLocalContentLocation);
		int idIndex = cursor.getColumnIndex(contentsId);
		
		cursor.moveToFirst();
		
		for (int progress=0; progress<cursor.getCount(); progress++) {
			Images image = new Images();
	    	image.setName(cursor.getString(nameIndex));
	    	image.setDate(cursor.getLong(dateIndex));
	    	image.setLatitude(cursor.getInt(latitudeIndex));
	    	image.setLongitude(cursor.getInt(longitudeIndex));
	    	image.setPath(cursor.getString(pathIndex));
	    	image.setAddress(cursor.getString(addressIndex));
	    	image.setTravelID(cursor.getLong(idIndex));
	    	image.setContext(context);
	    	images.add(image);
	    	cursor.moveToNext();
		} 
    	cursor.close();
    	traveldb.close();
    	return images;
	}
	
    public List<Images> fromMediastore() throws URISyntaxException { // Caricamento della lista di immagini dal MediaStore
    	
    	Cursor cursor = retriveImagesMediaContents();
    	cursor.moveToFirst();
    	
    	// recupero il numero della colonna con il nome e la data dell'immagine
        int nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);  
        int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
        int latitudeImageColumn = cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE);
        int longitudeImageColumn = cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE);
        int _id = cursor.getColumnIndex(MediaStore.Images.Media._ID);
    	
        for (int counter=0; counter<cursor.getCount(); counter++) {
        	
        	Images image = new Images();
        	image.setName(cursor.getString(nameColumn));
        	image.setDate(cursor.getLong(dateColumn));
    		
        	try {
            	double lat = cursor.getDouble(latitudeImageColumn) * 1E6;
            	double lng = cursor.getDouble(longitudeImageColumn) * 1E6;
            	
            	image.setLatitude((int)lat);
            	image.setLongitude((int)lng);
            	
        	} catch (NullPointerException npe) {}
        	
        	// ricavo l'uri dell'immagine e lo converto in String
            int imageID = cursor.getInt(_id);
            Uri uri = Uri.withAppendedPath( imagesUri, ""+imageID);
		    image.setPath(getRealPathFromURI(uri, context));
        	images.add(image);
        	
        	cursor.moveToNext();
        } 
        
        cursor.close();
    	return images;
    }
    
    // funzione per recuperare la path assoluta delle immagini
    public static String getRealPathFromURI(Uri contentUri, Context context) throws URISyntaxException {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    
	// funziona che recupera le immagini dal mediastore in un range di date
	protected Cursor retriveImagesMediaContents() {
			
			// recupero la data di start
			MySettings s = new MySettings(context);
			
			start = s.getDataScansione();
			stop = DateManipulation.getCurrentTimeMs();
			
			// definisco che proprietà delle immagini sto cercando
		    String[] projection = new String[]{
		            MediaStore.Images.Media.DATE_TAKEN,
		            MediaStore.Images.Media.DISPLAY_NAME,
		            MediaStore.Images.Media.LATITUDE,
		            MediaStore.Images.Media.LONGITUDE,
		            MediaStore.Images.Media._ID
		    };		    
		    
		    // Costruisco le query.
		    String select = "" + MediaStore.Images.Media.DATE_TAKEN + " BETWEEN " + start + " AND " + stop;
		    String order_by = "" + MediaStore.Images.Media.DATE_TAKEN + " ASC";
		    
		    // eseguo la query
		    Cursor cursor = context.getContentResolver().query(imagesUri, projection, select, null, order_by);
		    
		    return cursor;
		}

}
