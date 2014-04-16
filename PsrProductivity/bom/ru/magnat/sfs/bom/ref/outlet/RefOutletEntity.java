package ru.magnat.sfs.bom.ref.outlet;

import android.location.Location;
import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;
import ru.magnat.sfs.bom.ref.kpimatrix.RefKpiMatrixEntity;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceTypeEntity;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannelEntity;
import ru.magnat.sfs.bom.ref.storetype.RefStoreTypeEntity;

@OrmEntityOwner(owner = RefOutlet.class)
public final class RefOutletEntity extends
		RefGenericEntity<RefOutlet, RefCustomerEntity> {
	@EntityCardField(DisplayName = "�����", Sortkey = 11, SelectMethod = "ChangeOutlet")
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Address")
	public String Address;
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "LocationLat")
	public float LocationLat;
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "LocationLon")
	public float LocationLon;
	@EntityCardField(DisplayName = "����������� ����", Sortkey = 15, SelectMethod = "ChangeOutlet")
	@OrmEntityField(DisplayName = "��", isPrimary = 0, fields = "IsHQ")
	public Boolean IsHQ;
	@EntityCardField(DisplayName = "����������������� �����", Sortkey = 16, SelectMethod = "ChangeOutlet")
	@OrmEntityField(DisplayName = "��", isPrimary = 0, fields = "IsDC")
	public Boolean IsDC;
	@EntityCardField(DisplayName = "�������", Sortkey = 17, SelectMethod = "ChangeOutlet")
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "IsShop")
	public Boolean IsShop;
	@EntityCardField(DisplayName = "����� �������� ������", Sortkey = 18, SelectMethod = "ChangeOutlet")
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "IsOP")
	public Boolean IsOP;
	@EntityCardField(DisplayName = "�����", Sortkey = 12, SelectMethod = "ChangeOutlet")
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Channel")
	public RefStoreChannelEntity Channel;
	@EntityCardField(DisplayName = "��� ������������", Sortkey = 13, SelectMethod = "ChangeOutlet")
	@OrmEntityField(DisplayName = "������������", isPrimary = 0, fields = "ServiceType")
	public RefServiceTypeEntity ServiceType;
	@EntityCardField(DisplayName = "��� �����", Sortkey = 14, SelectMethod = "ChangeOutlet")
	@OrmEntityField(DisplayName = "���", isPrimary = 0, fields = "StoreType")
	public RefStoreTypeEntity StoreType;
	@OrmEntityField(DisplayName = "������� �����", isPrimary = 0, fields = "GoalMatrix")
	public RefKpiMatrixEntity GoalMatrix;
	@EntityCardField(DisplayName = "��� ISIS", Sortkey = 19, SelectMethod = "")
	@OrmEntityField(DisplayName = "��� ISIS", isPrimary = 0, fields = "ISISCode")
	public long ISISCode;
	@EntityCardField(DisplayName = "WelcomeOffer ������ ��������� ��������", Sortkey = 20, SelectMethod = "")
	@OrmEntityField(DisplayName = "WelcomeOffer ������ ��������� ��������", isPrimary = 0, fields = "WelcomeOfferDiscountByPayment")
	public Boolean WelcomeOfferDiscountByPayment;
	@EntityCardField(DisplayName = "������� ������ ��������� ��������", Sortkey = 21, SelectMethod = "")
	@OrmEntityField(DisplayName = "������� ������ ��������� ��������", isPrimary = 0, fields = "GoldenDiscountByPayment")
	public Boolean GoldenDiscountByPayment;
	@EntityCardField(DisplayName = "��������� ������� Non PG", Sortkey = 22, SelectMethod = "")
	@OrmEntityField(DisplayName = "��������� ������� Non PG", isPrimary = 0, fields = "IsNonPgNotAllowed")
	public Boolean IsNonPgNotAllowed;
	
	@Override
	public String getFooter() {
		return (Address != null) ? Address : "";
	}

	private Location _location = new Location("outletpoint");

	public Location getLocation() {
		if (LocationLat == 0 && LocationLon == 0)
			return null;

		_location.setLatitude(LocationLat);
		_location.setLongitude(LocationLon);
		return _location;
	}

	public boolean isGolden() {
		if (StoreType != null)
			if (StoreType.Descr.toLowerCase().contains("�������"))
				return true;
		return false;
	}
}
