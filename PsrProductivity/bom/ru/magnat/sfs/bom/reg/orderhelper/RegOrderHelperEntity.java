package ru.magnat.sfs.bom.reg.orderhelper;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegOrderHelper.class)
public final class RegOrderHelperEntity extends RegGenericEntity {

	@OrmEntityField(DisplayName = "Агент", isPrimary = 0, fields = "Employee")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefEmployeeEntity Employee;

	@OrmEntityField(DisplayName = "Точка отгрузки", isPrimary = 0, fields = "Outlet")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefOutletEntity Outlet;

	@OrmEntityField(DisplayName = "CSKU", isPrimary = 0, fields = "Csku")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefCskuEntity Csku;

	@OrmEntityField(DisplayName = "Сальдо", isPrimary = 0, fields = "Debt")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float Debt;

	@OrmEntityField(DisplayName = "Количество", isPrimary = 0, fields = "Quantity")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float Quantity;

}
