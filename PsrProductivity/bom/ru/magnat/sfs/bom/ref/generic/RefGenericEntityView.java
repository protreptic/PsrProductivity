package ru.magnat.sfs.bom.ref.generic;

import ru.magnat.sfs.ui.android.GenericEntityView;
import android.content.Context;

public class RefGenericEntityView<T extends RefGeneric<E>, E extends RefGenericEntity<T, ?>>
		extends GenericEntityView<T, E> {

	public RefGenericEntityView(Context context) {
		super(context);
	}

	public RefGenericEntityView(Context context, T ref, E entity) {
		super(context, ref, entity);
	}

	public Boolean onBackPressed() {
		return null;
	}

}
