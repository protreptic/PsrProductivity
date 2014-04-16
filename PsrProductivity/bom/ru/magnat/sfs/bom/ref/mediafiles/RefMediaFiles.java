package ru.magnat.sfs.bom.ref.mediafiles;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefMediaFiles extends RefGeneric<RefMediaFilesEntity> {

	public RefMediaFiles(Context context) {
		super(context, RefMediaFilesEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
