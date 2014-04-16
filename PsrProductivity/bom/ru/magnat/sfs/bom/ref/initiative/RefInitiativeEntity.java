package ru.magnat.sfs.bom.ref.initiative;

import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefInitiative.class)
public final class RefInitiativeEntity extends
		RefGenericEntity<RefInitiative, RefGenericEntity> {

}
