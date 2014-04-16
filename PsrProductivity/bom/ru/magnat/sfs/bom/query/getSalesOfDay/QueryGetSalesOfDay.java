package ru.magnat.sfs.bom.query.getSalesOfDay;

import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetSalesOfDay extends
		QueryGeneric<QueryGetSalesOfDayEntity> {

	final Date _day;
	static final String query = "SELECT 1 as Id, coalesce(sum(amount),0) as amount "
			+ "FROM DocOrderJournal "
			+ "where IsMark=0 and datediff(day,CreateDate,now())=0";

	public QueryGetSalesOfDay(Context context, Date date) {
		super(context, QueryGetSalesOfDayEntity.class, query);
		_day = date;
	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		return super.Select(criteria);
	}

	public float execute() {
		float result = 0;
		if (this.Select()) {
			if (this.Next()) {
				result = this.Current().Amount;
			}
		}
		this.close();
		return result;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
