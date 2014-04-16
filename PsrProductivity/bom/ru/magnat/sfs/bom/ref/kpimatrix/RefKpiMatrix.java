package ru.magnat.sfs.bom.ref.kpimatrix;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefKpiMatrix extends RefGeneric<RefKpiMatrixEntity> {

	public RefKpiMatrix(Context context) {
		super(context, RefKpiMatrixEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
