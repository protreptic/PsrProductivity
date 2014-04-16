package ru.magnat.sfs.bom.ref.outletservicetype;

import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefServiceType.class)
public final class RefServiceTypeEntity extends
		RefGenericEntity<RefServiceType, RefGenericEntity<?, ?>> {

}
