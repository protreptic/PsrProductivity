package ru.magnat.sfs.bom.invoice;

import java.util.Date;

import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.paymenttype.RefPaymentTypeEntity;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRuleEntity;
import ru.magnat.sfs.bom.ref.warehouse.RefWarehouseEntity;
import android.content.Context;

@OrmEntityOwner(owner = DocInvoiceJournal.class)
public final class DocInvoiceEntity extends
		DocGenericEntity<DocInvoiceJournal, DocInvoiceLineEntity> {

	@EntityCardField(DisplayName = "торговая точка", Sortkey = 3, SelectMethod = "changeOutlet")
	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "Outlet")
	public RefOutletEntity Outlet;
	@EntityCardField(DisplayName = "дата доставки", Sortkey = 1, SelectMethod = "changeShipmentDate", format = "dd MMMM yyyy")
	@OrmEntityField(DisplayName = "Дата поставки", isPrimary = 0, fields = "ShipmentDate")
	public Date ShipmentDate;
	@EntityCardField(DisplayName = "время доставки", Sortkey = 2, SelectMethod = "changeShipmentTime", format = "kk:mm")
	@OrmEntityField(DisplayName = "Время поставки", isPrimary = 0, fields = "ShipmentTime")
	public String ShipmentTime;
	@EntityCardField(DisplayName = "тип кредита", Sortkey = 6, SelectMethod = "changePaymentType")
	@OrmEntityField(DisplayName = "Тип кредита", isPrimary = 0, fields = "PaymentType")
	public RefPaymentTypeEntity PaymentType;
	@EntityCardField(DisplayName = "контрагент", Sortkey = 4, SelectMethod = "changeContactor")
	@OrmEntityField(DisplayName = "Контрагент", isPrimary = 0, fields = "Contractor")
	public RefContractorEntity Contractor;
	@EntityCardField(DisplayName = "сумма со скидкой", Sortkey = 8, SelectMethod = "refreshAmount", format = "##,###,###.## рублей")
	@OrmEntityField(DisplayName = "Сумма со скидкой", isPrimary = 0, fields = "Amount")
	public float mAmount;
	@EntityCardField(DisplayName = "cумма по базе", Sortkey = 9, SelectMethod = "refreshAmount", format = "##,###,###.## рублей")
	@OrmEntityField(DisplayName = "Сумма по базе", isPrimary = 0, fields = "AmountBase")
	public float AmountBase;
	@EntityCardField(DisplayName = "объем в SU", Sortkey = 10, SelectMethod = "", format = "##,###,###.##")
	@OrmEntityField(DisplayName = "Объем в SU", isPrimary = 0, fields = "Su")
	public float Su;
	@EntityCardField(DisplayName = "торговые условия", Sortkey = 7, SelectMethod = "changeTradeRule")
	@OrmEntityField(DisplayName = "ТУ", isPrimary = 0, fields = "TradeRule")
	public RefTradeRuleEntity TradeRule;
	@EntityCardField(DisplayName = "склад", Sortkey = 5, SelectMethod = "changeWarehouse")
	@OrmEntityField(DisplayName = "Склад", isPrimary = 0, fields = "Warehouse")
	public RefWarehouseEntity Warehouse;
	@EntityCardField(DisplayName = "комментарий", Sortkey = 11, SelectMethod = "changeDocComment")
	@OrmEntityField(DisplayName = "Комментарий", isPrimary = 0, fields = "DocComment")
	public String DocComment;
	@OrmEntityField(DisplayName = "Отсрочка", isPrimary = 0, fields = "Delay")
	public int Delay = 0;
	@EntityCardField(DisplayName = "Предоставить скидку WelcomeOffer", Sortkey = 130, SelectMethod = "changeWelcomeOfferDiscount")
	@OrmEntityField(DisplayName = "Предоставить скидку WelcomeOffer", isPrimary = 0, fields = "WelcomeOfferDiscount")
	public Boolean WelcomeOfferDiscount;
	@EntityCardField(DisplayName = "Не предоставлять TPR-скидку", Sortkey = 130, SelectMethod = "changeTprDiscountDisabled")
	@OrmEntityField(DisplayName = "Не предоставлять TPR-скидку", isPrimary = 0, fields = "TprDiscountDisabled")
	public Boolean TprDiscountDisabled;
	@EntityCardField(DisplayName = "время сохранения", Sortkey = 131, SelectMethod = "", format = "dd MMMM kk:mm")
	@OrmEntityField(DisplayName = "Время сохранения", isPrimary = 0, fields = "SaveDate")
	public Date SaveDate;
	@OrmEntityField(DisplayName = "Количество", isPrimary = 0, fields = "Quantity")
	public int Quantity = 0;
	
	public DocInvoiceEntity() {

	}


	@Override
	protected Class<?> getLinesContainer() {
		
		return null;
	}


	@Override
	public void setDefaults(Context context, GenericEntity<?> owner) {
		
		
	}


}
