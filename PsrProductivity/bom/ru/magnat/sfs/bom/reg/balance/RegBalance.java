package ru.magnat.sfs.bom.reg.balance;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class RegBalance extends RegGeneric<RegBalanceEntity> {

	public RegBalance(Context context) {
		super(context, RegBalanceEntity.class);

	}

	@Override
	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 3)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("Contractor",
					((GenericEntity<?>) dimensions[0]).Id));
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("Employee",
					((GenericEntity<?>) dimensions[1]).Id));
		if (dimensions[2] != null)
			criteria.add(new SqlCriteria("PaymentType",
					((GenericEntity<?>) dimensions[2]).Id));
		return super.Select(criteria, "ORDER BY PaymentData");
	}

	public Boolean SelectTotals() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		criteria.add(new SqlCriteria("COALESCE(Contractor,0)", 0, "<>"));
		criteria.add(new SqlCriteria("COALESCE(PaymentType,0)", 0));
		criteria.add(new SqlCriteria("COALESCE(DocSum,0)", 0, ">"));

		return super.Select(criteria, "ORDER BY Contractor");
	}

	public Boolean SelectTotals(RefContractorEntity entity) {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		criteria.add(new SqlCriteria("COALESCE(Contractor,0)", entity.Id));
		criteria.add(new SqlCriteria("COALESCE(PaymentType,0)", 0));
		criteria.add(new SqlCriteria("COALESCE(DocSum,0)", 0, ">"));

		return super.Select(criteria, "ORDER BY Contractor");
	}

	public Boolean SelectDocs(RefContractorEntity contractor) {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		criteria.add(new SqlCriteria("COALESCE(Contractor,0)", contractor.Id));
		criteria.add(new SqlCriteria("COALESCE(PaymentType,0)", 0, "<>"));
		criteria.add(new SqlCriteria("COALESCE(DocSum,0)", 0, "<>"));

		return super.Select(criteria, "ORDER BY PaymentData");
	}

	@Override
	public Boolean Find(RegBalanceEntity entity) {
		
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		if (this.Current() == null)
			return null;
		RegBalanceEntity entity = this.Current();
		if (entity.Employee == null)
			return null;
//		if (entity.Contractor == null)
//			return new BalanceEmployeeListItemView(getContext(), entity);

		return null;
	}

}
