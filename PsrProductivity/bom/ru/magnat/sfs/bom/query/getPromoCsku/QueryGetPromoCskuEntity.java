package ru.magnat.sfs.bom.query.getPromoCsku;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;



public final class QueryGetPromoCskuEntity extends
		QueryGenericEntity<QueryGetPromoCsku> {
	@OrmEntityField(DisplayName = "CSKU", isPrimary = 0, fields = "Csku")
	public long CskuId;
	@OrmEntityField(DisplayName = "Brand", isPrimary = 0, fields = "Brand")
	public long Brand;
	@OrmEntityField(DisplayName = "��� ������", isPrimary = 0, fields = "Kit")
	public long KitId;
	@OrmEntityField(DisplayName = "��� �����", isPrimary = 0, fields = "Promo")
	public long Promo;
	@OrmEntityField(DisplayName = "��� �����", isPrimary = 0, fields = "PromoType")
	public int PromoType;
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "Discount")
	public float Discount;
	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "Descr")
	public String Descr;
	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "IsAvailable")
	public Boolean IsAvailable;
	@OrmEntityField(DisplayName = "��� �����������", isPrimary = 0, fields = "CompensationType")
	public int CompensationType;
	@OrmEntityField(DisplayName = "��������� ��� �����������", isPrimary = 0, fields = "PrefferedCompensationType")
	public int PrefferedCompensationType;
	@OrmEntityField(DisplayName = "����������� �����", isPrimary = 0, fields = "MinOrderQuantity")
	public int MinOrder;
	@OrmEntityField(DisplayName = "����������� �����������", isPrimary = 0, fields = "MinCskuQuantity")
	public int MinCsku;
	@OrmEntityField(DisplayName = "������������", isPrimary = 0, fields = "MustHave")
	public Boolean MustHave;
	@OrmEntityField(DisplayName = "���������� ����������� ������", isPrimary = 0, fields = "FreeProductSize")
	public int FreeProductSize;
	@OrmEntityField(DisplayName = "�������� CSKU", isPrimary = 0, fields = "BonusCsku")
	public long BonusCsku;
	
	
}
