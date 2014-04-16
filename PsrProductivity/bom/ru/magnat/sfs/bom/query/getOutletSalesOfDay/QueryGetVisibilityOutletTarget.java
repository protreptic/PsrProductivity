package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.kpi.RefKpiEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

import com.ianywhere.ultralitejni12.ULjException;

public final class QueryGetVisibilityOutletTarget extends
		QueryGeneric<QueryGetVisibilityOutletTargetEntity> {

	final Date _day;
	final RefOutletEntity _outlet;
	final RefKpiEntity _kpi;
	final int _horizon;

	static final String query = "select 1 as Id, count(distinct MarketingObject) as KpiFact"
			+ ", MAX(m.Target) as KpiTarget"
			+ ", COALESCE(KpiFact/NULLIF(KpiTarget,0),0)*100 as KpiIndex "
			+ " from DocMarketingMeasureLine l "
			+ " inner join RegKpiMatrix m on m.Outlet=? and Horizon = ? and Period=? and m.Kpi=?  "
			+ " where l.MasterDocId=(select top 1 Id from DocMarketingMeasureJournal where Outlet=m.Outlet and datediff(day,CreateDate,now())<28 and IsMark=0 order by Id desc) "
			+ " and l.MasterDocAuthor = (select top 1 Author from DocMarketingMeasureJournal where Outlet=m.Outlet and datediff(day,CreateDate,now())<28 and IsMark=0 order by Id desc) "
			+ " and MarketingValue=1";

	;

	public QueryGetVisibilityOutletTarget(Context context

	, Date date, RefOutletEntity outlet, RefKpiEntity kpi, int horizon) {
		super(context, QueryGetVisibilityOutletTargetEntity.class, query);
		_day = Utils.RoundDate(date);
		_outlet = outlet;
		_horizon = (horizon == Calendar.DAY_OF_MONTH) ? 0 : 1;
		_kpi = kpi;
	}

	public Boolean Select() {
		return super.Select(null, "");
	}

	public float[] execute() {
		float result[] = new float[] { 0, 0, 0 };
		if (this.Select()) {
			if (this.Next()) {
				result = new float[] { this.Current().KpiFact,
						 this.Current().KpiTarget,
						 this.Current().KpiIndex };
			}

		}
		this.close();
		return result;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		try {
			if (_cursor != null)
				_cursor.close();
		} catch (ULjException e) {

		}
		try {
			_select_statement.set(1, _outlet.Id);
			_select_statement.set(2, _horizon);
			_select_statement.set(3, _day);
			_select_statement.set(4, _kpi.Id);

		} catch (ULjException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
