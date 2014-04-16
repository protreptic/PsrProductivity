package ru.magnat.sfs.bom.ref.initiative;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefInitiative extends RefGeneric<RefInitiativeEntity> {

	public RefInitiative(Context context) {
		super(context, RefInitiativeEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
