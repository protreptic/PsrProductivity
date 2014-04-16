package ru.magnat.sfs.bom.query.debts;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetDebtsEntity extends
		QueryGenericEntity<QueryGetDebts> {
	@OrmEntityField(DisplayName = "Контрагент", isPrimary = 0, fields = "ContractorDescr")
	public String ContractorDescr;
	@OrmEntityField(DisplayName = "Код контрагента", isPrimary = 0, fields = "ContractorId")
	public long ContractorId;
	@OrmEntityField(DisplayName = "Забрать", isPrimary = 0, fields = "CollectDebt")
	public float CollectDebt;
	@OrmEntityField(DisplayName = "ПДЗ", isPrimary = 0, fields = "Debt")
	public float Debt;
	@OrmEntityField(DisplayName = "Долг", isPrimary = 0, fields = "DocSum")
	public float DocSum;


}
