package ru.magnat.sfs.bom.query.targets;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.kpimatrix.RefKpiMatrixEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

import com.ianywhere.ultralitejni12.ULjException;

public final class QueryGetKpiByMatrix extends
		QueryGeneric<QueryGetKpiByMatrixEntity> {

	static final String query = "select distinct" + " k.Id " + " ,r.Kpi"
			+ " from RegKpiMatrix r " + " inner join RefKpi k on r.KPI=k.Id"
			+ " where r.KpiMatrix=? ";;
	final RefKpiMatrixEntity _matrix;
	Boolean _prepared = false;

	public QueryGetKpiByMatrix(Context context, RefKpiMatrixEntity matrix) {
		super(context, QueryGetKpiByMatrixEntity.class, query);
		_matrix = matrix;
		if (_prepared)
			setSelectParameters(null);

	}

	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		try {
			if (_cursor != null)
				_cursor.close();
		} catch (ULjException e) {

		}
		try {
			if (_matrix == null) {
				_select_statement.setNull(1);

			} else {
				_select_statement.set(1, _matrix.Id);
			}

		} catch (ULjException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
