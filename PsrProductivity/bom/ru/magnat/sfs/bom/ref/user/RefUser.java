package ru.magnat.sfs.bom.ref.user;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class RefUser extends RefGeneric {

	public RefUser(Context context) {
		super(context, RefUserEntity.class);
		
	}

	public RefUserEntity Current() {

		return (RefUserEntity) super.Current();

	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
