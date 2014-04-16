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

	@EntityCardField(DisplayName = "�������� �����", Sortkey = 3, SelectMethod = "changeOutlet")
	@OrmEntityField(DisplayName = "�������� �����", isPrimary = 0, fields = "Outlet")
	public RefOutletEntity Outlet;
	@EntityCardField(DisplayName = "���� ��������", Sortkey = 1, SelectMethod = "changeShipmentDate", format = "dd MMMM yyyy")
	@OrmEntityField(DisplayName = "���� ��������", isPrimary = 0, fields = "ShipmentDate")
	public Date ShipmentDate;
	@EntityCardField(DisplayName = "����� ��������", Sortkey = 2, SelectMethod = "changeShipmentTime", format = "kk:mm")
	@OrmEntityField(DisplayName = "����� ��������", isPrimary = 0, fields = "ShipmentTime")
	public String ShipmentTime;
	@EntityCardField(DisplayName = "��� �������", Sortkey = 6, SelectMethod = "changePaymentType")
	@OrmEntityField(DisplayName = "��� �������", isPrimary = 0, fields = "PaymentType")
	public RefPaymentTypeEntity PaymentType;
	@EntityCardField(DisplayName = "����������", Sortkey = 4, SelectMethod = "changeContactor")
	@OrmEntityField(DisplayName = "����������", isPrimary = 0, fields = "Contractor")
	public RefContractorEntity Contractor;
	@EntityCardField(DisplayName = "����� �� �������", Sortkey = 8, SelectMethod = "refreshAmount", format = "##,###,###.## ������")
	@OrmEntityField(DisplayName = "����� �� �������", isPrimary = 0, fields = "Amount")
	public float mAmount;
	@EntityCardField(DisplayName = "c���� �� ����", Sortkey = 9, SelectMethod = "refreshAmount", format = "##,###,###.## ������")
	@OrmEntityField(DisplayName = "����� �� ����", isPrimary = 0, fields = "AmountBase")
	public float AmountBase;
	@EntityCardField(DisplayName = "����� � SU", Sortkey = 10, SelectMethod = "", format = "##,###,###.##")
	@OrmEntityField(DisplayName = "����� � SU", isPrimary = 0, fields = "Su")
	public float Su;
	@EntityCardField(DisplayName = "�������� �������", Sortkey = 7, SelectMethod = "changeTradeRule")
	@OrmEntityField(DisplayName = "��", isPrimary = 0, fields = "TradeRule")
	public RefTradeRuleEntity TradeRule;
	@EntityCardField(DisplayName = "�����", Sortkey = 5, SelectMethod = "changeWarehouse")
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Warehouse")
	public RefWarehouseEntity Warehouse;
	@EntityCardField(DisplayName = "�����������", Sortkey = 11, SelectMethod = "changeDocComment")
	@OrmEntityField(DisplayName = "�����������", isPrimary = 0, fields = "DocComment")
	public String DocComment;
	@OrmEntityField(DisplayName = "��������", isPrimary = 0, fields = "Delay")
	public int Delay = 0;
	@EntityCardField(DisplayName = "������������ ������ WelcomeOffer", Sortkey = 130, SelectMethod = "changeWelcomeOfferDiscount")
	@OrmEntityField(DisplayName = "������������ ������ WelcomeOffer", isPrimary = 0, fields = "WelcomeOfferDiscount")
	public Boolean WelcomeOfferDiscount;
	@EntityCardField(DisplayName = "�� ������������� TPR-������", Sortkey = 130, SelectMethod = "changeTprDiscountDisabled")
	@OrmEntityField(DisplayName = "�� ������������� TPR-������", isPrimary = 0, fields = "TprDiscountDisabled")
	public Boolean TprDiscountDisabled;
	@EntityCardField(DisplayName = "����� ����������", Sortkey = 131, SelectMethod = "", format = "dd MMMM kk:mm")
	@OrmEntityField(DisplayName = "����� ����������", isPrimary = 0, fields = "SaveDate")
	public Date SaveDate;
	@OrmEntityField(DisplayName = "����������", isPrimary = 0, fields = "Quantity")
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
