package ru.magnat.sfs.bom.reg.generic;

import ru.magnat.sfs.bom.GenericEntity;

public abstract class RegGenericEntity<T extends RegGeneric<? extends RegGenericEntity<T>>>
		extends GenericEntity<T> {

}
