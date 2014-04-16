package ru.magnat.sfs.bom.ref.warehouse;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefWarehouse extends RefGeneric {

	public RefWarehouse(Context context) {
		super(context, RefWarehouseEntity.class);
		
	}

	public RefWarehouseEntity Current() {
		return (RefWarehouseEntity) super.Current();
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
