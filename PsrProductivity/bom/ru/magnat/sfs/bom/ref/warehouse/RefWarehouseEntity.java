package ru.magnat.sfs.bom.ref.warehouse;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.branch.RefBranchEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefWarehouse.class)
public final class RefWarehouseEntity extends RefGenericEntity {
	@OrmEntityField(DisplayName = "Филиал", isPrimary = 0, fields = "Branch")
	public RefBranchEntity Branch;

}
