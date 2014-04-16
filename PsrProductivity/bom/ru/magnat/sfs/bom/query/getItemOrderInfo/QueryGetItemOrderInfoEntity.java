package ru.magnat.sfs.bom.query.getItemOrderInfo;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetItemOrderInfoEntity extends
		QueryGenericEntity<QueryGetItemOrderInfo> {
	@OrmEntityField(DisplayName = "Количество", isPrimary = 0, fields = "Quantity")
	public long Quantity;
	@OrmEntityField(DisplayName = "Единица", isPrimary = 0, fields = "UnitLevel")
	public int UnitLevel;
	

	
}
