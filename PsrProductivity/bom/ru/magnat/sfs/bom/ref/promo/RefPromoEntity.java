package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@SuppressWarnings("rawtypes")
@OrmEntityOwner(owner = RefPromo.class)
public class RefPromoEntity extends RefGenericEntity<RefPromo, RefGenericEntity> {
	
	@OrmEntityField(DisplayName = "Бюджетный код промо ПиГ", isPrimary = 0, fields = "PromoSiebelId")
	public String PromoSiebelId;
	
	@OrmEntityField(DisplayName = "Тип промо", isPrimary = 0, fields = "PromoType")
	public PromoType PromoType;
	
	@OrmEntityField(DisplayName = "Бюджет акции", isPrimary = 0, fields = "Budget")
	public int Budget;
	
	@OrmEntityField(DisplayName = "Полное наименование", isPrimary = 0, fields = "DescriptionSfa")
	public String DescriptionSfa;
	
	@OrmEntityField(DisplayName = "Тип компенсации", isPrimary = 0, fields = "CompensationType")
	public CompensationType CompensationType;
}
