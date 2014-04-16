package ru.magnat.sfs.bom.ref.discountzone;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefDiscountZone.class)
public final class RefDiscountZoneEntity extends
		RefGenericEntity<RefDiscountZone, RefDiscountZoneEntity> {
	@OrmEntityField(DisplayName = "Скидка", isPrimary = 0, fields = "PrimaryDiscount")
	public float PrimaryDiscount;
	@OrmEntityField(DisplayName = "Дополнительная скидка", isPrimary = 0, fields = "SecondaryDiscount")
	public float SecondaryDiscount;
}
