package ru.magnat.sfs.bom.query.targets;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetDayTargets extends QueryGeneric<QueryGetDayTargetsEntity> {

	private static final String query = 
		"select " +
			" r.Id, " +
			" c.KpiKind, " +
			" r.Target " + 
		" from " + 
			" RegKpiMatrix r " + 
				" inner join RefKpi c on " +
					" r.Kpi = c.Id ";

	private final Long _outlet; 
	
	public QueryGetDayTargets(Context context, final Long outlet) {
		super(context, QueryGetDayTargetsEntity.class, query);
		this._outlet = outlet;
	}
	
	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("coalesce(r.Initiative, 0)", 0));
		criteria.add(new SqlCriteria("datediff(month, r.Period, now())", 0));
		criteria.add(new SqlCriteria("r.Horizon", 0));
		criteria.add(new SqlCriteria("r.Outlet", this._outlet));
		return super.Select(criteria);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
