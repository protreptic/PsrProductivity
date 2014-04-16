package ru.magnat.sfs.bom.query.extras;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetExtrasTypeEntity extends
		QueryGenericEntity<QueryGetExtrasType> {
	@OrmEntityField(DisplayName = "Наименование", isPrimary = 0, fields = "Descr")
	public String Descr;
	@Override
	public String toString() {
		return Descr;
	}
	}
