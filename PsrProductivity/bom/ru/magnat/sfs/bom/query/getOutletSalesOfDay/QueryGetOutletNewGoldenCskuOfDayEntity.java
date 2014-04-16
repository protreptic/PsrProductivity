package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetOutletNewGoldenCskuOfDayEntity extends
		QueryGenericEntity<QueryGetOutletNewGoldenCskuOfDay> {
	@OrmEntityField(DisplayName = "Количество", isPrimary = 0, fields = "NewCount")
	public int NewCount;

}
