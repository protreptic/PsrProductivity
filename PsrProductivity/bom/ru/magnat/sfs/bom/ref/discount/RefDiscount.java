package ru.magnat.sfs.bom.ref.discount;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefDiscount extends RefGeneric<RefDiscountEntity> {

	public RefDiscount(Context context) {
		super(context, RefDiscountEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
