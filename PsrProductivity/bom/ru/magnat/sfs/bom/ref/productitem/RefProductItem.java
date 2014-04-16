package ru.magnat.sfs.bom.ref.productitem;

import ru.magnat.sfs.bom.OrmOwner;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

@OrmOwner(owner = RefCskuEntity.class)
public final class RefProductItem extends RefGeneric<RefProductItemEntity> {

	public RefProductItem(Context context) {
		super(context, RefProductItemEntity.class);
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
