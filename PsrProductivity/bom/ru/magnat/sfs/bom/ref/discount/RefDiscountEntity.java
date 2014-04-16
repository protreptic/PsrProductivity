package ru.magnat.sfs.bom.ref.discount;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefDiscount.class)
public final class RefDiscountEntity extends
		RefGenericEntity<RefDiscount, RefDiscountEntity> {
	@OrmEntityField(DisplayName = "Тип", isPrimary = 0, fields = "DiscountType")
	public int DiscountType;
	@OrmEntityField(DisplayName = "Ассортимент", isPrimary = 0, fields = "Assortment")
	public RefAssortmentEntity Assortment;
	@OrmEntityField(DisplayName = "Значение", isPrimary = 0, fields = "DiscountValue")
	public float DiscountValue;

}
