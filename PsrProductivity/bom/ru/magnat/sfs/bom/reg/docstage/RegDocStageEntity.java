package ru.magnat.sfs.bom.reg.docstage;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegDocStage.class)
public final class RegDocStageEntity extends RegGenericEntity {
	@OrmEntityField(DisplayName = "Дата", isPrimary = 0, fields = "Period")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Date Period;

	@OrmEntityField(DisplayName = "Документ", isPrimary = 0, fields = "DocumentId,DocumentAuthor, DocumentType")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public DocGenericEntity<?, ?> Document;

	@OrmEntityField(DisplayName = "ТоррговыйАгент", isPrimary = 0, fields = "Employee")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefEmployeeEntity Employee;

	@OrmEntityField(DisplayName = "Статус", isPrimary = 0, fields = "Stage")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public int Stage;

}
