package ru.magnat.sfs.bom.reg.promo;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.promo.RefPromoDetailsEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;

public class RegPromoAvailabilityEntity {
	
	@OrmEntityField(DisplayName = "�������� �����", isPrimary = 0, fields = "Outlet")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefOutletEntity Outlet;

	@OrmEntityField(DisplayName = "������ �����-�����", isPrimary = 0, fields = "Promo")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefPromoDetailsEntity Promo;

	@OrmEntityField(DisplayName = "������ �����", isPrimary = 0, fields = "StartOfPromo")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Date StartOfPromo;
	
	@OrmEntityField(DisplayName = "����� �����", isPrimary = 0, fields = "EndOfPromo")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Date EndOfPromo;
	
	@OrmEntityField(DisplayName = "��� �����", isPrimary = 0, fields = "PromoType")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public int PromoType;
	
	@OrmEntityField(DisplayName = "����������� �����", isPrimary = 0, fields = "IsAvailable")
	@RegEntityField(Role = FieldRole.RESOURCE)
	// NULL - ���������� (�������� ������)
	// false - ����������
	// true - ��������
	public Boolean IsAvailable; 
}
