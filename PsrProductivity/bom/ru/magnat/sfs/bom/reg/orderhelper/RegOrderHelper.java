package ru.magnat.sfs.bom.reg.orderhelper;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegOrderHelper extends RegGeneric {

	public RegOrderHelper(Context context) {
		super(context, RegOrderHelperEntity.class);

	}

	public RegOrderHelperEntity Current() {
		return (RegOrderHelperEntity) super.Current();
	}

	@Override
	public Boolean Select(Object[] dimensions) {
		
		return null;
	}

	@Override
	public Boolean Find(GenericEntity entity) {
		
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
