package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefPromoKit.class)
public class RefPromoKitEntity extends RefGenericEntity<RefPromoKit, RefPromoDetailsEntity> {
	
	@OrmEntityField(DisplayName = "����������� ���������� ���������� ���� ������� CSKU �� ������, ����������� ��� ���������� ������� �����", isPrimary = 0, fields = "MinOrderQuantity")
	public int MinOrderQuantity;
	
	@OrmEntityField(DisplayName = "����������� ���������� ���������� ������ Csku �� ������, ����������� ��� ���������� ������� �����", isPrimary = 0, fields = "MinCskuQuantity")
	public int MinCskuQuantity;
	
}