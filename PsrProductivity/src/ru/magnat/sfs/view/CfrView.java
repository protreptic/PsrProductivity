package ru.magnat.sfs.view;

import android.content.Context;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.ui.android.SfsContentView;

public class CfrView extends SfsContentView {

	@SuppressWarnings("unused")
	private DocOrderEntity mDocOrderEntity;
	
	public CfrView(Context context) {
		super(context);
		
		layoutInflater.inflate(R.layout.doc_order_cfr, this);
		
		
	}
	
	public CfrView(Context context, DocOrderEntity entity) {
		super(context);
		
		mDocOrderEntity = entity;
	}

}
