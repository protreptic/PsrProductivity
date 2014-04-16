package ru.magnat.sfs.bom.reg.kpiinterpretation;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegKpiInterpretation extends
		RegGeneric<RegKpiInterpretationEntity> {

	public RegKpiInterpretation(Context context) {
		super(context, RegKpiInterpretationEntity.class);

	}

	@Override
	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 3)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("KpiMatrix",
					((GenericEntity) dimensions[0]).Id));
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("Kpi",
					((GenericEntity) dimensions[1]).Id));
		if (dimensions[2] != null)
			criteria.add(new SqlCriteria("Border", ((Float) dimensions[2]), "<"));

		return super.Select(criteria, "ORDER BY Border DESC");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

	@Override
	public Boolean Find(RegKpiInterpretationEntity entity) {
		
		return null;
	}

}
