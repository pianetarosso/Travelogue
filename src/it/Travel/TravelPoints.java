package it.Travel;

import it.VisualMap.BuildPointsList;
import it.VisualMap.Points;

import java.util.ArrayList;
import java.util.List;

import Settings.TravelBackup;
import Settings.TravelSettings;
import android.content.Context;
import android.location.Location;

public class TravelPoints {

	/**
	 */
	private Context context;
	/**
	 */
	private long id;
	/**
	 */
	private List<Points> points;
	/**
	 */
	private int distance=0;
	/**
	 */
	private TravelSettings ts;
	
	
	public TravelPoints(Context context, long id) {
		this.context = context;
		this.id = id;
		points = new ArrayList<Points>();
		ts = new TravelSettings(context);
	}
	
	/**
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @param distance
	 */
	public void setDistance(int distance) {
		this.distance += distance;
	}
	
	/**
	 * @return
	 */
	public int getDistance() {
		return distance;
	}
	
	public List<Points> getPoints() {
		return points;
	}
	
	
	void loadPoints(TravelBackup travelBackup) {
		
		BuildPointsList buildPointsList = new BuildPointsList(context, id);
		
		if (travelBackup == null)
			points = buildPointsList.fromDB();
		else 
			points = travelBackup.getPoints(context);
	}

	public List<Points> updatePoints(TravelBackup travelBackup) {
		loadPoints(travelBackup);
		return points;
	}
	
	public Points getLastPointAdded() {
		if (points.size()>=1)
			return points.get(points.size()-1);
		else
			return null;
	}
	
	public void addPoints(Points point, int time) {
		time = time-50;				// valore di test, risettare a 50 quando finito
		point.setContext(context);
		
		point.setTravelID(id);
		if (points.size() < 2) { // valore di test, risettare a 2 quando finito
			point.savePoint();
			points.add(point);						
		//	updateDistance(points.size()>1);	// eliminare anche questo quando finito
		}
		
		else {
			Points oldFirstPoint = points.get(points.size()-1);
			Points oldSecondPoint = points.get(points.size()-2);
			
			if ((oldFirstPoint.equals(point)>=time) && (oldFirstPoint.equals(oldSecondPoint) >= 0)) {
				oldFirstPoint.setDataRilevamento(point.getDataRilevamento());
				oldFirstPoint.updatePoint();
			}
			else {
				point.savePoint();
				points.add(point);
				updateDistance(true);
			}
		}
	}
	
	private void updateDistance(boolean test) {
		if (test) {
			Location old = points.get(points.size()-2).getLocation();
			Location newP = points.get(points.size()-1).getLocation();
			float d = Math.abs(old.distanceTo(newP));
			ts.setDistance(d);
		}
		ts.addPoint(getLastPointAdded());
		ts.saveInSP(context);
	}
	
	public void reloadDistanceAndZoom() {
		Location old = null;
		distance = 0;
		ts.resetZoom();
		for(Points p:points) {
			ts.addPoint(p);
			ts.setLastPointParsed(p.getDataRilevamento());
			if (old == null)
				old = p.getLocation();
			else {
				float d = Math.abs(old.distanceTo(p.getLocation()));
				distance += d;
				old = p.getLocation();
			}
		}
		ts.setDistance(distance);
		ts.saveInSP(context);
	}
	
	public List<Points> selectTimePoints(long start, long stop) {
		List<Points> result = new ArrayList<Points>();
		for(int i=0; i<points.size(); i++) {
			Points point = points.get(i);
			if ((start <= point.getDataRilevamento()) && (stop >= point.getDataRilevamento()))
					result.add(point);
		}
		return result;
	}
}
