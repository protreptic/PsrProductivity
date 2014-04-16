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
	@EntityCardField(DisplayName = "Контактное лицо", Sortkey = 3, SelectMethod = "changeContact")
	@OrmEntityField(DisplayName = "Контактное лицо", isPrimary = 0, fields = "Contact")
	public RefContactEntity Contact;
	@EntityCardField(DisplayName = "Код ISIS", Sortkey = 4, SelectMethod = "")
	@OrmEntityField(DisplayName = "Код ISIS", isPrimary = 0, fields = "ISISCode")
	public long ISISCode;
	@EntityCardField(DisplayName = "WelcomeOffer скидка отдельным платежем", Sortkey = 5, SelectMethod = "")
	@OrmEntityField(DisplayName = "WelcomeOffer скидка отдельным платежем", isPrimary = 0, fields = "WelcomeOfferDiscountByPayment")
	public Boolean WelcomeOfferDiscountByPayment;
	@EntityCardField(DisplayName = "Золотая скидка отдельным платежем", Sortkey = 6, SelectMethod = "")
	@OrmEntityField(DisplayName = "Золотая скидка отдельным платежем", isPrimary = 0, fields = "GoldenDiscountByPayment")
	public Boolean GoldenDiscountByPayment;

}
