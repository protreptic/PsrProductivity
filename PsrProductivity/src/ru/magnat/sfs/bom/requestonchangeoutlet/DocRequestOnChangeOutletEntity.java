package ru.magnat.sfs.bom.requestonchangeoutlet;

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

@OrmEntityOwner(owner = DocRequestOnChangeOutletJournal.class)
public final class DocRequestOnChangeOutletEntity
		extends
		DocGenericEntity<DocRequestOnChangeOutletJournal, DocRequestOnChangeOutletLineEntity> {

	// ���������� � �����
	@EntityCardField(DisplayName = "�������� �����", Sortkey = 0, SelectMethod = "changeOutlet")
	@OrmEntityField(DisplayName = "�������� �����", isPrimary = 0, fields = "Outlet")
	public RefOutletEntity Outlet;

	@EntityCardField(DisplayName = "������������", Sortkey = 1, SelectMethod = "changeDescr")
	@OrmEntityField(DisplayName = "������������", isPrimary = 0, fields = "Descr")
	public String Descr;

	@EntityCardField(DisplayName = "�����", Sortkey = 11, SelectMethod = "changeAddress")
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Address")
	public String Address;

	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "LocationLat")
	public float LocationLat;
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "LocationLon")
	public float LocationLon;

	@EntityCardField(DisplayName = "����������� ����", Sortkey = 15, SelectMethod = "changeHQ")
	@OrmEntityField(DisplayName = "��", isPrimary = 0, fields = "IsHQ")
	public Boolean IsHQ;

	@EntityCardField(DisplayName = "����������������� �����", Sortkey = 16, SelectMethod = "changeDC")
	@OrmEntityField(DisplayName = "��", isPrimary = 0, fields = "IsDC")
	public Boolean IsDC;

	@EntityCardField(DisplayName = "�������", Sortkey = 17, SelectMethod = "changeShop")
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "IsShop")
	public Boolean IsShop;

	@EntityCardField(DisplayName = "����� �������� ������", Sortkey = 18, SelectMethod = "changeOP")
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "IsOP")
	public Boolean IsOP;

	@EntityCardField(DisplayName = "�����", Sortkey = 12, SelectMethod = "changeChannel")
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Channel")
	public RefStoreChannelEntity Channel;

	@EntityCardField(DisplayName = "��� ������������", Sortkey = 13, SelectMethod = "changeServiceType")
	@OrmEntityField(DisplayName = "������������", isPrimary = 0, fields = "ServiceType")
	public RefServiceTypeEntity ServiceType;

	@EntityCardField(DisplayName = "��� �����", Sortkey = 14, SelectMethod = "changeStoreType")
	@OrmEntityField(DisplayName = "���", isPrimary = 0, fields = "StoreType")
	public RefStoreTypeEntity StoreType;

	@EntityCardField(DisplayName = "������ �������", Sortkey = 19, SelectMethod = "")
	// Added SDV 2012-09-11
	@OrmEntityField(DisplayName = "������ �������", isPrimary = 0, fields = "Approved")
	public RefApproveStateEntity Approved;

	@EntityCardField(DisplayName = "����������� �� ������� �������", Sortkey = 20, SelectMethod = "")
	// Added SDV 2012-09-11
	@OrmEntityField(DisplayName = "����������� �� ������� �������", isPrimary = 0, fields = "ApprovedComment")
	public String ApprovedComment;

	// @OrmEntityField(DisplayName = "������� �����", isPrimary = 0, fields =
	// "GoalMatrix") // ���� ������ ��� �� ����� ������ �� ���� �����
	// public RefKpiMatrixEntity GoalMatrix;
	//
	//
	//

	@Override
	final protected Class<?> getLinesContainer() {
		// return DocMarketingMeasureLine.class;
		return DocRequestOnChangeOutletLine.class;
	}

	@Override
	final public void setDefaults(Context context, GenericEntity<?> owner) {
		this.CreateDate = new Date();
		this.IsAccepted = false;
		this.IsMark = false;
		this.Employee = Globals.getEmployee();

		TaskWorkdayEntity workday = (TaskWorkdayEntity) owner;
		if (workday != null) {
			this.Author = workday.Author;
			this.MasterTask = workday;
		}
		this.Approved = Globals.getFirstApproveState();
	}

	final public void setContent(RefOutletEntity entity) {

		if (entity == null) {
			this.Outlet = entity;
			this.Address = "";
			this.Channel = null;
			this.IsDC = false;
			this.IsHQ = false;
			this.IsOP = false;
			this.IsShop = true;
			this.Descr = "";
			this.LocationLat = 0;
			this.LocationLon = 0;
			this.ServiceType = null;
			this.StoreType = null;

			return;
		}
		this.Outlet = entity;
		this.Address = entity.Address;
		this.Channel = entity.Channel;
		this.IsDC = entity.IsDC;
		this.IsHQ = entity.IsHQ;
		this.IsOP = entity.IsOP;
		this.IsShop = entity.IsShop;
		this.Descr = entity.Descr;
		this.LocationLat = entity.LocationLat;
		this.LocationLon = entity.LocationLon;
		this.ServiceType = entity.ServiceType;
		this.StoreType = entity.StoreType;

	}

	// //listeners
	//
	//
	// private final Set<IEventListener> _eventOutletChangedListeners = new
	// CopyOnWriteArraySet<IEventListener>();
	// public void setOnOutletChangedListener(OnOutletChangedListener
	// eventListener) {
	// EventListenerSubscriber.setListener(_eventOutletChangedListeners,
	// eventListener);
	// }
	// public void addOnOutletChangedListener(OnOutletChangedListener
	// eventListener) {
	// EventListenerSubscriber.addListener(_eventOutletChangedListeners,
	// eventListener);
	// }
	// public void removeOnOutletChangedListener(OnOutletChangedListener
	// eventListener) {
	// EventListenerSubscriber.removeListener(_eventOutletChangedListeners,
	// eventListener);
	// }
	// public void onOutletChanged(RefOutletEntity old_value, RefOutletEntity
	// new_value) {
	// for (IEventListener eventListener : _eventOutletChangedListeners)
	// ((OnOutletChangedListener)eventListener).onOutletChanged(this, old_value,
	// new_value);
	// }
	// private final Set<IEventListener> _eventCreditRequestChangedListeners =
	// new CopyOnWriteArraySet<IEventListener>();
	// public void
	// setOnMarketingMeasureChangedListener(OnCreditRequestChangedListener
	// eventListener) {
	// EventListenerSubscriber.setListener(_eventCreditRequestChangedListeners,
	// eventListener);
	// }
	// public void
	// addOnMarketingMeasureChangedListener(OnCreditRequestChangedListener
	// eventListener) {
	// EventListenerSubscriber.addListener(_eventCreditRequestChangedListeners,
	// eventListener);
	// }
	// public void
	// removeOnMarketingMeasureChangedListener(OnCreditRequestChangedListener
	// eventListener) {
	// EventListenerSubscriber.removeListener(_eventCreditRequestChangedListeners,
	// eventListener);
	// }
	// public void onMarketingMeasureChanged(RefMarketingMeasureEntity
	// old_value, RefMarketingMeasureEntity new_value) {
	// for (IEventListener eventListener : _eventCreditRequestChangedListeners)
	// ((OnMarketingMeasureChangedListener)eventListener).onMarketingMeasureChanged(this,
	// old_value, new_value);
	// }
	//
	// private final Set<IEventListener> _eventClosingListeners = new
	// CopyOnWriteArraySet<IEventListener>();
	// public void setOnClosingListener(OnClosingListener eventListener){
	// EventListenerSubscriber.setListener(_eventClosingListeners,
	// eventListener);
	// }
	// public void addOnClosingListener(OnClosingListener eventListener){
	// EventListenerSubscriber.addListener(_eventClosingListeners,
	// eventListener);
	// }
	// public void onClosing() {
	//
	// for (IEventListener eventListener : _eventClosingListeners)
	// ((OnClosingListener)eventListener).onClosing(this);
	// }
	@Override
	public String toString() {
		String str = "������ �� "
				+ ((Outlet == null) ? "����������" : "���������") + " �� � ";
		if (Author == null)
			return "������������ ������ �� ���������/���������� �����������";
		return (str + String.format("%03d/%06d", Author.Id, Id)); // +
																	// ((this.CreateDate==null)?"":" �� "
																	// +
																	// DateFormat.format("dd MMMM yyyy",
																	// this.CreateDate))
		// ;
	}
}