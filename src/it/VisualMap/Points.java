package it.VisualMap;

import it.DB.TravelDB;
import android.content.Context;
import android.util.Log;

public class Points extends SimplePoints {

	private static final long serialVersionUID = 1L;
	/**
	 */
	private Context context;
	
	public Points(){}
	
	/**
	 * @param context
	 */
	public void setContext(Context context) {
		this.context=context;
	}
	
	/**
	 * @return
	 */
	public Context getContext() {
		return context;
	}
	
	public static Points parseToPoints(SimplePoints sp, Context context) {
		Points p = new Points();
		p.setDataRilevamento(sp.getDataRilevamento());
		p.setGpsId(sp.getGpsId());
		p.setLatitude(sp.getLatitude());
		p.setLongitude(sp.getLongitude());
		p.setTravelID(sp.getTravelID());
		p.setContext(context);
		return p;
	}

	public void savePoint() {
		TravelDB traveldb = new TravelDB(context);
		traveldb.open();
		this.setGpsId(traveldb.createGPS(this.getLatitude(), this.getLongitude(), this.getDataRilevamento(), this.getTravelID()));
		Log.i("DDV", this.toString()+" SAVED");
		traveldb.close();
	}
	
	public boolean updatePoint() {
		TravelDB traveldb = new TravelDB(context);
		traveldb.open();
		boolean result = traveldb.updatePoint(this.getDataRilevamento(), this.getLatitude(), this.getLongitude(), this.getGpsId());
		Log.i("DDV", this.toString()+" UPDATED");
		traveldb.close();
		return result;
	}
	
	public boolean deletePoint() {
		TravelDB traveldb = new TravelDB(context);
		traveldb.open();
		boolean result = traveldb.deleteGps(this.getGpsId());
		traveldb.close();
		return result;
	}
	
	public int equals(Points point) {
		if ((this.getLatitude() == point.getLatitude()) && (this.getLongitude() == point.getLongitude()))
			return (int) Math.abs(point.getDataRilevamento()-this.getDataRilevamento());
		else
			return -1;
	}
	
	public String toString() {
		return "Point id:"+this.getGpsId()+
			", latitude:"+this.getLatitude()+
			", longitude:"+this.getLongitude()+
			", time get:"+this.getDataRilevamento()+
			", travel id:"+this.getTravelID();
	}
}
