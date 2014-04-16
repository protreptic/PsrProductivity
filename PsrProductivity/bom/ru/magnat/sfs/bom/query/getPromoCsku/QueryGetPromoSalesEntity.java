package ru.magnat.sfs.bom.query.getPromoCsku;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;



public final class QueryGetPromoSalesEntity extends
		QueryGenericEntity<QueryGetPromoSales> {
	@OrmEntityField(DisplayName = "Код промо", isPrimary = 0, fields = "Promo")
	public long Promo;
	@OrmEntityField(DisplayName = "Тип промо", isPrimary = 0, fields = "PromoType")
	public int PromoType;
	@OrmEntityField(DisplayName = "Csku", isPrimary = 0, fields = "Csku")
	public long Csku;
	@OrmEntityField(DisplayName = "Количество", isPrimary = 0, fields = "Quantity")
	public int Quantity;
	
}
