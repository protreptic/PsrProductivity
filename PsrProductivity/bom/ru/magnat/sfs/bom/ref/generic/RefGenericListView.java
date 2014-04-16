package ru.magnat.sfs.bom.ref.generic;

import ru.magnat.sfs.ui.android.GenericListView;
import android.content.Context;

public class RefGenericListView<C extends RefGeneric<E>, E extends RefGenericEntity<C, O>, O extends RefGenericEntity<?, ?>>
		extends GenericListView<C, E, O> {

	public RefGenericListView(Context context, C ref, O owner) {
		super(context, ref, owner);
		
	}

	public Boolean onBackPressed() {
		
		return null;
	}

	@Override
	protected void createEntity() {
		
	}

}
