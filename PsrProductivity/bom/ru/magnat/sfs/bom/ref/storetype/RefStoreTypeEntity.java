package ru.magnat.sfs.bom.ref.storetype;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefStoreType.class)
public final class RefStoreTypeEntity extends
		RefGenericEntity<RefStoreType, RefGenericEntity> {
	
	@OrmEntityField(DisplayName = "Внешний код", isPrimary = 0, fields = "ExtId")
	public String ExtId;

}
