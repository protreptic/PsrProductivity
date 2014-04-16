package ru.magnat.sfs.bom.ref.kpi;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefKpi.class)
public final class RefKpiEntity extends RefGenericEntity<RefKpi, RefKpiEntity> {
	@OrmEntityField(DisplayName = "“ип", isPrimary = 0, fields = "KpiKind")
	public String KpiKind;
}
