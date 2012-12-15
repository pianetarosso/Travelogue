package it.Travel;

import it.DB.TravelDB;
import Settings.MySettings;
import Settings.TravelSettings;
import android.content.Context;

public class SaveTravel {	
	
	protected static boolean save(Travel travel) {
		if (travel.getId() > -1)
			return updateTravel(travel);
		else
			return saveNewTravel(travel);
	}
		
	private static boolean saveNewTravel(Travel travel) {
		
		Context context = travel.getContext();
		TravelSettings travelSettings = new TravelSettings(context);
		MySettings ms = new MySettings(context);
		
		TravelDB travelDB = new TravelDB(context);
		travelDB.open();
		long id = travelDB.createTravel(
				travel.getNome(), 
				travel.getDescrizione(),
				travel.getDataPartenza(), 
				true);
		travelDB.close();
		travelSettings.setDateStart(travel.getDataPartenza());
		travelSettings.setInCorso(true);
		travelSettings.setNome(travel.getNome());
		travelSettings.setDescrizione(travel.getDescrizione());
		travelSettings.setTravelId(id);
		
		ms.setInCorso(id);
		ms.saveToSP(context);
		
		return (travelSettings.saveInSP(context) && (id > (-1)));			
	}
	
	private static boolean updateTravel(Travel travel) {
		
		Context context = travel.getContext();
		long id = travel.getId();
		TravelSettings travelSettings = new TravelSettings(context);
		
		TravelDB travelDB = new TravelDB(context);
		travelDB.open();
		
		boolean result = travelDB.updateTravel(
				id, 
				travel.getNome(), 
				travel.getDescrizione(),
				travel.getDataPartenza(), 
				travel.getDataFine(), 
				travel.getDistance(),
				travel.isViaggioInCorso());
		
		travelSettings.setDateStart(travel.getDataPartenza());
		travelSettings.setInCorso(true);
		travelSettings.setNome(travel.getNome());
		travelSettings.setDescrizione(travel.getDescrizione());
		travelSettings.setDateStop(travel.getDataFine());
		travelSettings.setDistance(travel.getDistance());
		
		travelSettings.saveInSP(context);
		travelDB.close();
		return result;	
	}
}
