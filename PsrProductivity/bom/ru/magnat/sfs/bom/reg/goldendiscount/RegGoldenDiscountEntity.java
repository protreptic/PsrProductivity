package ru.magnat.sfs.bom.reg.goldendiscount;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.discount.RefDiscountEntity;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceTypeEntity;
import ru.magnat.sfs.bom.ref.storechannel.GoldenProgramType;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegGoldenDiscount.class)
public final class RegGoldenDiscountEntity extends RegGenericEntity<RegGoldenDiscount> {

	@OrmEntityField(DisplayName = "Тип скидки", isPrimary = 0, fields = "Discount")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefDiscountEntity Discount;

	@OrmEntityField(DisplayName = "Тип обслуживания", isPrimary = 0, fields = "ServiceType")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefServiceTypeEntity ServiceType;


	@OrmEntityField(DisplayName = "Скидка", isPrimary = 0, fields = "DiscountValue")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float DiscountValue;

	@OrmEntityField(DisplayName = "Тип золотой программы", isPrimary = 0, fields = "GoldenProgramType")
	public GoldenProgramType GoldenProgramType;
	

}
