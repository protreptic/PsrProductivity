package ru.magnat.sfs.bom.reg.price;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegPrice extends RegGeneric {

	public RegPrice(Context context) {
		super(context, RegPriceEntity.class);
	}

	public RegPriceEntity Current() {
		return (RegPriceEntity) super.Current();
	}

	@Override
	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 3)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("Period", dimensions[0], "<"));
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("PriceType",
					((GenericEntity) dimensions[1]).Id));
		if (dimensions[2] != null)
			criteria.add(new SqlCriteria("ProductItem",
					((GenericEntity) dimensions[2]).Id));

		return super.Select(criteria, "ORDER BY Period DESC");
	}

	@Override
	public Boolean Find(GenericEntity entity) {
		
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
