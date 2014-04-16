package ru.magnat.sfs.bom.query.distribution;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetTurnoverEntity extends
		QueryGenericEntity<QueryGetTurnoverFromTodayOrder> {
	@OrmEntityField(DisplayName = "Товарооборот", isPrimary = 0, fields = "Value")
	public float Value;

}
