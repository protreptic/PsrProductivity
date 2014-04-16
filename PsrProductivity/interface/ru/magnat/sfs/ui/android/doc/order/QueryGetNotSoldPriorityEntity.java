package ru.magnat.sfs.ui.android.doc.order;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetNotSoldPriorityEntity extends QueryGenericEntity<QueryGetNotSoldPriority> {

	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "prodItem")
	public long ProductItemId;
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "prodItemDescr")
	public String ProductItemDescr;
	@OrmEntityField(DisplayName = "���������", isPrimary = 0, fields = "isPriority")
	public Boolean isPriority;
	@OrmEntityField(DisplayName = "��������� ����. ������", isPrimary = 0, fields = "isNextPriority")
	public Boolean isNextPriority;
	@OrmEntityField(DisplayName = "��� Csku", isPrimary = 0, fields = "CskuId")
	public long CskuId;
}
