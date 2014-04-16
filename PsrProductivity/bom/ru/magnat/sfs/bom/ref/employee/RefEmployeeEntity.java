package ru.magnat.sfs.bom.ref.employee;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.branch.RefBranchEntity;
import ru.magnat.sfs.bom.ref.business.RefBusinessEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;
import ru.magnat.sfs.bom.ref.kpimatrix.RefKpiMatrixEntity;

@OrmEntityOwner(owner = RefEmployee.class)
public final class RefEmployeeEntity extends
		RefGenericEntity<RefEmployee, RefGenericEntity> {
	@OrmEntityField(DisplayName = "Филиал", isPrimary = 0, fields = "Branch")
	public RefBranchEntity Branch;
	@OrmEntityField(DisplayName = "Направление бизнеса", isPrimary = 0, fields = "Business")
	public RefBusinessEntity Business;
	@OrmEntityField(DisplayName = "Менеджер", isPrimary = 0, fields = "Manager")
	public RefEmployeeEntity Manager;
	@OrmEntityField(DisplayName = "Email", isPrimary = 0, fields = "Email")
	public String Email;
	@OrmEntityField(DisplayName = "Телефон", isPrimary = 0, fields = "Phone")
	public String Phone;
	@OrmEntityField(DisplayName = "Ассортимент", isPrimary = 0, fields = "Assortment")
	public RefAssortmentEntity Assortment;
	@OrmEntityField(DisplayName = "Матрица зарплаты", isPrimary = 0, fields = "SalaryMatrix")
	public RefKpiMatrixEntity SalaryMatrix;
	@OrmEntityField(DisplayName = "Матрица целей", isPrimary = 0, fields = "GoalMatrix")
	public RefKpiMatrixEntity GoalMatrix;

}
