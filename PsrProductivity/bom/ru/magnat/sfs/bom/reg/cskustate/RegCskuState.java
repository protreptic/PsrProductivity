package ru.magnat.sfs.bom.reg.cskustate;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannelEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

;

public class RegCskuState extends RegGeneric<RegCskuStateEntity> {

	public RegCskuState(Context context) {
		super(context, RegCskuStateEntity.class);

	}

	public Boolean Select(RefStoreChannelEntity channel,RefCskuEntity csku) {
		return Select(new Object[] { channel, csku });
	}
	@Override
	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 2)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		
		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("StoreChannel",
					((GenericEntity<?>) dimensions[0]).Id));
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("Csku",
					((GenericEntity<?>) dimensions[1]).Id));
		
		return super.Select(criteria);
	}

	@Override
	public Boolean Find(RegCskuStateEntity entity) {
		
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
