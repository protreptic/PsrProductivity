package ru.magnat.sfs.ui.android.ref.customer;

import android.content.Context;
import ru.magnat.sfs.bom.ref.customer.RefCustomer;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.ui.android.SimpleGenericEntityView;

public class CustomerEntityView extends
		SimpleGenericEntityView<RefCustomer, RefCustomerEntity> {// Created
																	// Selyanin
																	// D.V.
																	// 2012-09-09
	public CustomerEntityView(Context context, RefCustomerEntity entity) {
		super(context, entity);
		
	}

	public void ChangeCustomer() {
	}
}
