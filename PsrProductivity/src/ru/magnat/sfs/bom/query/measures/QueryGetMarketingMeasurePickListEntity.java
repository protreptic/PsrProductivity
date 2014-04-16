package ru.magnat.sfs.bom.query.measures;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

@SuppressWarnings("rawtypes")
public final class QueryGetMarketingMeasurePickListEntity extends QueryGenericEntity {
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "marketingMeasureObjectId")
	public long marketingMeasureObjectId;
	@OrmEntityField(DisplayName = "������������", isPrimary = 0, fields = "marketingMeasureObjectDescr")
	public String marketingMeasureObjectDescr;
}
