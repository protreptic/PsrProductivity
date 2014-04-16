package ru.magnat.sfs.bom.query.getSalesOfDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.initiative.RefInitiativeEntity;
import ru.magnat.sfs.bom.ref.kpi.RefKpiEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetInitiativeTurnover extends
		QueryGeneric<QueryGetInitiativeTurnoverEntity> {

	final Date _period;
	final int _horizon;
	final RefKpiEntity _kpi;
	final RefInitiativeEntity _initiative;
	static final String query = "SELECT 1 as Id"
			+ " ,coalesce(sum(Fact-FactCIS),0) as KpiFact "
			+ " FROM RegKpiMatrix ";

	public QueryGetInitiativeTurnover(Context context, Date date, int horizon,
			RefKpiEntity kpi, RefInitiativeEntity initiative) {
		super(context, QueryGetInitiativeTurnoverEntity.class, query);
		if (horizon == Calendar.MONTH) {
			_period = Utils.FirstDateOfMonth(date);
			_horizon = 1;
		} else {
			_period = Utils.RoundDate(date);
			_horizon = 0;
		}
		_kpi = kpi;
		_initiative = initiative;

	}

	public Boolean Select() {
		if (Globals.getEmployee().GoalMatrix == null)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("KpiMatrix",
				Globals.getEmployee().GoalMatrix.Id, "<>")); // конечно
																		// тут
																		// должен
																		// стоять
																		// код
																		// матрицы
																		// торговой
																		// точки,
																		// пока
																		// проверяем
																		// что
																		// матрица
																		// не
																		// равна
																		// матрице
																		// агента
		criteria.add(new SqlCriteria("Kpi", _kpi.Id));
		criteria.add(new SqlCriteria("Horizon", _horizon));
		criteria.add(new SqlCriteria("Period", _period));
		criteria.add(new SqlCriteria("Initiative", _initiative.Id));
		return super.Select(criteria);
	}

	public float execute() {
		float result = 0;
		if (this.Select()) {
			if (this.Next()) {

				result = this.Current().KpiFact;
				// result[1] = this.Current().KpiTarget;
				// result[2] = this.Current().KpiIndex;
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
