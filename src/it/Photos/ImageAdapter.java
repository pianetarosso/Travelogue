package it.Photos;

import it.DiarioDiViaggio.R;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	
    /**
	 */
    private Context mContext;
    /**
	 */
    private Bitmap bm = null;
    /**
	 */
    private ImageView i;
    /**
	 */
    private List<Images> images= null;
    
    // costruisco l'oggetto che definisce la "galleria"
    public ImageAdapter(Context c, List<Images> images) {   	
    	this.images=images;
        mContext = c;
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public void removeImage(int position) {
    	images.remove(position);
    }
    
    // posiziono gli "Uri" delle immagini nella galleria
    public View getView(int position, View convertView, ViewGroup parent) {
        i = new ImageView(mContext);

        // comprimo le immagini dei thumbNails
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        
        bm = BitmapFactory.decodeFile(images.get(position).getPath(), options);
        
        i.setImageBitmap(bm);   
        i.setLayoutParams(new Gallery.LayoutParams(80, 70));
        i.setBackgroundResource(R.drawable.gallery_style);
        return i; 
    } 
}