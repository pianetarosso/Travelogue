package it.VisualMap;

import it.DiarioDiViaggio.R;
import it.DiarioDiViaggio.TravelMap;
import it.Service.GPS.MediaScan;
import it.Travel.Travel;
import it.VisualMap.Menu.NavigationMenu;

import java.net.URISyntaxException;
import java.util.List;

import Settings.MySettings;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Spinner;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class DesignMap extends AsyncTask<Void, Void, Void> {
	
	/**
	 */
	private MapView mapView;
	/**
	 */
	private Travel travel = null;
	/**
	 */
	private ProgressDialog progressDialog;
	/**
	 */
	private AddRoute addRoute;
	/**
	 */
	private AddMarkers addMarkers = null;
	/**
	 */
	private Context context;
	/**
	 */
	private TravelMap travelMap;
	/**
	 */
	private boolean newt = false;
	public boolean initialize(long id, TravelMap travelMap) {
		
		this.mapView = travelMap.getMapView();
		this.context = mapView.getContext();
		this.travelMap = travelMap;

		loadTravel(id);
		
		return (travel != null);
	}
	
	
	private ProgressDialog buildProgressDialog(String text) {
		
		ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
 	   	progressDialog.setCancelable(false);
 	   	progressDialog.setMessage(text);
 	   	progressDialog.setProgress(0);
 	   	progressDialog.setMax(300);
 	   	progressDialog.show();
 	   	return progressDialog;
	}
	
	private void loadTravel(long id) {
	
		MySettings mySettings = new MySettings(context);
		
		if (id == -1) {
			// tento il recupero dell'id del viaggio in corso 
			id = mySettings.getInCorso();
			newt = true;
		}
		
		// se non c'è nessun viaggio in corso tento il recupero dell'ultimo viaggio visualizzato
		if (id ==-1) {
			// recupero l'id dell'ultimo viaggio visualizzato 
			id = mySettings.getUltimoVisualizzato();
			newt = false;
		}
		
		if (id != -1) {
			travel= Travel.load(id, context);
			mySettings.setUltimoVisualizzato(id);
			mySettings.saveToSP(context);
			String progressDialogMessage = context.getString(R.string.loading);
			progressDialog = buildProgressDialog(progressDialogMessage);
		}
		else {
			mapView.displayZoomControls(true);
			setTransparentPanelVisibility(false);
		}
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		
		// verifico se ci sono immagini che ancora non sono state aggiunte
		// in caso affermativo le aggiungo
		if (newt) {
			MediaScan mediaScan = new MediaScan(travel);
			mediaScan.setprogressDialog(progressDialog);
			try {
				travel = mediaScan.scanImages();
			} catch (URISyntaxException e) {e.printStackTrace();}
			
		}
		// aggiungo alla mappa il percorso
		addRoute = new AddRoute(travel, progressDialog, listOfOverlays);
		listOfOverlays = addRoute.start();
		travel = addRoute.getTravel();
		
		// aggiungo alla mappa i marker con le immagini
		View view = (View) mapView.getParent();
		addMarkers = new AddMarkers((View)view.findViewById(R.id.photoLayout),travel, mapView, progressDialog, listOfOverlays);
		addMarkers.start();
		travel = addMarkers.getTravel();

		travel.save();
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		mapView.invalidate();
		updateTravelMap();
		loadSpinner();
		progressDialog.dismiss();
	}
	
	private void updateTravelMap() {
		travelMap.setTravel(travel);
		travelMap.setAddMarkers(addMarkers);
	}
	
	private void loadSpinner() {	
		setTransparentPanelVisibility(true);
		mapView.displayZoomControls(false);
		NavigationMenu nm = new NavigationMenu(travelMap);
		Spinner s = travelMap.setNavigationSpinner();
	
		nm.setControlSpinner(s);
		nm.moveToLastPointAdded();
	}
	
	private void setTransparentPanelVisibility(boolean visible) {
		View t = (View)mapView.getParent();
		View tp = (View)t.findViewById(R.id.transparent_panel);
		if (visible)
			tp.setVisibility(View.VISIBLE);
		else
			tp.setVisibility(View.GONE);
	}
}
