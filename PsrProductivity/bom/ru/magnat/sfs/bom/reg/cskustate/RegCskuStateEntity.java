package ru.magnat.sfs.bom.reg.cskustate;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.initiative.RefInitiativeEntity;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannelEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegCskuState.class)
public final class RegCskuStateEntity extends RegGenericEntity<RegCskuState> {

	@OrmEntityField(DisplayName = "Период", isPrimary = 0, fields = "Period")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Date Period;

	@OrmEntityField(DisplayName = "CSKU", isPrimary = 0, fields = "Csku")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefCskuEntity Csku;

	@OrmEntityField(DisplayName = "Канал", isPrimary = 0, fields = "StoreChannel")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefStoreChannelEntity StoreChannel;

	@OrmEntityField(DisplayName = "ABC", isPrimary = 0, fields = "ABC")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public long ABC;

	@OrmEntityField(DisplayName = "Приоритет", isPrimary = 0, fields = "IsPriority")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Boolean IsPriority;

	@OrmEntityField(DisplayName = "Инициатива", isPrimary = 0, fields = "IsDrive")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Boolean IsDrive;

	@OrmEntityField(DisplayName = "Товары инициативы", isPrimary = 0, fields = "Initiative")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public RefInitiativeEntity Initiative;
	
	@OrmEntityField(DisplayName = "Приоритет след. месяца", isPrimary = 0, fields = "IsNextPriority")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Boolean IsNextPriority;

	@OrmEntityField(DisplayName = "Инициатива след. месяца", isPrimary = 0, fields = "IsNextDrive")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Boolean IsNextDrive;

}
