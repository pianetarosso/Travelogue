package it.GestioneFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Settings.ListTravelsBackup;
import Settings.MapSettings;
import Settings.MySettings;
import android.os.Environment;

public class MyFile {
	
	/**
	 */
	private String travelBackupPath;

	/**
	 */
	private String settingsPath;

	/**
	 */
	private String mapSettingsPath; 
	
	private static final String home_folder = "Travelogue";
	private static final String data_folder = "Data";
	
	private static final String travelBackupPathS = "travels";
	private static final String settings = "settings";
	private static final String mapSettings = "map_settings";
	
	private static final String EXTENSION = ".dat";
	
	public MyFile() {	// creo tutte le path
		settingsPath = createPath(settings);
		mapSettingsPath = createPath(mapSettings);
		travelBackupPath = createPath(travelBackupPathS);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//Metodi base caricamento e salvataggio
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	private String createPath(String name) {
		
		File externalStorage = Environment.getExternalStorageDirectory();
		String path = externalStorage.getAbsolutePath();	
		
		if (!path.endsWith("/")) path += "/";
		
		path += home_folder+"/"+data_folder;
		
		File t = new File(path);
		t.mkdirs();
	
		path += "/";
		
		path += name + EXTENSION;
		
		return path;	
	}
	
	private void save(Object o, String path) { // funzione di salvataggio, funziona con qualunque oggetto
		
		File file = new File(path);
		
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(o);
			fos.close();
		} catch (IOException ioe) {ioe.printStackTrace(); }	
	}
	
	private Object load(String path) { // funzione di caricamento, funziona con qualunque oggetto
		
		File file = new File(path);
		Object o = null;
		
		try {
			
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			o = ois.readObject();			
			fis.close();
		} catch (Exception e) {e.printStackTrace();}
		
		return o;
	}
	
	// Travel Settings
	
	public void saveTravelsBackup(ListTravelsBackup ltb) {
		save(ltb, travelBackupPath);
	}
	
	public ListTravelsBackup loadTravels() {
		return (ListTravelsBackup)load(travelBackupPath);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// Settings
	
	public void saveSettings(MySettings mySettings) {
		save(mySettings, settingsPath);
	}
	
	public MySettings loadSettings() {
		return (MySettings)load(settingsPath);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// Map Settings
	
	public void saveMapSettings(MapSettings mapSettings) {
		save(mapSettings, mapSettingsPath);
	}
	
	public MapSettings loadMapSettings() {
		return (MapSettings)load(mapSettingsPath);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
}
