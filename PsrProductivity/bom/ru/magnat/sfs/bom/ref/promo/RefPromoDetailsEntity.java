package ru.magnat.sfs.bom.ref.promo;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefPromoDetails.class)
public class RefPromoDetailsEntity extends RefGenericEntity<RefPromoDetails, RefPromoEntity> {
	
	@OrmEntityField(DisplayName = "��� ISIS �������� ������ ��� �������", isPrimary = 0, fields = "IsisCustomerCode")
	public String IsisCustomerCode;
	
	@OrmEntityField(DisplayName = "������� ��� ������������", isPrimary = 0, fields = "ServiceType")
	public TargetServiceType ServiceType;
	
	@OrmEntityField(DisplayName = "������� ������ ��������", isPrimary = 0, fields = "GrowthIndex")
	public float GrowthIndex;
	
	@OrmEntityField(DisplayName = "������ �������� ������", isPrimary = 0, fields = "Discount")
	public float Discount;
	
	@OrmEntityField(DisplayName = "������ �����", isPrimary = 0, fields = "StartOfPromo")
	public Date StartOfPromo;
	
	@OrmEntityField(DisplayName = "����� �����", isPrimary = 0, fields = "EndOfPromo")
	public Date EndOfPromo;
	
	@OrmEntityField(DisplayName = "������ �������� �������", isPrimary = 0, fields = "StartOfBasePeriod")
	public Date StartOfBasePeriod;
	
	@OrmEntityField(DisplayName = "����� �������� �������", isPrimary = 0, fields = "EndOfBasePeriod")
	public Date EndOfBasePeriod;
}
