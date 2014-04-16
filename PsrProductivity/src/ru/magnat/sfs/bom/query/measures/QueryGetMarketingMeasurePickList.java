package ru.magnat.sfs.bom.query.measures;

import java.util.ArrayList;

import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureEntity;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.marketingmeasure.DocMarketingMeasurePickListItemView;
import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public final class QueryGetMarketingMeasurePickList extends QueryGeneric {

	final DocMarketingMeasureEntity _doc;
	final RefEmployeeEntity _employee;
	static final String query = "SELECT ra.Id as Id"
			+ ", i.id as marketingMeasureObjectId"
			+ ", i.Descr as marketingMeasureObjectDescr" 
			+ " FROM RegMarketingMeasure ra "
			+ " INNER JOIN RefMarketingMeasureObject i on ra.MarketingMeasureObject=i.Id ";
	static final String _EMPLOYEE_FIELD = "ra.Employee";
	static final String _OUTLET_FIELD = "ra.Outlet";
	static final String _MARKETING_MEASURE_FIELD = "ra.MarketingMeasure";
	static final String _orderFactor = "ORDER BY i.OrderKey";

	@SuppressWarnings("unchecked")
	public QueryGetMarketingMeasurePickList(Context context, DocMarketingMeasureEntity entity, RefEmployeeEntity employee) {
		super(context, QueryGetMarketingMeasurePickListEntity.class, query);
		
		_doc = entity;
		_employee = employee;
	}

	public QueryGetMarketingMeasurePickListEntity Current() {
		return (QueryGetMarketingMeasurePickListEntity) super.Current();
	}

	@SuppressWarnings("unchecked")
	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		if (_doc.MarketingMeasure != null)
			criteria.add(new SqlCriteria(_MARKETING_MEASURE_FIELD,
					_doc.MarketingMeasure.Id));
		RefEmployeeEntity employee = Globals.getEmployee();
		if (employee != null)
			criteria.add(new SqlCriteria(_EMPLOYEE_FIELD, employee.Id));
		if (_doc.Outlet != null)
			criteria.add(new SqlCriteria(_OUTLET_FIELD, _doc.Outlet.Id));
		return super.Select(criteria, _orderFactor);
	}
	
	@Override
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return new DocMarketingMeasurePickListItemView(_context, this, lv, _doc);
	}
	
	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
