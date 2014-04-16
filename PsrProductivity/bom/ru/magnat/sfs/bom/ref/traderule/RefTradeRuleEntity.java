package ru.magnat.sfs.bom.ref.traderule;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;
import ru.magnat.sfs.bom.ref.pricetype.RefPriceTypeEntity;

@OrmEntityOwner(owner = RefTradeRule.class)
public final class RefTradeRuleEntity extends RefGenericEntity {
	@OrmEntityField(DisplayName = "Ассортимент", isPrimary = 0, fields = "Assortment")
	public RefAssortmentEntity Assortment;
	@OrmEntityField(DisplayName = "Тип цены", isPrimary = 0, fields = "PriceType")
	public RefPriceTypeEntity PriceType;
	@OrmEntityField(DisplayName = "ТУ NonPG", isPrimary = 0, fields = "IsNonPgRule")
	public Boolean IsNonPgRule;

}
