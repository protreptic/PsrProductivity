package ru.magnat.sfs.bom.reg.discount;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRuleEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegDiscount extends RegGeneric<RegDiscountEntity> {

	public RegDiscount(Context context) {
		super(context, RegDiscountEntity.class);

	}

	public Boolean Select(RefTradeRuleEntity rule) {
		return Select(new Object[] { rule, Globals.getEmployee(), null });
	}

	public Boolean Select(RefTradeRuleEntity rule, RefCustomerEntity customer) {
		return Select(new Object[] { rule, Globals.getEmployee(), customer });
	}

	@Override
	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 3)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("TradeRule",
					((GenericEntity) dimensions[0]).Id));
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("Employee",
					((GenericEntity) dimensions[1]).Id));
		if (dimensions[2] != null)
			criteria.add(new SqlCriteria("Customer",
					((GenericEntity) dimensions[2]).Id));
		else
			criteria.add(new SqlCriteria("coalesce(Customer,0)", 0));

		return super.Select(criteria, "ORDER BY Discount,Border DESC");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

	@Override
	public Boolean Find(RegDiscountEntity entity) {
		
		return null;
	}

}
