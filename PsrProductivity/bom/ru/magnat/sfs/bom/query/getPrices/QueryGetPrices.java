package ru.magnat.sfs.bom.query.getPrices;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetPrices extends QueryGeneric<QueryGetPricesEntity> {

	static final String query = "SELECT p.Id, p.productItem, p.price*(1 + (1-t.includeVAT)*i.vat) price from RegPrice p" +
			" inner join RefProductItem i on i.Id = p.productItem" +
			" inner join RefPriceType t on p.priceType = t.Id";
	final long _priceType;

	public QueryGetPrices(Context context, long priceType) {
		super(context, QueryGetPricesEntity.class, query);
		_priceType = priceType;
	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("p.priceType", _priceType));
		return super.Select(criteria, "");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
