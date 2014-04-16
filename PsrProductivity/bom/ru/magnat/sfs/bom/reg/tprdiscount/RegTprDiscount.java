package ru.magnat.sfs.bom.reg.tprdiscount;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegTprDiscount extends RegGeneric<RegTprDiscountEntity> {

	public RegTprDiscount(Context context) {
		super(context, RegTprDiscountEntity.class);

	}

	

	public Boolean Select(long ISISCode) {
		return Select(new Object[] { ISISCode, null, null });
	}
	public Boolean Select(long ISISCode,RefProductItemEntity product) {
		return Select(new Object[] { ISISCode, product, null });
	}
	public Boolean Select(long ISISCode,RefCskuEntity csku) {
		return Select(new Object[] { ISISCode, null, csku });
	}
	@Override
	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 3)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("ISISCode",
					(Long) dimensions[0]));
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("ProductItem",
					((GenericEntity<?>) dimensions[1]).Id));
		if (dimensions[2] != null)
			criteria.add(new SqlCriteria("Csku",
					((GenericEntity<?>) dimensions[2]).Id));
		
		
			
		
		

		return super.Select(criteria, "ORDER BY DiscountValue DESC");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

	@Override
	public Boolean Find(RegTprDiscountEntity entity) {
		if (entity==null) return false;
		return (FindById(entity.Id) != null);
		
	}

}
