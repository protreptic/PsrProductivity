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

	@EntityCardField(DisplayName = "������ ���������", Sortkey = 1, SelectMethod = "")
	@OrmEntityField(DisplayName = "������ ���������", isPrimary = 0, fields = "Approved")
	public RequestStatusType Approved;
	
	@EntityCardField(DisplayName = "����������� �������", Sortkey = 1, SelectMethod = "")
	@OrmEntityField(DisplayName = "����������� �������", isPrimary = 0, fields = "ApprovedComment")
	public String ApprovedComment;
	
	@OrmEntityField(DisplayName = "", isPrimary = 0, fields = "AgreementWasGivenToContractorDate")
	public Date AgreementWasGivenToContractorDate;
	
	@EntityCardField(DisplayName = "������� ������ ��\n(�������, ����� ����������� ������� ����� ������� �����������)", Sortkey = 3, SelectMethod = "setAgreementWasGivenToContractor")
	public Boolean AgreementWasGivenToContractor;
	
	@OrmEntityField(DisplayName = "������������", isPrimary = 0, fields = "UserId")
	public RefUserEntity User;
	
	@OrmEntityField(DisplayName = "�������� �������������", isPrimary = 0, fields = "Employee")
	public RefEmployeeEntity EmployeeId;
	
	@OrmEntityField(DisplayName = "�������� �������������", isPrimary = 0, fields = "PsrId")
	public RefEmployeeEntity Employee;
	
	@OrmEntityField(DisplayName = "���������� ������� ��������� �������������", isPrimary = 0, fields = "TelNumber")
	public String TelNumber;
	
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "Territory")
	public RefBranchEntity Territory;
	
	@EntityCardField(DisplayName = "��������������-�������� ����� (������������)", Sortkey = 4, SelectMethod = "setBusinessStructureType")
	@OrmEntityField(DisplayName = "��������������-�������� �����", isPrimary = 0, fields = "BusinessStructureType")
	public BusinessStructure BusinessStructureType;
	
	@EntityCardField(DisplayName = "������������ ����������� (������������)", Sortkey = 5, SelectMethod = "setContractorLegalName")
	@OrmEntityField(DisplayName = "������������ �����������", isPrimary = 0, fields = "ContractorLegalName")
	public String ContractorLegalName;
	
	@EntityCardField(DisplayName = "����������� ����� (������������)", Sortkey = 6, SelectMethod = "setLegalAddress")
	@OrmEntityField(DisplayName = "����������� �����", isPrimary = 0, fields = "LegalAddress")
	public String LegalAddress;
	
	@EntityCardField(DisplayName = "�������� ���������� ��� �����������", Sortkey = 7, SelectMethod = "setPhoneContacts")
	@OrmEntityField(DisplayName = "�������� ���������� ��� �����������", isPrimary = 0, fields = "PhoneContacts")
	public String PhoneContacts;
	
	@EntityCardField(DisplayName = "�������������� ������� ������ (������������)", Sortkey = 8, SelectMethod = "setPaymentTerms")
	@OrmEntityField(DisplayName = "�������������� ������� ������", isPrimary = 0, fields = "PaymentTerms")
	public PaymentTerm PaymentTerms;
	
	@EntityCardField(DisplayName = "��� (������������)", Sortkey = 9, SelectMethod = "setUniqueTaxpayerId")
	@OrmEntityField(DisplayName = "���", isPrimary = 0, fields = "UniqueTaxpayerId")
	public String UniqueTaxpayerId;
	
	@EntityCardField(DisplayName = "���", Sortkey = 10, SelectMethod = "setRegistrationCause")
	@OrmEntityField(DisplayName = "���", isPrimary = 0, fields = "RegistrationCause")
	public String RegistrationCause;

	@EntityCardField(DisplayName = "������ �������� (������������)", Sortkey = 11, SelectMethod = "setShippingAddress")
	@OrmEntityField(DisplayName = "������ ��������", isPrimary = 0, fields = "ShippingAddress")
	public String ShippingAddress;
	
	@EntityCardField(DisplayName = "���", Sortkey = 12, SelectMethod = "setBankId")
	@OrmEntityField(DisplayName = "���", isPrimary = 0, fields = "BankId")
	public String BankId;
	
	@EntityCardField(DisplayName = "������������ �����", Sortkey = 13, SelectMethod = "setBankLegalName")
	@OrmEntityField(DisplayName = "������������ �����", isPrimary = 0, fields = "BankLegalName")
	public String BankLegalName;
	
	@EntityCardField(DisplayName = "��������� ����", Sortkey = 14, SelectMethod = "setCurrentAccount")
	@OrmEntityField(DisplayName = "��������� ����", isPrimary = 0, fields = "CurrentAccount")
	public String CurrentAccount;
	
	@EntityCardField(DisplayName = "������������� � ��������������� �����������", Sortkey = 15, SelectMethod = "setStateRegistrationCertificate")
	@OrmEntityField(DisplayName = "������������� � ��������������� �����������", isPrimary = 0, fields = "StateRegistrationCertificate")
	public String StateRegistrationCertificate;
	
	@EntityCardField(DisplayName = "��� ���������", Sortkey = 16, SelectMethod = "setVisitDays")
	@OrmEntityField(DisplayName = "��� ���������", isPrimary = 0, fields = "VisitDays")
	public String VisitDays;
	
	@EntityCardField(DisplayName = "�������������� ����������� ��������� �������������", Sortkey = 17, SelectMethod = "setPsrComment")
	@OrmEntityField(DisplayName = "�������������� ����������� ��������� �������������", isPrimary = 0, fields = "PsrComment")
	public String PsrComment;
	
	@OrmEntityField(DisplayName = "����������� �������", isPrimary = 0, fields = "BusinessType")
	public RefBusinessEntity BusinessType;
	
	@EntityCardField(DisplayName = "����� ���������� (������������)", Sortkey = 18, SelectMethod = "setSalesChannelType")
	@OrmEntityField(DisplayName = "����� ���������� (�����������)", isPrimary = 0, fields = "SalesChannelType")
	public RefStoreChannelEntity SalesChannelType;
	
	@EntityCardField(DisplayName = "��� �������", Sortkey = 19, SelectMethod = "setContractorType")
	@OrmEntityField(DisplayName = "��� �������", isPrimary = 0, fields = "ContractorType")
	public ClientType ContractorType;
	
	@EntityCardField(DisplayName = "���������� �������� �����", Sortkey = 20, SelectMethod = "setAmountStores")
	@OrmEntityField(DisplayName = "���������� �������� �����", isPrimary = 0, fields = "AmountStores")
	public long AmountStores;
	
	@EntityCardField(DisplayName = "������� ��������", Sortkey = 21, SelectMethod = "setServiceType")
	@OrmEntityField(DisplayName = "������� ��������", isPrimary = 0, fields = "ServiceType")
	public RefServiceTypeEntity ServiceType;
	
	@EntityCardField(DisplayName = "�����������", Sortkey = 22, SelectMethod = "setRemoteness")
	@OrmEntityField(DisplayName = "�����������", isPrimary = 0, fields = "Remoteness")
	public OutletRemoteness Remoteness;
	
	@EntityCardField(DisplayName = "����������� ����", Sortkey = 23, SelectMethod = "setIsHQ")
	@OrmEntityField(DisplayName = "����������� ����", isPrimary = 0, fields = "IsHQ")
	public Boolean IsHQ;
	
	@EntityCardField(DisplayName = "����������������� �����", Sortkey = 24, SelectMethod = "setIsDC")
	@OrmEntityField(DisplayName = "����������������� �����", isPrimary = 0, fields = "IsDC")
	public Boolean IsDC;
	
	@EntityCardField(DisplayName = "�������� �����", Sortkey = 25, SelectMethod = "setIsShop")
	@OrmEntityField(DisplayName = "�������� �����", isPrimary = 0, fields = "IsShop")
	public Boolean IsShop;
	
	@EntityCardField(DisplayName = "����� �������� ������", Sortkey = 26, SelectMethod = "setIsOP")
	@OrmEntityField(DisplayName = "����� �������� ������", isPrimary = 0, fields = "IsOP")
	public Boolean IsOP;
	
	@EntityCardField(DisplayName = "�������-����� (������������ ��� ��)", Sortkey = 27, SelectMethod = "setPassportN1")
	@OrmEntityField(DisplayName = "�������-�����", isPrimary = 0, fields = "PassportN1")
	public String PassportN1;
	
	@EntityCardField(DisplayName = "�������-����� (������������ ��� ��)", Sortkey = 28, SelectMethod = "setPassportN2")
	@OrmEntityField(DisplayName = "�������-�����", isPrimary = 0, fields = "PassportN2")
	public String PassportN2;
	
	@EntityCardField(DisplayName = "�������-��� ����� (������������ ��� ��)", Sortkey = 29, SelectMethod = "setPassportN3")
	@OrmEntityField(DisplayName = "�������-��� �����", isPrimary = 0, fields = "PassportN3")
	public String PassportN3;
	
	@EntityCardField(DisplayName = "�������-����� ����� (������������ ��� ��)", Sortkey = 30, SelectMethod = "setPassportN4")
	@OrmEntityField(DisplayName = "�������-����� �����", isPrimary = 0, fields = "PassportN4")
	public String PassportN4;
	
	@EntityCardField(DisplayName = "������������� ����������� ���������� ����������� �������", Sortkey = 31, SelectMethod = "setIsContractorsRepresentativeAuthorizedToSignContract")
	@OrmEntityField(DisplayName = "������������� ����������� ���������� ����������� �������", isPrimary = 0, fields = "IsContractorsRepresentativeAuthorizedToSignContract")
	public Boolean IsContractorsRepresentativeAuthorizedToSignContract;
	
	@EntityCardField(DisplayName = "� ���������� �� ����������� ���� ����������� ������ � �������", Sortkey = 32, SelectMethod = "setIsDocumentsFromContractorHasNecessaryStampsAndSignatures")
	@OrmEntityField(DisplayName = "� ���������� �� ����������� ���� ����������� ������ � �������", isPrimary = 0, fields = "IsDocumentsFromContractorHasNecessaryStampsAndSignatures")
	public Boolean IsDocumentsFromContractorHasNecessaryStampsAndSignatures;
	
	@EntityCardField(DisplayName = "�� ����: ����������� ����� �������� � �� (������������)", Sortkey = 33, SelectMethod = "setHasAgreementSigned")
	@OrmEntityField(DisplayName = "�� ����: ����������� ����� �������� � �� (������������)", isPrimary = 0, fields = "HasAgreementSigned")
	public Boolean HasAgreementSigned;
	
	@EntityCardField(DisplayName = "�� ����: ����� �������� �� (������������ ��� ������������)", Sortkey = 34, SelectMethod = "setHasPassportCopy")
	@OrmEntityField(DisplayName = "�� ����: ����� �������� �� (������������ ��� ������������)", isPrimary = 0, fields = "HasPassportCopy")
	public Boolean HasPassportCopy;
	
	@EntityCardField(DisplayName = "�� ����: ����� ��� (������������)", Sortkey = 35, SelectMethod = "setHasUniqueTaxpayerIdCopy")
	@OrmEntityField(DisplayName = "�� ����: ����� ��� (������������)", isPrimary = 0, fields = "HasUniqueTaxpayerIdCopy")
	public Boolean HasUniqueTaxpayerIdCopy;
	
	@EntityCardField(DisplayName = "�� ����: ����� ������ (������������)", Sortkey = 36, SelectMethod = "setHasStateRegistrationCertificate")
	@OrmEntityField(DisplayName = "�� ����: ����� ������ (������������)", isPrimary = 0, fields = "HasStateRegistrationCertificate")
	public Boolean HasStateRegistrationCertificate;
	
	@EntityCardField(DisplayName = "�� ����: ������� ������/���������", Sortkey = 37, SelectMethod = "setHasLeaseContract")
	@OrmEntityField(DisplayName = "�� ����: ������� ������/���������", isPrimary = 0, fields = "HasLeaseContract")
	public Boolean HasLeaseContract;
	
	@EntityCardField(DisplayName = "�� ����: ������� �� �����", Sortkey = 38, SelectMethod = "setHasExtractFromUnifiedStateRegisterOfIndividualEntrepreneurs")
	@OrmEntityField(DisplayName = "�� ����: ������� �� �����", isPrimary = 0, fields = "HasExtractFromUnifiedStateRegisterOfIndividualEntrepreneurs")
	public Boolean HasExtractFromUnifiedStateRegisterOfIndividualEntrepreneurs;
	
	@EntityCardField(DisplayName = "�� ����: ��������/������� � ��������", Sortkey = 39, SelectMethod = "setHasProtocolSolutionsToCreate")
	@OrmEntityField(DisplayName = "�� ����: ��������/������� � ��������", isPrimary = 0, fields = "HasProtocolSolutionsToCreate")
	public Boolean HasProtocolSolutionsToCreate;
	
	@EntityCardField(DisplayName = "�� ����: ������� ����������� � ���������� ������������ (������������ ��� ������������)", Sortkey = 40, SelectMethod = "setHasTheOrganizationsDecisionOnTheAppointmentOfTheHead")
	@OrmEntityField(DisplayName = "�� ����: ������� ����������� � ���������� ������������ (������������ ��� ������������)", isPrimary = 0, fields = "HasTheOrganizationsDecisionOnTheAppointmentOfTheHead")
	public Boolean HasTheOrganizationsDecisionOnTheAppointmentOfTheHead;
	
	@EntityCardField(DisplayName = "�� ����: ������� �� ������ ���������� ��������", Sortkey = 41, SelectMethod = "setHasExcerptFromTheListOfMembersOfTheCompany")
	@OrmEntityField(DisplayName = "�� ����: ������� �� ������ ���������� ��������", isPrimary = 0, fields = "HasExcerptFromTheListOfMembersOfTheCompany")
	public Boolean HasExcerptFromTheListOfMembersOfTheCompany;
	
	@EntityCardField(DisplayName = "�� ����: ����� ������ (������������ ��� ������������)", Sortkey = 42, SelectMethod = "setHasCharterCopy")
	@OrmEntityField(DisplayName = "�� ����: ����� ������ (������������ ��� ������������)", isPrimary = 0, fields = "HasCharterCopy")
	public Boolean HasCharterCopy;
	
	@EntityCardField(DisplayName = "�� ����: ���� (������������)", Sortkey = 43, SelectMethod = "setHasPrimaryStateRegistrationNumber")
	@OrmEntityField(DisplayName = "�� ����: ���� (������������)", isPrimary = 0, fields = "HasPrimaryStateRegistrationNumber")
	public Boolean HasPrimaryStateRegistrationNumber;
	
	@EntityCardField(DisplayName = "�� ����: ������������ �� ���� ������������� ������� (������������ ��� ������������)", Sortkey = 44, SelectMethod = "setHasPowerOfAttorneyForThePersonSigningTheContract")
	@OrmEntityField(DisplayName = "�� ����: ������������ �� ���� ������������� ������� (������������ ��� ������������)", isPrimary = 0, fields = "HasPowerOfAttorneyForThePersonSigningTheContract")
	public Boolean HasPowerOfAttorneyForThePersonSigningTheContract;
	
	@EntityCardField(DisplayName = "�� ����: ������� �� �����", Sortkey = 45, SelectMethod = "setHasAnExtractFromTheUnifiedStateRegisterOfLegalEntities")
	@OrmEntityField(DisplayName = "�� ����: ������� �� �����", isPrimary = 0, fields = "HasAnExtractFromTheUnifiedStateRegisterOfLegalEntities")
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
