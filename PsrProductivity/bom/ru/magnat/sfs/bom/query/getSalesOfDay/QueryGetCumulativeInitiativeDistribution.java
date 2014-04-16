package ru.magnat.sfs.bom.query.getSalesOfDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.kpi.RefKpiEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetCumulativeInitiativeDistribution extends
		QueryGeneric<QueryGetCumulativeInitiativeDistributionEntity> {

	final Date _period;
	final int _horizon;
	final RefKpiEntity _kpi;
	static final String query = "SELECT 1 as Id"
			+ ", coalesce(sum(Fact)-sum(FactCIS),0) as KpiFact "
			+ ", coalesce(sum(Target),0) as KpiTarget "
			+ ", avg(coalesce(Fact/NULLIF(Target,0),1)*100) as KpiIndex "
			+ " FROM RegKpiMatrix ";

	public QueryGetCumulativeInitiativeDistribution(Context context, Date date,
			int horizon, RefKpiEntity kpi) {
		super(context, QueryGetCumulativeInitiativeDistributionEntity.class,
				query);
		if (horizon == Calendar.MONTH) {
			_period = Utils.FirstDateOfMonth(date);
			_horizon = 1;
		} else {
			_period = Utils.RoundDate(date);
			_horizon = 0;
		}
		_kpi = kpi;

	}

	public Boolean Select() {
		if (Globals.getEmployee().GoalMatrix == null)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("KpiMatrix",
				Globals.getEmployee().GoalMatrix.Id)); // конечно тут
																// должен стоять
																// код матрицы
																// торговой
																// точки, пока
																// проверяем что
																// матрица не
																// равна матрице
																// агента
		criteria.add(new SqlCriteria("Kpi", _kpi.Id));
		criteria.add(new SqlCriteria("Horizon", _horizon));
		criteria.add(new SqlCriteria("Period", _period));
		criteria.add(new SqlCriteria("COALESCE(NULLIF(Initiative,0),0)", 0,
				"<>"));
		return super.Select(criteria);
	}

	public float[] execute() {
		float result[] = new float[] { 0, 0, 0 };
		if (this.Select()) {
			if (this.Next()) {

				result[0] = this.Current().KpiFact;
				result[1] = this.Current().KpiTarget;
				result[2] = this.Current().KpiIndex;
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
