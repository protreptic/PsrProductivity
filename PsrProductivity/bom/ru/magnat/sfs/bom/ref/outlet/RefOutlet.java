package ru.magnat.sfs.bom.ref.outlet;

import ru.magnat.sfs.bom.OrmOwner;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.ref.customer.RefOutletExtendedListItemView;
import android.content.Context;
import android.widget.ListAdapter;
import android.widget.ListView;

@OrmOwner(owner = RefCustomerEntity.class)
public final class RefOutlet extends RefGeneric<RefOutletEntity> implements
		ListAdapter, IBO<RefOutlet, RefOutletEntity, RefCustomerEntity> {

	public RefOutlet(Context context) {
		super(context, RefOutletEntity.class);
		
	}

	public RefOutletEntity Current() {
		return (RefOutletEntity) super.Current();

	}

	public SfsContentView GetEditView(RefOutletEntity outlet) {
		return null;
	}

	public SfsContentView GetViewView(RefOutletEntity outlet) {
		return null;
	}

	@Override
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return new RefOutletExtendedListItemView(_context, this, lv);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return new RefOutletExtendedListItemView(_context, this, lv);
	}

	public GenericListView<RefOutlet, RefOutletEntity, RefCustomerEntity> GetSelectView(
			RefCustomerEntity owner) {
		
		return null;
	}

	public GenericListView<RefOutlet, RefOutletEntity, RefCustomerEntity> GetListView(
			RefCustomerEntity owner) {
		
		return null;
	}

}
