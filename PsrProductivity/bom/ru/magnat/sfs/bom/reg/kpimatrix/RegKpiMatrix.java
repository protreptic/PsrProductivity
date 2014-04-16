package ru.magnat.sfs.bom.reg.kpimatrix;

import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegKpiMatrix extends RegGeneric<RegKpiMatrixEntity> {

	public RegKpiMatrix(Context context) {
		super(context, RegKpiMatrixEntity.class);

	}

	@Override
	public Boolean Select(Object[] dimensions) {
		
		return null;
	}

	@Override
	public Boolean Find(RegKpiMatrixEntity entity) {
		
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
