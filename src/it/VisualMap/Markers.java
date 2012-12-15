package it.VisualMap;

import it.Date.DateManipulation;
import it.DiarioDiViaggio.R;
import it.Photos.Images;
import it.Photos.PhotoGallery;
import it.Travel.Travel;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

 @SuppressWarnings("unchecked")
class Markers extends ItemizedOverlay {


	 /**
	 */
	private ArrayList<MyOverlayItem> mOverlays = new ArrayList<MyOverlayItem>();
	 /**
	 */
	private Context context;
	 /**
	 */
	private MapController mc;
	 /**
	 */
	private MapView mapView;
	 /**
	 */
	private View view;
	 /**
	 */
	private Drawable hMarker;
	/**
	 */
	private Drawable defaultMarker;
	 /**
	 */
	private int oldMarkerIndex=-1;
	/**
	 */
	private int index = 0;
	 /**
	 */
	private CreateMarkerDrawable cmd;
	 /**
	 */
	private PhotoGallery photoGallery = null;
	 /**
	 */
	private long touchTime;
	 /**
	 */
	private boolean longTouch = false;
	 /**
	 */
	private Travel travel;
	 
	 private static final int MIN_TOUCH_TIME = 800;
	 
	 public Markers(Drawable defaultMarker) {
		 super(boundCenter(defaultMarker));
	 }
	 
	 /**
	 * @param travel
	 */
	public void setTravel(Travel travel) {
		 this.travel = travel;
	 }

	 /**
	 * @param mapView
	 */
	public void setMapView(MapView mapView) {
		 this.mapView = mapView;
		 mc = mapView.getController();
		 this.context = mapView.getContext();
		 cmd = new CreateMarkerDrawable(null, context);
	 }
	 
	 public void addOverlay(MyOverlayItem overlay) {
		 mOverlays.add(overlay);
	 }

	 public void populateNow() {
		 populate();
	 }
	 
