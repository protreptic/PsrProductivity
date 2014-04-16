package ru.magnat.sfs.view;

import android.content.Context;
import android.widget.TextView;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.ui.android.SfsContentView;

public class ErrorView extends SfsContentView {

	private TextView mMessageView;
	
	public ErrorView(Context context) {
		super(context);
		
		layoutInflater.inflate(R.layout.error_view_layout, this);
		
		mMessageView = (TextView) findViewById(R.id.message);
		mMessageView.setTypeface(typeface); 
	}
	
}
