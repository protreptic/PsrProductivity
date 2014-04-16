package ru.magnat.sfs.bom.query.getPromoCsku;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;



public final class QueryGetPromoCskuEntity extends
		QueryGenericEntity<QueryGetPromoCsku> {
	@OrmEntityField(DisplayName = "CSKU", isPrimary = 0, fields = "Csku")
	public long CskuId;
	@OrmEntityField(DisplayName = "Brand", isPrimary = 0, fields = "Brand")
	public long Brand;
	@OrmEntityField(DisplayName = "Код пакета", isPrimary = 0, fields = "Kit")
	public long KitId;
	@OrmEntityField(DisplayName = "Код промо", isPrimary = 0, fields = "Promo")
	public long Promo;
	@OrmEntityField(DisplayName = "Тип промо", isPrimary = 0, fields = "PromoType")
	public int PromoType;
	@OrmEntityField(DisplayName = "Скидка", isPrimary = 0, fields = "Discount")
	public float Discount;
	@OrmEntityField(DisplayName = "Описание", isPrimary = 0, fields = "Descr")
	public String Descr;
	@OrmEntityField(DisplayName = "Включено", isPrimary = 0, fields = "IsAvailable")
	public Boolean IsAvailable;
	@OrmEntityField(DisplayName = "Тип компенсации", isPrimary = 0, fields = "CompensationType")
	public int CompensationType;
	@OrmEntityField(DisplayName = "Выбранный тип компенсации", isPrimary = 0, fields = "PrefferedCompensationType")
	public int PrefferedCompensationType;
	@OrmEntityField(DisplayName = "Минимальный заказ", isPrimary = 0, fields = "MinOrderQuantity")
	public int MinOrder;
	@OrmEntityField(DisplayName = "Минимальная дистрибуция", isPrimary = 0, fields = "MinCskuQuantity")
	public int MinCsku;
	@OrmEntityField(DisplayName = "Обязательная", isPrimary = 0, fields = "MustHave")
	public Boolean MustHave;
	@OrmEntityField(DisplayName = "Количество бесплатного товара", isPrimary = 0, fields = "FreeProductSize")
	public int FreeProductSize;
	@OrmEntityField(DisplayName = "Бонусное CSKU", isPrimary = 0, fields = "BonusCsku")
	public long BonusCsku;
	
	
}
