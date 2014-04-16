package ru.magnat.sfs.bom.contract;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public class QueryGetContractorsEntity extends QueryGenericEntity<QueryGetContractors> {
	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "draft")
	public int Draft;
	
	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "contractor_legal_name")
	public String ContractorLegalName;
	
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "contractor_address")
	public String ContractorAddress;
	
	@OrmEntityField(DisplayName = "����� ��������", isPrimary = 0, fields = "create_date")
	public Date CreateDate;
	
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "is_accepted")
	public int IsAccepted;
	
	@OrmEntityField(DisplayName = "������ ���������", isPrimary = 0, fields = "approved_status")
	public int Status;
}
