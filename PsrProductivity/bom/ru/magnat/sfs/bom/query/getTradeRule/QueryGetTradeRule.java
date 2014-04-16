package ru.magnat.sfs.bom.query.getTradeRule;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetTradeRule extends
		QueryGeneric<QueryGetTradeRuleEntity> {

	final RefCustomerEntity _customer;
	final RefEmployeeEntity _employee;
	static final String query = "SELECT Id, TradeRule from RegTradeRule";

	public QueryGetTradeRule(Context context, RefCustomerEntity customer,
			RefEmployeeEntity employee) {
		super(context, QueryGetTradeRuleEntity.class, query);
		_customer = customer;
		_employee = employee;
	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (_customer != null)
			criteria.add(new SqlCriteria("Customer", _customer.Id));
		else
			criteria.add(new SqlCriteria("COALESCE(Customer,0)", 0));
		if (_employee != null)
			criteria.add(new SqlCriteria("Employee", _employee.Id));
		return super.Select(criteria);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
