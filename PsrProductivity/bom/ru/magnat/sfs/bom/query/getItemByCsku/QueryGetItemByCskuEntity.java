package ru.magnat.sfs.bom.query.getItemByCsku;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetItemByCskuEntity extends
		QueryGenericEntity<QueryGetItemByCsku> {
	
	@OrmEntityField(DisplayName = "ABC", isPrimary = 0, fields = "abc")
	public int ABC;
	@OrmEntityField(DisplayName = "Απενδ", isPrimary = 0, fields = "brand")
	public long Brand;
	

	

	
}
