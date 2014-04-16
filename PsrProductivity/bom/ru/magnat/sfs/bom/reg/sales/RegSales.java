package ru.magnat.sfs.bom.reg.sales;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegSales extends RegGeneric {

	public RegSales(Context context) {
		super(context, RegSalesEntity.class);

	}

	public RegSalesEntity Current() {
		return (RegSalesEntity) super.Current();
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
