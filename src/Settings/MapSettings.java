package Settings;

import it.GestioneFile.MyFile;

import java.io.Serializable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

public class MapSettings implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 */
	private int map_dropdown;
	/**
	 */
	private int marker_color;
	/**
	 */
	private int marker_highlight_color;
	/**
	 */
	private int marker_size;
	/**
	 */
	private int marker_highlight_size;
	/**
	 */
	private int route_color;
	/**
	 */
	private boolean mappa_stradale;
	
	private static final String map_settings = "Map Settings";
	private static final String map_dropDownS ="navigation_dropdown";
	private static final String marker_colorS ="marker_color";
	private static final String marker_highlight_colorS ="marker_h_color";
	private static final String marker_sizeS ="marker_size";
	private static final String marker_highlight_sizeS ="marker_h_size";
	private static final String route_colorS = "route_color";
	private static final String mappa_stradaleS = "mappa_stradale";
	
	private static final int DROPDOWN_DEFAULT = 168;
	
	private static final int DEFAULT_MARKER_COLOR = Color.RED; 	
	private static final int DEFAULT_MARKER_H_COLOR = Color.YELLOW; 
	private static final int DEFAULT_ROUTE_COLOR = Color.GREEN;
	
	private static final int DEFAULT_MARKER_SIZE = 11;
	private static final int DEFAULT_MARKER_H_SIZE = 2;
	
	private static final boolean DEFAULT_MAP_TYPE = true;
	
	public MapSettings(Context context) {
		loadFromSP(context);
	}
	
	private void loadFromSP(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(map_settings, Context.MODE_PRIVATE);
		
		map_dropdown = sp.getInt(map_dropDownS, DROPDOWN_DEFAULT);
		marker_color = sp.getInt(marker_colorS, DEFAULT_MARKER_COLOR);
		marker_highlight_color = sp.getInt(marker_highlight_colorS, DEFAULT_MARKER_H_COLOR);
		marker_size = sp.getInt(marker_sizeS, DEFAULT_MARKER_SIZE);
		marker_highlight_size = sp.getInt(marker_highlight_sizeS, DEFAULT_MARKER_H_SIZE);
		route_color = sp.getInt(route_colorS, DEFAULT_ROUTE_COLOR);
		mappa_stradale = sp.getBoolean(mappa_stradaleS, DEFAULT_MAP_TYPE);
	}
	
	public boolean saveToSP(Context context) {
		
		Editor editor = context.getSharedPreferences(map_settings, Context.MODE_PRIVATE).edit();
		
		editor.putBoolean(mappa_stradaleS, mappa_stradale);
		editor.putInt(map_dropDownS, map_dropdown);
		editor.putInt(marker_colorS, marker_color);
		editor.putInt(marker_highlight_colorS, marker_highlight_color);
		editor.putInt(marker_highlight_sizeS, marker_highlight_size);
		editor.putInt(marker_sizeS, marker_size);
		editor.putInt(route_colorS, route_color);
		
		return editor.commit();
	}
	
	public void saveToFile() {
		MyFile mf = new MyFile();
		mf.saveMapSettings(this);
	}
	
	public static MapSettings loadFromFile() {
		MyFile mf = new MyFile();
		return mf.loadMapSettings();
	}
	
	/**
	 * @return
	 */
	public int getMap_dropdown() {
		return map_dropdown;
	}
	
	/**
	 * @param mapDropdown
	 */
	public void setMap_dropdown(int mapDropdown) {
		map_dropdown = mapDropdown;
	}

	/**
	 * @return
	 */
	public int getMarker_size() {
		return marker_size;
	}
	
	/**
	 * @param markerSize
	 */
	public void setMarker_size(int markerSize) {
		marker_size = markerSize;
	}
	
	/**
	 * @return
	 */
	public int getMarker_highlight_size() {
		return marker_highlight_size;
	}
	
	/**
	 * @param markerHighlightSize
	 */
	public void setMarker_highlight_size(int markerHighlightSize) {
		marker_highlight_size = markerHighlightSize;
	}

	/**
	 * @return
	 */
	public int getMarker_color() {
		return marker_color;
	}

	/**
	 * @param markerColor
	 */
	public void setMarker_color(int markerColor) {
		marker_color = markerColor;
	}

	/**
	 * @return
	 */
	public int getMarker_highlight_color() {
		return marker_highlight_color;
	}

	/**
	 * @param markerHighlightColor
	 */
	public void setMarker_highlight_color(int markerHighlightColor) {
		marker_highlight_color = markerHighlightColor;
	}

	/**
	 * @return
	 */
	public int getRoute_color() {
		return route_color;
	}

	/**
	 * @param routeColor
	 */
	public void setRoute_color(int routeColor) {
		route_color = routeColor;
	}

	/**
	 * @return
	 */
	public boolean isMappa_stradale() {
		return mappa_stradale;
	}

	/**
	 * @param mappaStradale
	 */
	public void setMappa_stradale(boolean mappaStradale) {
		mappa_stradale = mappaStradale;
	}
}
