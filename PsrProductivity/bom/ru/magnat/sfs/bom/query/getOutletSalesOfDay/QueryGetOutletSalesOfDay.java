package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetOutletSalesOfDay extends
		QueryGeneric<QueryGetOutletSalesOfDayEntity> {

	final Date _day;
	final RefOutletEntity _outlet;
	static final String query = "SELECT 1 as Id, coalesce(sum(amount),0) as amount FROM DocOrderJournal ";

	public QueryGetOutletSalesOfDay(Context context, Date date,
			RefOutletEntity outlet) {
		super(context, QueryGetOutletSalesOfDayEntity.class, query);
		_day = date;
		_outlet = outlet;
	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Outlet", _outlet.Id));
		criteria.add(new SqlCriteria("IsMark", 0));
		criteria.add(new SqlCriteria("datediff(day,CreateDate,now())", 0));

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
