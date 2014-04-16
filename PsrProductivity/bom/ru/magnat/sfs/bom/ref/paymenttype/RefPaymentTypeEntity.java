package ru.magnat.sfs.bom.ref.paymenttype;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.business.RefBusinessEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefPaymentType.class)
public final class RefPaymentTypeEntity extends RefGenericEntity {
	@OrmEntityField(DisplayName = "Направление бизнеса", isPrimary = 0, fields = "Business")
	public RefBusinessEntity Business;
	@OrmEntityField(DisplayName = "Отсрочка", isPrimary = 0, fields = "Delay")
	public long Delay;
	@OrmEntityField(DisplayName = "Тип отсрочки", isPrimary = 0, fields = "DelayScale")
	public long DelayScale;
	@OrmEntityField(DisplayName = "Граничная дата оплаты", isPrimary = 0, fields = "Maxpaydate")
	public Date Maxpaydate;
	@OrmEntityField(DisplayName = "Ассортимент", isPrimary = 0, fields = "Assortment")
	public RefAssortmentEntity Assortment;
	@OrmEntityField(DisplayName = "Лимит", isPrimary = 0, fields = "MaxLimit")
	public float Limit;
	@OrmEntityField(DisplayName = "Кредитная линия", isPrimary = 0, fields = "CreditLine")
	public Boolean CreditLine;
	@OrmEntityField(DisplayName = "До конца месяца", isPrimary = 0, fields = "ToEOM")
	public Boolean ToEOM;
	@OrmEntityField(DisplayName = "Всеобщий", isPrimary = 0, fields = "IsCommon")
	public Boolean IsCommon;
}
