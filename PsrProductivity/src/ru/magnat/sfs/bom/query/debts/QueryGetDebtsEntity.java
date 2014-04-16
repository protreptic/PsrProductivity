package ru.magnat.sfs.bom.query.debts;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetDebtsEntity extends
		QueryGenericEntity<QueryGetDebts> {
	@OrmEntityField(DisplayName = "����������", isPrimary = 0, fields = "ContractorDescr")
	public String ContractorDescr;
	@OrmEntityField(DisplayName = "��� �����������", isPrimary = 0, fields = "ContractorId")
	public long ContractorId;
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "CollectDebt")
	public float CollectDebt;
	@OrmEntityField(DisplayName = "���", isPrimary = 0, fields = "Debt")
	public float Debt;
	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "DocSum")
	public float DocSum;


}
