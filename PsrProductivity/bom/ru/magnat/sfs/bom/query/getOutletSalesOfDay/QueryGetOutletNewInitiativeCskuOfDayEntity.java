package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetOutletNewInitiativeCskuOfDayEntity extends
		QueryGenericEntity<QueryGetOutletNewInitiativeCskuOfDay> {
	@OrmEntityField(DisplayName = "Количество", isPrimary = 0, fields = "NewCount")
	public int NewCount;

}
