package it.VisualMap;

import it.Date.DateManipulation;
import it.Travel.Travel;

import java.util.ArrayList;
import java.util.List;

import Settings.TravelSettings;
import android.app.ProgressDialog;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

public class AddRoute {

	/**
	 */
	private ProgressDialog progressDialog;
	/**
	 */
	private List<Overlay> listOfOverlays;
	/**
	 */
	private List<GeoPoint> polyLine;
	/**
	 */
	private int length;
	/**
	 */
	private int baseOfProgress;
	/**
	 */
	private Travel travel;
	
	/**
	 */
	private long lastTimePointParsed = 0;
	/**
	 */
	private int count = 0;
	
	/**
	 */
	private List<Points> points;
	/**
	 */
	private TravelSettings ts;
	
	
	public AddRoute(Travel travel, ProgressDialog progressDialog, List<Overlay> listOfOverlays) {
		this.travel=travel;
		this.progressDialog=progressDialog;
		this.listOfOverlays = listOfOverlays;
		polyLine = new ArrayList<GeoPoint>();
		baseOfProgress = progressDialog.getProgress();
		points = new ArrayList<Points>();
		ts = new TravelSettings(travel.getContext());
		lastTimePointParsed = ts.getLastPointParsed();
	}
	
	public List<Overlay> start() {
		buildRoute();
		return save();
	}
	
	//tipo in input, valore incrementale, valore restituito 
	private void buildRoute() {
		
		points = travel.getPoints();	
		
		length = points.size();
		long tmp = DateManipulation.getCurrentTimeMs();
		Log.i("DDV", "START PARSIN POINTS: " +tmp);
			
		if (length > 1) {
			
			Points currentPoint = points.get(0);
			Points oldPoint=currentPoint;
			
			for (int progress=0; progress<length; progress++) {
				
				currentPoint = points.get(progress);
				
				if (currentPoint.getGeoPoint() != oldPoint.getGeoPoint()) {
					oldPoint = currentPoint;
					polyLine.add(currentPoint.getGeoPoint());
					setZoomAndCenter(currentPoint);
					
				}
				onProgressUpdate((int)(progress*100/length));
			}
		}
		saveCenterPosition();
	}

	
	private void setZoomAndCenter(Points currentPoint) {
		
		if ((currentPoint.getDataRilevamento() > lastTimePointParsed)
				&& (travel.isViaggioInCorso())) {
			ts.addPoint(currentPoint);
			count++;
		}
	}
	
	private void saveCenterPosition() {
		if (count > 0) {
			ts.saveInSP(travel.getContext());
		}
	}
	
	/**
	 * @return
	 */
	public Travel getTravel() {
		return travel;
	}
	
	private void onProgressUpdate(int progress) {
		progressDialog.setProgress(baseOfProgress+progress);
	}
	
	private List<Overlay> save() {
		
		if (polyLine.size()>1) {
			DesignRoutes designRoutes = new DesignRoutes(polyLine);
			listOfOverlays.add(designRoutes);
			return listOfOverlays;
		}
		return listOfOverlays;
	}		
}
