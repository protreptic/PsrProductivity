package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.initiative.RefInitiativeEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetOutletInitiativeSalesOfDay extends
		QueryGeneric<QueryGetOutletInitiativeSalesOfDayEntity> {

	final Date _day;
	final RefOutletEntity _outlet;
	final RefInitiativeEntity _initiative;
	static final String query = "select 1 as Id, sum(l.Quantity*i.SuFactor) as Sales"
			+ " from DocOrderLine l "
			+ " inner join DocOrderJournal j on l.MasterDocId=j.Id and l.MasterDocAuthor=j.Author "
			+ " inner join RefProductItem i on l.Item=i.Id "
			+ " inner join RegCskuState st on i.ParentExt = st.Csku and st.IsDrive=1 ";

	public QueryGetOutletInitiativeSalesOfDay(Context context, Date date,
			RefOutletEntity outlet, RefInitiativeEntity initiative) {
		super(context, QueryGetOutletInitiativeSalesOfDayEntity.class, query);
		_day = date;
		_outlet = outlet;
		_initiative = initiative;

	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("j.Outlet", _outlet.Id));
		criteria.add(new SqlCriteria("j.IsMark", 0));
		criteria.add(new SqlCriteria("datediff(day,j.CreateDate,now())", 0));
		//criteria.add(new SqlCriteria(
		//		"datediff(month,COALESCE(st.Period,now()),now())", 2, "<"));
		if (_outlet.Channel == null)
			criteria.add(new SqlCriteria("st.StoreChannel", 0));
		else
			criteria.add(new SqlCriteria("st.StoreChannel", _outlet.Channel.Id));
		if (_initiative == null)
			criteria.add(new SqlCriteria("st.Initiative", 0));
		else
			criteria.add(new SqlCriteria("st.Initiative", _initiative.Id));

		return super.Select(criteria);
	}

	public float execute() {
		float result = 0;
		if (this.Select()) {
			if (this.Next()) {
				result = this.Current().Sales;
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
