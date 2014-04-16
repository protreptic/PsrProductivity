package ru.magnat.sfs.bom.query.getSalesOfDay;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetGoldenSalesOfDayEntity extends
		QueryGenericEntity<QueryGetGoldenSalesOfDay> {
	@OrmEntityField(DisplayName = "����� SU", isPrimary = 0, fields = "Amount")
	public float Amount;

}
