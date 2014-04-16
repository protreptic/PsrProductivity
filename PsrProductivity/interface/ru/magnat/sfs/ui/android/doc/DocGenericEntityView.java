package ru.magnat.sfs.ui.android.doc;

import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.ui.android.GenericEntityView;
import android.content.Context;

public abstract class DocGenericEntityView<C extends DocGeneric<E, ?>, E extends DocGenericEntity<C, ?>>
		extends GenericEntityView<C, E> {

	public DocGenericEntityView(Context context, C docs, E doc) {
		super(context, docs, doc);
	}

}
