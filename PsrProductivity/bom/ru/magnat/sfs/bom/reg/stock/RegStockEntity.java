package ru.magnat.sfs.bom.reg.stock;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.ref.warehouse.RefWarehouseEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegStock.class)
public class RegStockEntity extends RegGenericEntity {

	@OrmEntityField(DisplayName = "Склад", isPrimary = 0, fields = "Warehouse")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefWarehouseEntity Warehouse;

	@OrmEntityField(DisplayName = "Номенклатура", isPrimary = 0, fields = "ProductItem")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefProductItemEntity ProductItem;

	@OrmEntityField(DisplayName = "Активно", isPrimary = 0, fields = "IsActive")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long Quantity;

}
