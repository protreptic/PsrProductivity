package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetOutletInitiativeSalesOfDayEntity extends
		QueryGenericEntity<QueryGetOutletInitiativeSalesOfDay> {
	@OrmEntityField(DisplayName = "SU", isPrimary = 0, fields = "Sales")
	public float Sales;

}
