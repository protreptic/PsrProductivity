package ru.magnat.sfs.bom.contract;

import java.util.Date;

import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.ref.branch.RefBranchEntity;
import ru.magnat.sfs.bom.ref.business.RefBusinessEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceTypeEntity;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannelEntity;
import ru.magnat.sfs.bom.ref.user.RefUserEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.doc.contract.BusinessStructure;
import ru.magnat.sfs.ui.android.doc.contract.ClientType;
import ru.magnat.sfs.ui.android.doc.contract.OutletRemoteness;
import ru.magnat.sfs.ui.android.doc.contract.PaymentTerm;
import ru.magnat.sfs.ui.android.doc.contract.RequestStatusType;
import android.content.Context;

public class DocContractEntity extends DocGenericEntity<DocContractJournal, DocContractLineEntity>  {

	@EntityCardField(DisplayName = "Статус документа", Sortkey = 1, SelectMethod = "")
	@OrmEntityField(DisplayName = "Статус документа", isPrimary = 0, fields = "Approved")
	public RequestStatusType Approved;
	
	@EntityCardField(DisplayName = "Комментарий статуса", Sortkey = 1, SelectMethod = "")
	@OrmEntityField(DisplayName = "Комментарий статуса", isPrimary = 0, fields = "ApprovedComment")
	public String ApprovedComment;
	
	@OrmEntityField(DisplayName = "", isPrimary = 0, fields = "AgreementWasGivenToContractorDate")
	public Date AgreementWasGivenToContractorDate;
	
	@EntityCardField(DisplayName = "Договор предан КА\n(Нажмите, когда подписанный договор будет передан контрагенту)", Sortkey = 3, SelectMethod = "setAgreementWasGivenToContractor")
	public Boolean AgreementWasGivenToContractor;
	
	@OrmEntityField(DisplayName = "Пользователь", isPrimary = 0, fields = "UserId")
	public RefUserEntity User;
	
	@OrmEntityField(DisplayName = "Торговый представитель", isPrimary = 0, fields = "Employee")
	public RefEmployeeEntity EmployeeId;
	
	@OrmEntityField(DisplayName = "Торговый представитель", isPrimary = 0, fields = "PsrId")
	public RefEmployeeEntity Employee;
	
	@OrmEntityField(DisplayName = "Контактный телефон торгового представителя", isPrimary = 0, fields = "TelNumber")
	public String TelNumber;
	
	@OrmEntityField(DisplayName = "Локация", isPrimary = 0, fields = "Territory")
	public RefBranchEntity Territory;
	
	@EntityCardField(DisplayName = "Организационно-правовая форма (обязательное)", Sortkey = 4, SelectMethod = "setBusinessStructureType")
	@OrmEntityField(DisplayName = "Организационно-правовая форма", isPrimary = 0, fields = "BusinessStructureType")
	public BusinessStructure BusinessStructureType;
	
	@EntityCardField(DisplayName = "Наименование контрагента (обязательное)", Sortkey = 5, SelectMethod = "setContractorLegalName")
	@OrmEntityField(DisplayName = "Наименование контрагента", isPrimary = 0, fields = "ContractorLegalName")
	public String ContractorLegalName;
	
	@EntityCardField(DisplayName = "Юридический адрес (обязательное)", Sortkey = 6, SelectMethod = "setLegalAddress")
	@OrmEntityField(DisplayName = "Юридический адрес", isPrimary = 0, fields = "LegalAddress")
	public String LegalAddress;
	
	@EntityCardField(DisplayName = "Телефоны контактных лиц контрагента", Sortkey = 7, SelectMethod = "setPhoneContacts")
	@OrmEntityField(DisplayName = "Телефоны контактных лиц контрагента", isPrimary = 0, fields = "PhoneContacts")
	public String PhoneContacts;
	
	@EntityCardField(DisplayName = "Предполагаемые условия оплаты (обязательное)", Sortkey = 8, SelectMethod = "setPaymentTerms")
	@OrmEntityField(DisplayName = "Предполагаемые условия оплаты", isPrimary = 0, fields = "PaymentTerms")
	public PaymentTerm PaymentTerms;
	
