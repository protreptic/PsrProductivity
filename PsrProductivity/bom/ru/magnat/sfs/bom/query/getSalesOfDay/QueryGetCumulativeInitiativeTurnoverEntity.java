package ru.magnat.sfs.bom.query.getSalesOfDay;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetCumulativeInitiativeTurnoverEntity extends
		QueryGenericEntity<QueryGetCumulativeInitiativeTurnover> {
	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "KpiFact")
	public float KpiFact;
	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "KpiTarget")
	public float KpiTarget;
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "KpiIndex")
	public float KpiIndex;

}
