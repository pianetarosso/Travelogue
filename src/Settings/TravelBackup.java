package Settings;

import it.Photos.Images;
import it.Photos.SimpleImage;
import it.VisualMap.Points;
import it.VisualMap.SimplePoints;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class TravelBackup implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 */
	private TravelSettings settings;
	/**
	 */
	private List<SimplePoints> points;
	/**
	 */
	private List<SimpleImage> images;

	public TravelBackup(TravelSettings settings, List<Points> points, List<Images> images) {
		this.settings = settings;
		this.images = parseImages(images);
		this.points = parsePoints(points);
	}
	/**
	 * @return
	 */
	public TravelSettings getSettings() {
		return settings;
	}
	
	public void removeImages(List<Images> im) {
		for (SimpleImage si:images) {
			if (im.size()>0) {
				for(Images i:im) {
					if(si.getId() == i.getId()) {
						im.remove(i);
						images.remove(si);
					}
				}
			}
			else
				break;
		}
		ListTravelsBackup.update(this);
	}
	
	private List<SimpleImage> parseImages(List<Images> images) {
		List<SimpleImage> si = new ArrayList<SimpleImage>();
		for(Images i:images) 
			si.add(Images.parseToSimpleImages(i));
		return si;
	}
	
	private List<SimplePoints> parsePoints(List<Points> points) {
		List<SimplePoints> sp = new ArrayList<SimplePoints>();
		for(Points p:points) 
			sp.add(Points.parseToSimplePoints(p));
		return sp;
	}
	
	public List<Images> getImages(Context context) {
		List<Images> i = new ArrayList<Images>();
		for(SimpleImage si:images) 
			i.add(Images.parseBaseImages(si, context));
		return i;
	}
	
	public List<Points> getPoints(Context context) {
		List<Points> p = new ArrayList<Points>();
		for(SimplePoints sp:points) 
			p.add(Points.parseToPoints(sp, context));
		return p;
	}
}
