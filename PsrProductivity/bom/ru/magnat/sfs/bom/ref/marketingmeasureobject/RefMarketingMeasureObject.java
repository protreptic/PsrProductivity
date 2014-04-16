package ru.magnat.sfs.bom.ref.marketingmeasureobject;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefMarketingMeasureObject extends RefGeneric<RefMarketingMeasureObjectEntity> {

	public RefMarketingMeasureObject(Context context) {
		super(context, RefMarketingMeasureObjectEntity.class);
		
	}

	public RefMarketingMeasureObjectEntity Current() {
		return (RefMarketingMeasureObjectEntity) super.Current();
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
