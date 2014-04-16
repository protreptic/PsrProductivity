package ru.magnat.sfs.bom.ref.discountzone;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefDiscountZone extends RefGeneric<RefDiscountZoneEntity> {

	public RefDiscountZone(Context context) {
		super(context, RefDiscountZoneEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
