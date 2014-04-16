package ru.magnat.sfs.bom.reg.assortment;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class RegAssortment extends RegGeneric<RegAssortmentEntity> {

	public RegAssortment(Context context) {
		super(context, RegAssortmentEntity.class);

	}

	@Override
	public Boolean Select(Object dimensions[]) {
		if (dimensions.length != 2)
			return false;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (dimensions[0] != null)
			criteria.add(new SqlCriteria("Assortment",
					(Long) dimensions[0]));
		if (dimensions[1] != null)
			criteria.add(new SqlCriteria("ProductItem",
					(Long) dimensions[1]));

		return super.Select(criteria, "");
	}

	public Boolean inAssortment(RefAssortmentEntity assortment,
			RefProductItemEntity item) {

		return Find(new RegAssortmentEntity(assortment, item));
	}

	@Override
	public Boolean Find(RegAssortmentEntity entity) {
		Object[] params = { entity.Assortment.Id, entity.ProductItem.Id };
		if (Select(params)) {
			return this.Count() > 0;
		}
		return false;

	}
	public Boolean inAssortment(Long assortment,
			Long item) {

		return Find(assortment,item);
	}
	public Boolean Find(Long assortmentId, Long productId) {
		Object[] params = { assortmentId, productId };
		if (Select(params)) {
			return this.Count() > 0;
		}
		return false;

	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
