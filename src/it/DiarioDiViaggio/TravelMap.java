package it.DiarioDiViaggio;

import it.Travel.Travel;
import it.VisualMap.AddMarkers;
import it.VisualMap.DesignMap;
import it.VisualMap.Menu.MapMenu;
import it.VisualMap.Menu.TravelMenu;
import Settings.MapSettings;
import Settings.MySettings;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class TravelMap extends MapActivity {


	/**
	 */
	private MapView mapView;
	/**
	 */
	private AddMarkers addMarkers = null;
	/**
	 */
	private long id = -1;
	/**
	 */
	private Travel travel;
	/**
	 */
	private Menu mapMenu = null;
	/**
	 */
	private boolean loading = false; // varibile che ci comunica se la mappa è già stata caricata, utilizzata quando l'app viene richiamata dal background
	
	/**
	 */
	private String nessun_viaggio;
 
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		
    	super.onCreate(savedInstanceState); 
    	setContentView(R.layout.travelmap);
    	mapView = (MapView) findViewById(R.id.mapview);
        
    	MapSettings ms = new MapSettings(this); // carico le settings della mappa
    	
    	nessun_viaggio = this.getString(R.string.no_travels);
		
        boolean streetView = ms.isMappa_stradale(); // setto il tipo di mappa secondo le impostazioni salvate
        
        if (streetView && mapView.isSatellite())
        	mapView.setStreetView(true);
        
        if ((!streetView) && mapView.isTraffic())
        	mapView.setSatellite(true);
        
        loadMap();
	}
    
    
    public Spinner setNavigationSpinner() { // creo lo spinner di navigazione della mappa
    	Spinner spinner = (Spinner)findViewById(R.id.controlSpinner);
    	String[] s = this.getResources().getStringArray(R.array.navigation);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
    	        R.layout.map_spinner, R.id.navigationElements, s);
    	
    	 spinner.setAdapter(adapter);
         
         return spinner;
    }
    
    @Override
	protected void onResume() { // nel caso che l'app sia messa in background, eseguo alcune operazioni al rispristino
		super.onResume();
		if (travel != null) {
			travel.reloadImages(); // ricarico le immagini (nel caso che ne siano state aggiunte altre)
			id = travel.getId();
		}
		else if (!loading)
			loadMap();
	}

    @Override
	protected void onPause() {
    	loading = false;
		super.onPause();
	}
	
    @Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event)  {
    	// intercetto il tasto "indietro" di android, di modo che, se sto visualizzando le immagini sulla
    	// mappa, vengono chiuse queste ultime al posto di ritornare al menu principale
    	boolean test;
    	
    	test = (keyCode == android.view.KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0);
    	
    	View photo = (View)findViewById(R.id.photoImageView);
    	View gallery = (View)findViewById(R.id.photoGallery);
    
    	boolean t1 = test && (photo.getVisibility() == View.VISIBLE);
    	boolean t2 = test && (gallery.getVisibility() == View.VISIBLE);
    	 
	    if (t1)  {
	        photo.setVisibility(View.INVISIBLE);
	        return true;
	    }
	    
	    if (t2)  {
	        gallery.setVisibility(View.INVISIBLE);
	        return true;
	    }    

	    return super.onKeyDown(keyCode, event);
	}    
    
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    public Context getContext() {
    	return this;
    }
    
    /**
	 * @return
	 */
    public MapView getMapView() {
    	return mapView;
    }
    
    /**
	 * @param travel
	 */
    public void setTravel(Travel travel) {
		this.travel=travel;
		this.id = travel.getId();
	}
	
	/**
	 * @return
	 */
	public Travel getTravel() {
		return travel;
	}
	
	/**
	 * @param addMarkers
	 */
	public void setAddMarkers(AddMarkers addMarkers) {
    	this.addMarkers = addMarkers;
    }
	
    private void loadMap() { // funzione di caricamento della mappa
    	loading = true;
    	DesignMap dm = new DesignMap();
    	boolean test = dm.initialize(-1, this); // verifico se è possibile iniziare il caricamento
    	
    	if (test) 
    		dm.execute(); // lancio il caricamento in un thread asincrono
    	
    	else
    		Toast.makeText(this, nessun_viaggio, Toast.LENGTH_LONG).show();
    }

	 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // creo il menu delle opzioni
    	this.mapMenu = menu;
    	setMenuItemsEnabled();
        return true;
    }
    
   
    public void setMenuItemsEnabled() { // verifico quali opzioni del menu possono essere abilitate
    	if (mapMenu != null) {			// alcune come "termina viaggio" non possono essere utilizzate
	    	mapMenu.clear();			// se non si sta visualizzando alcun viaggio
	    	MenuInflater inflater = getMenuInflater();
	    	inflater.inflate(R.menu.map_menu, mapMenu);
	    	
	        MySettings s = new MySettings(this);
	        
	    	long id = s.getInCorso();
	    	
	        MenuItem close_travel = mapMenu.findItem(R.id.close_travel);
	        MenuItem edit_travel = mapMenu.findItem(R.id.editTravel);
	        MenuItem stats_travel = mapMenu.findItem(R.id.statsTravel);
	       
	        if (travel != null) {
		        if (id != travel.getId()) 
		        	close_travel.setEnabled(false);
		        else 
		        	close_travel.setEnabled(true);
	        }
	        else {
	        	edit_travel.setEnabled(false);
	        	stats_travel.setEnabled(false);
	        	close_travel.setEnabled(false);
	        }    
    	}
    }
    
    // stabilisco le azioni per ogni oggetto del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	MapMenu mapMenu = new MapMenu(mapView, addMarkers);
    	TravelMenu travelMenu = new TravelMenu(this);
    	
        switch (item.getItemId()) {
            case R.id.changeTravel:   
            	travelMenu.changeTravel();
            	break;
            	
            case R.id.editTravel:
            	if (id != -1)
            		travelMenu.editTravel();
            	break;
            	
            case R.id.statsTravel:
            	if (id != -1)
            		travelMenu.statsTravel(travel);
            	break;
            
            case R.id.close_travel:
            	if (id!= -1)
            		travelMenu.closeTravel();
            	break;
            	
            case R.id.map_type:
            	mapMenu.changeMapType();
            	break;
            	
            case R.id.marker_color:
            	mapMenu.changeMarkerColor();
            	break;
            case R.id.route_color: 
            	mapMenu.changeRouteColor();
            	break;
            
        }
        return true;
    }
    	
}