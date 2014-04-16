package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetVisibilityOutletTargetEntity extends
		QueryGenericEntity<QueryGetVisibilityOutletTarget> {
	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "KpiTarget")
	public float KpiTarget;
	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "KpiFact")
	public float KpiFact;
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "KpiIndex")
	public float KpiIndex;
}