	 @Override
	 public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		 super.draw(canvas, mapView, false);
	 }
	 
	 @Override
	 protected OverlayItem createItem(int i) {
		 return mOverlays.get(i);
	 }

	 @Override
	 public int size() {
		 return mOverlays.size();
	 }	 
	 
	 public void clear() {
		 mOverlays.clear();
	 }
	 
	 /**
	 * @param view
	 */
	public void setView(View view) {
		 this.view=view;
	 }
	 
	 public ArrayList<MyOverlayItem> getMyOverlays() {
		 return mOverlays;
	 }
	 
	 public void updateMarkers() {
		 
		 buildMarkersImages();
		 
		 for (int i=0; i<mOverlays.size(); i++) {
			 
			 if (i==index) 
				 mOverlays.get(i).setMarker(boundCenter(hMarker));
			 else
				 mOverlays.get(i).setMarker(boundCenter(defaultMarker));
		 }
		 
	 }
	 
	 private void buildMessage(MyOverlayItem item) {
		 
		 int length = item.getNumberOfImages()-1;
		 String title = "";
		 if (length > 0) {
				title = context.getString(R.string.da)+" "+DateManipulation.parseMs(item.getImages().get(0).getDate())+"\n"
				+context.getString(R.string.a)+" "+DateManipulation.parseMs(item.getImages().get(length).getDate());
			} 
			else if (length == 0){
				title = context.getString(R.string.data)+" "+DateManipulation.parseMs(item.getImages().get(0).getDate());
			}
		 title = title +"\n"+ item.getSnippet()+"\n";
		 title = title +context.getString(R.string.numero_immagini)+" "+item.getNumberOfImages();
		 
		 Toast t = Toast.makeText(context, title, 0);
		 t.setDuration(10);
		 t.show();
	 }
	 
	 
	 // sensore di "tocco" per i markers
	 @Override
	 protected boolean onTap(final int index) {
		 
		 this.index = index;
		 
		 final MyOverlayItem item = mOverlays.get(index);
			
		 if (longTouch) {
			 deleteMarkerQuestion(item);
			 longTouch = false;
		 }
		 else {
			 
			 buildMessage(item);
				 
			 View gallery = (View)view.findViewById(R.id.photoGallery);
			 view.findViewById(R.id.photoScrollView).setBackgroundColor(Color.TRANSPARENT);
			 gallery.setVisibility(View.VISIBLE);
				 
			 buildMarkersImages();
			 
			 if (oldMarkerIndex >= 0) {
				 MyOverlayItem old = mOverlays.get(oldMarkerIndex); 
				 old.setMarker(boundCenter(defaultMarker)); 
			 }
				 
			 oldMarkerIndex = index;
			 
			 item.setMarker(boundCenter(hMarker));
				 
			// item.removeImages(removeRemovedImages(index));
			
			 mc.animateTo(item.getPoint());
				
			 if (mapView.getZoomLevel() < 15) mc.setZoom(15);
				 
			 if (photoGallery!= null) 
				 photoGallery.releaseMemory();
			 photoGallery = new PhotoGallery(context, item.getImages(), index, view, travel);
		 }
		 return true;
	 }
	 
	 private void deleteMarkerQuestion(final MyOverlayItem item) {
		 
		 final Dialog dialog = new Dialog(context);

		 dialog.setContentView(R.layout.custom_delete_dialog);
		 dialog.setTitle(R.string.delete_marker);
		 dialog.show();
		 
		 final CheckBox also_images = (CheckBox)dialog.findViewById(R.id.delete_also_images);
		 Button delete = (Button)dialog.findViewById(R.id.delete_marker);
		 Button no_delete = (Button)dialog.findViewById(R.id.no_delete_marker);
		 
		 no_delete.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			 });
		 
		 delete.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				dialog.dismiss();
				if (also_images.isChecked())
					areYouSure(false, item);
				else
					areYouSure(true, item);
			}
		 });
	 }
	 
	 private void deleteMarker(MyOverlayItem item) {
		 mOverlays.remove(item);
		 travel.removeMarker(item.getPoint());
		 setLastFocusedIndex(-1);
		 hidePhoto();
		 populate();
	 }
	 
	 private void hidePhoto() {
		 View all = (View) mapView.getParent();
		 View photo = (View)all.findViewById(R.id.photoImageView);
		 View gallery = (View)all.findViewById(R.id.photoGallery);
		 photo.setVisibility(View.INVISIBLE);
		 gallery.setVisibility(View.INVISIBLE);
	 }
	 
	 private void deleteFilesImages(MyOverlayItem item) {
		 for (Images i:item.getImages()) 
			 i.deleteAlsoFile();	
		 deleteImagesFromTravel(item.getImages());
	 }
	 
	 private void deleteOnlyImages(MyOverlayItem item) {
		 for (Images i:item.getImages()) 
			 i.delete();	
		 deleteImagesFromTravel(item.getImages());
	 }
	 
	 private void deleteImagesFromTravel(List<Images> images) {
		 travel.removeImages(images);
	 }
	 
	 private void areYouSure(final boolean onlyMarker, final MyOverlayItem item) {
		 final AlertDialog.Builder confirm = new AlertDialog.Builder(context);
		 confirm.setMessage(R.string.are_you_sure);
		 confirm.setPositiveButton(R.string.yes,
				 new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface dialog, int id) {						
				 dialog.cancel();
				 if (onlyMarker) {
					 deleteOnlyImages(item);
					 deleteMarker(item);
				 }
				 else {
					 deleteFilesImages(item);
					 deleteMarker(item);
				 }
			 }
		 });
		 
		 confirm.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		 });
		 confirm.show();
	 }
	 
	 // rilevo il longtouch event
	 @Override
	 public boolean onTouchEvent(MotionEvent event, MapView mv) {
		 
		 long touchDuration = 0;
		
		 if (event.getAction() == MotionEvent.ACTION_DOWN)
			 touchTime = System.currentTimeMillis();
		 else if ( event.getAction() == MotionEvent.ACTION_UP )
     
         //stop timer
         touchDuration = System.currentTimeMillis() - touchTime;

         if ( touchDuration > MIN_TOUCH_TIME )
        	 longTouch = true;
        
         return super.onTouchEvent(event, mv);
        
	 }
	 
	 private void buildMarkersImages() {
				
		 defaultMarker = cmd.draw(-1, -1, Integer.MIN_VALUE, Integer.MIN_VALUE, false);
		 defaultMarker.setBounds(0,0,defaultMarker.getIntrinsicWidth(),defaultMarker.getIntrinsicHeight());
		 hMarker = cmd.draw(-1, -1, Integer.MIN_VALUE, Integer.MIN_VALUE, true);
		 hMarker.setBounds(0,0,hMarker.getIntrinsicWidth(),hMarker.getIntrinsicHeight());
	 }
 }