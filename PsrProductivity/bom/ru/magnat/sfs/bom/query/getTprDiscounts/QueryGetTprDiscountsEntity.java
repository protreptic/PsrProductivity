package ru.magnat.sfs.bom.query.getTprDiscounts;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetTprDiscountsEntity extends QueryGenericEntity<QueryGetTprDiscounts> {
	
	@OrmEntityField(DisplayName = "�������� �����", isPrimary = 0, fields = "Descr")
	public String Descr;
	
	@OrmEntityField(DisplayName = "��� �����", isPrimary = 0, fields = "PromoType")
	public int PromoType;
	
	@OrmEntityField(DisplayName = "������ �����", isPrimary = 0, fields = "BeginOfAction")
	public Date BeginOfAction;
	
	@OrmEntityField(DisplayName = "����� �����", isPrimary = 0, fields = "EndOfAction")
	public Date EndOfAction;
}
