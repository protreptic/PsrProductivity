package ru.magnat.sfs.bom.ref.contact;

import java.io.IOException;

import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

import com.ianywhere.ultralitejni12.*;

;

public final class RefContact extends RefGeneric<RefContactEntity> implements
		IBO<RefContact, RefContactEntity, RefCustomerEntity> {

	public RefContact(Context context) throws ULjException, IOException {
		super(context, RefContactEntity.class);
		
	}

	public RefContactEntity Current() {
		return (RefContactEntity) super.Current();

	}

	public GenericListView<RefContact, RefContactEntity, RefCustomerEntity> GetSelectView(
			RefCustomerEntity owner) {
		
		return null;
	}

	public SfsContentView GetViewView(RefContactEntity entity) {
		
		return null;
	}

	public GenericListView<RefContact, RefContactEntity, RefCustomerEntity> GetListView(
			RefCustomerEntity owner) {
		
		return null;
	}

	public SfsContentView GetEditView(RefContactEntity entity) {
		
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
