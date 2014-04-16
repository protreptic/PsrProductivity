package ru.magnat.sfs.bom.reg.kpiinterpretation;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.kpi.RefKpiEntity;
import ru.magnat.sfs.bom.ref.kpimatrix.RefKpiMatrixEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegKpiInterpretation.class)
public final class RegKpiInterpretationEntity extends
		RegGenericEntity<RegKpiInterpretation> {

	@OrmEntityField(DisplayName = "Матрица", isPrimary = 0, fields = "KpiMatrix")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefKpiMatrixEntity KpiMatrix;

	@OrmEntityField(DisplayName = "KPI", isPrimary = 0, fields = "KPI")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefKpiEntity Kpi;

	@OrmEntityField(DisplayName = "Граница", isPrimary = 0, fields = "Border")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public float Border;

	@OrmEntityField(DisplayName = "Значение", isPrimary = 0, fields = "Value")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public float Value;
}
