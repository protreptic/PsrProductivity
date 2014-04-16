package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.OrmOwner;
import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@OrmOwner(owner = RefPromoEntity.class)
public class RefPromoDetails extends RefGeneric<RefPromoDetailsEntity> {

	public RefPromoDetails(Context context) {
		super(context, RefPromoDetailsEntity.class);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
