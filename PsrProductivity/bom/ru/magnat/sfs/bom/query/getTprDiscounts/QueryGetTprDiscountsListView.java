package ru.magnat.sfs.bom.query.getTprDiscounts;

import java.io.Closeable;
import java.util.ArrayList;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;
import android.view.View;
import android.widget.ListView;

public class QueryGetTprDiscountsListView extends GenericListView< QueryGetTprDiscounts,  QueryGetTprDiscountsEntity, RefOutletEntity> implements Closeable {

	@SuppressWarnings("unused")
	private final QueryGetTprDiscounts.TprDiscountVariant _type;
	public static ListView mListView;
	
	public QueryGetTprDiscountsListView(Context context, RefOutletEntity owner, QueryGetTprDiscounts.TprDiscountVariant type) {
		super(context, new QueryGetTprDiscounts(MainActivity.getInstance(), owner, type), owner);
		_type = type; 
		_catalog.SetListType(SfsListType.EXTENDED_LIST);
		_catalog.Select(new ArrayList<SqlCriteria>(),"");
		
	}
	
	public long getCount(){
		return this._catalog.Count();
	}
	
	protected void requery(){
		this._catalog.Select(new ArrayList<SqlCriteria>(),"");
	} 
	
	@Override
	public SfsContentView inflate() {
		super.ñatalogInflate(R.layout.doc_marketing_measure_list_view, "");
		View header = (View) findViewById(R.id.tab_header);
		header.setVisibility(GONE);
	 
        return this;
	}
	
	@Override
	protected void createEntity() {}

	@Override
	public void close()  {
		if (_catalog!=null){
			_catalog.close();
		}
	}
	
}
