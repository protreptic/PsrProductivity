package ru.magnat.sfs.bom.ref.pricetype;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public final class RefPriceType extends RefGeneric {

	@SuppressWarnings("unchecked")
	public RefPriceType(Context context) {
		super(context, RefPriceTypeEntity.class);
	}

	public RefPriceTypeEntity Current() {
		return (RefPriceTypeEntity) super.Current();
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
