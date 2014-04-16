package ru.magnat.sfs.bom.query.measures;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetMarketingMeasuresEntity extends QueryGenericEntity<QueryGetMarketingMeasures> {
	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "draft")
	public int Draft;
	
	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "customer_legal_name")
	public String CustomerLegalName;
	
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "customer_address")
	public String CustomerAddress;
	
	@OrmEntityField(DisplayName = "����� ��������", isPrimary = 0, fields = "create_date")
	public Date CreateDate;
	
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "is_accepted")
	public int IsAccepted;
}
