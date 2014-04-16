package ru.magnat.sfs.ui.android;

import ru.magnat.sfs.android.OnBackPressedListener;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.util.Fonts;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public abstract class SfsContentView extends RelativeLayout implements OnBackPressedListener {

	protected boolean _inflated;
	protected final LayoutInflater layoutInflater;
	protected final Typeface typeface;
	
	public SfsContentView inflate() { return this; }
	public Boolean onBackPressed() { 
		MainActivity.getInstance().removeFromFlipper(this);
		MainActivity.getInstance().removeOnBackPressedListener(this);
		
		return false; 
	}
	public void fill() {}
	public void fill(GenericEntity<?> entity) {}
	public void refresh() {}
	public void moveTaskToBack(Boolean handled) {}
	
	public SfsContentView(Context context) {
		super(context);

		layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		typeface = Fonts.getInstance(context).getTypeface("RobotoCondensed-Light");
	}

}
