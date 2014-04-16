package ru.magnat.sfs.bom.ref.paymenttype;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefPaymentType extends RefGeneric {

	public RefPaymentType(Context context) {
		super(context, RefPaymentTypeEntity.class);
		
	}

	public RefPaymentTypeEntity Current() {
		return (RefPaymentTypeEntity) super.Current();
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
