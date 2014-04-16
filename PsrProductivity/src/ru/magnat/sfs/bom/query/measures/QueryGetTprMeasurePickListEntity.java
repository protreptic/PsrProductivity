package ru.magnat.sfs.bom.query.measures;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

@SuppressWarnings("rawtypes")
public final class QueryGetTprMeasurePickListEntity extends QueryGenericEntity {
	
	@OrmEntityField(DisplayName = "Descr", isPrimary = 0, fields = "Descr")
	public String Descr;
}
