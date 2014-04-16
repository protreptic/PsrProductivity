package ru.magnat.sfs.bom.ref.pricetype;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@SuppressWarnings("rawtypes")
@OrmEntityOwner(owner = RefPriceType.class)
public final class RefPriceTypeEntity extends RefGenericEntity {
	@OrmEntityField(DisplayName = "Базовый тип", isPrimary = 0, fields = "BaseType")
	public RefPriceTypeEntity BaseType;
	@OrmEntityField(DisplayName = "Наценка", isPrimary = 0, fields = "Margin")
	public float Margin;
	@OrmEntityField(DisplayName = "с НДС", isPrimary = 0, fields = "IncludeVAT")
	public Boolean IncludeVAT;

}
