package ru.magnat.sfs.bom.ref.generic;

import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.ref.csku.RefCsku;
import ru.magnat.sfs.util.TreeNode;

public abstract class RefGenericEntity<C extends RefGeneric<? extends RefGenericEntity<C, ?>>, O extends RefGenericEntity<?, ?>>
		extends GenericEntity<C> implements TreeNode {

	@EntityCardField(DisplayName = "������������", Sortkey = 1, SelectMethod = "ChangeDescr")
	@OrmEntityField(DisplayName = "������������", isPrimary = 0, fields = "Descr")
	public String Descr;
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "Parent")
	public RefGenericEntity<C, O> Parent;
	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "ParentExt")
	public O ParentExt;
	@OrmEntityField(DisplayName = "��� ������", isPrimary = 0, fields = "IsGroup")
	public Boolean IsGroup;
	@EntityCardField(DisplayName = "����������", Sortkey = 20, SelectMethod = "ChangeActivity")
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "IsMark")
	public Boolean IsMark;

	@Override
	public String toString() {
		return Descr;
	}

	public long getId() {
		return Id;
	}

	public String getCaption() {
		return Descr;
	}

	public String getFooter() {
		return "";
	}

	public RefGenericEntity<C, O> getParent() {

		return Parent;
	}
}
