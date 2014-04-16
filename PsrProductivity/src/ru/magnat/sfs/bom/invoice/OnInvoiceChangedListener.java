/**
 * 
 */
package ru.magnat.sfs.bom.invoice;

import java.util.Date;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.generic.DocGenericLineEntity;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.paymenttype.RefPaymentTypeEntity;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRuleEntity;
import ru.magnat.sfs.bom.ref.warehouse.RefWarehouseEntity;

/**
 * @author alex_us
 * 
 */
@SuppressWarnings("rawtypes")
public interface OnInvoiceChangedListener {
	public interface OnClosingListener extends IEventListener {
		public abstract void onClosing(GenericEntity sender);
	}

	public interface OnWarehouseChangedListener extends IEventListener {
		public abstract void onWarehouseChanged(GenericEntity sender,
				RefWarehouseEntity old_value, RefWarehouseEntity new_value);
	}

	public interface OnLineChangedListener extends IEventListener {
		public abstract void onLineChanged(GenericEntity sender,
				DocGenericLineEntity old_value, DocGenericLineEntity new_value);
	}

	public interface OnOutletChangedListener extends IEventListener {
		public abstract void onOutletChanged(GenericEntity sender,
				RefOutletEntity old_value, RefOutletEntity new_value);
	}

	public interface OnTradeRuleChangedListener extends IEventListener {
		public abstract void onTradeRuleChanged(GenericEntity sender,
				RefTradeRuleEntity old_value, RefTradeRuleEntity new_value);
	}

	public interface OnContractorChangedListener extends IEventListener {
		public abstract void onContractorChanged(GenericEntity sender,
				RefContractorEntity old_value, RefContractorEntity new_value);
	}

	public interface OnShipmentDateChangedListener extends IEventListener {
		public abstract void onShipmentDateChanged(GenericEntity sender,
				Date old_value, Date new_value);
	}

	public interface OnShipmentTimeChangedListener extends IEventListener {
		public abstract void onShipmentTimeChanged(GenericEntity sender,
				String old_value, String new_value);
	}

	public interface OnPaymentTypeChangedListener extends IEventListener {
		public abstract void onPaymentTypeChanged(GenericEntity sender,
				RefPaymentTypeEntity old_value, RefPaymentTypeEntity new_value);
	}

	public interface OnDelayChangedListener extends IEventListener {
		public abstract void onDelayChanged(GenericEntity sender,
				Integer old_value, Integer new_value);
	}

	public interface OnAmountChangedListener extends IEventListener {
		public abstract void onAmountChanged(GenericEntity sender,
				float old_value, float new_value);
	}

	public interface OnAmountBaseChangedListener extends IEventListener {
		public abstract void onAmountBaseChanged(GenericEntity sender,
				float old_value, float new_value);
	}

	public interface OnLineQuantityChangedListener extends IEventListener {
		public abstract void onLineQuantityChanged(GenericEntity sender,
				long old_value, long new_value);
	}

	public interface OnLineUnitChangedListener extends IEventListener {
		public abstract void onLineUnitChanged(GenericEntity sender,
				DocInvoiceLine.Units old_value, DocInvoiceLine.Units new_value);
	}

	public interface OnLineAmountChangedListener extends IEventListener {
		public abstract void onLineAmountChanged(GenericEntity sender,
				float old_value, float new_value);
	}

	public interface OnLineAmountBaseChangedListener extends IEventListener {
		public abstract void onLineAmountBaseChanged(GenericEntity sender,
				float old_value, float new_value);
	}
}
