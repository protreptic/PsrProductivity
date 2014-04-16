package ru.magnat.sfs.bom.query.getItemOrderInfo;

import java.util.ArrayList;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetItemOrderInfo extends
		QueryGeneric<QueryGetItemOrderInfoEntity> {

	final long _id;
	final long _author;
	final long _product;
	
	static final String query = "SELECT Id, Quantity,UnitLevel"
			+ " FROM DocOrderLine ";

	public QueryGetItemOrderInfo(Context context, long id, long author,long product) {
		super(context, QueryGetItemOrderInfoEntity.class, query);
		_id = id;
		_author = author;
		_product = product;
		

	}

	public Boolean Select() {
		
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("MasterDocId",_id));
		criteria.add(new SqlCriteria("MasterDocAuthor", _author));
		criteria.add(new SqlCriteria("Item", _product));
		return super.Select(criteria);
	}

	public QueryGetItemOrderInfoEntity execute() {
		QueryGetItemOrderInfoEntity result = null;
		if (this.Select()) {
			if (this.Next()) {

				result = (QueryGetItemOrderInfoEntity) this.Current().clone();
			}
			this.close();
		}
		return result;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
