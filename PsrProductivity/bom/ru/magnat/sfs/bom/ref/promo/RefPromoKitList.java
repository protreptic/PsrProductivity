package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.OrmOwner;
import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@OrmOwner(owner = RefPromoKitEntity.class)
public class RefPromoKitList extends RefGeneric<RefPromoKitListEntity> {

	public RefPromoKitList(Context context) {
		super(context, RefPromoKitListEntity.class);
	}
	
	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
