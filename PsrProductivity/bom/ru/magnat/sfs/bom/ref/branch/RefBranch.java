package ru.magnat.sfs.bom.ref.branch;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public final class RefBranch extends RefGeneric {

	@SuppressWarnings("unchecked")
	public RefBranch(Context context) {
		super(context, RefBranchEntity.class);
	}

	public RefBranchEntity Current() {
		return (RefBranchEntity) super.Current();
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
