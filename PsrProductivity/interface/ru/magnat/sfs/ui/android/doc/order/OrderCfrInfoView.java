package ru.magnat.sfs.ui.android.doc.order;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.invoice.DocInvoiceEntity;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.TextView;

public class OrderCfrInfoView extends SfsContentView {

	DocOrderEntity _entity;
	DocOrderJournal _catalog;

	AlertDialog _alertDialog;

	public OrderCfrInfoView(Context context) {
		super(context);
	}

	public OrderCfrInfoView(Context context, DocOrderEntity entity) {
		super(context);
		this._entity = entity;
	}

	
	public void refreshFields() {}

	@Override
	public SfsContentView inflate() {

		layoutInflater.inflate(R.layout.doc_order_cfr, this);
		if (_entity==null) return this;
		//заполнение полей заказа
		
		((TextView) findViewById(R.id.order_descr)).setText(String.format("№%03d/%09d от %td.%tm.%ty", _entity.Author.Id,_entity.Id, _entity.CreateDate, _entity.CreateDate, _entity.CreateDate));
		((TextView) findViewById(R.id.order_amount)).setText(String.format("%.2f", _entity.mAmount));
		((TextView) findViewById(R.id.order_outlet)).setText(_entity.Outlet.toString());
		((TextView) findViewById(R.id.order_outlet_address)).setText(Utils.prepareAddress(_entity.Outlet.Address));
		if (_entity.CFR > 0) {
			DocInvoiceEntity invoice = Globals.getDerivedInvoice(_entity);
			if (invoice != null) {
				((TextView) findViewById(R.id.invoice_descr)).setText(String.format("№%s от %td.%tm.%ty", invoice.DocNo, invoice.ShipmentDate, invoice.ShipmentDate, invoice.CreateDate));
				((TextView) findViewById(R.id.invoice_amount)).setText(String.format("%.2f", invoice.mAmount));
				((TextView) findViewById(R.id.order_cfr_value)).setText(String.format("%d", _entity.Quantity - invoice.Quantity));
			}
			((TextView) findViewById(R.id.order_cfr)).setText(String.format("%.0f%%", _entity.CFR*100f));
		} else {
			((TextView) findViewById(R.id.invoice_descr)).setText("Отгрузка не подтверждена");
			((TextView) findViewById(R.id.invoice_amount)).setVisibility(GONE);
			((TextView) findViewById(R.id.invoice_amount_label)).setVisibility(GONE);
			((TextView) findViewById(R.id.order_cfr)).setVisibility(GONE);
			((TextView) findViewById(R.id.order_cfr_label)).setVisibility(GONE);
			((TextView) findViewById(R.id.order_cfr_value)).setVisibility(GONE);
			((TextView) findViewById(R.id.order_cfr_value_label)).setVisibility(GONE);
		}
	
		return this;
	}

}
