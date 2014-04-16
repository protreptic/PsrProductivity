package ru.magnat.sfs.bom.contract;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.generic.DocGenericLine;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public class DocContractLine extends DocGenericLine<DocContractLineEntity> {

	public DocContractLine(Context context, Class<?> entityType, DocGenericEntity<?, ?> owner) {
		super(context, entityType, owner);
	}
	
	@Override
	public DocContractLineEntity getLine(Context context, GenericEntity<?> entity) {
		return null;
	}
	
	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}
}
