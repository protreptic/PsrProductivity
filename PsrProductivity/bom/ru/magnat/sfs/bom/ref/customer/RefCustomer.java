package ru.magnat.sfs.bom.ref.customer;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefCustomer extends RefGeneric<RefCustomerEntity> implements IBO<RefCustomer, RefCustomerEntity, RefCustomerEntity> {

	public RefCustomer(Context context)  {
		super(context, RefCustomerEntity.class);
	}

	public RefCustomerEntity Current() {
		return (RefCustomerEntity) super.Current();
	}

	public SfsContentView GetEditView(RefCustomerEntity entity) {
		return null;
	}

	public SfsContentView GetViewView(RefCustomerEntity entity) {
		return null;
	}

	public GenericListView<RefCustomer, RefCustomerEntity, RefCustomerEntity> GetSelectView(RefCustomerEntity owner) {
		return null;
	}

	public GenericListView<RefCustomer, RefCustomerEntity, RefCustomerEntity> GetListView(RefCustomerEntity owner) {
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
