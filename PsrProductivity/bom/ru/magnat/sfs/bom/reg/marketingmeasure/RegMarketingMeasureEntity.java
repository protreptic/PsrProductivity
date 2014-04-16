package ru.magnat.sfs.bom.reg.marketingmeasure;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasureEntity;
import ru.magnat.sfs.bom.ref.marketingmeasureobject.RefMarketingMeasureObjectEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegMarketingMeasure.class)
public final class RegMarketingMeasureEntity extends RegGenericEntity {
	@OrmEntityField(DisplayName = "Период", isPrimary = 0, fields = "Period")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Date Period;

	@OrmEntityField(DisplayName = "Торговый агент", isPrimary = 0, fields = "Employee")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefEmployeeEntity Employee;

	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "Outlet")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefOutletEntity Outlet;

	@OrmEntityField(DisplayName = "Вид измерения", isPrimary = 0, fields = "MarketingMeasure")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefMarketingMeasureEntity MarketingMeasure;

	@OrmEntityField(DisplayName = "Объект измерения", isPrimary = 0, fields = "MarketingMeasureObject")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefMarketingMeasureObjectEntity MarketingMeasureObject;

	@OrmEntityField(DisplayName = "Value", isPrimary = 0, fields = "Value")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Float Value;

	@OrmEntityField(DisplayName = "Код галереи", isPrimary = 0, fields = "DocPhotoId")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Integer DocPhotoId;
	@OrmEntityField(DisplayName = "Код автора галереи", isPrimary = 0, fields = "DocPhotoAuthor")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Integer DocPhotoAuthor;

}
