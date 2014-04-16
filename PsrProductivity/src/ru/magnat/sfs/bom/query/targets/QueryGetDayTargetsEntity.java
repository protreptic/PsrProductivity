package ru.magnat.sfs.bom.query.targets;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetDayTargetsEntity extends QueryGenericEntity<QueryGetDayTargets> {
	@OrmEntityField(DisplayName = "Наименование", isPrimary = 0, fields = "KpiKind")
	public String Descr;
	@OrmEntityField(DisplayName = "План", isPrimary = 0, fields = "Target")
	public float Target;
}
