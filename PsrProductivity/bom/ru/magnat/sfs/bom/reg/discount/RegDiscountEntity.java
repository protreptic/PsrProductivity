package ru.magnat.sfs.bom.reg.discount;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.discount.RefDiscountEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRuleEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegDiscount.class)
public final class RegDiscountEntity extends RegGenericEntity<RegDiscount> {

	@OrmEntityField(DisplayName = "ТУ", isPrimary = 0, fields = "TradeRule")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefTradeRuleEntity TradeRule;

	@OrmEntityField(DisplayName = "ТА", isPrimary = 0, fields = "Employee")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefEmployeeEntity Employee;

	@OrmEntityField(DisplayName = "КА", isPrimary = 0, fields = "Customer")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefCustomerEntity Customer;

	@OrmEntityField(DisplayName = "Скидка", isPrimary = 0, fields = "Discount")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefDiscountEntity Discount;

	@OrmEntityField(DisplayName = "Граница", isPrimary = 0, fields = "Border")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public float Border;

	@OrmEntityField(DisplayName = "Значение", isPrimary = 0, fields = "DiscountValue")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public float DiscountValue;

	@OrmEntityField(DisplayName = "Ассортимент", isPrimary = 0, fields = "Assortment")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public RefAssortmentEntity Assortment;

	@OrmEntityField(DisplayName = "Тип", isPrimary = 0, fields = "DiscountType")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public int DiscountType;

}
