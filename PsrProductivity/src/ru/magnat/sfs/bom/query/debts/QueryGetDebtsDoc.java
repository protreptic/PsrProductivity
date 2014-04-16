package ru.magnat.sfs.bom.query.debts;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetDebtsDoc extends QueryGeneric<QueryGetDebtsDocEntity> {

	static final String query = "SELECT r.Id" +
			", r.CreditInfo" +
			", COALESCE(pt.Descr,'') as PaymentTypeDescr " +
			", COALESCE(r.Debt,0) as Debt " +
			", COALESCE(r.DocSum,0) as DocSum " +
			", r.PaymentData" +
			", r.DocDate" +
			" from RegBalance r " +
			" left join RefPaymentType pt " +
			" on r.PaymentType=pt.Id";

	public QueryGetDebtsDoc(Context context) {
		super(context, QueryGetDebtsDocEntity.class, query);

	}

	public Boolean SelectDocs(long contractorId) {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		criteria.add(new SqlCriteria("COALESCE(Contractor,0)", contractorId));
		criteria.add(new SqlCriteria("COALESCE(PaymentType,0)", 0, "<>"));
		criteria.add(new SqlCriteria("COALESCE(DocSum,0)", 0, "<>"));

		return super.Select(criteria, "ORDER BY PaymentData");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
