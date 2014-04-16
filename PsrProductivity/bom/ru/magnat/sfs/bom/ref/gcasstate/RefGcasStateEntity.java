package ru.magnat.sfs.bom.ref.gcasstate;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefGcasState.class)
public final class RefGcasStateEntity extends RefGenericEntity {
	@OrmEntityField(DisplayName = "������ ������������", isPrimary = 0, fields = "Fullname")
	public String Fullname;
	@OrmEntityField(DisplayName = "���� ����������", isPrimary = 0, fields = "OrderKey")
	public String OrderKey;
}
