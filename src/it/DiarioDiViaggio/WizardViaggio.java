package it.DiarioDiViaggio;

import it.Travel.Travel;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class WizardViaggio extends Activity {
	
	/**
	 */
	private boolean test = false;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_viaggio);
	}
	
	// salvo la variabile test nel caso in cui debba attivare il gps
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  savedInstanceState.putBoolean("test", test);
	  super.onSaveInstanceState(savedInstanceState);
	}
	
	// ripristino la variabile test
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  test = savedInstanceState.getBoolean("test");
	}
	
	// se la variabile test è true, vuol dire che ero uscito per attivare il gps,
	// quindi indipendentemente dal valore attivo il servizio di monitoraggio
	@Override
	public void onResume() {
		super.onResume();
		
		if (test) {
			if (SaveNewTravel.testGps(this)) {
				SaveNewTravel.startGps(this);}
			else 
				Toast.makeText(this, R.string.no_start_gps, Toast.LENGTH_SHORT).show();	
			SaveNewTravel.returnToMain(this);
		}
	}
	
	
	// Modalità per iniziare subito ad usare l'applicazione
	public void startNow (View view) {
		test = true;
		SaveNewTravel.saveTravel(new Travel(this), true);
	}
	
	// porta al wizard per la creazione di un viaggio normale
	public void travel (View view) {
		Intent i = new Intent(WizardViaggio.this, TravelWizard.class);
	    startActivity(i);
	}
	
	
}