	@EntityCardField(DisplayName = "ИНН (обязательное)", Sortkey = 9, SelectMethod = "setUniqueTaxpayerId")
	@OrmEntityField(DisplayName = "ИНН", isPrimary = 0, fields = "UniqueTaxpayerId")
	public String UniqueTaxpayerId;
	
	@EntityCardField(DisplayName = "КПП", Sortkey = 10, SelectMethod = "setRegistrationCause")
	@OrmEntityField(DisplayName = "КПП", isPrimary = 0, fields = "RegistrationCause")
	public String RegistrationCause;

	@EntityCardField(DisplayName = "Адреса доставки (обязательное)", Sortkey = 11, SelectMethod = "setShippingAddress")
	@OrmEntityField(DisplayName = "Адреса доставки", isPrimary = 0, fields = "ShippingAddress")
	public String ShippingAddress;
	
	@EntityCardField(DisplayName = "БИК", Sortkey = 12, SelectMethod = "setBankId")
	@OrmEntityField(DisplayName = "БИК", isPrimary = 0, fields = "BankId")
	public String BankId;
	
	@EntityCardField(DisplayName = "Наименование банка", Sortkey = 13, SelectMethod = "setBankLegalName")
	@OrmEntityField(DisplayName = "Наименование банка", isPrimary = 0, fields = "BankLegalName")
	public String BankLegalName;
	
	@EntityCardField(DisplayName = "Расчетный счет", Sortkey = 14, SelectMethod = "setCurrentAccount")
	@OrmEntityField(DisplayName = "Расчетный счет", isPrimary = 0, fields = "CurrentAccount")
	public String CurrentAccount;
	
	@EntityCardField(DisplayName = "Свидетельство о государственной регистрации", Sortkey = 15, SelectMethod = "setStateRegistrationCertificate")
	@OrmEntityField(DisplayName = "Свидетельство о государственной регистрации", isPrimary = 0, fields = "StateRegistrationCertificate")
	public String StateRegistrationCertificate;
	
	@EntityCardField(DisplayName = "Дни посещения", Sortkey = 16, SelectMethod = "setVisitDays")
	@OrmEntityField(DisplayName = "Дни посещения", isPrimary = 0, fields = "VisitDays")
	public String VisitDays;
	
	@EntityCardField(DisplayName = "Дополнительные комментарии торгового представителя", Sortkey = 17, SelectMethod = "setPsrComment")
	@OrmEntityField(DisplayName = "Дополнительные комментарии торгового представителя", isPrimary = 0, fields = "PsrComment")
	public String PsrComment;
	
	@OrmEntityField(DisplayName = "Направление бизнеса", isPrimary = 0, fields = "BusinessType")
	public RefBusinessEntity BusinessType;
	
	@EntityCardField(DisplayName = "Канал реализации (обязательное)", Sortkey = 18, SelectMethod = "setSalesChannelType")
	@OrmEntityField(DisplayName = "Канал реализации (сегментация)", isPrimary = 0, fields = "SalesChannelType")
	public RefStoreChannelEntity SalesChannelType;
	
	@EntityCardField(DisplayName = "Тип клиента", Sortkey = 19, SelectMethod = "setContractorType")
	@OrmEntityField(DisplayName = "Тип клиента", isPrimary = 0, fields = "ContractorType")
	public ClientType ContractorType;
	
	@EntityCardField(DisplayName = "Количество торговых точек", Sortkey = 20, SelectMethod = "setAmountStores")
	@OrmEntityField(DisplayName = "Количество торговых точек", isPrimary = 0, fields = "AmountStores")
	public long AmountStores;
	
	@EntityCardField(DisplayName = "Сегмент магазина", Sortkey = 21, SelectMethod = "setServiceType")
	@OrmEntityField(DisplayName = "Сегмент магазина", isPrimary = 0, fields = "ServiceType")
	public RefServiceTypeEntity ServiceType;
	
	@EntityCardField(DisplayName = "Удаленность", Sortkey = 22, SelectMethod = "setRemoteness")
	@OrmEntityField(DisplayName = "Удаленность", isPrimary = 0, fields = "Remoteness")
	public OutletRemoteness Remoteness;
	
