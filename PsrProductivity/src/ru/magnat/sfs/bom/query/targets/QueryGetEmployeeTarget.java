package ru.magnat.sfs.bom.query.targets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.report.target.InitiativeTargetListViewItem;
import ru.magnat.sfs.ui.android.report.target.SalaryListViewItem;
import ru.magnat.sfs.ui.android.report.target.TargetListViewItem;
import android.content.Context;
import android.widget.ListView;

import com.ianywhere.ultralitejni12.ULjException;

public final class QueryGetEmployeeTarget extends QueryGetTarget {

	static final String query = "select 1 as Id"
			+ " ,k.DESCR+' '+COALESCE(i.Descr,'') as Descr" 
			+ " ,r.Kpi"
			+ " ,r.KpiMatrix" + " ,COALESCE(r.Target,0) as Target "
			+ " ,COALESCE(r.Fact,0) as Fact "
			+ " ,(COALESCE(r.Target,0) - COALESCE(r.Fact,0)) as GAP "
			+ " ,(CASE WHEN COALESCE(r.Target,0) = 0 THEN 0 ELSE r.KpiIndex*0.01 END) as TargetIndex " + " , k.KpiKind as KpiKind "
			+ " ,COALESCE(r.KpiShare,0) as KpiShare "
			+ " ,r.Initiative as Initiative"
			+ " from RegKpiMatrix r "
			+ " inner join RefKpi k on r.KPI=k.Id"
			+ " left join RefInitiative i on r.Initiative = i.Id "
			+ " where r.KpiMatrix=? " + " and r.Horizon = ?"
			+ " and r.Period = ? " + " and r.Employee = ? "
			+ " and COALESCE(r.KpiShare,0)>?" + " order by r.Kpi, i.Descr";;
	final private Date _day;
	final private int _horizon;
	private Mode _mode;

	public QueryGetEmployeeTarget(Context context, Date day, int horizon) {
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

		_mode = Mode.Target;
		_horizon = horizon;
		if (_prepared) {
			setSelectParameters(null);
		}
	}

	public void setMode(Mode mode) {
		_mode = mode;
	}

	public enum Mode {
		Target, Salary
	}

	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		try {
			if (_cursor != null)
				_cursor.close();
		} catch (ULjException e) {

		}
		try {

			RefEmployeeEntity employee = Globals.getEmployee();
			if (employee == null) {
				_select_statement.setNull(1);
				_select_statement.setNull(4);
			} else {
				_select_statement.set(4, employee.Id);
				if (employee.GoalMatrix == null) {
					_select_statement.setNull(1);
				} else {
					_select_statement.set(1, employee.GoalMatrix.Id);
				}

			}
			_select_statement.set(2, (_horizon == Calendar.DAY_OF_MONTH) ? 0
					: 1);
			_select_statement.set(3, _day);
			_select_statement.set(5, ((_mode == Mode.Salary) ? 0 : -1));

		} catch (ULjException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		if (_mode == Mode.Salary) {
			return new SalaryListViewItem(_context, this, lv, this.Current());
		}
		if (this.Current()==null)
			return new TargetListViewItem(_context, this, lv, this.Current());
		else if (this.Current().Initiative==null)
			return new TargetListViewItem(_context, this, lv, this.Current());
		return new InitiativeTargetListViewItem(_context, this, lv, this.Current());
	}

}
