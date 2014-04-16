package ru.magnat.sfs.bom.query.debts;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetDebtsDocEntity extends
		QueryGenericEntity<QueryGetDebtsDoc> {
	@OrmEntityField(DisplayName = "Информация", isPrimary = 0, fields = "CreditInfo")
	public String CreditInfo;
	@OrmEntityField(DisplayName = "Тип кредита", isPrimary = 0, fields = "PaymentTypeDescr")
	public String PaymentTypeDescr;
	
	@OrmEntityField(DisplayName = "ПДЗ", isPrimary = 0, fields = "Debt")
	public float Debt;
	@OrmEntityField(DisplayName = "Долг", isPrimary = 0, fields = "DocSum")
	public float DocSum;
	@OrmEntityField(DisplayName = "Дата оплаты", isPrimary = 0, fields = "PaymentData")
	public Date PaymentDate;
	@OrmEntityField(DisplayName = "Дата отгрузки", isPrimary = 0, fields = "DocDate")
	public Date DocDate;
	
	
	


}
