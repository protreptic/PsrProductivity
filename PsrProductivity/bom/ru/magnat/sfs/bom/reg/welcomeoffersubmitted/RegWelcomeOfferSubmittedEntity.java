package ru.magnat.sfs.bom.reg.welcomeoffersubmitted;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegWelcomeOfferSubmitted.class)
public final class RegWelcomeOfferSubmittedEntity extends RegGenericEntity<RegWelcomeOfferSubmitted> {

	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "Outlet")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefOutletEntity Outlet;

	

	@OrmEntityField(DisplayName = "Документ", isPrimary = 0, fields = "Document")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public String Document;

	

}
