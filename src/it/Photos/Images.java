package it.Photos;

import it.DB.TravelDB;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;

import com.google.android.maps.GeoPoint;


@SuppressWarnings("serial")
public class Images extends SimpleImage {

	/**
	 */
	private Context context;
	
	public Images() {}
	
	/**
	 * @param context
	 */
	public void setContext(Context context) {
		this.context=context;
	}
	
	// Funzione per convertire una SimpleImage in una Image 
	public static Images parseBaseImages(SimpleImage bi, Context context) {
		Images i = new Images();
		i.setContext(context);
		i.setAddress(bi.getAddress());
		i.setDate(bi.getDate());
		i.setId(bi.getId());
		i.setLatitude(bi.getLatitude());
		i.setLongitude(bi.getLongitude());
		i.setName(bi.getName());
		i.setPath(bi.getPath());
		i.setTravelID(i.getTravelId());
		return i;
	}
	
	public void deleteAlsoFile() { // oltre che dal Mediastore cancella l'immagine anche dal supporto fisico
		File file = new File(this.getPath());
		if(file.delete()) 
		    deleteFromMediaStore();
		delete();
	}
	
	private void deleteFromMediaStore() { // cancella l'immagine dal Mediastore
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, 
				Uri.parse("file://" + Environment.getExternalStorageDirectory()))); 
	}
	
	public void save() { // salva i paramentri di una nuova immagine sul db
		TravelDB traveldb = new TravelDB(context);
		traveldb.open();
		try {
	    this.setId(traveldb.createContent(this.getName(), this.getDate(), this.getPath(), this.getLatitude(), this.getLongitude(), "","", this.getTravelId()));
	    } catch (SQLiteConstraintException sce) {update();}
	    traveldb.close();
	}
	
	private boolean update() { // esegue l'update sul db
		TravelDB traveldb = new TravelDB(context);
		traveldb.open();
		boolean result = traveldb.updateContent(this.getDate(), this.getLatitude(), this.getLongitude(), this.getAddress(), this.getPath(), this.getId());
		traveldb.close();
		return result;
	}

	public boolean delete() { // cancella un'immagine dal db
		TravelDB traveldb = new TravelDB(context);
		traveldb.open();
		boolean result = traveldb.deleteContent(this.getId());
		traveldb.close();
		return result;
	}
	
	/**
	 * @return
	 */
	public Context getContext() {
		return context;
	}
	
	// funzioni che semplificano l'analisi delle coordinate, restituendole anche sottoforma di GeoPoint o Location
	public GeoPoint getGeopoint() {
		GeoPoint geoPoint = new GeoPoint(this.getLatitude(), this.getLongitude());
		return geoPoint;
	}
	
	public Location getLocation() {
		Location location = new Location(this.getName());
		location.setLatitude(this.getLatitude()/1E6);
		location.setLongitude(this.getLongitude()/1E6);
		return location;
	}
}
