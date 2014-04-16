package ru.magnat.sfs.bom.reg.welcomeoffersubmitted;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegWelcomeOfferSubmitted extends RegGeneric<RegWelcomeOfferSubmittedEntity> {

	public RegWelcomeOfferSubmitted(Context context) {
		super(context, RegWelcomeOfferSubmittedEntity.class);

	}
	
	public Boolean Select(RefOutletEntity outlet) {
		return Select(new Object[] {outlet});
	}

	@Override
	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 1)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		
		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("Outlet",
					((GenericEntity<?>) dimensions[0]).Id));
		return super.Select(criteria, "ORDER BY Document");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

	@Override
	public Boolean Find(RegWelcomeOfferSubmittedEntity entity) {
		
		return null;
	}

}
