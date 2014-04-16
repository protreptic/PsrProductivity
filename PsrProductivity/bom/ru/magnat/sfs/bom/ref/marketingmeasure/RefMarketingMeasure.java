package ru.magnat.sfs.bom.ref.marketingmeasure;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefMarketingMeasure extends RefGeneric {

	public RefMarketingMeasure(Context context) {
		super(context, RefMarketingMeasureEntity.class);
		
	}

	public RefMarketingMeasureEntity Current() {
		return (RefMarketingMeasureEntity) super.Current();
	}

	@Override
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return new RefMarketingMeasureSimpleItemListView(_context, this, lv);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
