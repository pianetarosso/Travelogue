package it.VisualMap.Menu;

import it.Date.DateManipulation;
import it.DiarioDiViaggio.EditTravel;
import it.DiarioDiViaggio.Main;
import it.DiarioDiViaggio.R;
import it.DiarioDiViaggio.TravelMap;
import it.Travel.Travel;
import it.VisualMap.DesignMap;

import java.util.List;

import Settings.MySettings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TravelMenu extends Activity{

	/**
	 */
	private Travel travel;
	/**
	 */
	private Context context;
	/**
	 */
	private long id;
	/**
	 */
	private Travel item;
	/**
	 */
	private TravelMap travelMap;
	/**
	 */
	private TextView oldView = null;
	
	public TravelMenu (TravelMap travelMap) {
		this.travel = travelMap.getTravel();
		this.context = travelMap.getContext();
		if (travel == null) 			
			this.id = -1;
		else 
			this.id = travel.getId();
		this.travelMap = travelMap;
	}
	
	
	public void closeTravel() {
		
		 Builder builder = new AlertDialog.Builder(travel.getContext());
		
		builder.setCancelable(false);
		builder.setMessage(R.string.close_travel_question);
		builder.setPositiveButton(R.string.yes, 
				new DialogInterface.OnClickListener() { 
		    	   public void onClick(DialogInterface dialog, int id) {
		    		   travel.closeTravel(DateManipulation.getCurrentTimeMs());
		    		   Intent intent = new Intent(travel.getContext(), Main.class);
		    		   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		    		   context.startActivity(intent);	
		    	   }
		});		
		builder.setNegativeButton(R.string.no, 
				new DialogInterface.OnClickListener() { 
	    	   public void onClick(DialogInterface dialog, int id) {
	    		   dialog.dismiss();
	    	   }
		});		
		
		builder.show();
	}
	
	
	
	private void setTextView(final Travel travel, LinearLayout sv, boolean current, boolean inCorso) {
		
		String text = travel.getNome();
		if (inCorso)
			text += " "+ context.getString(R.string.in_corso);
		
		TextView t = new TextView(context);
		t.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		t.setText(text);
		t.setClickable(true);
		t.setSingleLine();
		t.setTextSize(20);
		t.setMinimumHeight(15);
		
		t.setPadding(3, 5, 3, 5);
		t.setGravity(Gravity.CENTER);
		t.setTextColor(Color.BLACK);
		t.setBackgroundResource(R.drawable.linear_layout_hoose_travel_selection);
		t.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				setItem(travel); 
				
				if (oldView != null) {
					oldView.setTextColor(Color.BLACK);
					oldView.setSelected(false);
				}
				
				TextView t = (TextView)arg0;
				oldView = t;
				t.setSelected(true);
				t.setTextColor(Color.WHITE);
			}
		});
		
		sv.addView(t);
		if (current) {
			t.setSelected(true);
			oldView = t;
		}
	}
	
	public void changeTravel() {
		final Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		dialog.setContentView(R.layout.choose_travel_dialog);
		LinearLayout sv = (LinearLayout)dialog.findViewById(R.id.chooseTravelLinearLayout);
		
        Travel travel = new Travel(context);
		final List<Travel> travels = travel.getAllTravels();
		MySettings ms = new MySettings(context);
		
		for (Travel t:travels) 
			setTextView(t, sv, (id == t.getId()), (ms.getInCorso()== t.getId()));

		Button info = (Button)dialog.findViewById(R.id.travel_choose_info);
		Button load = (Button)dialog.findViewById(R.id.travel_choose_load);
		
		info.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				description(item);
			}
		});
		
		load.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DesignMap dm = new DesignMap();
				if (item != null) {
	            	boolean test = dm.initialize(getItem().getId(), travelMap);
	            	if (test) {
	            		dialog.dismiss();
	            		dm.execute();
	            	}
				}
			}
		});
		
		dialog.show();
		
	}
	
	/**
	 * @return
	 */
	private Travel getItem() {
		return item;
	}
	
	/**
	 * @param k
	 */
	private void setItem(Travel k) {
		this.item = k;
	}
	
	public void description(Travel travel) {
    	
    	String data_partenza = context.getString(R.string.stats_data_partenza);
    	String foto = context.getString(R.string.photo);
    	String descrizione = context.getString(R.string.description);
    	String hai_scattato = context.getString(R.string.stats_photo);
    	String data_fine = context.getString(R.string.stats_data_fine);
    	
    	if (travel != null) {
	    	String text = "";
	    	long startDate = travel.getDataPartenza();
	    	text = text + data_partenza+":\n"+DateManipulation.parseMs(startDate)+"\n";
	    	//text = text + hai_scattato + " "+travel.getImages().size()+" "+foto+"\n";
	    	long stopDate = travel.getDataFine();
	    	if (stopDate>0)
	    		text = text + data_fine+":\n"+DateManipulation.parseMs(stopDate)+"\n";
	    	if (travel.getDescrizione().length()>0)
	    		text = text + descrizione + "\n"+travel.getDescrizione()+"\n";
	    	Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    	}
	}
	
	
	public void statsTravel(Travel travel) {
    	
    	AlertDialog.Builder dialog = new AlertDialog.Builder(context);
    	
    	String data_partenza = context.getString(R.string.stats_data_partenza);
    	String data_fine = context.getString(R.string.stats_data_fine);
    	String sei_in_viaggio_da = context.getString(R.string.stats_period);
    	String hai_percorso = context.getString(R.string.stats_distance);
    	String hai_scattato = context.getString(R.string.stats_photo);
    	String foto = context.getString(R.string.photo);
    	
    	dialog.setTitle(travel.getNome());
    	String text = "";
    	long startDate = travel.getDataPartenza();
    	text = text + data_partenza+":\n"+DateManipulation.parseMs(startDate)+"\n";
    	long lastDate = DateManipulation.getCurrentTimeMs();
    
    	if (travel.getDataFine()!=0) {
			lastDate = travel.getDataFine();
			text = text + data_fine +":\n"+ DateManipulation.parseMs(lastDate)+"\n";
		}	
    	if (startDate<=lastDate)
    		text = text+sei_in_viaggio_da+DateManipulation.getPeriod(startDate, lastDate, context)+"\n";
    	
    	int metri = travel.getDistance();
    	int km = metri / 1000;
    	metri = metri - (km*1000);
    	text = text + hai_percorso + " "+km+" Km, "+metri+" m" +"\n";
    	text = text + hai_scattato + " "+travel.getImages().size()+" "+foto+"\n";
    	
    	dialog.setMessage(text);
    	dialog.show();	
	}
	
	public void editTravel() {
		Bundle b = new Bundle();
    	b.putLong("id", id);
    	Intent i = new Intent(context, EditTravel.class);
    	i.putExtras(b);
    	context.startActivity(i);
	}
	
	 

}
