package ru.magnat.sfs.bom.reg.balance;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.paymenttype.RefPaymentTypeEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegBalance.class)
public final class RegBalanceEntity extends RegGenericEntity<RegBalance> {

	@OrmEntityField(DisplayName = "����������", isPrimary = 0, fields = "Contractor")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefContractorEntity Contractor;

	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Employee")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefEmployeeEntity Employee;

	@OrmEntityField(DisplayName = "��� �������", isPrimary = 0, fields = "PaymentType")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefPaymentTypeEntity PaymentType;

	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "CreditInfo")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public String CreditInfo;

	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "Debt")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float Debt;

	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "CollectDebt")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float CollectDebt;

	@OrmEntityField(DisplayName = "����� ���-�", isPrimary = 0, fields = "DocSum")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float DocSum;

	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "PaymentData")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Date PaymentDate;

	@OrmEntityField(DisplayName = "���� ���-�", isPrimary = 0, fields = "DocDate")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Date DocDate;

}
