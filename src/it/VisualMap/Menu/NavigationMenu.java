package it.VisualMap.Menu;

import it.DiarioDiViaggio.R;
import it.DiarioDiViaggio.TravelMap;
import it.Travel.Travel;
import Settings.MapSettings;
import Settings.TravelSettings;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class NavigationMenu {

	/**
	 */
	private Spinner spinner;
	/**
	 */
	private MapView mapView;
	/**
	 */
	private View view;
	/**
	 */
	private Context context;
	/**
	 */
	private TravelMap travelMap;
	/**
	 */
	private GeoPoint lastMarkerCentered;
	/**
	 */
	private int lastSpinner = 0;
	/**
	 */
	private boolean stateButtons = true;
	
	public NavigationMenu(TravelMap travelMap) {
		this.mapView = travelMap.getMapView();
		this.context = travelMap.getContext();
		this.travelMap = travelMap;
		this.view = (View)mapView.getParent();
		loadNavigationButtons();
	}
    
	private Travel travel() {
		return travelMap.getTravel();
	}
	
	public void setControlSpinner(Spinner spinner) {
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        this.spinner = spinner;
        saveMeasures();
    }
	
	private void saveMeasures() {
		View view = mapView.getRootView();
		int size = (int)(view.getWidth()*0.7) ;
		
		MapSettings ms = new MapSettings(context);
		ms.setMap_dropdown(size);
		ms.saveToSP(context);
	}
	
	public void setButtonsSymbols(boolean test) {
		
		Button buttonP = (Button)view.findViewById(R.id.previous);
		Button buttonN = (Button)view.findViewById(R.id.next);
		if (test) {
			buttonP.setText(R.string.previous);
			buttonN.setText(R.string.next);
			stateButtons = true;
		}
		else {
			buttonP.setText(R.string.zoomOut);
			buttonN.setText(R.string.zoomIn);
			stateButtons=false;
		}
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
        	
        	if (id > -1) {
	        	switch (pos) {
	        	
	        	case 0:			// data creazione
	        		setButtonsSymbols(true);
	        		lastSpinner = 0;
	        		break;
	        	case 1:	
	        		setButtonsSymbols(false);
	        		lastSpinner = 1;
	        		break;
	        	case 2:	// centra ultimo 
	        		moveToLastPointAdded();
	        		break;
	        	case 3:		// mostra tutto
	        		showAll();
	        		break;	
	        	}
        	}
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
	
	
	public void loadNavigationButtons() {
		Button buttonP = (Button)view.findViewById(R.id.previous);
		buttonP.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						if (stateButtons)
							previous();
						else
							mapView.getController().zoomOut();
					}
				});
	
		Button buttonN = (Button)view.findViewById(R.id.next);
		buttonN.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						if (stateButtons)
							next();	
						else
							mapView.getController().zoomIn();
					}
				});
	}

	private void previous() {
		moveTo(true);
	}
	
	private void next() {
		moveTo(false);
	}
	
	private void showAll() {
		
		TravelSettings ts = new TravelSettings(context);
		
		MapController mc = mapView.getController();
		mc.zoomToSpan(ts.getZoomSpanLat(), ts.getZoomSpanLng());
		mc.animateTo(new GeoPoint(ts.getCenterLat(), ts.getCenterLng()));
		
    	view.findViewById(R.id.photoGallery).setVisibility(View.INVISIBLE);
    	
    	spinner.setSelection(lastSpinner);
    	
	}
	
	private void moveTo(boolean previous) {
		try {
			if (previous) 
				lastMarkerCentered = travel().getPreviousGeoPoint(lastMarkerCentered);
			else 
				lastMarkerCentered = travel().getNextGeoPoint(lastMarkerCentered);
			
			mapView.getController().animateTo(lastMarkerCentered);
		} catch (NullPointerException ne) {};
		view.findViewById(R.id.photoGallery).setVisibility(View.INVISIBLE);
	}
	
	public void moveToLastPointAdded() {
		
		try {
			GeoPoint lastPoint = travel().getLastPointAdded().getGeoPoint();
			lastMarkerCentered = travel().getLastMarkerAdded();
			
			if (lastMarkerCentered!= null) {
				int latitude = (lastPoint.getLatitudeE6()+lastMarkerCentered.getLatitudeE6())/2;
				int longitude = (lastPoint.getLongitudeE6()+lastMarkerCentered.getLongitudeE6())/2;
				int panLatitude = Math.abs(lastPoint.getLatitudeE6()-lastMarkerCentered.getLatitudeE6());
				int panLongitude = Math.abs(lastPoint.getLongitudeE6()-lastMarkerCentered.getLongitudeE6());
				
				mapView.getController().animateTo(new GeoPoint(latitude, longitude));
				mapView.getController().zoomToSpan(panLatitude, panLongitude);
			}
			else		
				mapView.getController().animateTo(lastPoint);	
		} catch (NullPointerException ne) {}
		
		spinner.setSelection(lastSpinner);
	}
	
}
