package ru.magnat.sfs.bom.query.getSalesOfDay;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetSalesOfDayEntity extends
		QueryGenericEntity<QueryGetSalesOfDay> {
	@OrmEntityField(DisplayName = "Сумма", isPrimary = 0, fields = "Amount")
	public float Amount;

}
