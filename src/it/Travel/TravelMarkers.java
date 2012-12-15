package it.Travel;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

public class TravelMarkers {

	/**
	 */
	private List<GeoPoint> markers;
	
	public TravelMarkers() {
		markers = new ArrayList<GeoPoint>();
	}
	
	public void addMarkerGeoPoint(GeoPoint i) {
		markers.add(i);
	}
	
	public GeoPoint getLastMarkerAdded() {
		int l = markers.size()-1;
		if (l>=0)
			return markers.get(l);
		else
			return null;
	}
	
	protected void removeMarker(GeoPoint i) {
		markers.remove(i);
	}
	
	public GeoPoint getPreviousGeoPoint(GeoPoint i) {
		int p = markers.indexOf(i);
		if (p>0) 
			return markers.get(p-1);
		else
			return i;
	}
	
	public GeoPoint getNextGeoPoint(GeoPoint i) {
		int p = markers.indexOf(i);
		if (p<markers.size()-1) 
			return markers.get(p+1);
		else
			return i;
	}
}
