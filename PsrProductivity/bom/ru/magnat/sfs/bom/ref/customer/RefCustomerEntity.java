package ru.magnat.sfs.bom.ref.customer;

import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.contact.RefContactEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@SuppressWarnings("rawtypes")
@OrmEntityOwner(owner = RefCustomer.class)
public final class RefCustomerEntity extends
		RefGenericEntity<RefCustomer, RefGenericEntity> {
	@EntityCardField(DisplayName = "���������� ����", Sortkey = 3, SelectMethod = "changeContact")
	@OrmEntityField(DisplayName = "���������� ����", isPrimary = 0, fields = "Contact")
	public RefContactEntity Contact;
	@EntityCardField(DisplayName = "��� ISIS", Sortkey = 4, SelectMethod = "")
	@OrmEntityField(DisplayName = "��� ISIS", isPrimary = 0, fields = "ISISCode")
	public long ISISCode;
	@EntityCardField(DisplayName = "WelcomeOffer ������ ��������� ��������", Sortkey = 5, SelectMethod = "")
	@OrmEntityField(DisplayName = "WelcomeOffer ������ ��������� ��������", isPrimary = 0, fields = "WelcomeOfferDiscountByPayment")
	public Boolean WelcomeOfferDiscountByPayment;
	@EntityCardField(DisplayName = "������� ������ ��������� ��������", Sortkey = 6, SelectMethod = "")
	@OrmEntityField(DisplayName = "������� ������ ��������� ��������", isPrimary = 0, fields = "GoldenDiscountByPayment")
	public Boolean GoldenDiscountByPayment;

}
