package ru.magnat.sfs.bom.query.debts;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetDebts extends QueryGeneric<QueryGetDebtsEntity> {

	static final String query = "SELECT c.Id as Id" +
			", c.Descr as ContractorDescr" +
			", c.Id as ContractorId" +
			", r.CollectDebt" +
			", r.Debt" +
			", r.DocSum" +
			" from RegBalance r " +
			" inner join RefContractor c " +
			" on r.Contractor=c.Id";

	public QueryGetDebts(Context context) {
		super(context, QueryGetDebtsEntity.class, query);

	}

	public Boolean SelectTotals() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("COALESCE(r.Contractor,0)", 0, "<>"));
		criteria.add(new SqlCriteria("COALESCE(r.PaymentType,0)", 0));
		criteria.add(new SqlCriteria("COALESCE(r.DocSum,0)", 0, ">"));
		return super.Select(criteria, "ORDER BY c.Descr ASC");
	}
	public Boolean SelectTotals(long contractorId) {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("COALESCE(r.Contractor,0)", contractorId, "="));
		criteria.add(new SqlCriteria("COALESCE(r.PaymentType,0)", 0));
		criteria.add(new SqlCriteria("COALESCE(r.DocSum,0)", 0, ">"));
		return super.Select(criteria, "ORDER BY c.Descr ASC");
	}
	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
