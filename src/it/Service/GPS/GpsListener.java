package it.Service.GPS;

import it.Date.DateManipulation;
import it.Travel.Travel;
import it.VisualMap.Points;

import java.sql.SQLException;

import Settings.TimerSettings;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GpsListener {

	/**
	 */
	private LocationManager locationManager;
	/**
	 */
	private String bestProvider;
	/**
	 */
	private LocationListener myLocationListener = null;
	/**
	 */
	private String serviceString = Context.LOCATION_SERVICE;
	/**
	 */
	private Travel travel;
	/**
	 */
	private int time;
	/**
	 */
	private int oldTime=0;
	
	
	public GpsListener(Travel travel) {
		
		locationManager = (LocationManager)travel.getContext().getSystemService(serviceString);
 	   	bestProvider = locationManager.getBestProvider(setCriteria(), true);
		this.travel=travel;
		
		TimerSettings.removeTimer(travel.getContext());
	}
	
	public void stopListener() {		
		locationManager.removeUpdates(myLocationListener);
	}
	
	public void startListener(final int time, int space) {	// recupera la posizione gps
				this.time = time;
	 	   		myLocationListener = new LocationListener() {
	 	   		
	 	   			Location location1 = null;
	 	   		
	 	   			public void onLocationChanged(Location location) {
	 	   		
	 	   			if ((location != null) && (location != location1)) {
	 	   				try {
	 	   					savePosition(location);
	 	   					location1 = location;
	 	   				} catch (SQLException e) { e.printStackTrace(); }
	 	   			}	   		
	 	   		}
	 		  	public void onProviderDisabled(String provider){}
	 		  	public void onProviderEnabled(String provider){}
	 		  	
	 		  	public void onStatusChanged(String provider, int status, Bundle extras){}
	 	   };
	
	 	   locationManager.requestLocationUpdates(bestProvider, time, space, myLocationListener);
	 	  
	 	   
		}
		// salva la posizione passatagli nel db
		private void savePosition(Location location) throws SQLException {
			Points point = new Points();
			point.setLatitude((int) (location.getLatitude() * 1E6));
			point.setLongitude((int) (location.getLongitude() * 1E6));
			point.setDataRilevamento(DateManipulation.getCurrentTimeMs());
		
			if (time == oldTime)
				travel.addPoints(point, time);
			else {
				oldTime = time;
				travel.addPoints(point, 0);
			}
				
		}
		
		// criteri globali per la gestione del gps
		private Criteria setCriteria() {
			
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setSpeedRequired(false);
			criteria.setCostAllowed(false);
			return criteria;
		}
}
