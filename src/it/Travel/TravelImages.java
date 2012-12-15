package it.Travel;

import it.Photos.BuildImageList;
import it.Photos.Images;

import java.util.ArrayList;
import java.util.List;

import Settings.TravelBackup;
import android.content.Context;
import android.util.Log;

public class TravelImages {

	/**
	 */
	private List<Images> images;
	/**
	 */
	private Context context;
	/**
	 */
	private long id;
	/**
	 */
	private int numberOfImages = 0;
	
	
	protected TravelImages(Context context, long id) {
		images = new ArrayList<Images>();
		this.context = context;
		this.id = id;
	}
	
	public int getNuberOfImages() {
		return numberOfImages;
	}
	
	/**
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @param numberOfImages
	 */
	public void setNumberOfImages(int numberOfImages) {
		this.numberOfImages = numberOfImages;
	}
	
	public List<Images> getImages() {
		return images;
	}
	
	public void reloadImages(TravelBackup travelBackup) {
		
		try {
			images.clear();
		} catch (NullPointerException ne) {};
		
		loadImages(travelBackup);
	}
	
	public void loadImages(TravelBackup travelBackup) {
		BuildImageList buildImageList = new BuildImageList(context, id);
		Log.i("DDV", "travelbckup:"+ (travelBackup==null));
		if (travelBackup==null)
			images =  buildImageList.fromDataBase();
		else {
			images = buildImageList.fromFile();
		}
	}	
	
	public void updateImages(TravelBackup travelBackup) {
		loadImages(travelBackup);
	}
	
	public void addImages(Images image, long id) {
		
		image.setContext(context);
		image.setTravelID(id);
		
		boolean test = true;
		int i=0;
		for (; ((i<images.size()) && test);i++) 
			test = test && (!images.get(i).getName().contentEquals(image.getName()));
		
		if (test) images.add(image);
		else images.set((i-1), image);
		image.save();
		
		numberOfImages++;
	}
}
