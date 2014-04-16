package ru.magnat.sfs.bom.query.getSalesOfDay;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetCumulativeInitiativeDistributionEntity extends
		QueryGenericEntity<QueryGetCumulativeInitiativeDistribution> {
	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "KpiFact")
	public int KpiFact;
	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "KpiTarget")
	public int KpiTarget;
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "KpiIndex")
	public int KpiIndex;

}
