package ru.magnat.sfs.bom.reg.marketingmeasureobject;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasureEntity;
import ru.magnat.sfs.bom.ref.marketingmeasureobject.RefMarketingMeasureObjectEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegMarketingMeasureObject.class)
public final class RegMarketingMeasureObjectEntity extends RegGenericEntity {

	@OrmEntityField(DisplayName = "Вид измерения", isPrimary = 0, fields = "MarketingMeasure")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefMarketingMeasureEntity MarketingMeasure;

	@OrmEntityField(DisplayName = "Объект измерения", isPrimary = 0, fields = "MarketingMeasureObject")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefMarketingMeasureObjectEntity MarketingMeasureObject;

	@OrmEntityField(DisplayName = "Активно", isPrimary = 0, fields = "IsActive")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Boolean IsActive;

}
