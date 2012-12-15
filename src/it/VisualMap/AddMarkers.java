package it.VisualMap;

import it.Photos.Images;
import it.Travel.Travel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class AddMarkers {

	/**
	 */
	private MapView mapView;
	/**
	 */
	private Markers markers;
	/**
	 */
	private ProgressDialog progressDialog;
	/**
	 */
	private List<Overlay> listOfOverlays;
	/**
	 */
	private int gap = 50; // si occupa del "gap" entro cui far collidere 2 punti vicini in uno solo sulla mappa
	
	/**
	 */
	private int baseOfProgress;
	/**
	 */
	private int length = 0;
	/**
	 */
	private int progress;
	/**
	 */
	private MyOverlayItem overlayitem = null;
	/**
	 */
	private Travel travel;
	/**
	 */
	private Images oldImage = null;
	/**
	 */
	private Images currentImage;
	/**
	 */
	private View view;
	
	/**
	 */
	private CreateMarkerDrawable cmd;
	
	public AddMarkers(View view, Travel travel, MapView mapView, ProgressDialog progressDialog, List<Overlay> listOfOverlays) {
		
		this.travel=travel;
		this.mapView=mapView;
		this.progressDialog=progressDialog;
		this.listOfOverlays = listOfOverlays;
		this.view = view;
		baseOfProgress = progressDialog.getProgress();
		
		cmd = new CreateMarkerDrawable(null, travel.getContext());
		
		setMarkers();	
	}
	
	public List<Overlay> start() {
		buildPoints();
		return save();
	}
	
	/**
	 * @return
	 */
	public Travel getTravel() {
		return travel;
	}
	
	public void updateMarkersImage() {
		markers.updateMarkers();
	}
	
	private void setMarkers() {
		
		Drawable marker = cmd.draw(-1, -1, Integer.MIN_VALUE, Integer.MIN_VALUE, false);
	 	markers = new Markers(marker);
	 	markers.setMapView(mapView);
	 	markers.setTravel(travel);
	 	markers.setView(view);
	}
	
	protected Images geoCode(Images image) {
		
		String result = "";
		Geocoder geoCoder = new Geocoder(travel.getContext(), Locale.getDefault());
		try {
			// tento la connessione
            List<Address> list = geoCoder.getFromLocation(
                image.getGeopoint().getLatitudeE6() / 1E6, 
                image.getGeopoint().getLongitudeE6() / 1E6, 1
                );
            
            if (list != null && list.size() > 0) {
                Address address = list.get(0);
                
                // parso il risultato per ottenere l'indirizzo
                result = address.getAddressLine(0);
                if (address.getLocality()!=null) 
                	result = result + ", " + address.getLocality();
                
                image.setAddress(result);
                travel.addImages(image);
            }
            
            list.clear();
        } catch (IOException e) {}  
        return image;
	}
	
	protected boolean near(GeoPoint a, GeoPoint b) {
		return ((Math.abs(a.getLatitudeE6()-b.getLatitudeE6())<=gap) && (Math.abs(a.getLongitudeE6()-b.getLongitudeE6()) <= gap));
	}
	
	private void buildPoints () {
		
		List<Images> images = travel.getImages();
		Log.i("DDV", "images:"+images.size());
		// se esistono punti
		if (!images.isEmpty()) {
			
			length = images.size();
			
			for (progress = 0 ; progress < length; progress++) {
				
				currentImage = images.get(progress);
			
				// verifico se una località con coordinate simili era già stata presa in esame
				// in quel caso la raggruppo insieme alle altre
				if ((oldImage != null) && (near(oldImage.getGeopoint(), currentImage.getGeopoint()))) {
					
					overlayitem.addImage(currentImage);
					if (currentImage.getAddress().length() == 0) {
						if (oldImage.getAddress().length() > 0) {
							currentImage.setAddress(oldImage.getAddress());
							travel.addImages(currentImage);
						}
					}
				}
				
				// in questo caso vuol dire che le coordinate sono troppo differenti per unirle in una
				// quindi salvo l'oggetto vecchio e ne instanzio uno nuovo
				else if (oldImage != null) {
						setTextAndSave(oldImage);
						createOverlay(currentImage.getGeopoint());
						oldImage = currentImage;
					}
				
				// in questo caso è la prima volta che instanzio una coordinata
				else {
					
					oldImage = currentImage;
				
					// geocoding per determinare il nome della via dove è stato generato il contenuto
					// se nel db non è presente
					
					if (currentImage.getAddress().length() == 0) 
						currentImage = geoCode(currentImage);	                	
					
					// aggiungo il marker
					createOverlay(currentImage.getGeopoint());
				}					
			
				// faccio progredire la sbarra
				onProgressUpdate((int)(progress*100/length));
			}
			setTextAndSave(oldImage);
		}
	}	
		
	
	protected void onProgressUpdate(int progress) {
		progressDialog.setProgress(baseOfProgress+progress);
	}
	
	
	protected List<Overlay> save() {
		
		if (markers.size() > 0) {
			
			// una volta terminato di aggiungere gli overlay lancio il comando "populate"
			// per unirli in un unico overlay (è comodo per questioni di efficienza)
			markers.populateNow();
			
			// aggiungo l'overlay alla mappa
			listOfOverlays.add(markers);
			
			mapView.getController().animateTo(oldImage.getGeopoint());
		}
		return listOfOverlays;
	}
	
	private void createOverlay(GeoPoint p) {
		
		// costruisco un nuovo overlay 
		overlayitem = new MyOverlayItem(p, "", "");
		overlayitem.addImage(currentImage);
		
		travel.addMarkerGeoPoint(currentImage.getGeopoint());
		
	}
	
	private void addMarker() {	
		
		// aggiungo un nuovo overlay ai marker
		markers.addOverlay(overlayitem);
	}
	
	private void setTextAndSave(Images image) {
		
		String text = "";
		
		if ((text = image.getAddress()).length()==0) {
			text = "latitude: "+image.getLatitude()/1E6+",\n"+
				"longitude: "+image.getLongitude()/1E6+";";
		}
		
		// costruisco la stringa da visualizzare
		text = "Posizione:"+"\n"+text;
		
		overlayitem.setSnippet(text);
		addMarker();
	}		
}

