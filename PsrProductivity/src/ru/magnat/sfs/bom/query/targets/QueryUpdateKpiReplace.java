package ru.magnat.sfs.bom.query.targets;

import java.util.Calendar;
import java.util.Date;

import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.ref.initiative.RefInitiativeEntity;
import ru.magnat.sfs.bom.ref.kpi.RefKpiEntity;
import ru.magnat.sfs.bom.ref.kpimatrix.RefKpiMatrixEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;

import ru.magnat.sfs.android.Log;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;

public final class QueryUpdateKpiReplace {

	final float _amount;
	boolean _prepared = false;
	final Connection _connection;
	final RefKpiMatrixEntity _matrix;
	final RefKpiEntity _kpi;
	final int _horizon;
	final Date _period;
	final RefOutletEntity _outlet;
	final RefInitiativeEntity _initiative;
	final Float _index;
	static final String query = "update RegKpiMatrix "
			+ "set Fact = ? "
			+ " ,KpiIndex = coalesce(?, ? / nullif(Target,0)*100,0) "
			+ " where KpiMatrix=? " + "and Kpi=? " + "and Horizon=? "
			+ " and COALESCE(Outlet,0)=COALESCE(cast(? as int),0) " 
			+ " and COALESCE(Initiative,0)=COALESCE(?,0) "
			+ " and datediff(day,Period,?)=0";

	public QueryUpdateKpiReplace(Connection connection,
			float amount, RefKpiMatrixEntity matrix, RefKpiEntity kpi,int horizon, RefOutletEntity outlet, RefInitiativeEntity initiative, Float index) {
		_connection = connection;
		_amount = amount;
		_matrix = matrix;
		_kpi = kpi;
		_outlet = outlet;
		_initiative = initiative;
		_index = index;
		switch (horizon){
			case Calendar.DAY_OF_MONTH:
				_horizon = 0;
				_period = Utils.RoundDate(new Date(System.currentTimeMillis()));
				break;
			case Calendar.MONTH:
				_period = Utils.FirstDateOfMonth(new Date(System.currentTimeMillis()));
				_horizon = 1;
				break;
			default:
				_horizon = -1;
				_period = Utils.RoundDate(new Date(System.currentTimeMillis()));
		}

	}
	public QueryUpdateKpiReplace(Connection connection,
			float amount, RefKpiMatrixEntity matrix, RefKpiEntity kpi,int horizon) {
		this(connection,amount,matrix,kpi,horizon,null,null,null);
	}
	public QueryUpdateKpiReplace(Connection connection,
			float amount, RefKpiMatrixEntity matrix, RefKpiEntity kpi,int horizon,Float index) {
		this(connection,amount,matrix,kpi,horizon,null,null,index);
	}
	public QueryUpdateKpiReplace(Connection connection,
			float amount, RefKpiMatrixEntity matrix, RefKpiEntity kpi,int horizon, RefOutletEntity outlet) {
		this(connection,amount,matrix,kpi,horizon,outlet,null,null);
	}
	public QueryUpdateKpiReplace(Connection connection,
			float amount, RefKpiMatrixEntity matrix, RefKpiEntity kpi,int horizon, RefOutletEntity outlet,RefInitiativeEntity initiative) {
		this(connection,amount,matrix,kpi,horizon,outlet,initiative,null);
	}
	public Boolean execute() {
		boolean result = false;
		try {
			PreparedStatement ps = _connection.prepareStatement(query);
			int param = 0;
			ps.set(++param, _amount);
			
			if (_index == null)
				ps.setNull(++param);
			else
				ps.set(++param, _index);

			
			ps.set(++param, _amount);
			
			if (_matrix == null)
				ps.setNull(++param);
			else
				ps.set(++param, _matrix.Id);

			if (_kpi == null)
				ps.setNull(++param);
			else
				ps.set(++param, _kpi.Id);
			
			ps.set(++param, _horizon);
			
			if (_outlet == null)
				ps.setNull(++param);
			else
				ps.set(++param, _outlet.Id);
			
			if (_initiative == null)
				ps.setNull(++param);
			else
				ps.set(++param, _initiative.Id);
			ps.set(++param, _period);
			result = ps.execute();
			ps.close();
		} catch (Exception e) {
			Log.v(MainActivity.LOG_TAG, "Ошибка обновления целей " + _kpi.Descr +": "+e.getMessage());
		}
		return result;
	}

}
