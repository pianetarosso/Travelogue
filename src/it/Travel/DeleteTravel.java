package it.Travel;

import it.DB.TravelDB;
import it.Photos.Images;
import it.Service.GPS.MonitorService;
import it.VisualMap.Points;

import java.util.List;

import Settings.ListTravelsBackup;
import Settings.MySettings;
import Settings.TimerSettings;
import Settings.TravelSettings;
import android.content.Context;
import android.content.Intent;

public class DeleteTravel {	
	
	protected static boolean deleteTravel(Travel travel) {
		
		long id = travel.getId();
		Context context = travel.getContext();
		boolean test = true;
		
		if (travel.isViaggioInCorso()) {
			
			Intent i = new Intent(context, MonitorService.class);
			context.stopService(i);
			
			TravelDB travelDB = new TravelDB(context);
			travelDB.open();
	
			test = (
					travelDB.deleteTravel(id) && 
					travelDB.deleteGpsByTravelId(id) && 
					travelDB.deleteContentByTravelId(id) 
			);
			
			travelDB.close();
			
			TravelSettings.deleteTravel(context);
			TimerSettings.removeTimer(context);
		}
		else 
			ListTravelsBackup.deleteTravel(id);
		
		MySettings.deleteTravel(context, id);
		
		return test;
	}	
	
	protected static boolean deleteTimeSelectedPointsAndContents(boolean b, boolean a, Travel travel) {
		
		boolean before = true, after = true;
		long id = travel.getId();
		
		if (travel.isViaggioInCorso()) {
			Context context = travel.getContext();
			
			TravelDB travelDB = new TravelDB(context);
			travelDB.open();
			if (b)
				before = travelDB.deleteContentBeforeDate(id, travel.getDataPartenza()) && travelDB.deleteGpsBeforeDate(id, travel.getDataPartenza());
			if ((travel.getDataFine() != 0) && a)
				after = travelDB.deleteContentAfterDate(id, travel.getDataFine()) && travelDB.deleteGpsAfterDate(id, travel.getDataFine());
			
			travelDB.close();
			
			travel.loadImages();
			travel.loadPoints();
		}
		else {
			List<Points> points = travel.getPoints();
			List<Images> images = travel.getImages();
			
			long partenza = travel.getDataPartenza();
			long fine = travel.getDataFine();
			
			for(Points p:points) {
				if (b && (p.getDataRilevamento()<partenza)) 
					points.remove(p);
				if (a && (p.getDataRilevamento()>fine)) 
					points.remove(p);
			}
			
			for(Images i:images) {
				if (b && (i.getDate()<partenza)) 
					points.remove(i);
				if (a && (i.getDate()>fine)) 
					points.remove(i);
			}
			travel.save();
		}

		return before && after;
	}
}
