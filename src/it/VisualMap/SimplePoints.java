package it.VisualMap;

import java.io.Serializable;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class SimplePoints implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 */
	private long gpsId;
	/**
	 */
	private int latitude;
	/**
	 */
	private int longitude;
	/**
	 */
	private long dataRilevamento;
	/**
	 */
	private long travelID;
	/**
	 */
	private GeoPoint geoPoint = null;

		
	public SimplePoints(){}
	
	public static SimplePoints parseToSimplePoints(Points p) {
		SimplePoints sp = new SimplePoints();
		sp.setDataRilevamento(p.getDataRilevamento());
		sp.setGpsId(p.getGpsId());
		sp.setLatitude(p.getLatitude());
		sp.setLongitude(p.getLongitude());
		sp.setTravelID(p.getTravelID());
		return sp;
	}
	
	/**
	 * @param travelID
	 */
	public void setTravelID(long travelID) {
		this.travelID = travelID;
	}
			
	/**
	 * @return
	 */
	public long getTravelID() {
		return travelID;
	}
		
	/**
	 * @return
	 */
	public int getLatitude() {
		return latitude;
	}
		
	/**
	 * @return
	 */
	public int getLongitude() {
		return longitude;
	}
		
	/**
	 * @return
	 */
	public long getGpsId() {
		return gpsId;
	}
	
	/**
	 * @return
	 */
	public GeoPoint getGeoPoint() {
		
		if (this.geoPoint == null) {
	        this.geoPoint = new GeoPoint(this.latitude, this.longitude);
	    }
	    return this.geoPoint;
	  
	}
		
	public Location getLocation() {
		Location location = new Location(""+gpsId);
		location.setLatitude(latitude/1E6);
		location.setLongitude(longitude/1E6);
		return location;
	}
		
		
	/**
	 * @return
	 */
	public long getDataRilevamento() {
		return dataRilevamento;
	}
		
	/**
	 * @param latitude
	 */
	public void setLatitude(int latitude) {
		this.latitude=latitude;
	}
		
	/**
	 * @param longitude
	 */
	public void setLongitude(int longitude) {
		this.longitude=longitude;
	}
		
	/**
	 * @param dataRilevamento
	 */
	public void setDataRilevamento(long dataRilevamento) {
		this.dataRilevamento=dataRilevamento;
	}
		
	/**
	 * @param gpsId
	 */
	public void setGpsId(long gpsId) {
		this.gpsId=gpsId;
	}
	
	public int equals(Points point) {
		if ((latitude == point.getLatitude()) && (longitude == point.getLongitude()))
			return (int) Math.abs(point.getDataRilevamento()-dataRilevamento);
		else
			return -1;
	}
		
	public String toString() {
		return "Point id:"+gpsId+
		", latitude:"+latitude+
		", longitude:"+longitude+
		", time get:"+dataRilevamento+
		", travel id:"+travelID;
	}
}


