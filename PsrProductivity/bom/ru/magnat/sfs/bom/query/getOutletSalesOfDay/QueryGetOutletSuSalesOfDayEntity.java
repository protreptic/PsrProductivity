package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;


public final class QueryGetOutletSuSalesOfDayEntity extends
		QueryGenericEntity<QueryGetOutletSuSalesOfDay> {
	@OrmEntityField(DisplayName = "Сумма", isPrimary = 0, fields = "Amount")
	public float Amount;

}
