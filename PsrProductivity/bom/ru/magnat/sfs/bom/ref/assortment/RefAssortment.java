package ru.magnat.sfs.bom.ref.assortment;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class RefAssortment extends RefGeneric<RefAssortmentEntity> {

	public RefAssortment(Context context)  {
		super(context, RefAssortmentEntity.class);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
