package ru.magnat.sfs.bom.ref.brand;

import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefBrand.class)
public final class RefBrandEntity extends RefGenericEntity<RefBrand, RefGenericEntity> {}
