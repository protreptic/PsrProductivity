package ru.magnat.sfs.bom.ref.gcasstate;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefGcasState extends RefGeneric {

	public RefGcasState(Context context) {
		super(context, RefGcasStateEntity.class);
		
	}

	public RefGcasStateEntity Current() {
		return (RefGcasStateEntity) super.Current();
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
