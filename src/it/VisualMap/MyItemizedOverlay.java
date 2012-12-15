package it.VisualMap;

import it.Photos.Images;

import java.util.ArrayList;

import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

// classe necessaria per la creazione di overlay (marker) personalizzati sulla mappa, a cui vengono
// associati i link alle immagini

class MyOverlayItem extends OverlayItem {
	
	/**
	 */
	private ArrayList<Images> images;
	/**
	 */
	private String title;
	/**
	 */
	private String snippet;

	public MyOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
		title="";
		snippet="";
		images = new ArrayList<Images>();
	}
	 
	 public int getNumberOfImages() {
		 return images.size();
	 }
	 
	 public ArrayList<Images> getImages() {
		 return images;
	 }
	 
	 
	 /**
	 * @param title
	 */
	public void setTitle(String title){
		 this.title=title;
	 }
	 
	 /**
	 * @return
	 */
	@Override
	 public String getTitle() {
		 return title;
	 }	 
	 
	 /**
	 * @param snippet
	 */
	public void setSnippet(String snippet){
		 this.snippet=snippet;
	 }
	 
	 /**
	 * @return
	 */
	@Override
	 public String getSnippet() {
		 return snippet;
	 }
	 public void addImage(Images image) {
		 images.add(image);
	 }
	 
	 public void clear() {
		 images.clear();
	 }
	 
	 public void removeImages(long[] list) {
		 if (list != null) {
			 boolean test;
			 for( int k=0; k<list.length; k++) {
				 test = true;
				 for(int i=0; ((i<images.size()) && test); i++) {
					 if (images.get(i).getId() == list[k]) {
						 Log.i("DDV", "remove image:"+i);
						 images.remove(i);
						 test = false;
					 }
				 }
			 }
		 }
	 }
	 
	 public boolean removeImage(Images image) {
		 int index = images.indexOf(image);
		 if (index == (-1)) return false;
		 else images.remove(index);
		 return true;
	 }
	 
}
