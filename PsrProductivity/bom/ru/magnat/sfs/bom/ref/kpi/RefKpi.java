package ru.magnat.sfs.bom.ref.kpi;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefKpi extends RefGeneric<RefKpiEntity> {

	public RefKpi(Context context) {
		super(context, RefKpiEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
