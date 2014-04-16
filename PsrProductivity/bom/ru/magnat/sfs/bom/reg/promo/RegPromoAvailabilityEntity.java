package ru.magnat.sfs.bom.reg.promo;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.promo.RefPromoDetailsEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;

public class RegPromoAvailabilityEntity {
	
	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "Outlet")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefOutletEntity Outlet;

	@OrmEntityField(DisplayName = "Детали промо-акции", isPrimary = 0, fields = "Promo")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefPromoDetailsEntity Promo;

	@OrmEntityField(DisplayName = "Начало акции", isPrimary = 0, fields = "StartOfPromo")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Date StartOfPromo;
	
	@OrmEntityField(DisplayName = "Конец акции", isPrimary = 0, fields = "EndOfPromo")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Date EndOfPromo;
	
	@OrmEntityField(DisplayName = "Тип промо", isPrimary = 0, fields = "PromoType")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public int PromoType;
	
	@OrmEntityField(DisplayName = "Доступность акции", isPrimary = 0, fields = "IsAvailable")
	@RegEntityField(Role = FieldRole.RESOURCE)
	// NULL - недоступна (поменять нельзя)
	// false - недоступно
	// true - доступно
	public Boolean IsAvailable; 
}
