package it.VisualMap;

import Settings.MapSettings;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public class CreateMarkerDrawable {

	/**
	 */
	private View view = null;
	/**
	 */
	private Context context;
	
	public CreateMarkerDrawable (View view, Context context) {
		this.view = view;
		this.context = context;
	}
	
	public Drawable draw(int dimensioni, int corona, int color, int hColor, boolean selezionato) {
		
		dimensioni = test(dimensioni, 1);
		corona = test(corona, 2);
		color = test(color, 3);
		hColor = test(hColor, 4);
		
		int dimX, dimY;
		
		if (view != null) {
			dimX = 40;
			dimY = 40;
		}
		else {
			dimX = dimensioni;
			dimY = dimensioni;
		}

		int centerX = dimX/2;
		int centerY = dimY/2;
		
		int mRadius = (int)((dimensioni/2) -(dimensioni*corona/10));
		int hRadius = (int)(dimensioni/2);
		
		Bitmap bitmap = Bitmap.createBitmap(dimX, dimY, Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(bitmap);
		
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(50);
        
        if (selezionato) {
	        paint.setColor(hColor);
	        canvas.drawCircle(centerX, centerY, hRadius, paint);
        }
        
        paint.setColor(color);
        canvas.drawCircle(centerX, centerY, mRadius, paint);
            
        paint.setAlpha(255);
        canvas.drawCircle(centerX, centerY, (mRadius-1), paint);
        
		return new BitmapDrawable(bitmap);
	}
	
	private int test(int input, int type) {

		MapSettings ms = new MapSettings(context);
		
		int o = 0;
		
		switch (type) {
		
		case 1:
			if (input < 0)
				o = ms.getMarker_size();
			else
				o = input;
			break;
			
		case 2:
			if (input < 0)
				o = ms.getMarker_highlight_size();
			else
				o = input;
			break;
			
		case 3:
			if (input == Integer.MIN_VALUE)
				o = ms.getMarker_color();
			else
				o = input;
			break;
		
		case 4:
			if (input == Integer.MIN_VALUE)
				o = ms.getMarker_highlight_color();
			else
				o = input;
			break;
	
		}
		
		return o;
	}
}
