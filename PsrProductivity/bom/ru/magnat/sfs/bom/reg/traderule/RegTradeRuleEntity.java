package ru.magnat.sfs.bom.reg.traderule;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRuleEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegTradeRule.class)
public final class RegTradeRuleEntity extends RegGenericEntity {

	@OrmEntityField(DisplayName = "Период", isPrimary = 0, fields = "Period")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Date Period;

	@OrmEntityField(DisplayName = "Заказчик", isPrimary = 0, fields = "Customer")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefCustomerEntity Customer;

	@OrmEntityField(DisplayName = "Агент", isPrimary = 0, fields = "Employee")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefEmployeeEntity Employee;

	@OrmEntityField(DisplayName = "ТУ", isPrimary = 0, fields = "TradeRule")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefTradeRuleEntity TradeRule;

	@OrmEntityField(DisplayName = "Действует", isPrimary = 0, fields = "IsActive")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Boolean IsActive;
}
