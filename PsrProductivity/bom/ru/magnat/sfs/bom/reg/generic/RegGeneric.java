package ru.magnat.sfs.bom.reg.generic;

import ru.magnat.sfs.bom.OrmObject;

import android.content.Context;

public abstract class RegGeneric<T extends RegGenericEntity<? extends RegGeneric<T>>>
		extends OrmObject<T> {

	public RegGeneric(Context context, Class<?> entityType) {
		super(context, entityType);

	}

	abstract public Boolean Select(Object dimensions[]);

}
