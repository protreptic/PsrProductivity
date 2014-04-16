package ru.magnat.sfs.bom.reg.tprdiscountbyoutlet;

import java.util.Date;

import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceTypeEntity;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegTprDiscountByOutlet.class)
public final class RegTprDiscountByOutletEntity extends RegGenericEntity<RegTprDiscountByOutlet> {
	
	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "Outlet")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefOutletEntity Outlet;
	
	@EntityCardField(DisplayName = "Код ISIS точки или канала", Sortkey = 1, SelectMethod = "")
	@OrmEntityField(DisplayName = "Код ISIS", isPrimary = 0, fields = "ISISCode")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public long ISISCode;

	@OrmEntityField(DisplayName = "Номенклатура", isPrimary = 0, fields = "ProductItem")
	@RegEntityField(Role = FieldRole.DIMENSION)
	//public RefProductItemEntity ProductItem;
	public RefProductItemEntity ProductItem;
	
	@OrmEntityField(DisplayName = "CSKU", isPrimary = 0, fields = "Csku")
	@RegEntityField(Role = FieldRole.DIMENSION)
	//public RefCskuEntity Csku;
	public RefCskuEntity Csku;
	@EntityCardField(DisplayName = "Только для золотых магазинов", Sortkey = 2, SelectMethod = "")
	@OrmEntityField(DisplayName = "Только для золотых магазинов", isPrimary = 0, fields = "ForGoldenStoreOnly")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Boolean ForGoldenStoreOnly;
	@EntityCardField(DisplayName = "Тип обслуживания", Sortkey = 3, SelectMethod = "")
	@OrmEntityField(DisplayName = "Тип обслуживания", isPrimary = 0, fields = "ServiceType")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefServiceTypeEntity ServiceType;

	@EntityCardField(DisplayName = "Величина скидки,%", Sortkey = 4, SelectMethod = "", format = "##,###,###.##")
	@OrmEntityField(DisplayName = "Скидка", isPrimary = 0, fields = "DiscountValue")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float DiscountValue;
	
	@EntityCardField(DisplayName = "Начало действия", Sortkey = 5, SelectMethod = "",format="dd.MM.yy")
	@OrmEntityField(DisplayName = "Начало действия", isPrimary = 0, fields = "BeginOfAction")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Date BeginOfAction;
	
	@EntityCardField(DisplayName = "Конец действия", Sortkey = 6, SelectMethod = "",format="dd.MM.yy")
	@OrmEntityField(DisplayName = "Конец действия", isPrimary = 0, fields = "EndOfAction")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Date EndOfAction;
	
	

	

}
