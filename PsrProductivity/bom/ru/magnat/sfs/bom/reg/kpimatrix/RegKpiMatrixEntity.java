package ru.magnat.sfs.bom.reg.kpimatrix;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.initiative.RefInitiativeEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegKpiMatrix.class)
public final class RegKpiMatrixEntity extends RegGenericEntity<RegKpiMatrix> {
	@OrmEntityField(DisplayName = "Дата", isPrimary = 0, fields = "Period")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Date Period;

	@OrmEntityField(DisplayName = "Агент", isPrimary = 0, fields = "Employee")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefEmployeeEntity Employee;

	@OrmEntityField(DisplayName = "Точка", isPrimary = 0, fields = "Outlet")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefOutletEntity Outlet;

	@OrmEntityField(DisplayName = "CSKU", isPrimary = 0, fields = "Csku")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefCskuEntity Csku;

	@OrmEntityField(DisplayName = "ABC", isPrimary = 0, fields = "ABC")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public long ABC;

	@OrmEntityField(DisplayName = "Драйв", isPrimary = 0, fields = "IsDrive")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Boolean IsDrive;

	@OrmEntityField(DisplayName = "Приоритет", isPrimary = 0, fields = "IsPriority")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Boolean IsPriority;

	@OrmEntityField(DisplayName = "Инициатива", isPrimary = 0, fields = "Initiative")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefInitiativeEntity Initiative;

	@OrmEntityField(DisplayName = "Количество", isPrimary = 0, fields = "Quantity")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long Quantity;

	@OrmEntityField(DisplayName = "Товарооборот", isPrimary = 0, fields = "Turnover")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float Turnover;

}
