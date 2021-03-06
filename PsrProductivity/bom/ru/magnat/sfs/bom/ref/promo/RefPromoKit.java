package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.OrmOwner;
import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@OrmOwner(owner = RefPromoDetailsEntity.class)
public class RefPromoKit extends RefGeneric<RefPromoKitEntity> {

	public RefPromoKit(Context context) {
		super(context, RefPromoKitEntity.class);
	}

	public RefPromoKitEntity Current() {
		return (RefPromoKitEntity) super.Current();
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
