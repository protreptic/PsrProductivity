package ru.magnat.sfs.bom.ref.approvestate;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefApproveState extends RefGeneric<RefApproveStateEntity> {

	public RefApproveState(Context context) {
		super(context, RefApproveStateEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
