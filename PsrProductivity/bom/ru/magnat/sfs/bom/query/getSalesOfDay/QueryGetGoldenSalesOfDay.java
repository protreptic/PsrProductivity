package ru.magnat.sfs.bom.query.getSalesOfDay;

import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetGoldenSalesOfDay extends
		QueryGeneric<QueryGetGoldenSalesOfDayEntity> {

	final Date _day;
	static final String query = "SELECT 1 as Id, coalesce(sum(j.su),0) as amount "
			+ "  FROM DocOrderJournal  j "
			+ "  inner join RefOutlet o on j.Outlet = o.Id"
			+ "  inner join RefStoreType t on o.StoreType = t.Id"
			+ "  where j.IsMark=0 " 
			+ "  and datediff(day,j.CreateDate,now())=0 " 
			+ "  and t.ExtId = 'Золотой'";

	public QueryGetGoldenSalesOfDay(Context context, Date date) {
		super(context, QueryGetGoldenSalesOfDayEntity.class, query);
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
