package ru.magnat.sfs.bom.query.targets;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;
import ru.magnat.sfs.bom.ref.kpi.RefKpiEntity;

public final class QueryGetKpiByMatrixEntity extends
		QueryGenericEntity<QueryGetKpiByMatrix> {
	@OrmEntityField(DisplayName = "Показатель", isPrimary = 0, fields = "KPI")
	public RefKpiEntity KPI;

}
