package ru.magnat.sfs.bom.reg.stock;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.productitem.RefProductItem;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.ref.warehouse.RefWarehouseEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegStock extends RegGeneric {

	public RegStock(Context context) {
		super(context, RegStockEntity.class);

	}

	public RegStockEntity Current() {
		return (RegStockEntity) super.Current();
	}

	@Override
	public Boolean Select(Object[] dimensions) {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		for (int i = 0; i < dimensions.length; i++) {
			if (dimensions[i] == null)
				continue;
			switch (i) {
			case 0:
				if (dimensions[i] instanceof RefWarehouseEntity) {
					criteria.add(new SqlCriteria("Warehouse",
							((RefWarehouseEntity) dimensions[i]).Id));
				}
				break;
			case 1:
				if (dimensions[i] instanceof RefProductItem) {
					criteria.add(new SqlCriteria("ProductItem",
							((RefProductItemEntity) dimensions[i]).Id));
				}
				break;
			}
		}
		return super.Select(criteria);

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
