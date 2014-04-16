package ru.magnat.sfs.bom.ref.brand;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefBrand extends RefGeneric<RefBrandEntity> {

	public RefBrand(Context context) {
		super(context, RefBrandEntity.class);

	}

	@Override
	public boolean hasChild(int position) {
		return false;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
