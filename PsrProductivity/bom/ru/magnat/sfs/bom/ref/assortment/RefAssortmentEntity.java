package ru.magnat.sfs.bom.ref.assortment;

import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefAssortment.class)
public final class RefAssortmentEntity extends
		RefGenericEntity<RefAssortment, RefGenericEntity<?, ?>> {

}
