package ru.magnat.sfs.bom.requestonchangecontractor;

import java.util.Date;

import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.ref.approvestate.RefApproveStateEntity;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import android.content.Context;

@OrmEntityOwner(owner = DocRequestOnChangeContractorJournal.class)
public final class DocRequestOnChangeContractorEntity
		extends
		DocGenericEntity<DocRequestOnChangeContractorJournal, DocRequestOnChangeContractorLineEntity> {

	// Передается в форму
	@EntityCardField(DisplayName = "Контрагент", Sortkey = 1, SelectMethod = "сhangeContractor")
	@OrmEntityField(DisplayName = "Контрагент", isPrimary = 0, fields = "Contractor")
	public RefContractorEntity Contractor;

	@EntityCardField(DisplayName = "Наименование", Sortkey = 2, SelectMethod = "changeName")
	@OrmEntityField(DisplayName = "Наименование", isPrimary = 0, fields = "Name")
	public String Name;

	@EntityCardField(DisplayName = "Юридический адрес", Sortkey = 3, SelectMethod = "changeAddress")
	@OrmEntityField(DisplayName = "Адрес", isPrimary = 0, fields = "Address")
	public String Address;

	@EntityCardField(DisplayName = "Фактический адрес", Sortkey = 4, SelectMethod = "changeDeliveryAddress")
	@OrmEntityField(DisplayName = "Адрес доставки", isPrimary = 0, fields = "DeliveryAddress")
	public String DeliveryAddress;

	@EntityCardField(DisplayName = "ИНН/КПП", Sortkey = 5, SelectMethod = "changeINN_KPP")
	@OrmEntityField(DisplayName = "ИНН/КПП", isPrimary = 0, fields = "INN_KPP")
	public String INN_KPP;

	@EntityCardField(DisplayName = "БИК", Sortkey = 6, SelectMethod = "changeBic")
	@OrmEntityField(DisplayName = "БИК", isPrimary = 0, fields = "Bic")
	public String Bic;

	@EntityCardField(DisplayName = "Номер счета", Sortkey = 7, SelectMethod = "changeNumberAccount")
	@OrmEntityField(DisplayName = "Номер счета", isPrimary = 0, fields = "NumberAccount")
	public String NumberAccount;

	@EntityCardField(DisplayName = "ОКПО", Sortkey = 8, SelectMethod = "changeOKPO")
	@OrmEntityField(DisplayName = "ОКПО", isPrimary = 0, fields = "OKPO")
	public String OKPO;

	@EntityCardField(DisplayName = "Паспортные данные", Sortkey = 9, SelectMethod = "changePassportData")
	@OrmEntityField(DisplayName = "Паспортные данные", isPrimary = 0, fields = "PassportData")
	public String PassportData;

	@EntityCardField(DisplayName = "Свидетельство ИП", Sortkey = 10, SelectMethod = "changeFEcertificate")
	@OrmEntityField(DisplayName = "Свидетельство ИП", isPrimary = 0, fields = "FEcertificate")
	public String FEcertificate;

	@EntityCardField(DisplayName = "Телефон", Sortkey = 11, SelectMethod = "changePhone")
	@OrmEntityField(DisplayName = "Телефон", isPrimary = 0, fields = "Phone")
	public String Phone;

	@EntityCardField(DisplayName = "Эл. почта", Sortkey = 12, SelectMethod = "changeEmail")
	@OrmEntityField(DisplayName = "Эл. почта", isPrimary = 0, fields = "Email")
	public String Email;

	@EntityCardField(DisplayName = "Статус обработки запроса", Sortkey = 13, SelectMethod = "")
	// Added SDV 2012-09-11
	@OrmEntityField(DisplayName = "Статус запроса", isPrimary = 0, fields = "Approved")
	public RefApproveStateEntity Approved;

	@EntityCardField(DisplayName = "Комментарий по статусу обработки запроса", Sortkey = 14, SelectMethod = "")
	// Added SDV 2012-09-11
	@OrmEntityField(DisplayName = "Комментарий по статусу обработки запроса", isPrimary = 0, fields = "ApprovedComment")
	public String ApprovedComment;

	@Override
	final protected Class<?> getLinesContainer() {
		// return DocMarketingMeasureLine.class;
		return DocRequestOnChangeContractorLine.class;
	}

	// ~~~
	// ЗДЕСЬ, ПОХОЖЕ, НАДО ДЛЯ КАЖДОГО СВОЙСТВА ВСТАВИТЬ ПО ДВА МЕТОДА GET и SET
	// Спросить у Лёхи.

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
	// if (this.CreditRequest==null) return "Ошибка";
	// return this.CreditRequest.Descr+" №"+ String.format("%03d/%09d",
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
		}
		this.Approved = Globals.getFirstApproveState();
	}

	final public void setContent(RefContractorEntity entity) {

		if (entity == null) {
			this.Contractor = entity;
			this.Address = "";
			this.DeliveryAddress = "";
			this.Bic = "";
			this.Email = "";
			this.FEcertificate = "";
			this.INN_KPP = "";
			this.Name = "";
			this.NumberAccount = "";
			this.OKPO = "";
			this.PassportData = "";
			this.Phone = "";
			return;
		}
		this.Contractor = entity;
		this.Address = entity.LegalAddress;
		this.DeliveryAddress = entity.RealAddress;
		this.Bic = entity.BIC;
		this.Email = entity.Email;
		this.FEcertificate = "";
		this.INN_KPP = entity.INN;
		this.Name = entity.Descr;
		this.NumberAccount = entity.AccountInfo;
		this.OKPO = entity.OKPO;
		this.PassportData = entity.Passport;
		this.Phone = entity.Phone;

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
		String str = "Запрос на "
				+ ((Contractor == null) ? "добавление" : "изменение")
				+ " КА № ";
		if (Author == null)
			return "Некорректный запрос на изменение/добавление контрагента";
		return (str + String.format("%03d/%06d", Author.Id, Id)); // +
																	// ((this.CreateDate==null)?"":" от "
																	// +
																	// DateFormat.format("dd MMMM yyyy",
																	// this.CreateDate))
		// ;
	}
}