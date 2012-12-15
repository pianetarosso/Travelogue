package it.DiarioDiViaggio;

import it.Photos.Images;
import it.Service.GPS.MediaScan;
import it.Travel.Travel;
import it.VisualMap.Points;

import java.util.Calendar;
import java.util.List;

import Settings.ListTravelsBackup;
import Settings.MapSettings;
import Settings.MySettings;
import Settings.TravelBackup;
import Settings.TravelSettings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class Backup_Restore extends Activity {

	/**
	 */
	private Context context;
	/**
	 */
	private MySettings ms;
	/**
	 */
	private TravelBackup current;
	/**
	 */
	private ListTravelsBackup ltb;
	/**
	 */
	private long maxId = 0;
	
	private static final int MERGE = 1;
	private static final int CLOSE_DB = 2;
	private static final int CLOSE_FILE = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup_restore);
		context = this;
	}
	
	public void backup(View view) {
		backup b = new backup();
		ProgressDialog pd = createProgressDialog(R.string.saving);
		pd.show();
		b.initialize(pd);
		b.execute();
	}
	
	public void restore(View view) {
		Builder dialog = createWarningDialog();
		dialog.show();
	}

	private void startResolveConflict(ProgressDialog pd, int mode) {
		resolveConflict rc = new resolveConflict();
		rc.initialize(pd);
		pd.show();
		rc.execute(mode);
	}
	
	private Dialog createConflictDialog(final ProgressDialog pd) {
		
		final Dialog dialog = new Dialog(context);
		dialog.setTitle(R.string.choose_what_to_restore);
		LayoutInflater inflater =  (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.conflict_restore_dialog, (ViewGroup)findViewById(R.id.conflictLayout));
		dialog.setContentView(layout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		Button close_db = (Button)layout.findViewById(R.id.close_db_travel);
		close_db.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {	
						// chiudi il viaggio nel db
						startResolveConflict(pd, CLOSE_DB);
						dialog.dismiss();
					}
				});
		
		Button close_file = (Button)layout.findViewById(R.id.close_file_travel);
		close_file.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {	
						// chiudi il viaggio nel file
						startResolveConflict(pd, CLOSE_FILE);
						dialog.dismiss();
					}
				});
		
		Button merge = (Button)layout.findViewById(R.id.merge_travels);
		merge.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View v) {	
						// unisci i punti le immagini e le preferenze
						startResolveConflict(pd, MERGE);
						dialog.dismiss();
					}
				});
		
		
		return dialog;
	}
	
	private Builder createWarningDialog() {
		AlertDialog.Builder dialog = new Builder(this);
		dialog.setMessage(R.string.restore_warning);
		dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {		
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();	
			}
		});
		
		dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				ProgressDialog pd = createProgressDialog(R.string.loading);
				restoreFromSD r = new restoreFromSD();
				r.initialize(pd, createConflictDialog(pd));
				pd.show();
				r.execute();
			}
		});
		return dialog;
	}
	
	private ProgressDialog createProgressDialog(int title) {
		ProgressDialog pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setTitle(title);
		return pd;
	}

	private class backup extends AsyncTask <Void, Void, Void > {
		
		private ProgressDialog pd;
		private boolean error = false;
		
		protected void initialize(ProgressDialog pd) {
			this.pd = pd;
		}
			
		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				MySettings ms = new MySettings(context);
				ms.saveToSD();
				
				MapSettings mps = new MapSettings(context);
				mps.saveToFile();
				
				if (ms.getInCorso()!=-1) {
					Travel travel = Travel.load(ms.getInCorso(), context);
					MediaScan mediascan = new MediaScan(travel);
					travel = mediascan.scanImages();
					travel.save();
					
					ListTravelsBackup ltb = ListTravelsBackup.loadAll();
					if (ltb==null) 
						ltb = new ListTravelsBackup();
						
					ltb.add(travel);
					ltb.save();		
				}
			} catch (Exception e) { 
				error = true;
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();
			if (error)
				Toast.makeText(context, R.string.error_data_saving, Toast.LENGTH_SHORT).show();	
			else
				Toast.makeText(context, R.string.backup_ok, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private class restoreFromSD extends AsyncTask <Void, Void, Boolean > {
		
		private ProgressDialog pd;
		private Dialog warning;
		private boolean error = false;
		
		protected void initialize(ProgressDialog pd, Dialog warning) {
			this.pd = pd;
			this.warning = warning;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			boolean test = false;
			
			try {
					ltb = ListTravelsBackup.loadAll();
						
					ms = MySettings.loadFromSD();
					MySettings msSP = new MySettings(context); 
					
					if (ms == null) ms = new MySettings(context);
						
					if (ltb!=null) {
							
						current = null;
						for(TravelBackup tb:ltb) {
							if (tb.getSettings().getTravelId()>maxId)
								maxId = tb.getSettings().getTravelId();
							if (tb.getSettings().isInCorso()) 
								current = tb;
						}
							
						if (current != null) {	
							if (msSP.getInCorso()==-1) 
								copyTravel(current, ltb);
							else {
								test = true;
							}
						}
					}
					ms.setOffset((int)maxId);
					ms.restore(context);
					
					MapSettings mps = MapSettings.loadFromFile();
					if (mps!=null) mps.saveToSP(context);
				
					
			} catch (Exception e) {
				error = true;
				e.printStackTrace();
			}
			
			return test;
		}	
		

		@Override
		protected void onPostExecute(Boolean result) {
			pd.dismiss();
			if (error)
				Toast.makeText(context, R.string.error_data_loading, Toast.LENGTH_SHORT).show();	
			else if (result)
				warning.show();
			else
				Toast.makeText(context, R.string.restore_ok, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private class resolveConflict extends AsyncTask <Integer, Void, Void > {
		
		private ProgressDialog pd;
		private boolean error = false;
		
		protected void initialize(ProgressDialog pd) {
			this.pd = pd;
		}
		
		@Override
		protected Void doInBackground(Integer... params) {
			
			try {
												
				switch(params[0]) {
					
					case MERGE: { // merge
						
						ltb.remove(current);
						ltb.save();
						
						List <Points> currentPoints = current.getPoints(context);
						List<Images> currentImages = current.getImages(context);
						copyPoints(currentPoints, ms.getInCorso());
						copyImages(currentImages, ms.getInCorso());
						
						TravelSettings ts = new TravelSettings(context);
						
						Travel travel = Travel.loadOnlyTravel(ts.getTravelId(), context);
						
						int size = currentPoints.size();
						ts.setLastPointParsed(currentPoints.get(size-1).getDataRilevamento());
						travel.setDistance(current.getSettings().getDistance());
						
						copyPositionTravelSettings(ts, current.getSettings());
						
						int numberOfPhotos = ts.getNumberOfPhotos()+currentImages.size();
						travel.setNumberOfImages(numberOfPhotos);
						
						travel.setdataPartenza(current.getSettings().getDateStart());
						
						if (ts.getDateStop()<=0)
							travel.setDateStop(current.getSettings().getDateStop());
						
						if (travel.getDescrizione().length()==0)
							travel.setDescrizione(current.getSettings().getDescrizione());
						if (travel.getNome().length()==0)
							travel.setNome(current.getSettings().getNome());
						
						travel.save();
						break;
					}
					
					case CLOSE_DB: { // chiudo il viaggio nel db
						ltb.remove(current);
						ltb.save();
						Travel travel = Travel.load(ms.getInCorso(), context);
						travel.closeTravel(Calendar.getInstance().getTimeInMillis());
						copyTravel(current, ltb);
						break;
					}
				
					case CLOSE_FILE: {
						ltb.remove(current);
						TravelSettings ts = current.getSettings();
						ts.setDateStop(Calendar.getInstance().getTimeInMillis());
						ts.setInCorso(false);
						ltb.add(current);
						ltb.save();
						break;
					}
				}	
			} catch (Exception e) {
				error = true;	
				e.printStackTrace();
			}
			return null;
		}	
		

		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();
			if (error)
				Toast.makeText(context, R.string.error_data_loading, Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(context, R.string.restore_ok, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private void copyPoints(List<Points> points, long id) {
		if (points != null) {
			for (Points p:points) {
				p.setTravelID(id);
				p.setContext(context);
				p.savePoint();
			}	
		}
	}
	
	private void copyImages(List<Images> images, long id) {
		if (images != null) {
			for (Images i:images) {
				i.setTravelID(id);
				i.setContext(context);
				i.save();
			}	
		}
	}
	
	private void copyTravel(TravelBackup current, ListTravelsBackup ltb) {
		Travel travel = new Travel(context);
		travel.parseSettings(current.getSettings());
			
		TravelSettings ts = new TravelSettings(context);
		copyPositionTravelSettings(ts, current.getSettings());
		
		copyPoints(current.getPoints(context), ts.getTravelId());
		copyImages(current.getImages(context), ts.getTravelId());
		
		ltb.remove(current);
		ltb.save();
	}
	
	private void copyPositionTravelSettings(TravelSettings ts, TravelSettings input) {
		ts.setMaxCoord(input.getMaxLat(), input.getMaxLng());
		ts.setMinCoord(input.getMinLat(), input.getMinLng());
		ts.saveInSP(context);
	}
}