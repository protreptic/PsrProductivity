package ru.magnat.sfs.bom.query.getSalesOfDay;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetInitiativeDistributionEntity extends
		QueryGenericEntity<QueryGetInitiativeDistribution> {
	@OrmEntityField(DisplayName = "Факт", isPrimary = 0, fields = "KpiFact")
	public int KpiFact;
	// @OrmEntityField(DisplayName = "Цель", isPrimary = 0, fields =
	// "KpiTarget")
	// public int KpiTarget;
	// @OrmEntityField(DisplayName = "Индекс", isPrimary = 0, fields =
	// "KpiIndex")
	// public int KpiIndex;

}
