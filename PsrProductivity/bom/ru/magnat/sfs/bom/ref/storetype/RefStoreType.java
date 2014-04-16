package ru.magnat.sfs.bom.ref.storetype;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefStoreType extends RefGeneric<RefStoreTypeEntity> {

	public RefStoreType(Context context) {
		super(context, RefStoreTypeEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
