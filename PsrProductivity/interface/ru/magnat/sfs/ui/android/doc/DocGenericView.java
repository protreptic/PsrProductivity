package ru.magnat.sfs.ui.android.doc;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.generic.DocGenericLineEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import android.content.Context;

@SuppressWarnings("rawtypes")
public abstract class DocGenericView<C extends DocGeneric<E, ? extends DocGenericLineEntity>, E extends DocGenericEntity<C, ?>, O extends GenericEntity<?>>
		extends GenericListView<C, E, O> {

	public DocGenericView(Context context, C docs, O visit) {
		super(context, docs, visit);
	}

}
