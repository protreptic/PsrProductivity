package ru.magnat.sfs.bom.ref.branch;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@SuppressWarnings("rawtypes")
@OrmEntityOwner(owner = RefBranch.class)
public final class RefBranchEntity extends RefGenericEntity {
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "LocationLat")
	public float LocationLat;
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "LocationLon")
	public float LocationLon;
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Address")
	public float Address;

}
