package ru.magnat.sfs.bom.ref.contractor;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.discountzone.RefDiscountZoneEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefContractor.class)
public final class RefContractorEntity extends
		RefGenericEntity<RefContractor, RefCustomerEntity> {
	@OrmEntityField(DisplayName = "Юридическое наименование", isPrimary = 0, fields = "LegalName")
	public String LegalName;
	@OrmEntityField(DisplayName = "Юридический адрес", isPrimary = 0, fields = "LegalAddress")
	public String LegalAddress;
	@OrmEntityField(DisplayName = "Фактический адрес", isPrimary = 0, fields = "RealAddress")
	public String RealAddress;
	@OrmEntityField(DisplayName = "ИНН", isPrimary = 0, fields = "INN")
	public String INN;
	@OrmEntityField(DisplayName = "ОКПО", isPrimary = 0, fields = "OKPO")
	public String OKPO;
	@OrmEntityField(DisplayName = "Банковский счет", isPrimary = 0, fields = "AccountInfo")
	public String AccountInfo;
	@OrmEntityField(DisplayName = "Директор", isPrimary = 0, fields = "Director")
	public String Director;
	@OrmEntityField(DisplayName = "Главный бухгалтер", isPrimary = 0, fields = "Bookkeeper")
	public String Bookkeeper;
	@OrmEntityField(DisplayName = "БИК", isPrimary = 0, fields = "BIC")
	public String BIC;
	@OrmEntityField(DisplayName = "Паспорт", isPrimary = 0, fields = "Passport")
	public String Passport;
	@OrmEntityField(DisplayName = "Телефон", isPrimary = 0, fields = "Phone")
	public String Phone;
	@OrmEntityField(DisplayName = "e-mail", isPrimary = 0, fields = "Email")
	public String Email;
	@OrmEntityField(DisplayName = "Зона", isPrimary = 0, fields = "DiscountZone")
	public RefDiscountZoneEntity DiscountZone;

}
