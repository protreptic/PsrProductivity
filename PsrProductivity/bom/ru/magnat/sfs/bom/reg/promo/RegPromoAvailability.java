package ru.magnat.sfs.bom.reg.promo;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public class RegPromoAvailability extends RegGeneric {

	@SuppressWarnings("unchecked")
	public RegPromoAvailability(Context context) {
		super(context, RegPromoAvailabilityEntity.class);
	}

	@Override
	public Boolean Select(Object[] dimensions) {
		return null;
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
