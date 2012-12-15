package it.Service.GPS;

import it.Date.DateManipulation;
import it.Photos.BuildImageList;
import it.Photos.Images;
import it.Travel.Travel;
import it.VisualMap.Points;

import java.net.URISyntaxException;
import java.util.List;

import Settings.MySettings;
import android.app.ProgressDialog;
import android.content.Context;

public class MediaScan {
		
	/**
	 */
	private ProgressDialog progressDialog = null;
	/**
	 */
	private int baseOfProgress=0;
	/**
	 */
	private Travel travel;
	/**
	 */
	private Context context;
	private static final int TIME = 10; //60000;
	
	public MediaScan(Travel travel) {
		this.travel=travel;
		this.context = travel.getContext();
	}
	
	public void setprogressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
		baseOfProgress = progressDialog.getProgress();
	}
	
	private void AddIncrement(int length, int position) {
		 if(progressDialog!=null)
         	progressDialog.setProgress(baseOfProgress+(int)(position*100/length));
	}

	public Travel scanImages() throws URISyntaxException {
		
		List<Images> images;
		BuildImageList buildImageList = new BuildImageList(travel.getContext(), travel.getId());
		images = buildImageList.fromMediastore();
		
		
		long start = buildImageList.getDateStart();
		long stop = buildImageList.getDateStop();
		
		long date = -1;
		
		List<Points> points;
		points = travel.selectTimePoints(start, stop);
	
		if (points.size()<2) {
			points = travel.updatePoints();
		}
			
		if ((!images.isEmpty()) && (!points.isEmpty())) {
    	
			int pointsCount = 0;
            
        	Images image;
        	Points oldPoint;
        	Points currentPoint;
        	
        	oldPoint = points.get(pointsCount);
        	currentPoint = oldPoint;
        	
        	pointsCount++;        	
        	 
	        for (int imagesCount = 0; images.size() > imagesCount; imagesCount++) {
	        	
	        	image = images.get(imagesCount);
	        	
	        	if (pointsCount<points.size())
	        		currentPoint = points.get(pointsCount);
	        	
	        	if (image.getDate()<oldPoint.getDataRilevamento()) //immagine scattata PRIMA del 1o punto
	        		setImage(image, oldPoint);
	        	
	        	// immagine scattata entro TIME minuti dall'ultimo rilevamento della posizione 
	        	else if ((image.getDate()<=(currentPoint.getDataRilevamento()+TIME)) && (image.getDate()>currentPoint.getDataRilevamento()) && (pointsCount==(points.size()-1))) 
	        		setImage(image, currentPoint); 
	        	
	        	// immagine scattata MOLTO dopo l'ultimo rilevamento, cerco di includerla nella prossima scansione
	        	else if ((image.getDate()>=(currentPoint.getDataRilevamento()+TIME)) && (pointsCount==(points.size()-1))) 
	        		date = image.getDate() - TIME;
	        	
	        	else if (image.getDate() == oldPoint.getDataRilevamento()) 
	        		setImage(image, oldPoint);
	        		
	        	else if (image.getDate() == currentPoint.getDataRilevamento()) 
	        		setImage(image, currentPoint);
	        	
	        	else if ((image.getDate() > oldPoint.getDataRilevamento()) && (image.getDate() < currentPoint.getDataRilevamento())) {
	        		if (oldPoint.equals(currentPoint)>0)
	        			setImage(image, oldPoint);
	        		else 
	        			calculatePosition(image, oldPoint, currentPoint);	
	        	}
	        	else if (image.getDate() > currentPoint.getDataRilevamento()) {
	        		 	boolean test = true;
	        		
	        		for(; test; pointsCount++) {
	        			test = (image.getDate() > currentPoint.getDataRilevamento());
	        			test = test && (pointsCount < points.size());
	        			if(test) {
	        				oldPoint = currentPoint;
	        				currentPoint = points.get(pointsCount);
	        			}
	        		}
	        	}
	        	AddIncrement(images.size(), imagesCount);
	        }
		}
		
		MySettings s = new MySettings(context);
		
		if (date < 0)
			date = DateManipulation.getCurrentTimeMs();
		
		s.setDataScansione(date);
		s.saveToSP(context);
	
		return travel;
	}
	
	
	private void calculatePosition(Images image, Points oldPoint, Points currentPoint) {

		int ax = oldPoint.getLatitude();
		int ay = oldPoint.getLongitude();

		int bx = currentPoint.getLatitude();
		int by = currentPoint.getLongitude();

		long at = oldPoint.getDataRilevamento(); // recupero il tempo del primo punto 
		long bt = currentPoint.getDataRilevamento(); // recupero il tempo del secondo punto
		long xt = image.getDate(); // tempo del punto da trovare

		// Trovo il tempo relativo dal punto A a quello B
		double t=(bt==at) ? 0 : ((double)(xt-at))/((double)(bt-at));
		// Trovo il nuovo punto grazie ai punti di start e stop e al tempo relativo
		int[] xpos=getIntermediatePoint(ax,ay,bx,by,t);
		image.setLatitude(xpos[0]); // imposto la latitudine di X
		image.setLongitude(xpos[1]); // imposto la longitudine di X
		
		travel.addImages(image);
		
	}
	
	
	public static int[] getIntermediatePoint(
	        int startLatMicroDeg,
	        int startLonMicroDeg,
	        int endLatMicroDeg,
	        int endLonMicroDeg,
	        double t // Quanta distanza usare, da 0 a 1
	    ){
	        // Converto le  microdegrees in radianti
	        double alatRad=Math.toRadians((double)startLatMicroDeg/1000000);
	        double alonRad=Math.toRadians((double)startLonMicroDeg/1000000);
	        double blatRad=Math.toRadians((double)endLatMicroDeg/1000000);
	        double blonRad=Math.toRadians((double)endLonMicroDeg/1000000);
	        // Calcolo la distanza in longitudine 
	        double dlon=blonRad-alonRad;
	        // Calcolo le variabili comuni
	        double alatRadSin=Math.sin(alatRad);
	        double blatRadSin=Math.sin(blatRad);
	        double alatRadCos=Math.cos(alatRad);
	        double blatRadCos=Math.cos(blatRad);
	        double dlonCos=Math.cos(dlon);
	        // Trovo la distanza da A a B
	        double distance=Math.acos(alatRadSin*blatRadSin +
	                                  alatRadCos*blatRadCos *
	                                  dlonCos);
	        // Trovo la direzione da A a B
	        double bearing=Math.atan2(
	            Math.sin(dlon) * blatRadCos,
	            alatRadCos*blatRadSin -
	            alatRadSin*blatRadCos*dlonCos);
	        // Trovo il nuovo punto
	        double angularDistance=distance*t;
	        double angDistSin=Math.sin(angularDistance);
	        double angDistCos=Math.cos(angularDistance);
	        double xlatRad = Math.asin( alatRadSin*angDistCos +
	                                   alatRadCos*angDistSin*Math.cos(bearing) );
	        double xlonRad = alonRad + Math.atan2(
	            Math.sin(bearing)*angDistSin*alatRadCos,
	            angDistCos-alatRadSin*Math.sin(xlatRad));
	        // Converto i radianti in microdegrees
	        int xlat=(int)Math.round(Math.toDegrees(xlatRad)*1000000);
	        int xlon=(int)Math.round(Math.toDegrees(xlonRad)*1000000);
	        return new int[]{xlat,xlon};
	    }

	private void setImage(Images image, Points point) {
		image.setLatitude(point.getLatitude());
		image.setLongitude(point.getLongitude());
		travel.addImages(image);
	}
	
}	