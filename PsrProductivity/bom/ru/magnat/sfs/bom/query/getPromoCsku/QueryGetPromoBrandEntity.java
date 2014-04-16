package ru.magnat.sfs.bom.query.getPromoCsku;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;



public final class QueryGetPromoBrandEntity extends
		QueryGenericEntity<QueryGetPromoBrand> {
	

			
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Brand")
	public long BrandId;
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
	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "Target")
	public float Target;
	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "Fact")
	public float Fact;
	

	
	
}
