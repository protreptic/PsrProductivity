package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.kpi.RefKpiEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetCumulativeOutletTarget extends
		QueryGeneric<QueryGetCumulativeOutletTargetEntity> {

	final Date _day;
	final RefOutletEntity _outlet;
	final int _horizon;
	final RefKpiEntity _kpi;

	static final String query = "select " 
			+ "1 as Id "
			+ ",sum(fact) as KpiFact " 
			+ ",sum(target) as KpiTarget "
			+ ", avg(coalesce(fact/nullif(target,0),1)*100) as KpiIndex "
			+ "from RegKpiMatrix ";

	public QueryGetCumulativeOutletTarget(Context context, Date date,
			RefOutletEntity outlet, int horizon, RefKpiEntity kpi) {
		super(context, QueryGetCumulativeOutletTargetEntity.class, query);
		_day = date;
		_outlet = outlet;
		if (horizon == Calendar.MONTH)
			_horizon = 1;
		else
			_horizon = 0;
		_kpi = kpi;
	}

	public Boolean Select() {
		if (_outlet.GoalMatrix == null)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Period", _day));
		criteria.add(new SqlCriteria("coalesce(Initiative,0)", 0, "<>"));
		criteria.add(new SqlCriteria("Employee",
				Globals.getEmployee().Id));
		criteria.add(new SqlCriteria("Outlet", _outlet.Id));
		criteria.add(new SqlCriteria("Horizon", _horizon));
		criteria.add(new SqlCriteria("KpiMatrix", _outlet.GoalMatrix.Id));
		criteria.add(new SqlCriteria("Kpi", _kpi.Id));

		return super.Select(criteria);
	}

	public float[] execute() {
		float result[] = new float[] { 0, 0, 0 };
		if (this.Select()) {
			if (this.Next()) {
				result[0] =  this.Current().KpiFact;
				result[1] =  this.Current().KpiTarget;
				result[2] =  this.Current().KpiIndex;
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
