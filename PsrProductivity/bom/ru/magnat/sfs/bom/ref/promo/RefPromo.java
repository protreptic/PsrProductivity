package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RefPromo extends RefGeneric<RefPromoEntity> {

	public RefPromo(Context context) {
		super(context, RefPromoEntity.class);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}
}
