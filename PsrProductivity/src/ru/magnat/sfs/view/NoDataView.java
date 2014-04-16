package ru.magnat.sfs.view;

import android.content.Context;
import android.widget.TextView;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.ui.android.SfsContentView;

public class NoDataView extends SfsContentView {
	private TextView mMessageView;
	
	public NoDataView(Context context) {
		super(context);
		
		layoutInflater.inflate(R.layout.no_data_layout, this);
		
		mMessageView = (TextView) findViewById(R.id.message);
		mMessageView.setTypeface(typeface); 
	}
}
