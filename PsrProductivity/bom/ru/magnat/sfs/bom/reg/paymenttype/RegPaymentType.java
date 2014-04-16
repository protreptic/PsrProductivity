package ru.magnat.sfs.bom.reg.paymenttype;

import java.util.ArrayList;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegPaymentType extends RegGeneric {

	public RegPaymentType(Context context) {
		super(context, RegPaymentTypeEntity.class);

	}

	public RegPaymentTypeEntity Current() {
		return (RegPaymentTypeEntity) super.Current();
	}

	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 3)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("Contractor",
					((GenericEntity) dimensions[0]).Id));
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("Employee",
					((GenericEntity) dimensions[1]).Id));
		if (dimensions[2] != null)
			criteria.add(new SqlCriteria("PaymentType",
					((GenericEntity) dimensions[2]).Id));

		return super.Select(criteria);
	}

	@Override
	public Boolean Find(GenericEntity entity) {
		
		return null;
	}

	@Override
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return new RegPaymentTypeSimpleItemListView(_context, this, lv);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
