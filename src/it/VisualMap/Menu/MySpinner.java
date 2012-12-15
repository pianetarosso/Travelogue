package it.VisualMap.Menu;

import it.DiarioDiViaggio.R;
import Settings.MapSettings;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class MySpinner extends Spinner {

		/**
		 */
		private AlertDialog mPopup;

		public MySpinner(Context context) {
		    super(context);
		}

		public MySpinner(Context context, AttributeSet attrs) {
		    super(context, attrs);
		}

		public MySpinner(Context context, AttributeSet attrs, int defStyle) {
		    super(context, attrs, defStyle);
		}

		@Override
		protected void onAttachedToWindow() {
		    super.onAttachedToWindow();
		}

		@Override
		protected void onDetachedFromWindow() {
		    super.onDetachedFromWindow();

		    if (mPopup != null && mPopup.isShowing()) {
		        mPopup.dismiss();
		        mPopup = null;
		    }
		}


		//when clicked alertDialog is made
		@Override
		public boolean performClick() {
		    Context context = getContext();
		   
		    AlertDialog.Builder builder = new AlertDialog.Builder(context);
		    CharSequence prompt = getPrompt();
		    if (prompt != null) {
		        builder.setTitle(prompt);
		    }



		    mPopup = builder.setSingleChoiceItems(
		            new DropDownAdapter(getAdapter()),
		            getSelectedItemPosition(), this).show();
		   
		    WindowManager.LayoutParams WMLP = mPopup.getWindow().getAttributes();
		    
		    MapSettings ms = new MapSettings(context);
		    int size = ms.getMap_dropdown();
		  
	        WMLP.gravity = Gravity.BOTTOM;
	        WMLP.y = 0;
		    WMLP.x = 0; 
		    WMLP.height = 170;
		    WMLP.width = size; 
		    WMLP.horizontalMargin = 0; 
		    WMLP.verticalMargin = 0;
		   
		    mPopup.getWindow().setAttributes(WMLP);

		    ListView listView = mPopup.getListView();
		    
		    listView.setBackgroundResource(R.drawable.dropdown_style);
		   
		    ViewParent parent = listView.getParent();
		    while (parent != null && parent instanceof View) {
		        ((View) parent).setBackgroundDrawable(null);

		        parent = parent.getParent();
		    }

		    return true;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
		    setSelection(which);
		    dialog.dismiss();
		    mPopup = null;
		}

		
		private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
		    private SpinnerAdapter mAdapter;

		    
		    public DropDownAdapter(SpinnerAdapter adapter) {
		        this.mAdapter = adapter;
		    }

		    public int getCount() {
		        return mAdapter == null ? 0 : mAdapter.getCount();
		    }

		    public Object getItem(int position) {
		        return mAdapter == null ? null : mAdapter.getItem(position);
		    }

		    public long getItemId(int position) {
		        return mAdapter == null ? -1 : mAdapter.getItemId(position);
		    }

		    public View getView(int position, View convertView, ViewGroup parent) {
		        return getDropDownView(position, convertView, parent);
		    }

		    public View getDropDownView(int position, View convertView, ViewGroup parent) {
		        return mAdapter == null ? null :
		                mAdapter.getDropDownView(position, convertView, parent);
		    }

		    public boolean hasStableIds() {
		        return mAdapter != null && mAdapter.hasStableIds();
		    }

		    public void registerDataSetObserver(DataSetObserver observer) {
		        if (mAdapter != null) {
		            mAdapter.registerDataSetObserver(observer);
		        }
		    }

		    public void unregisterDataSetObserver(DataSetObserver observer) {
		        if (mAdapter != null) {
		            mAdapter.unregisterDataSetObserver(observer);
		        }
		    }
 
		    public boolean areAllItemsEnabled() {
		        return true;
		    }

		    public boolean isEnabled(int position) {
		        return true;
		    }

		    public int getItemViewType(int position) {
		        return 0;
		    }

		    public int getViewTypeCount() {
		        return 1;
		    }

		    public boolean isEmpty() {
		        return getCount() == 0;
		    }

		}	   
}
