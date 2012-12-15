package Settings;

import it.GestioneFile.MyFile;
import it.Photos.Images;
import it.Travel.Travel;
import it.VisualMap.Points;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class ListTravelsBackup extends ArrayList<TravelBackup>{

	private static final long serialVersionUID = 1L;

	public ListTravelsBackup() {
		super();
	}
	
	public boolean add(Travel travel) {
		
		MySettings ms = new MySettings(travel.getContext());
		TravelSettings ts = new TravelSettings(travel.getContext());
		ts.setTravelId(ts.getTravelId()+ms.getOffset());
		TravelBackup tb = new TravelBackup(ts, travel.getPoints(), travel.getImages());
		for (TravelBackup i:this) {
			if (i.getSettings().getTravelId() == ts.getTravelId()) {
				this.remove(i);
				break;
			}
		}
		return this.add(tb);
	}

	public static void deleteTravel(long id) {
		ListTravelsBackup ltb = loadAll();
		for (TravelBackup temp:ltb) {
			if (temp.getSettings().getTravelId() == id) {
				ltb.remove(temp);
				ltb.save();
				break;
			}
		}	
	}
	
	public static void update(TravelBackup tb) {
		ListTravelsBackup ltb = loadAll();
		for (TravelBackup temp:ltb) {
			if (temp.getSettings().getTravelId() == tb.getSettings().getTravelId()) {
				Log.i("DDV", "removing tb");
				ltb.remove(temp);
				ltb.add(tb);
				ltb.save();
				break;
			}
		}	
	}

	public void save() {
		MyFile mf = new MyFile();
		mf.saveTravelsBackup(this);
	}
	
	public static ListTravelsBackup loadAll() {
		MyFile mf = new MyFile();
		ListTravelsBackup ts = mf.loadTravels();
		return ts;
	}
	
	public static TravelBackup load(long id) {
		
		ListTravelsBackup ts = loadAll();
		
		for (TravelBackup i:ts) {
			if (i.getSettings().getTravelId() == id)
				return i;
		}
		return null;
	}
	
	public static List<Images> loadImages(long id, Context context) {
		TravelBackup tb = load(id);
		if (tb!=null)
			return tb.getImages(context);
		return null;
	}
	
	public static List<Points> loadPoints(long id, Context context) {
		TravelBackup tb = load(id);
		if (tb!=null)
			return tb.getPoints(context);
		return null;
	}
}
