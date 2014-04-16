package ru.magnat.sfs.bom.reg.tprdiscountbyoutlet;

import java.io.Closeable;
import java.io.IOException;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ListView;

public class RegTprDiscountByOutletListView extends
		GenericListView<RegTprDiscountByOutlet, RegTprDiscountByOutletEntity, RefOutletEntity> implements Closeable{


	public enum TprNotificationType{
		NEW,
		EXPIRING
	}
	
	final TprNotificationType _type;
	public RegTprDiscountByOutletListView(Context context, RegTprDiscountByOutlet reg, RefOutletEntity owner, TprNotificationType type) {
		super(context, reg, owner);
		_type = type; 
		reg.SetListType(SfsListType.EXTENDED_LIST);
		
	}

	protected void requery(){
		switch (_type){
		case NEW:
			this._catalog.SelectNewByOutlet(this._owner);
			break;
		case EXPIRING:
			this._catalog.SelectExpiringByOutlet(this._owner);
			break;
		}
	} 
	
	@Override
	public SfsContentView inflate() {
		super.ñatalogInflate(R.layout.doc_marketing_measure_list_view,
				"");
		View header = (View) findViewById(R.id.tab_header);
		header.setVisibility(GONE);
		
	
	 
        return this;
	}


	
	public static ListView mListView;

	

	
	
	
	
	@SuppressLint("NewApi")
	protected void openEntity(RegTprDiscountByOutletEntity entity) {
	
		
		
		
	}
	
	@Override
	protected void OpenEntity(RegTprDiscountByOutletEntity entity) {
		
	}

	View _selected_view = null;



	@Override
	protected void createEntity() {}

	@Override
	public void close() throws IOException {
		if (_catalog!=null){
			_catalog.close();
		}
		
	}
	
	

}
