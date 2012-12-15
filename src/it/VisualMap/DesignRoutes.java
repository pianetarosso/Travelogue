package it.VisualMap;

import java.util.ArrayList;
import java.util.List;

import Settings.MapSettings;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;


class DesignRoutes extends com.google.android.maps.Overlay  { 

   /**
 */
private  List<GeoPoint> polyLine = new ArrayList<GeoPoint>();
   /**
 */
private int defaultColor;
   
   public DesignRoutes(List<GeoPoint> polyLine) {   
	   this.polyLine = polyLine;
	}
   
   @Override
   public boolean draw (Canvas canvas, MapView mapView, boolean shadow, long when) {
       Projection projection = mapView.getProjection();
       setColor(mapView.getContext());
       if (shadow == false) {
           Paint paint = new Paint();
           paint.setAntiAlias(true);
           paint.setColor(defaultColor);
           GeoPoint oldGp = null;
           
           if (polyLine.size() >=1)
           		oldGp = polyLine.get(0);
           
           for (int i = 1; i <= (polyLine.size()-1); i++) {
        
	           	GeoPoint gp = polyLine.get(i);
	           	Point point = new Point();
	           	projection.toPixels(oldGp, point);
	           	Point point2 = new Point();
	           	projection.toPixels(gp, point2);
	           	canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
	           	oldGp = gp;
           }
          
       }
       return super.draw(canvas, mapView, shadow, when);
   }
   
   private void setColor(Context context) {
	   MapSettings ms = new MapSettings(context);
	   defaultColor = ms.getRoute_color();
   }
} 
	
	
	
	
	
	 
	 
	 