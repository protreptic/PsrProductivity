package ru.magnat.sfs.bom.ref.business;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefBusiness extends RefGeneric {

	public RefBusiness(Context context) {
		super(context, RefBusinessEntity.class);
		
	}

	public RefBusinessEntity Current() {

		return (RefBusinessEntity) super.Current();

	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
