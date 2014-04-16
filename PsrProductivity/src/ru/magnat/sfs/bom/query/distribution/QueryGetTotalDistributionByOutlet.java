package ru.magnat.sfs.bom.query.distribution;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetTotalDistributionByOutlet extends
		QueryGeneric<QueryGetDistributionByOutletEntity> {

	final RefOutletEntity _outlet;
	final RefEmployeeEntity _employee;
	static final String query = "select Max(Outlet) as Id,  count(distinct Csku) as Distribution from regsales";

	public QueryGetTotalDistributionByOutlet(Context context,
			RefOutletEntity outlet, RefEmployeeEntity employee) {
		super(context, QueryGetDistributionByOutletEntity.class, query);
		_outlet = outlet;
		_employee = employee;
	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (_outlet != null)
			criteria.add(new SqlCriteria("Outlet", _outlet.Id));
		if (_employee != null)
			criteria.add(new SqlCriteria("Employee", _employee.Id));
		return super.Select(criteria);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
