package ru.magnat.sfs.bom.ref.employee;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefEmployee extends RefGeneric<RefEmployeeEntity> {

	public RefEmployee(Context context) {
		super(context, RefEmployeeEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
