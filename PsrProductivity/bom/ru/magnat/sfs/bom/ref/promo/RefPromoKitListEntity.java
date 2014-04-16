package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefPromoKitList.class)
public class RefPromoKitListEntity extends RefGenericEntity<RefPromoKitList, RefPromoKitEntity> {
	
	@OrmEntityField(DisplayName = "Признак обязательного Csku", isPrimary = 0, fields = "MustHave")
	public Boolean MustHave;
	
}