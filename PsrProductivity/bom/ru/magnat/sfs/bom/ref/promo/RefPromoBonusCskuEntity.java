package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefPromoBonusCsku.class)
public class RefPromoBonusCskuEntity extends RefGenericEntity<RefPromoBonusCsku, RefPromoDetailsEntity> {
	
	@OrmEntityField(DisplayName = "CSKU", isPrimary = 0, fields = "Csku")
	public RefCskuEntity Csku;
	
	@OrmEntityField(DisplayName = "Количество бонусного товара", isPrimary = 0, fields = "Quantity")
	public int Quantity;
	
}