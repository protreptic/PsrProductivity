package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetWorkdaysProgressEntity extends
		QueryGenericEntity<QueryGetWorkdaysProgress> {
	@OrmEntityField(DisplayName = "� �������", isPrimary = 0, fields = "inpast")
	public int inpast;
}
