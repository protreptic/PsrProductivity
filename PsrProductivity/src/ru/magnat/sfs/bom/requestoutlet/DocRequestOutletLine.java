package ru.magnat.sfs.bom.requestoutlet;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.generic.DocGenericLine;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class DocRequestOutletLine extends
		DocGenericLine<DocRequestOutletLineEntity> {

	public DocRequestOutletLine(Context context, Class<?> entityType,
			DocGenericEntity<?, ?> owner) {
		super(context, entityType, owner);
	}

	@Override
	public DocRequestOutletLineEntity getLine(Context context,
			GenericEntity<?> entity) {
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
