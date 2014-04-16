package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefPromoKit.class)
public class RefPromoKitEntity extends RefGenericEntity<RefPromoKit, RefPromoDetailsEntity> {
	
	@OrmEntityField(DisplayName = "Минимальное количество заказанных штук каждого CSKU из набора, необходимое для выполнения условий акции", isPrimary = 0, fields = "MinOrderQuantity")
	public int MinOrderQuantity;
	
	@OrmEntityField(DisplayName = "Минимальное заказанное количество разных Csku из набора, необходимое для выполнения условий акций", isPrimary = 0, fields = "MinCskuQuantity")
	public int MinCskuQuantity;
	
}