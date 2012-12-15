package it.VisualMap.Menu;

import it.DiarioDiViaggio.R;
import it.VisualMap.AddMarkers;
import it.VisualMap.CreateMarkerDrawable;
import Settings.MapSettings;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.SeekBar;

import com.google.android.maps.MapView;

public class MapMenu {

	/**
	 */
	private Context context;
	
	/**
	 */
	private boolean changeButton = true;
	/**
	 */
	private int color=0;

	/**
	 */
	private int hColor=0;
	
	/**
	 */
	private MapView mapView;
	/**
	 */
	private AddMarkers addMarkers;
	
	/**
	 */
	private int dimensioni;
	/**
	 */
	private int corona;
	
	/**
	 */
	private MapSettings ms;
	
	/**
	 */
	private CreateMarkerDrawable cmd;
	
	private static final int DIM_MAX = 12;

	private static final int DIM_MIN = 8;
	
    public MapMenu(MapView mapView, AddMarkers addMarkers) {
    	this.context = mapView.getContext();
		this.mapView = mapView;
		this.addMarkers = addMarkers;
		
		ms = new MapSettings(context);
		
		dimensioni = ms.getMarker_size();
		corona = ms.getMarker_highlight_size();
    }
	
	
    // Cambio il "tipo" di mappa, le scelte sono Satellitare o Stradale
	public void changeMapType() {
	    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	    dialog.setTitle(context.getString(R.string.map_type));
	    
	    boolean street = ms.isMappa_stradale();
	    
	    final CharSequence[] type = new CharSequence[2]; 
		
	    type[0] = context.getString(R.string.street_view);
	    type[1] = context.getString(R.string.satellite_view);
	    
	    final int position;
	    
	    if (street) position=0;
	    else position=1;
	    
        dialog.setSingleChoiceItems(type, position, new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int item) {
        	
        		ms.setMappa_stradale(item==0);
        		ms.saveToSP(context);
        		
        		mapView.setStreetView(item==0);
        		mapView.setSatellite(item==1);
        		mapView.invalidate();
        	}
        });
        
        dialog.show();
	}
	
	// Cambio il colore del percorso
	public void changeRouteColor() {
		 
		int screenWidth = mapView.getWidth();
		final Dialog yourDialog = new Dialog(context);
		LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.color_route_dialog, (ViewGroup)mapView.findViewById(R.id.color_route_dialog));
		 
		int width = (screenWidth * 9) / 10 ;
		yourDialog.setContentView(layout, new LayoutParams(width, LayoutParams.WRAP_CONTENT));
		 
		yourDialog.setTitle(R.string.set_color);
		 
		Button save = (Button)layout.findViewById(R.id.save_color_button);
		Button reset = (Button)layout.findViewById(R.id.reset_color_button);

		final SeekBar red = (SeekBar)layout.findViewById(R.id.seekBarRed);
		final SeekBar green = (SeekBar)layout.findViewById(R.id.seekBarGreen);
		final SeekBar blue = (SeekBar)layout.findViewById(R.id.seekBarBlue);		
		
		final View view = (View)layout.findViewById(R.id.androidTransparentImageView);		
		
		setSeekerBar(red, view, 0, false);
		setSeekerBar(green, view, 1, false);
		setSeekerBar(blue, view, 2, false);
		
		
		reset.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						int oldColor = ms.getRoute_color();
						red.setProgress(Color.red(oldColor));
						green.setProgress(Color.green(oldColor));
						blue.setProgress(Color.blue(oldColor));
					}
				});
		
		save.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						int color = Color.rgb(red.getProgress(), green.getProgress(), blue.getProgress());
						ms.setRoute_color(color);
						ms.saveToSP(context);
						yourDialog.dismiss();
						mapView.invalidate();
					}
				});
		 
		reset.performClick();
		yourDialog.show();
		
	}
	
	// Cambio il colore/dimensione del marker, e del modo in cui evidenzio quelli selezionati
	public void changeMarkerColor() {
		
		
		int screenWidth = mapView.getWidth();
		final Dialog yourDialog = new Dialog(context);
		LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.marker_dialog, (ViewGroup)mapView.findViewById(R.id.marker_dialog));
		 
		int width = (screenWidth * 9) / 10 ;
		yourDialog.setContentView(layout, new LayoutParams(width, LayoutParams.WRAP_CONTENT));
		 
		yourDialog.setTitle(R.string.set_color);
		 
		Button save = (Button)layout.findViewById(R.id.save_colorM_button);
		final Button reset = (Button)layout.findViewById(R.id.reset_colorM_button);
		final Button change = (Button)layout.findViewById(R.id.buttonChangeSMC);
		
		
		final SeekBar red = (SeekBar)layout.findViewById(R.id.seekBarRedM);
		final SeekBar green = (SeekBar)layout.findViewById(R.id.seekBarGreenM);
		final SeekBar blue = (SeekBar)layout.findViewById(R.id.seekBarBlueM);	
		final SeekBar dim = (SeekBar)layout.findViewById(R.id.seekBarDim);
		
		dim.setEnabled(true);
		
		final View view = (View)layout.findViewById(R.id.markerImageView);	
		
		cmd = new CreateMarkerDrawable(view, context);

		setSeekerBar(red, view, 0, true);
		setSeekerBar(green, view, 1, true);
		setSeekerBar(blue, view, 2, true);
		
		setDimSeekBar(dim);
		
		dim.setOnSeekBarChangeListener(
				new SeekBar.OnSeekBarChangeListener() {
					
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
					
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
					
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {						
						if (changeButton) 
							dimensioni = progress+DIM_MIN;
						else 
							corona = progress+1;
						
						Drawable t = cmd.draw(dimensioni, corona, color, hColor, true);
						view.setBackgroundDrawable(t);
					}
				});
		
		reset.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {

						int c;
						if (changeButton) {
							color = ms.getMarker_color();
							c = color;
							change.setText(R.string.select_crown);
						}
						else {
							hColor = ms.getMarker_highlight_color();
							c = hColor;
							change.setText(R.string.select_marker);
						}
						
						red.setProgress(Color.red(c));
						green.setProgress(Color.green(c));
						blue.setProgress(Color.blue(c));
						
						Drawable t = cmd.draw(dimensioni, corona, color, hColor, true);
						view.setBackgroundDrawable(t);
					}
				});
		
		save.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						ms.setMarker_color(color);
						ms.setMarker_highlight_color(hColor);
						ms.setMarker_size(dimensioni);
						ms.setMarker_highlight_size(corona);
						ms.saveToSP(context);
						
						changeButton = true;
						if (addMarkers!=null)
							addMarkers.updateMarkersImage();
						mapView.postInvalidate();
						yourDialog.dismiss();						
					}
				});
		
		change.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {
						if (changeButton) changeButton=false;
						else changeButton = true;
						
						int c;
						if (changeButton) {
							c = color;
							change.setText(R.string.select_crown);
						}
							
						else {
							c = hColor;
							change.setText(R.string.select_marker);
						}
						
						red.setProgress(Color.red(c));
						green.setProgress(Color.green(c));
						blue.setProgress(Color.blue(c));
						
						setDimSeekBar(dim);
						
						Drawable t = cmd.draw(dimensioni, corona, color, hColor, true);
						view.setBackgroundDrawable(t);
						
					}
				});
		
		boolean t = changeButton;
		changeButton = true;
		reset.performClick();
		changeButton = false;
		reset.performClick();
		changeButton = t;
		reset.performClick();
		yourDialog.show();
		
	}
	
	private void setDimSeekBar(SeekBar dim) {
		
		int v = 0;
		if(changeButton) {
			v = dimensioni;
			dim.setMax(DIM_MAX+DIM_MIN);
			dim.setProgress(v-DIM_MIN);
		}
		else {
			v = corona;
			dim.setMax(4);
			dim.setProgress(v-1);
		}
	}
	
	
	// Questa decide solamente quale colore stiamo variando
	private void setBars(int r, int g, int b, View view) {
		
		if(changeButton) 
			color = changeColor(r,g,b,color);
		else 
			hColor = changeColor(r,g,b,hColor);
		
		Drawable t = cmd.draw(dimensioni, corona, color, hColor, true);
		
		view.setBackgroundDrawable(t);
	}
	
	// questa funzione si occupa di modificare le varie componenti del colore quando le seekbar
	// vengono spostate
	private int changeColor(int r, int g, int b, int color) {
		int oldR = Color.red(color);
		int oldG = Color.green(color);
		int oldB = Color.blue(color);
		if (r>-1)
			color = Color.rgb(r, oldG, oldB);
		if (g>-1)
			color = Color.rgb(oldR, g, oldB);
		if (b>-1)
			color = Color.rgb(oldR, oldG, b);
		
		return color;
	}
	
	
	// Funzione che si occupa dell'inizializzazione delle barre per questioni quali sfondo, 
	// abilitazione e ascoltatori
	public void setSeekerBar(final SeekBar t, final View layout, final int colorType, final boolean f) {
		
		t.setBackgroundColor(Color.TRANSPARENT);
		
		switch(colorType) {
		
		case 0:
			t.setProgressDrawable(context.getResources().getDrawable(R.drawable.seekbar_red_progress));
			break;
		case 1:
			t.setProgressDrawable(context.getResources().getDrawable(R.drawable.seekbar_green_progress));
			break;
		case 2:
			t.setProgressDrawable(context.getResources().getDrawable(R.drawable.seekbar_blue_progress));
			break;
		}
		
		t.setEnabled(true);
		
		t.setOnSeekBarChangeListener(
				new SeekBar.OnSeekBarChangeListener() {
					
					public void onStopTrackingTouch(SeekBar seekBar) {}
					
					public void onStartTrackingTouch(SeekBar seekBar) {}
					
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						
						changeColors(colorType, t.getProgress(), f, layout);
					}
				});
	}
	
	// Questa viene chiamata dai due menu sopracedenti per la gestione dei colori, si occupa di
	// "smistare" gli input nel modo corretto.
	private void changeColors(int colorType, int value, boolean f, View layout) {
		
		if (f) {
			switch (colorType) {
			case 0:
				setBars(value, -1, -1, layout);	
				break;
			case 1:
				setBars(-1, value, -1, layout);
				break;
			case 2:
				setBars(-1, -1, value, layout);
				break;
			}
		}
		else {
			switch (colorType) {
			case 0:
				color = changeColor(value, -1, -1, color);	
				break;
			case 1:
				color = changeColor(-1, value, -1, color);
				break;
			case 2:
				color = changeColor(-1, -1, value, color);
				break;
			}
			layout.setBackgroundColor(color);
			
		}
	}
}
