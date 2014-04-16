package ru.magnat.sfs.bom.query.targets;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;
import ru.magnat.sfs.bom.ref.initiative.RefInitiativeEntity;
import ru.magnat.sfs.bom.ref.kpi.RefKpiEntity;
import ru.magnat.sfs.bom.ref.kpimatrix.RefKpiMatrixEntity;

public final class QueryGetTargetEntity extends
		QueryGenericEntity<QueryGetTarget> {
	@OrmEntityField(DisplayName = "Матрица", isPrimary = 0, fields = "KpiMatrix")
	public RefKpiMatrixEntity KpiMatrix;
	@OrmEntityField(DisplayName = "Наименование", isPrimary = 0, fields = "Descr")
	public String Descr;
	@OrmEntityField(DisplayName = "Показатель", isPrimary = 0, fields = "KPI")
	public RefKpiEntity KPI;
	@OrmEntityField(DisplayName = "Цель", isPrimary = 0, fields = "Target")
	public float Target;
	@OrmEntityField(DisplayName = "Факт", isPrimary = 0, fields = "Fact")
	public float Fact;
	@OrmEntityField(DisplayName = "GAP", isPrimary = 0, fields = "GAP")
	public float GAP;
	@OrmEntityField(DisplayName = "Индекс", isPrimary = 0, fields = "TargetIndex")
	public float Index;
	@OrmEntityField(DisplayName = "Вес", isPrimary = 0, fields = "KpiShare")
	public float Weight;
	@OrmEntityField(DisplayName = "Вид", isPrimary = 0, fields = "KpiKind")
	public String KpiKind;
	@OrmEntityField(DisplayName = "Инициатива", isPrimary = 0, fields = "Initiative")
	public RefInitiativeEntity Initiative;
}
