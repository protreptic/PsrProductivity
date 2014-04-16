package ru.magnat.sfs.bom.ref.contact;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefContact.class)
public final class RefContactEntity extends
		RefGenericEntity<RefContact, RefCustomerEntity> {
	@OrmEntityField(DisplayName = "Телефон", isPrimary = 0, fields = "Phone")
	public String Phone;
	@OrmEntityField(DisplayName = "Эл.адрес", isPrimary = 0, fields = "Email")
	public String Email;

}
