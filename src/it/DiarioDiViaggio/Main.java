package it.DiarioDiViaggio;

import Settings.MySettings;
import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity {
  
	/**
	 */
	private long id;
	/**
	 */
	private boolean scanning = false;
	/**
	 */
	private MySettings mySettings;
	
	/**
	 */
	private String service_started;
	/**
	 */
	private String service_not_started;
	/**
	 */
	private String no_start_gps;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mySettings = new MySettings(this);
        
        service_started = this.getString(R.string.service_started);
        service_not_started = this.getString(R.string.service_not_started);
        no_start_gps = this.getString(R.string.no_start_gps);
        
        changeButtons(false); // definisco i bottoni del menu
    }   

 
    private boolean testGps() { // testa se l'accesso al modulo gps è attivo o meno
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean isGPS = locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
		return (isGPS);
	}
    
    public void Main_NewTravel(View view) { // tasto per la creazione di un nuovo viaggio
    	
    	boolean service = false;
    	Intent i = new Intent(Main.this, it.Service.GPS.MonitorService.class);
    	
    	// verifico se c'è un viaggio attivo
    	if (id > -1) {
    		// se il viaggio è attivo, allora questo bottone è deputato all'on/off della registrazione:
    		// se la variabile "scanning" NON è vera, vuol dire che il background service che gestisce
    		// il gps è (forse) disattivato, quindi lo attivo
    		if (!scanning) {
    			if (testGps()) {
    				service = (startService(i)!= null);
    				if (service) 
    					Toast.makeText(this, service_started , 10);
    				else
    					Toast.makeText(this, service_not_started, 10);
    			}
    			else
    				Toast.makeText(this, no_start_gps, 20);	
    		}
    		
	  		// in questo caso cerco invece di disattivarlo, rimuovendo anche i suoi riferimenti
    		// nelle SP
    		else {
	  			this.stopService(i);
				if (mySettings.isGps_service()) {
					mySettings.setGps_service(false);
					mySettings.saveToSP(this);
				}
    		}
    	}
    	// se non c'è un viaggio attivo propongo di crearne uno nuovo
  	  	else {
  		  // porto l'utente al wizard di creazione nuovo viaggio
  		  Intent v = new Intent(Main.this, WizardViaggio.class);
  		  startActivity(v);    
  	  	}	 
    	// alla fine chiamo la funzione per ridefinire il nome del bottone
    	changeButtons(service);
    }
    
    public void Main_Map(View view) {	       
  		  // porto l'utente alla mappa
  		  Intent i = new Intent(Main.this, TravelMap.class);
  		  startActivity(i);
    }
    
    public void Main_Settings(View view) { // porto l'utente alle settings dell'applicazione
 		 Intent i = new Intent(Main.this, Settings.class);
 		 startActivity(i);   		  
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onWindowFocusChanged(hasFocus);
    	if (hasFocus)
    		changeButtons(false);
    }
    
    // piccola funzione per modificare i bottoni del menu in base alle opzioni salvate
    private void changeButtons(boolean service) {
    	    
    	mySettings.loadFromSP(this);
    	id = mySettings.getInCorso();
    	scanning = mySettings.isGps_service();
        
        // ho dovuto inviare la variabile "service" alla funzione poichè questa veniva chiamata 
        // prima che il bg service del gps riuscisse a modificare le sp
        if (service) scanning = true;
        
        Button button = (Button)findViewById(R.id.new_Travel);
  
        // semplice struttura if per decidere come visualizzare il bottone "crea viaggio",
        // "ferma la registrazione" o "fai partire la registrazione"
        if (id > 0) {
        	if (scanning)
        		button.setText(R.string.stop_service);
        	else
        		button.setText(R.string.start_service);
        }
       else
    	   button.setText(R.string.new_travel);
    }
}
