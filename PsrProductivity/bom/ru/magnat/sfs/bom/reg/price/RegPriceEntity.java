package ru.magnat.sfs.bom.reg.price;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.pricetype.RefPriceTypeEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegPrice.class)
public final class RegPriceEntity extends RegGenericEntity {

	@OrmEntityField(DisplayName = "Период", isPrimary = 0, fields = "Period")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Date Period;

	@OrmEntityField(DisplayName = "Тип цен", isPrimary = 0, fields = "PriceType")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefPriceTypeEntity PriceType;

	@OrmEntityField(DisplayName = "Номенклатура", isPrimary = 0, fields = "ProductItem")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefEmployeeEntity ProductItem;

	@OrmEntityField(DisplayName = "Цена", isPrimary = 0, fields = "Price")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public float Price;

}
