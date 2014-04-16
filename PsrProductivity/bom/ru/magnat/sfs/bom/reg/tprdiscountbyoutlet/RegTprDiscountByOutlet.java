package ru.magnat.sfs.bom.reg.tprdiscountbyoutlet;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegTprDiscountByOutlet extends RegGeneric<RegTprDiscountByOutletEntity> {

	public RegTprDiscountByOutlet(Context context) {
		super(context, RegTprDiscountByOutletEntity.class);

	}

	

	public Boolean SelectAllByOutlet(RefOutletEntity outlet) {
		return Select(new Object[] { outlet, null, null, null, null });
		
	}
	public Boolean SelectExpiringByOutlet(RefOutletEntity outlet) {
		return Select(new Object[] { outlet, null, 28, null, null });
	}
	public Boolean SelectNewByOutlet(RefOutletEntity outlet) {
		return Select(new Object[] { outlet, 28, null, null, null });
	}
	@Override
	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 5)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("Outlet",
					((GenericEntity<?>) dimensions[0]).Id));
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("datediff(day,BeginOfAction,now()",
					(Integer)dimensions[1],"<="));
		if (dimensions[2] != null)
			criteria.add(new SqlCriteria("datediff(day,now(),EndOfAction)",
					(Integer)dimensions[2],"<="));
		if (dimensions[3] != null)
			criteria.add(new SqlCriteria("Csku",
					((GenericEntity<?>) dimensions[3]).Id));
		if (dimensions[4] != null)
			criteria.add(new SqlCriteria("ProductItem",
					((GenericEntity<?>) dimensions[4]).Id));
		
		
			
		
		

		return super.Select(criteria, "ORDER BY DiscountValue DESC");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return new RegTprDiscountByOutletExtendedListItemView(_context,this,lv);
	}

	@Override
	public Boolean Find(RegTprDiscountByOutletEntity entity) {
		if (entity==null) return false;
		return (FindById(entity.Id) != null);
		
	}

}
