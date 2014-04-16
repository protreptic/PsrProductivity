package ru.magnat.sfs.bom.reg.marketingmeasure;

import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasureEntity;
import ru.magnat.sfs.bom.ref.marketingmeasureobject.RefMarketingMeasureObjectEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegMarketingMeasure extends RegGeneric {

	public RegMarketingMeasure(Context context) {
		super(context, RegMarketingMeasureEntity.class);

	}

	public RegMarketingMeasureEntity Current() {
		return (RegMarketingMeasureEntity) super.Current();
	}

	@Override
	public Boolean Select(Object[] dimensions) {
		
		return null;
	}

	@Override
	public Boolean Find(GenericEntity entity) {
		
		return null;
	}

	public Boolean SelectLast(Date date, RefEmployeeEntity employee,
			RefOutletEntity outlet, RefMarketingMeasureEntity marketingMeasure,
			RefMarketingMeasureObjectEntity marketingMeasureObject) {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Period", date, "<="));
		if (employee != null)
			criteria.add(new SqlCriteria("Employee", employee));
		if (outlet != null)
			criteria.add(new SqlCriteria("Outlet", outlet));
		if (marketingMeasure != null)
			criteria.add(new SqlCriteria("MarketingMeasure", marketingMeasure));
		if (marketingMeasureObject != null)
			criteria.add(new SqlCriteria("MarketingMeasureObject",
					marketingMeasureObject));
		return this.Select(criteria, "ORDER BY Period DESC");

	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
