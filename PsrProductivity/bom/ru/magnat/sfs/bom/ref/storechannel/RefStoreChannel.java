package ru.magnat.sfs.bom.ref.storechannel;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefStoreChannel extends RefGeneric<RefStoreChannelEntity> {

	public RefStoreChannel(Context context) {
		super(context, RefStoreChannelEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
