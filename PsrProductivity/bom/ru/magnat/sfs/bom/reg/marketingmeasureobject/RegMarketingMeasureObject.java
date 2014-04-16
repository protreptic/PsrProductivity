package ru.magnat.sfs.bom.reg.marketingmeasureobject;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegMarketingMeasureObject extends RegGeneric {

	public RegMarketingMeasureObject(Context context) {
		super(context, RegMarketingMeasureObjectEntity.class);

	}

	public RegMarketingMeasureObjectEntity Current() {
		return (RegMarketingMeasureObjectEntity) super.Current();
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
