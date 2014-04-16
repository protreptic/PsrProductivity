package ru.magnat.sfs.bom.query.getPrices;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetPricesEntity extends
		QueryGenericEntity<QueryGetPrices> {
	@OrmEntityField(DisplayName = "productItem", isPrimary = 0, fields = "productItem")
	public long ProductItem;
	@OrmEntityField(DisplayName = "price", isPrimary = 0, fields = "price")
	public float Price;

}
