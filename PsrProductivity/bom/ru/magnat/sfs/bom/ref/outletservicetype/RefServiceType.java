package ru.magnat.sfs.bom.ref.outletservicetype;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefServiceType extends RefGeneric<RefServiceTypeEntity> {

	public RefServiceType(Context context) {
		super(context, RefServiceTypeEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
