package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetOutletNewPowerCskuOfDay extends
		QueryGeneric<QueryGetOutletNewPowerCskuOfDayEntity> {

	final Date _day;
	final RefOutletEntity _outlet;
	static final String query = "select 1 as Id, count(distinct i.ParentExt) as NewCount"
			+ " from DocOrderLine l "
			+ " inner join DocOrderJournal j on l.MasterDocId=j.Id and l.MasterDocAuthor=j.Author "
			+ " inner join RefProductItem i on l.Item=i.Id "
			+ " left join RegOrderHelper s on j.Outlet = s.Outlet and s.CSKU  = i.ParentExt and s.CurrentPeriod = 1"
			+ " inner join RegCskuState st on i.ParentExt = st.Csku and (st.Abc = 1 OR st.Abc = 3)";

	public QueryGetOutletNewPowerCskuOfDay(Context context, Date date,
			RefOutletEntity outlet) {
		super(context, QueryGetOutletNewPowerCskuOfDayEntity.class, query);
		_day = date;
		_outlet = outlet;

	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("j.Outlet", _outlet.Id));
		criteria.add(new SqlCriteria("j.IsMark", 0));
		criteria.add(new SqlCriteria("datediff(day,j.CreateDate,now())", 0));
		//criteria.add(new SqlCriteria("datediff(month,COALESCE(s.Period,now()),now())", 3,"<"));
		criteria.add(new SqlCriteria("COALESCE(s.Csku,0)", 0));
		if (_outlet.Channel == null)
			criteria.add(new SqlCriteria("st.StoreChannel", 0));
		else
			criteria.add(new SqlCriteria("st.StoreChannel", _outlet.Channel.Id));

		return super.Select(criteria);
	}

	public int execute() {
		int result = 0;
		if (this.Select()) {
			if (this.Next()) {
				result = this.Current().NewCount;
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
