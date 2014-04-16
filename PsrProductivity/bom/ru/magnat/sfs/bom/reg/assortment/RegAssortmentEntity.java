package ru.magnat.sfs.bom.reg.assortment;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegAssortment.class)
public final class RegAssortmentEntity extends RegGenericEntity<RegAssortment> {

	@OrmEntityField(DisplayName = "Ассортимент", isPrimary = 0, fields = "Assortment")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefAssortmentEntity Assortment;

	@OrmEntityField(DisplayName = "Номенклатура", isPrimary = 0, fields = "ProductItem")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefProductItemEntity ProductItem;

	@OrmEntityField(DisplayName = "Активно", isPrimary = 0, fields = "IsActive")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Boolean IsActive;

	public RegAssortmentEntity(RefAssortmentEntity assortment,
			RefProductItemEntity item) {
		super();
		Assortment = assortment;
		ProductItem = item;
		IsActive = true;
	}

}
