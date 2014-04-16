package ru.magnat.sfs.bom.reg.traderule;

import java.util.ArrayList;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public class RegTradeRule extends RegGeneric {

	@SuppressWarnings("unchecked")
	public RegTradeRule(Context context) {
		super(context, RegTradeRuleEntity.class);

	}

	public RegTradeRuleEntity Current() {
		return (RegTradeRuleEntity) super.Current();
	}

	@SuppressWarnings("unchecked")
	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 3)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("Customer",
					((GenericEntity) dimensions[0]).Id));
		else {
			criteria.add(new SqlCriteria("COALESCE(Customer, -1)", -1));
		}
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("Employee",
					((GenericEntity) dimensions[1]).Id));
		if (dimensions[2] != null)
			criteria.add(new SqlCriteria("TradeRule",
					((GenericEntity) dimensions[2]).Id));
		criteria.add(new SqlCriteria("IsActive", 1));
		return super.Select(criteria);
	}

	@Override
	public Boolean Find(GenericEntity entity) {
		return null;
	}

	@Override
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return new RegTradeRuleSimpleItemListView(_context, this, lv);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
