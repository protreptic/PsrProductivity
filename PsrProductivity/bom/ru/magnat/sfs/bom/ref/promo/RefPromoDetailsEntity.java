package ru.magnat.sfs.bom.ref.promo;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefPromoDetails.class)
public class RefPromoDetailsEntity extends RefGenericEntity<RefPromoDetails, RefPromoEntity> {
	
	@OrmEntityField(DisplayName = "Код ISIS целевого канала или клиента", isPrimary = 0, fields = "IsisCustomerCode")
	public String IsisCustomerCode;
	
	@OrmEntityField(DisplayName = "Целевой тип обслуживания", isPrimary = 0, fields = "ServiceType")
	public TargetServiceType ServiceType;
	
	@OrmEntityField(DisplayName = "Целевой индекс прироста", isPrimary = 0, fields = "GrowthIndex")
	public float GrowthIndex;
	
	@OrmEntityField(DisplayName = "Размер бонусной скидки", isPrimary = 0, fields = "Discount")
	public float Discount;
	
	@OrmEntityField(DisplayName = "Начало акции", isPrimary = 0, fields = "StartOfPromo")
	public Date StartOfPromo;
	
	@OrmEntityField(DisplayName = "Конец акции", isPrimary = 0, fields = "EndOfPromo")
	public Date EndOfPromo;
	
	@OrmEntityField(DisplayName = "Начало базового периода", isPrimary = 0, fields = "StartOfBasePeriod")
	public Date StartOfBasePeriod;
	
	@OrmEntityField(DisplayName = "Конец базового периода", isPrimary = 0, fields = "EndOfBasePeriod")
	public Date EndOfBasePeriod;
}
