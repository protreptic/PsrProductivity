package ru.magnat.sfs.bom.reg.tprdiscount;

import java.util.Date;

import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceTypeEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegTprDiscount.class)
public final class RegTprDiscountEntity extends RegGenericEntity<RegTprDiscount> {
	@EntityCardField(DisplayName = "��� ISIS ����� ��� ������", Sortkey = 1, SelectMethod = "")
	@OrmEntityField(DisplayName = "��� ISIS", isPrimary = 0, fields = "ISISCode")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public long ISISCode;

	@OrmEntityField(DisplayName = "������������", isPrimary = 0, fields = "ProductItem")
	@RegEntityField(Role = FieldRole.DIMENSION)
	//public RefProductItemEntity ProductItem;
	public long ProductItem;
	
	@OrmEntityField(DisplayName = "CSKU", isPrimary = 0, fields = "Csku")
	@RegEntityField(Role = FieldRole.DIMENSION)
	//public RefCskuEntity Csku;
	public long Csku;
	@EntityCardField(DisplayName = "������ ��� ������� ���������", Sortkey = 2, SelectMethod = "")
	@OrmEntityField(DisplayName = "������ ��� ������� ���������", isPrimary = 0, fields = "ForGoldenStoreOnly")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Boolean ForGoldenStoreOnly;
	@EntityCardField(DisplayName = "��� ������������", Sortkey = 3, SelectMethod = "")
	@OrmEntityField(DisplayName = "��� ������������", isPrimary = 0, fields = "ServiceType")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefServiceTypeEntity ServiceType;

	@EntityCardField(DisplayName = "�������� ������,%", Sortkey = 4, SelectMethod = "", format = "##,###,###.##")
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "DiscountValue")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float DiscountValue;
	
	@EntityCardField(DisplayName = "������ ��������", Sortkey = 5, SelectMethod = "",format="dd.MM.yy")
	@OrmEntityField(DisplayName = "������ ��������", isPrimary = 0, fields = "BeginOfAction")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Date BeginOfAction;
	
	@EntityCardField(DisplayName = "����� ��������", Sortkey = 6, SelectMethod = "",format="dd.MM.yy")
	@OrmEntityField(DisplayName = "����� ��������", isPrimary = 0, fields = "EndOfAction")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Date EndOfAction;
	
	

	

}
