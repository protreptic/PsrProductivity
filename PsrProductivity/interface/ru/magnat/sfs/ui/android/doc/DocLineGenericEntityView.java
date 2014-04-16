package ru.magnat.sfs.ui.android.doc;

import ru.magnat.sfs.bom.generic.DocGenericLineEntity;
import android.content.Context;
import android.view.View;

public class DocLineGenericEntityView extends View {

	DocLineGenericEntityView(Context context) {
		super(context);
	}

	public DocLineGenericEntityView(Context context,
			DocGenericLineEntity<?> entity) {
		super(context);
	}

}
