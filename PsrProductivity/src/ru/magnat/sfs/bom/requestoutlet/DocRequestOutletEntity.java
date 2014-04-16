package ru.magnat.sfs.bom.requestoutlet;

import java.util.Date;

import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.ref.approvestate.RefApproveStateEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceTypeEntity;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannelEntity;
import ru.magnat.sfs.bom.ref.storetype.RefStoreTypeEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import android.content.Context;

@OrmEntityOwner(owner = DocRequestOutletJournal.class)
public final class DocRequestOutletEntity
		extends
		DocGenericEntity<DocRequestOutletJournal, DocRequestOutletLineEntity> {

	// ���������� � �����
	//@EntityCardField(DisplayName = "�������� �����", Sortkey = 0, SelectMethod = "changeOutlet")
	@OrmEntityField(DisplayName = "�������� �����", isPrimary = 0, fields = "Outlet")
	public RefOutletEntity Outlet;

	@EntityCardField(DisplayName = "������������", Sortkey = 1, SelectMethod = "changeDescr")
	@OrmEntityField(DisplayName = "������������", isPrimary = 0, fields = "Descr")
	public String Descr;
	@EntityCardField(DisplayName = "�����", Sortkey = 11, SelectMethod = "changeCity")
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "City")
	public String City;
	
	@EntityCardField(DisplayName = "����������� �����", Sortkey = 12, SelectMethod = "changeAddress")
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Address")
	public String Address;

	@EntityCardField(DisplayName = "����������� ����", Sortkey = 13, SelectMethod = "changeLegalAddress")
	@OrmEntityField(DisplayName = "����������� ����", isPrimary = 0, fields = "LegalAddress")
	public String LegalAddress;

	@EntityCardField(DisplayName = "�������� ����", Sortkey = 14, SelectMethod = "changeKeyPerson")
	@OrmEntityField(DisplayName = "�������� ����", isPrimary = 0, fields = "KeyPerson")
	public String KeyPerson;

	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "LocationLat")
	public float LocationLat;
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "LocationLon")
	public float LocationLon;

	//@EntityCardField(DisplayName = "����������� ����", Sortkey = 15, SelectMethod = "changeHQ")
	@OrmEntityField(DisplayName = "��", isPrimary = 0, fields = "IsHQ")
	public Boolean IsHQ;

	//@EntityCardField(DisplayName = "����������������� �����", Sortkey = 16, SelectMethod = "changeDC")
	@OrmEntityField(DisplayName = "��", isPrimary = 0, fields = "IsDC")
	public Boolean IsDC;

	//@EntityCardField(DisplayName = "�������", Sortkey = 17, SelectMethod = "changeShop")
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "IsShop")
	public Boolean IsShop;

	//@EntityCardField(DisplayName = "����� �������� ������", Sortkey = 18, SelectMethod = "changeOP")
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "IsOP")
	public Boolean IsOP;

	@EntityCardField(DisplayName = "���������� ����� � ����", Sortkey = 19, SelectMethod = "changeChainSize")
	@OrmEntityField(DisplayName = "���������� ����� � ����", isPrimary = 0, fields = "ChainSize")
	public int ChainSize;
	
	@EntityCardField(DisplayName = "������ ��������", Sortkey = 20, SelectMethod = "changeSizeVariant")
	@OrmEntityField(DisplayName = "������ ��������", isPrimary = 0, fields = "SizeVariant")
	public OutletSizeVariant SizeVariant;
	
	
	//@EntityCardField(DisplayName = "���������� ��������� ���.�����", Sortkey = 21, SelectMethod = "changeLcbCategories")
	@OrmEntityField(DisplayName = "���������� ��������� ���.�����", isPrimary = 0, fields = "LcbCategories")
	public int LcbCategories;
	
	//@EntityCardField(DisplayName = "���������� ��������� P&G", Sortkey = 22, SelectMethod = "changeLcbPgCategories")
	@OrmEntityField(DisplayName = "���������� ��������� P&G", isPrimary = 0, fields = "LcbPgCategories")
	public int LcbPgCategories;
	
	@EntityCardField(DisplayName = "������� ������� �����", Sortkey = 23, SelectMethod = "changeLcbShare")
	@OrmEntityField(DisplayName = "������� ������� �����", isPrimary = 0, fields = "LcbShare")
	public int LcbShare;
	
	@EntityCardField(DisplayName = "������� ���������", Sortkey = 24, SelectMethod = "changeCurrentSupplier")
	@OrmEntityField(DisplayName = "������� ���������", isPrimary = 0, fields = "CurrentSupplier")
	public String CurrentSupplier;
	
	@EntityCardField(DisplayName = "�����", Sortkey = 25, SelectMethod = "changeChannel")
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Channel")
	public RefStoreChannelEntity Channel;

	@EntityCardField(DisplayName = "��� ������������", Sortkey = 26, SelectMethod = "changeServiceType")
	@OrmEntityField(DisplayName = "������������", isPrimary = 0, fields = "ServiceType")
	public RefServiceTypeEntity ServiceType;

	//@EntityCardField(DisplayName = "��� �����", Sortkey = 27, SelectMethod = "changeStoreType")
	@OrmEntityField(DisplayName = "���", isPrimary = 0, fields = "StoreType")
	public RefStoreTypeEntity StoreType;

	@EntityCardField(DisplayName = "������ �������", Sortkey = 28, SelectMethod = "")
	@OrmEntityField(DisplayName = "������ �������", isPrimary = 0, fields = "Approved")
	public RefApproveStateEntity Approved;

	@EntityCardField(DisplayName = "����������� �� ������� �������", Sortkey = 29, SelectMethod = "")
	@OrmEntityField(DisplayName = "����������� �� ������� �������", isPrimary = 0, fields = "ApprovedComment")
	public String ApprovedComment;
	
	@OrmEntityField(DisplayName = "�������������", isPrimary = 0, fields = "IsPotential")
	public Boolean IsPotential;


	@Override
	final protected Class<?> getLinesContainer() {
		// return DocMarketingMeasureLine.class;
		return DocRequestOutletLine.class;
	}

	@Override
	final public void setDefaults(Context context, GenericEntity<?> owner) {
		this.CreateDate = new Date();
		this.IsAccepted = false;
		this.IsMark = false;
		this.Employee = Globals.getEmployee();
	    this.IsPotential = true;
	    this.ChainSize = 1;
		TaskWorkdayEntity workday = (TaskWorkdayEntity) owner;
		if (workday != null) {
			this.Author = workday.Author;
			this.MasterTask = workday;
		}
		this.Approved = Globals.getFirstApproveState();
	}

	
	@Override
	public String toString() {
		String str = "������ ������������� �� � ";
		if (Author == null)
			return "������������ ������ ������������� ��";
		return (str + String.format("%03d/%06d", Author.Id, Id)); // +
																	// ((this.CreateDate==null)?"":" �� "
																	// +
																	// DateFormat.format("dd MMMM yyyy",
																	// this.CreateDate))
		// ;
	}
	public Boolean checkString(String str){
		if (str==null)
			return false;
		if (str.trim().isEmpty())
			return false;
		return true;
	}
	public boolean checkData() {
		if (!checkString(Descr))
			return false;
		if (!checkString(Address))
			return false;
		if (!checkString(City))
			return false;
		if (!checkString(LegalAddress))
			return false;
		if (SizeVariant==null)
			return false;
		if (Channel==null)
			return false;
		if (ServiceType==null)
			return false;
		return true;
	}
}