	@EntityCardField(DisplayName = "Центральный офис", Sortkey = 23, SelectMethod = "setIsHQ")
	@OrmEntityField(DisplayName = "Центральный офис", isPrimary = 0, fields = "IsHQ")
	public Boolean IsHQ;
	
	@EntityCardField(DisplayName = "Распределительный центр", Sortkey = 24, SelectMethod = "setIsDC")
	@OrmEntityField(DisplayName = "Распределительный центр", isPrimary = 0, fields = "IsDC")
	public Boolean IsDC;
	
	@EntityCardField(DisplayName = "Торговая точка", Sortkey = 25, SelectMethod = "setIsShop")
	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "IsShop")
	public Boolean IsShop;
	
	@EntityCardField(DisplayName = "Точка принятия заказа", Sortkey = 26, SelectMethod = "setIsOP")
	@OrmEntityField(DisplayName = "Точка принятия заказа", isPrimary = 0, fields = "IsOP")
	public Boolean IsOP;
	
	@EntityCardField(DisplayName = "Паспорт-серия (обязательное для ИП)", Sortkey = 27, SelectMethod = "setPassportN1")
	@OrmEntityField(DisplayName = "Паспорт-серия", isPrimary = 0, fields = "PassportN1")
	public String PassportN1;
	
	@EntityCardField(DisplayName = "Паспорт-номер (обязательное для ИП)", Sortkey = 28, SelectMethod = "setPassportN2")
	@OrmEntityField(DisplayName = "Паспорт-номер", isPrimary = 0, fields = "PassportN2")
	public String PassportN2;
	
	@EntityCardField(DisplayName = "Паспорт-кем выдан (обязательное для ИП)", Sortkey = 29, SelectMethod = "setPassportN3")
	@OrmEntityField(DisplayName = "Паспорт-кем выдан", isPrimary = 0, fields = "PassportN3")
	public String PassportN3;
	
	@EntityCardField(DisplayName = "Паспорт-когда выдан (обязательное для ИП)", Sortkey = 30, SelectMethod = "setPassportN4")
	@OrmEntityField(DisplayName = "Паспорт-когда выдан", isPrimary = 0, fields = "PassportN4")
	public String PassportN4;
	
	@EntityCardField(DisplayName = "Представитель контрагента полномочен подписывать договор", Sortkey = 31, SelectMethod = "setIsContractorsRepresentativeAuthorizedToSignContract")
	@OrmEntityField(DisplayName = "Представитель контрагента полномочен подписывать договор", isPrimary = 0, fields = "IsContractorsRepresentativeAuthorizedToSignContract")
	public Boolean IsContractorsRepresentativeAuthorizedToSignContract;
	
	@EntityCardField(DisplayName = "В документах от контрагента есть необходимые печати и подписи", Sortkey = 32, SelectMethod = "setIsDocumentsFromContractorHasNecessaryStampsAndSignatures")
	@OrmEntityField(DisplayName = "В документах от контрагента есть необходимые печати и подписи", isPrimary = 0, fields = "IsDocumentsFromContractorHasNecessaryStampsAndSignatures")
	public Boolean IsDocumentsFromContractorHasNecessaryStampsAndSignatures;
	
	@EntityCardField(DisplayName = "ТП взял: подписанный бланк договора с КА (обязательное)", Sortkey = 33, SelectMethod = "setHasAgreementSigned")
	@OrmEntityField(DisplayName = "ТП взял: подписанный бланк договора с КА (обязательное)", isPrimary = 0, fields = "HasAgreementSigned")
	public Boolean HasAgreementSigned;
	
	@EntityCardField(DisplayName = "ТП взял: копию паспорта ИП (обязательное при кредитовании)", Sortkey = 34, SelectMethod = "setHasPassportCopy")
	@OrmEntityField(DisplayName = "ТП взял: копию паспорта ИП (обязательное при кредитовании)", isPrimary = 0, fields = "HasPassportCopy")
	public Boolean HasPassportCopy;
	
	@EntityCardField(DisplayName = "ТП взял: копию ИНН (обязательное)", Sortkey = 35, SelectMethod = "setHasUniqueTaxpayerIdCopy")
	@OrmEntityField(DisplayName = "ТП взял: копию ИНН (обязательное)", isPrimary = 0, fields = "HasUniqueTaxpayerIdCopy")
	public Boolean HasUniqueTaxpayerIdCopy;
	
	@EntityCardField(DisplayName = "ТП взял: копию ОГРНИП (обязательное)", Sortkey = 36, SelectMethod = "setHasStateRegistrationCertificate")
	@OrmEntityField(DisplayName = "ТП взял: копию ОГРНИП (обязательное)", isPrimary = 0, fields = "HasStateRegistrationCertificate")
	public Boolean HasStateRegistrationCertificate;
	
	@EntityCardField(DisplayName = "ТП взял: договор аренды/субаренды", Sortkey = 37, SelectMethod = "setHasLeaseContract")
	@OrmEntityField(DisplayName = "ТП взял: договор аренды/субаренды", isPrimary = 0, fields = "HasLeaseContract")
	public Boolean HasLeaseContract;
	
	@EntityCardField(DisplayName = "ТП взял: выписку из ЕГРИП", Sortkey = 38, SelectMethod = "setHasExtractFromUnifiedStateRegisterOfIndividualEntrepreneurs")
	@OrmEntityField(DisplayName = "ТП взял: выписку из ЕГРИП", isPrimary = 0, fields = "HasExtractFromUnifiedStateRegisterOfIndividualEntrepreneurs")
	public Boolean HasExtractFromUnifiedStateRegisterOfIndividualEntrepreneurs;
	
	@EntityCardField(DisplayName = "ТП взял: протокол/решение о создании", Sortkey = 39, SelectMethod = "setHasProtocolSolutionsToCreate")
	@OrmEntityField(DisplayName = "ТП взял: протокол/решение о создании", isPrimary = 0, fields = "HasProtocolSolutionsToCreate")
	public Boolean HasProtocolSolutionsToCreate;
	
	@EntityCardField(DisplayName = "ТП взял: решение организации о назначении руководителя (обязательное при кредитовании)", Sortkey = 40, SelectMethod = "setHasTheOrganizationsDecisionOnTheAppointmentOfTheHead")
	@OrmEntityField(DisplayName = "ТП взял: решение организации о назначении руководителя (обязательное при кредитовании)", isPrimary = 0, fields = "HasTheOrganizationsDecisionOnTheAppointmentOfTheHead")
	public Boolean HasTheOrganizationsDecisionOnTheAppointmentOfTheHead;
	
	@EntityCardField(DisplayName = "ТП взял: выписку из списка участников Общества", Sortkey = 41, SelectMethod = "setHasExcerptFromTheListOfMembersOfTheCompany")
	@OrmEntityField(DisplayName = "ТП взял: выписку из списка участников Общества", isPrimary = 0, fields = "HasExcerptFromTheListOfMembersOfTheCompany")
	public Boolean HasExcerptFromTheListOfMembersOfTheCompany;
	
	@EntityCardField(DisplayName = "ТП взял: копию Устава (обязательное при кредитовании)", Sortkey = 42, SelectMethod = "setHasCharterCopy")
	@OrmEntityField(DisplayName = "ТП взял: копию Устава (обязательное при кредитовании)", isPrimary = 0, fields = "HasCharterCopy")
	public Boolean HasCharterCopy;
	
	@EntityCardField(DisplayName = "ТП взял: ОГРН (обязательное)", Sortkey = 43, SelectMethod = "setHasPrimaryStateRegistrationNumber")
	@OrmEntityField(DisplayName = "ТП взял: ОГРН (обязательное)", isPrimary = 0, fields = "HasPrimaryStateRegistrationNumber")
	public Boolean HasPrimaryStateRegistrationNumber;
	
	@EntityCardField(DisplayName = "ТП взял: доверенность на лицо подписывающее договор (обязательное при кредитовании)", Sortkey = 44, SelectMethod = "setHasPowerOfAttorneyForThePersonSigningTheContract")
	@OrmEntityField(DisplayName = "ТП взял: доверенность на лицо подписывающее договор (обязательное при кредитовании)", isPrimary = 0, fields = "HasPowerOfAttorneyForThePersonSigningTheContract")
	public Boolean HasPowerOfAttorneyForThePersonSigningTheContract;
	
	@EntityCardField(DisplayName = "ТП взял: выписку из ЕГРЮЛ", Sortkey = 45, SelectMethod = "setHasAnExtractFromTheUnifiedStateRegisterOfLegalEntities")
	@OrmEntityField(DisplayName = "ТП взял: выписку из ЕГРЮЛ", isPrimary = 0, fields = "HasAnExtractFromTheUnifiedStateRegisterOfLegalEntities")
	public Boolean HasAnExtractFromTheUnifiedStateRegisterOfLegalEntities;
	
	@OrmEntityField(DisplayName = "", isPrimary = 0, fields = "Latitude")
	public float Latitude;
	
	@OrmEntityField(DisplayName = "", isPrimary = 0, fields = "Longitude")
	public float Longitude;
	
	@Override
	protected Class<?> getLinesContainer() {
		return null;
	}

	@Override
	public Boolean getReadOnly() {
		if (IsMark) {
			return false;
		}
		if (Approved.equals(new RequestStatusType(1)) && !this.IsAccepted) {
			return false;
		}
		if (Approved.equals(new RequestStatusType(10)) && !this.IsAccepted) {
			return false;
		}
		
		return true;
	}
	
	private TaskWorkdayEntity mWorkday;
	private RefUserEntity mUser;
	private RefEmployeeEntity mEmployee;
	
	@Override
	public void setDefaults(Context context, GenericEntity<?> owner) {
		mWorkday = Globals.getCurrentWorkday();
		mUser = Globals.getUser();
		mEmployee = Globals.getEmployee();
		EmployeeId = mEmployee;
		
		CreateDate = new Date();
		IsAccepted = false;
		IsMark = true;
		Author = mUser; 
		MasterTask = mWorkday;
		
		Latitude = 0f;
		Longitude = 0f;
		AgreementWasGivenToContractor = false;
		AgreementWasGivenToContractorDate = null;
		User = mUser;
		Employee = mEmployee;
		TelNumber = "";
		ContractorLegalName = "";
		BusinessStructureType = null;
		PaymentTerms = null;
		UniqueTaxpayerId = "";
		RegistrationCause = "";
		LegalAddress = "";
		ShippingAddress = "";
		BankId = "";
		BankLegalName = "";
		CurrentAccount = "";
		StateRegistrationCertificate = "";
		PhoneContacts = "";
		VisitDays = "";
		PsrComment = "";
		BusinessType = mEmployee.Business;
		SalesChannelType = null;
		ContractorType = null;
		AmountStores = 0;
		IsDC = false;
		IsShop = false;
		IsHQ = false;
		IsOP = false;
		ServiceType = null;
		Remoteness = null;
		PassportN1 = "";
		PassportN2 = "";
		PassportN3 = "";
		PassportN4 = null; 
		IsContractorsRepresentativeAuthorizedToSignContract = false;
		IsDocumentsFromContractorHasNecessaryStampsAndSignatures = false;
		HasAgreementSigned = false;
		HasPassportCopy = false;
		HasUniqueTaxpayerIdCopy = false;
		HasStateRegistrationCertificate = false;
		HasLeaseContract = false;
		HasExtractFromUnifiedStateRegisterOfIndividualEntrepreneurs = false;
		HasProtocolSolutionsToCreate = false;
		HasTheOrganizationsDecisionOnTheAppointmentOfTheHead = false;
		HasExcerptFromTheListOfMembersOfTheCompany = false;
		HasCharterCopy = false;
		HasPrimaryStateRegistrationNumber = false;
		HasPowerOfAttorneyForThePersonSigningTheContract = false;
		HasAnExtractFromTheUnifiedStateRegisterOfLegalEntities = false;
		ApprovedComment = "";
		Approved = new RequestStatusType(1);
	}	

}
