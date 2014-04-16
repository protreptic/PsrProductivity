package ru.magnat.sfs.bom.ref.brand;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;
import ru.magnat.sfs.bom.ref.mediafiles.RefMediaFilesEntity;

@OrmEntityOwner(owner = RefBrand.class)
public final class RefBrandEntity extends
		RefGenericEntity<RefBrand, RefGenericEntity> {
	
}
