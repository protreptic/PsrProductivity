package ru.magnat.sfs.bom.reg.goldendiscount;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SfsEnum;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.discount.RefDiscountEntity;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceTypeEntity;
import ru.magnat.sfs.bom.ref.storechannel.GoldenProgramType;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegGoldenDiscount extends RegGeneric<RegGoldenDiscountEntity> {

	public RegGoldenDiscount(Context context) {
		super(context, RegGoldenDiscountEntity.class);

	}

	

	public Boolean Select(RefDiscountEntity discount, RefServiceTypeEntity serviceType, GoldenProgramType goldenProgramType) {
		return Select(new Object[] { discount, serviceType, goldenProgramType });
	}

	@Override
	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 3)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("Discount",
					((GenericEntity<?>) dimensions[0]).Id));
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("ServiceType",
					((GenericEntity<?>) dimensions[1]).Id));
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("GoldenProgramType",
					((SfsEnum) dimensions[2]).getId()));
		

		return super.Select(criteria, "ORDER BY DiscountValue DESC");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

	@Override
	public Boolean Find(RegGoldenDiscountEntity entity) {
		
		return null;
	}

}
