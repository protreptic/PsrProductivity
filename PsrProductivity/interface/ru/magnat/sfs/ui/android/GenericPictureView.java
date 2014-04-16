package ru.magnat.sfs.ui.android;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.*;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GenericPictureView
		extends SfsContentView{

	protected final String _filename;
	

	public GenericPictureView(Context context, String filename) {

		super(context);
		_filename = filename;
		

	}

	

	
	public SfsContentView inflate() {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.picture_view, this);
		ImageView v = (ImageView) this.findViewById(R.id.picture);
		if (Globals.findDownloadedFile(_filename)){
			Drawable drawable = Drawable.createFromPath(Globals.getInboxPath()+_filename);
			if (drawable!=null){
				v.setImageDrawable(drawable);
			}
		}
	
		return this;

	}


	

	@Override
	public void refresh() {
	}

	@Override
	public void fill() {
		
	}

	public void moveTaskToBack(Boolean handled) {
		RelativeLayout page2layout = (RelativeLayout) findViewById(R.id.relativeLayout);

		if (page2layout.getVisibility() == View.VISIBLE) {
			handled = false;
			if (!onRemove())
				return;
			handled = true;
			closeView();

			return;
		}

		handled = false;
	}

	public void closeView() {
		MainActivity.sInstance.removeFromFlipper(this);
		MainActivity.sInstance.removeOnBackPressedListener(this);
	}

	public void cancelCloseView() {
		MainActivity.sInstance.addOnBackPressedListener(this);
	}

	protected Boolean onRemove() {
		return true;
	}

}
