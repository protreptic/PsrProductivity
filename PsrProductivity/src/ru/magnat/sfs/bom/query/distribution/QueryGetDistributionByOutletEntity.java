package ru.magnat.sfs.bom.query.distribution;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetDistributionByOutletEntity extends QueryGenericEntity<QueryGetTotalDistributionByOutlet> {
	@OrmEntityField(DisplayName = "Дистрибьюция", isPrimary = 0, fields = "Distribution")
	public int Distribution;
}
