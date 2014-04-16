package ru.magnat.sfs.bom.ref.csku;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefCsku extends RefGeneric<RefCskuEntity> {

	public RefCsku(Context context) {
		super(context, RefCskuEntity.class);

	}

	@Override
	public boolean hasChild(int position) {
		RefCskuEntity node = (RefCskuEntity) getNode(position);
		if (node == null)
			return false;
		// � Csku ����� ��� ������ �� ������� � ������ �� ���������� ���,
		// ������������� ���� ��� �������� ����, �� �� �� � ���������
		if (!node.IsGroup)
			return false;
		return (node.getParent() == null);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
