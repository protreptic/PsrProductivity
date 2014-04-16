package ru.magnat.sfs.bom.ref.paymenttype;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.business.RefBusinessEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefPaymentType.class)
public final class RefPaymentTypeEntity extends RefGenericEntity {
	@OrmEntityField(DisplayName = "����������� �������", isPrimary = 0, fields = "Business")
	public RefBusinessEntity Business;
	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "Delay")
	public long Delay;
	@OrmEntityField(DisplayName = "��� ��������", isPrimary = 0, fields = "DelayScale")
	public long DelayScale;
	@OrmEntityField(DisplayName = "��������� ���� ������", isPrimary = 0, fields = "Maxpaydate")
	public Date Maxpaydate;
	@OrmEntityField(DisplayName = "�����������", isPrimary = 0, fields = "Assortment")
	public RefAssortmentEntity Assortment;
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "MaxLimit")
	public float Limit;
	@OrmEntityField(DisplayName = "��������� �����", isPrimary = 0, fields = "CreditLine")
	public Boolean CreditLine;
	@OrmEntityField(DisplayName = "�� ����� ������", isPrimary = 0, fields = "ToEOM")
	public Boolean ToEOM;
	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "IsCommon")
	public Boolean IsCommon;
}
