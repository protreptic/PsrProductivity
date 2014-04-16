package ru.magnat.sfs.bom.requestonchangeroute;

import java.util.Date;

import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.ref.approvestate.RefApproveStateEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import android.content.Context;

@OrmEntityOwner(owner = DocRequestOnChangeRouteJournal.class)
public final class DocRequestOnChangeRouteEntity
		extends
		DocGenericEntity<DocRequestOnChangeRouteJournal, DocRequestOnChangeRouteLineEntity> {

	// ���������� � �����
	@EntityCardField(DisplayName = "�������� �����", Sortkey = 1, SelectMethod = "changeOutlet")
	@OrmEntityField(DisplayName = "�������� �����", isPrimary = 0, fields = "Outlet")
	public RefOutletEntity Outlet;

	@EntityCardField(DisplayName = "���� ������", Sortkey = 2, SelectMethod = "changeVisitDate", format = "dd MMMM yyyy")
	@OrmEntityField(DisplayName = "���� ������", isPrimary = 0, fields = "VisitDate")
	public Date VisitDate;

	@EntityCardField(DisplayName = "����� ������", Sortkey = 3, SelectMethod = "changeVisitTime", format = "kk:mm")
	@OrmEntityField(DisplayName = "����� ������", isPrimary = 0, fields = "VisitTime")
	public Date VisitTime;

	// @EntityCardField(DisplayName = "��� ������", Sortkey = 4, SelectMethod =
	// "changeVisitKind")
	@OrmEntityField(DisplayName = "��� ������", isPrimary = 0, fields = "VisitKindId")
	public int VisitKindId;

	@EntityCardField(DisplayName = "��� ������", Sortkey = 4, SelectMethod = "changeVisitKind")
	public String VisitKind;

	@EntityCardField(DisplayName = "���� ������", Sortkey = 5, SelectMethod = "changeVisitTarget")
	@OrmEntityField(DisplayName = "���� ������", isPrimary = 0, fields = "VisitTarget")
	public String VisitTarget;

	@EntityCardField(DisplayName = "������������ ��������� (���)", Sortkey = 6, SelectMethod = "changeVisitCyclicity")
	@OrmEntityField(DisplayName = "������������ ��������� (���)", isPrimary = 0, fields = "VisitCyclicity")
	public int VisitCyclicity;

	@EntityCardField(DisplayName = "������ �������", Sortkey = 7, SelectMethod = "")
	// Added SDV 2012-09-11
	@OrmEntityField(DisplayName = "������ �������", isPrimary = 0, fields = "Approved")
	public RefApproveStateEntity Approved;

	@EntityCardField(DisplayName = "����������� �� ������� �������", Sortkey = 8, SelectMethod = "")
	// Added SDV 2012-09-11
	@OrmEntityField(DisplayName = "����������� �� ������� �������", isPrimary = 0, fields = "ApprovedComment")
	public String ApprovedComment;

	@Override
	final protected Class<?> getLinesContainer() {
		// return DocMarketingMeasureLine.class;
		return DocRequestOnChangeRouteLine.class;
	}

	// ~~~
	// �����, ������, ���� ��� ������� �������� �������� �� ��� ������ GET � SET
	// �������� � ˸��.

	// public RefOutletEntity getOutlet()
	// {
	// return Outlet;
	// }
	// public void setOutlet(RefOutletEntity val){
	// if (Outlet.equals(val)) return;
	// onOutletChanged(Outlet,val);
	// Outlet=val;
	// }

	// public RefCreditRequestEntity getCreditRequest()
	// {
	// return CreditRequest;
	// }

	// public void setCreditRequest(RefCreditRequestEntity val){
	// if (CreditRequest.equals(val)) return;
	// onCreditRequestChanged(CreditRequest,val);
	// CreditRequest=val;
	// }

	//
	// public DocCreditRequestLineEntity getLine(Context context,
	// RefCreditRequestObjectEntity object) {
	// DocCreditRequestLine lines = (DocCreditRequestLine) getLines(context);
	// return (DocCreditRequestLineEntity) lines.getLine(context, object);
	//
	// }

	// public void changeValue(Context context,RefCreditRequestObjectEntity
	// object, Float value) {
	// DocCreditRequestLine lines = (DocCreditRequestLine) getLines(context);
	// DocCreditRequestLineEntity entity = (DocCreditRequestLineEntity)
	// lines.getLine(context, object);
	// if (value==null){
	// if (entity!=null){
	// lines.delete(entity);
	// }
	// }
	// else{
	// if (entity==null){
	// lines.NewEntity();
	// entity = lines.Current();
	// entity.setMarketingObject(object);
	// }
	// entity.setValue(value);
	//
	// lines.save();
	// }
	// }

	// @Override
	// public String toString(){
	// if (this.CreditRequest==null) return "������";
	// return this.CreditRequest.Descr+" �"+ String.format("%03d/%09d",
	// Author.Id,Id);
	//
	// }
	@Override
	final public void setDefaults(Context context, GenericEntity<?> owner) {
		this.CreateDate = new Date();
		this.IsAccepted = false;
		this.IsMark = false;
		this.Employee = Globals.getEmployee();
		// TaskVisitEntity task = ((SFSActivity) context).getCurrentVisit();
		TaskWorkdayEntity workday = (TaskWorkdayEntity) owner;
		if (workday != null) {
			this.Author = workday.Author;
			this.MasterTask = workday;

			// this.Outlet = visit.Outlet;
			// this.CreditRequest =
			// Globals.getDefaultCreditRequest(this.Outlet);

		}
		this.Approved = Globals.getFirstApproveState();
	}

	final public void setContent(RefOutletEntity entity) {

		this.Outlet = entity;
		this.VisitCyclicity = 0;
		this.VisitDate = Utils.AddDay(new Date(), 1);
		this.VisitKindId = 0;
		this.VisitKind = TaskVisitEntity.getTypeString(this.VisitKindId);
		this.VisitTime = new Date();

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
		if (Author == null)
			return "������������ ������ �� ��������� ��������";
		return ("������ �� ��������� �������� � " + String.format("%03d/%06d",
				Author.Id, Id)); // + ((this.CreateDate==null)?"":" �� " +
									// DateFormat.format("dd MMMM yyyy",
									// this.CreateDate))
		// ;
	}
}