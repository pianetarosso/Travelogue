package it.Photos;

import it.Date.DateManipulation;
import it.DiarioDiViaggio.R;
import it.Travel.Travel;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PhotoGallery {
	
	
	/**
	 */
	private List<Images> images = null;
    /**
	 */
    private ImageView imageView;
    /**
	 */
    private ImageAdapter imageAdapter;
    /**
	 */
    private Gallery g;
    /**
	 */
    private Bitmap bmp;
    /**
	 */
    private Context context;
    /**
	 */
    private int position=0;
    /**
	 */
    private List<Long> imagesDeleted;
    /**
	 */
    private View view;
    /**
	 */
    private Travel travel;
    
	
	public PhotoGallery (Context context, ArrayList<Images> images, long thisMarker, View view, Travel travel) {
		
		this.context = context;
		this.images = images;
		this.view = view;
		this.travel = travel;
	    
	    // setto la prima immagine della "imageView"
	    imageView = (ImageView)view.findViewById(R.id.photoImageView);
	    buildGallery();
	} 	
	
	 private void buildGallery() {
		 
		 final String[] items = {context.getString(R.string.delete), context.getString(R.string.share)};
		 
		 bmp = BitmapFactory.decodeFile(images.get(0).getPath());
		 imageView.setImageBitmap(bmp);
    	    
		 imageAdapter = new ImageAdapter(context, images);
		    
		 g = (Gallery) view.findViewById(R.id.photoGallery);
		 g.setSpacing(3);
		 g.setPadding(0, 3, 0, 3);
		 g.setAdapter(imageAdapter);
		 g.setBackgroundColor(Color.TRANSPARENT);
		    
		 imageView.setVisibility(View.INVISIBLE);
		 imageView.setPadding(0, 2, 0, 2);
		   
		 imageView.setOnLongClickListener(new OnLongClickListener() {
	
			 public boolean onLongClick(View v) {
 
				 AlertDialog.Builder builder = new AlertDialog.Builder(context);
				 builder.setTitle(R.string.choose_action);
				 builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0: {
							dialog.dismiss();
							AlertDialog.Builder areYouSure = new AlertDialog.Builder(context);
							areYouSure.setMessage(R.string.are_you_sure);
							areYouSure.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									clearImage();
								}
							});
							areYouSure.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							areYouSure.show();
						}
							break;
						case 1: 
							loadShare();
							break;
						}
					}
				 });
				 builder.show();
				return false;
			 }
		 });
				
		 ///////////////////////////////////////////////////////////////////////////////////////////////// 
		 // definisco l'ascoltatore per passare da un'immagine all'altra nella "imageView"
		 g.setOnItemClickListener(new OnItemClickListener() {
			 @SuppressWarnings("unchecked")
			 public void onItemClick(AdapterView parent, View v, int position, long id) {
		        	
				 imageView.setVisibility(View.VISIBLE);
				 view.getRootView().findViewById(R.id.transparent_panel).setVisibility(View.GONE); 	    
		        	
				 loadImage(position);
		        
			 }
		 });
		   
	 }
		 
		 
	 private void loadShare() {
		 Intent share = new Intent(Intent.ACTION_SEND);
		 share.setType("image/png");
		 
		 share.putExtra(Intent.EXTRA_STREAM,
				 Uri.parse("file://"+images.get(position).getPath()));
		 
		 Intent chooser = Intent.createChooser(share, context.getString(R.string.share));
		 context.startActivity(chooser);	
	 }
		 
	 private void loadImage(int position) {
	
		 buildMessage(position);
		 releaseMemory();
		 bmp = BitmapFactory.decodeFile(images.get(position).getPath());
		 imageView.setImageBitmap(bmp);
		 this.position = position;   	
	 }
		 
	 public void releaseMemory() {
			 
		 if (imageView!=null) {
			 imageView.setImageBitmap(null);
			 imageView.destroyDrawingCache();
		 }
		 if (bmp!=null)
			 bmp.recycle();
		 bmp = null;
	 }
		 
	 private void clearImage() {
		 int size = imageAdapter.getCount();
		 if (size>1) {
					
			 if (position == 0) loadImage(1);
			 else loadImage(position-1);
				 
			 imagesDeleted.add(images.get(position).getId());
			 travel.removeImage(images.get(position));
			 images.get(position).deleteAlsoFile();
					
			 imageAdapter.removeImage(position);
			 imageAdapter.notifyDataSetChanged();
					
		 }
		 else
			 Toast.makeText(context, R.string.error_delete_image, Toast.LENGTH_LONG).show();
	 }
	
	 private void buildMessage(int position) { 
		 String title = context.getString(R.string.data)+" "+DateManipulation.parseMs(images.get(position).getDate());
		 Toast t = Toast.makeText(context, title, Toast.LENGTH_SHORT);
		 t.setGravity(Gravity.CENTER, 0, 0);
		 t.show();
	 }
}

	

