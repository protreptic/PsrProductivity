package ru.magnat.sfs.bom.ref.contractor;

import ru.magnat.sfs.bom.OrmOwner;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

// Created Selyanin D.V. 2012-09-05
@OrmOwner(owner = RefCustomerEntity.class)
public final class RefContractor extends RefGeneric<RefContractorEntity> {

	public RefContractor(Context context) {
		super(context, RefContractorEntity.class);
		
	}

	public RefContractorEntity Current() {
		return (RefContractorEntity) super.Current();
	}

	@Override
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return new RefContractorSimpleItemListView(_context, this, lv);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}
}
