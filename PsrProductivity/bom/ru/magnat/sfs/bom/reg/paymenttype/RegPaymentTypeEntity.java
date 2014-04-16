package ru.magnat.sfs.bom.reg.paymenttype;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.paymenttype.RefPaymentTypeEntity;
import ru.magnat.sfs.bom.reg.generic.RegEntityField;
import ru.magnat.sfs.bom.reg.generic.RegEntityField.FieldRole;
import ru.magnat.sfs.bom.reg.generic.RegGenericEntity;

@OrmEntityOwner(owner = RegPaymentType.class)
public final class RegPaymentTypeEntity extends RegGenericEntity {

	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "Period")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public Date Period;

	@OrmEntityField(DisplayName = "����������", isPrimary = 0, fields = "Contractor")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefContractorEntity Contractor;

	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Employee")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefEmployeeEntity Employee;

	@OrmEntityField(DisplayName = "��� �������", isPrimary = 0, fields = "PaymentType")
	@RegEntityField(Role = FieldRole.DIMENSION)
	public RefPaymentTypeEntity PaymentType;

	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "Delay")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public int Delay;

	@OrmEntityField(DisplayName = "��� ��������", isPrimary = 0, fields = "DelayScale")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public long DelayScale;

	@OrmEntityField(DisplayName = "��������� ���� ������", isPrimary = 0, fields = "Maxpaydate")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Date Maxpaydate;

	@OrmEntityField(DisplayName = "�����������", isPrimary = 0, fields = "Assortment")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public RefAssortmentEntity Assortment;

	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "MaxLimit")
	@RegEntityField(Role = FieldRole.RESOURCE)
	public float Limit;

	@OrmEntityField(DisplayName = "��������� �����", isPrimary = 0, fields = "CreditLine")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Boolean CreditLine;

	@OrmEntityField(DisplayName = "�� ����� ������", isPrimary = 0, fields = "ToEOM")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Boolean ToEOM;

	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "IsCommon")
	@RegEntityField(Role = FieldRole.PROPERTY)
	public Boolean IsCommon;
}
