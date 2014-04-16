package ru.magnat.sfs.bom.reg.goldenprogram;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegGoldenProgram.class)
public final class RegGoldenProgramEntity extends RegGenericEntity {

	@OrmEntityField(DisplayName = "Период", isPrimary = 0, fields = "Period")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Date Period;

	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "Outlet")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefOutletEntity Outlet;

	@OrmEntityField(DisplayName = "Потенциально золотая", isPrimary = 0, fields = "IsPotential")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public Boolean IsPotential;

	@OrmEntityField(DisplayName = "Золотая", isPrimary = 0, fields = "IsGolden")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public Boolean IsGolden;

	@OrmEntityField(DisplayName = "Факт ОПД", isPrimary = 0, fields = "TotalDistribution")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long TotalDistribution;

	@OrmEntityField(DisplayName = "План ОПД", isPrimary = 0, fields = "TotalDistributionPlan")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long TotalDistributionPlan;

	@OrmEntityField(DisplayName = "Факт ЗПД", isPrimary = 0, fields = "GoldenDistribution")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long GoldenDistribution;

	@OrmEntityField(DisplayName = "План ЗПД", isPrimary = 0, fields = "GoldenDistributionPlan")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long GoldenDistributionPlan;

	@OrmEntityField(DisplayName = "Факт ТО", isPrimary = 0, fields = "Shipments")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float Shipments;

	@OrmEntityField(DisplayName = "План ТО", isPrimary = 0, fields = "ShipmentsPlan")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float ShipmentsPlan;

	@OrmEntityField(DisplayName = "Представленность", isPrimary = 0, fields = "Visibility")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long Visibility;

	@OrmEntityField(DisplayName = "План по представленности", isPrimary = 0, fields = "VisibilityPlan")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long VisibilityPlan;

	@OrmEntityField(DisplayName = "Дисплеи", isPrimary = 0, fields = "Display")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long Display;

	@OrmEntityField(DisplayName = "План по дисплеям", isPrimary = 0, fields = "DisplayPlan")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long DisplayPlan;

	@OrmEntityField(DisplayName = "Кассы", isPrimary = 0, fields = "Checkouts")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long Checkouts;

	@OrmEntityField(DisplayName = "План по кассам", isPrimary = 0, fields = "CheckoutsPlan")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public long CheckoutsPlan;
}
