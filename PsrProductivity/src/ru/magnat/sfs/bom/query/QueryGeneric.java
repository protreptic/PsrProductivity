package ru.magnat.sfs.bom.query;

import ru.magnat.sfs.bom.OrmObject;
import android.content.Context;

public abstract class QueryGeneric<E extends QueryGenericEntity<? extends QueryGeneric<E>>> extends OrmObject<E> {

	public QueryGeneric(Context context, Class<?> entityType, String query) {
		super(context, entityType);
		
		_query = query;
	}

	/**
	 * 	
	 * 	Должен быть переопределен для сложных ключей
	 * 
	 * 	Should be overridden for a complicated keys
	 */
	public Boolean Find(E entity) {
		return (FindById(entity.Id) != null);
	}

}
