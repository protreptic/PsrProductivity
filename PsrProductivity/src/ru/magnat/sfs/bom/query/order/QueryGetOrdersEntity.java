package ru.magnat.sfs.bom.query.order;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetOrdersEntity extends QueryGenericEntity<QueryGetOrders> {
	@OrmEntityField(DisplayName = "Черновик", isPrimary = 0, fields = "draft")
	public int Draft;
	
	@OrmEntityField(DisplayName = "Заказчик", isPrimary = 0, fields = "customer_legal_name")
	public String CustomerLegalName;
	
	@OrmEntityField(DisplayName = "Адрес", isPrimary = 0, fields = "customer_address")
	public String CustomerAddress;
	
	@OrmEntityField(DisplayName = "Сумма со скидкой", isPrimary = 0, fields = "amount")
	public float mAmount;
	
	@OrmEntityField(DisplayName = "Дата поставки", isPrimary = 0, fields = "shipment_date")
	public Date ShipmentDate;
	
	@OrmEntityField(DisplayName = "Время поставки", isPrimary = 0, fields = "shipment_time")
	public String ShipmentTime;
	
	@OrmEntityField(DisplayName = "Время создания", isPrimary = 0, fields = "create_date")
	public Date CreateDate;
	
	@OrmEntityField(DisplayName = "Время сохранения", isPrimary = 0, fields = "save_date")
	public Date SaveDate;

	@OrmEntityField(DisplayName = "Время отправки", isPrimary = 0, fields = "sent_date")
	public Date ReceiveDate;

	@OrmEntityField(DisplayName = "Время импорта", isPrimary = 0, fields = "import_date")
	public Date ImportDate;

	@OrmEntityField(DisplayName = "Время проведения", isPrimary = 0, fields = "accept_date")
	public Date AcceptDate;

	@OrmEntityField(DisplayName = "Время отмены", isPrimary = 0, fields = "cancel_date")
	public Date CancelDate;

	@OrmEntityField(DisplayName = "КУЗ", isPrimary = 0, fields = "cfr")
	public float Cfr;
}
