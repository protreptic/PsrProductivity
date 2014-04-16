package ru.magnat.sfs.bom.query.targets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import android.content.Context;

import com.ianywhere.ultralitejni12.ULjException;

public final class QueryGetOutletTarget extends QueryGetTarget {
	static final String query = "select 1 as Id "
			+ " ,k.DESCR+' '+COALESCE(i.Descr,'') as Descr" + " ,r.Kpi"
			+ " ,r.KpiMatrix" + " ,COALESCE(r.Target,0) as Target "
			+ " ,COALESCE(r.Fact,0) as Fact "
			+ " ,(COALESCE(r.Target,0) - COALESCE(r.Fact,0)) as GAP "
			+ " ,r.KpiIndex*0.01 as TargetIndex "
			+ " ,COALESCE(r.KpiShare,0) as KpiShare "
			+ " ,k.KpiKind as KpiKind "
			+ " ,r.Initiative as Initiative"
			+ " from RegKpiMatrix r "
			+ " inner join RefKpi k on r.KPI=k.Id"
			+ " left join RefInitiative i on r.Initiative = i.Id "
			+ " where r.KpiMatrix=COALESCE(?,r.KpiMatrix) "
			+ " and r.Horizon = ?" + " and r.Period = ? "
			+ " and r.Employee = ? " + " and r.Outlet  = ? "
			+ " order by r.Kpi,i.Descr";
	final private Date _day;
	final private int _horizon;
	final private RefOutletEntity _outlet;

	public QueryGetOutletTarget(Context context, Date day, int horizon,
			RefOutletEntity outlet) {
		super(context, query);
		switch (horizon) {
		case Calendar.DAY_OF_MONTH:
			_day = Utils.RoundDate(day);
			break;
		case Calendar.MONTH:
			_day = Utils.FirstDateOfMonth(day);
			break;
		default:
			_day = new Date();
		}

		_horizon = horizon;
		_outlet = outlet;
	}

	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		try {
			if (_cursor != null)
				_cursor.close();
		} catch (ULjException e) {

		}
		try {
			if (_outlet == null) {
				_select_statement.setNull(1);
				_select_statement.setNull(5);
			} else {
				_select_statement.set(5, _outlet.Id);
				if (_outlet.GoalMatrix == null) {
					_select_statement.setNull(1);
				} else {
					_select_statement.set(1, _outlet.GoalMatrix.Id);
				}
			}
			RefEmployeeEntity employee = Globals.getEmployee();
			if (employee == null)
				_select_statement.setNull(1);
			else
				_select_statement.set(4, employee.Id);
			_select_statement.set(2, (_horizon == Calendar.DAY_OF_MONTH) ? 0
					: 1);
			_select_statement.set(3, _day);

		} catch (ULjException e) {
			e.getStackTrace();
			return false;
		}

		return true;
	}

}
