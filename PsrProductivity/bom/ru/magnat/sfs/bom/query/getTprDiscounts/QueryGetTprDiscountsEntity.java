package ru.magnat.sfs.bom.query.getTprDiscounts;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetTprDiscountsEntity extends QueryGenericEntity<QueryGetTprDiscounts> {
	
	@OrmEntityField(DisplayName = "Описание акции", isPrimary = 0, fields = "Descr")
	public String Descr;
	
	@OrmEntityField(DisplayName = "Тип промо", isPrimary = 0, fields = "PromoType")
	public int PromoType;
	
	@OrmEntityField(DisplayName = "Начало акции", isPrimary = 0, fields = "BeginOfAction")
	public Date BeginOfAction;
	
	@OrmEntityField(DisplayName = "Конец акции", isPrimary = 0, fields = "EndOfAction")
	public Date EndOfAction;
}
