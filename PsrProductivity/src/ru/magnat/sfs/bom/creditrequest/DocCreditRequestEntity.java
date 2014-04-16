package ru.magnat.sfs.bom.creditrequest;

import java.util.Date;

import android.content.Context;
import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.ref.approvestate.RefApproveStateEntity;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.ref.paymenttype.RefPaymentTypeEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;

@OrmEntityOwner(owner = DocCreditRequestJournal.class)
public final class DocCreditRequestEntity extends DocGenericEntity<DocCreditRequestJournal, DocCreditRequestLineEntity> {
	// @OrmEntityField(DisplayName = "�������� �����", isPrimary = 0, fields =
	// "Outlet")
	// public RefOutletEntity Outlet;

	// @OrmEntityField(DisplayName = "������", isPrimary = 0, fields =
	// "CreditRequest")
	// public RefCreditRequestEntity CreditRequest;
	@EntityCardField(DisplayName = "���������", Sortkey = 1, SelectMethod = "changeContractor")
	@OrmEntityField(DisplayName = "���������", isPrimary = 0, fields = "Contractor")
	public RefContractorEntity Contractor;

	@EntityCardField(DisplayName = "��� �������", Sortkey = 2, SelectMethod = "changeCreditType")
	@OrmEntityField(DisplayName = "��� �������", isPrimary = 0, fields = "CreditType")
	public RefPaymentTypeEntity CreditType;

	@EntityCardField(DisplayName = "������� ������� (���������� ���)", Sortkey = 3, SelectMethod = "changeCreditDepth")
	@OrmEntityField(DisplayName = "������� ������� (���������� ���)", isPrimary = 0, fields = "CreditDepth")
	public int CreditDepth;

	@EntityCardField(DisplayName = "����� �������", Sortkey = 4, SelectMethod = "changeCreditLimit", format = "##,###,###.## ������")
	@OrmEntityField(DisplayName = "����� �������", isPrimary = 0, fields = "CreditLimit")
	public float CreditLimit;

	@EntityCardField(DisplayName = "�����������", Sortkey = 5, SelectMethod = "changeCreditComment")
	@OrmEntityField(DisplayName = "�����������", isPrimary = 0, fields = "CreditComment")
	public String CreditComment;

	// ref.contractor.entity

	@EntityCardField(DisplayName = "������ �������", Sortkey = 6, SelectMethod = "")
	@OrmEntityField(DisplayName = "������ �������", isPrimary = 0, fields = "Approved")
	public RefApproveStateEntity Approved;

	@EntityCardField(DisplayName = "����������� �� ������� �������", Sortkey = 7, SelectMethod = "")
	@OrmEntityField(DisplayName = "����������� �� ������� �������", isPrimary = 0, fields = "ApprovedComment")
	public String ApprovedComment;

	@Override
	final protected Class<?> getLinesContainer() {
		// return DocMarketingMeasureLine.class;
		return DocCreditRequestLine.class;
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

	final public void setContent(RefContractorEntity entity) {
		this.Contractor = entity;
		this.CreditComment = "";
		this.CreditDepth = 0;
		this.CreditLimit = 0;
		this.CreditType = null;

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
			return "������������ ������ �� ��������� ���� �������";
		return ("������ �� � " + String.format("%03d/%06d", Author.Id, Id)); // +
																				// ((this.CreateDate==null)?"":" �� "
																				// +
																				// DateFormat.format("dd MMMM yyyy",
																				// this.CreateDate))
		// ;
	}

}