package ru.magnat.sfs.ui.android.doc.order;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class DocOrderListViewItem extends GenericListItemView<DocOrderJournal, DocOrderEntity> {

	final DocOrderEntity _doc;
	TextView _order_text;

	public DocOrderListViewItem(Context context, DocOrderJournal list, ListView lv, DocOrderEntity doc) {
		super(context, list, lv);
		_doc = doc;
		
		
	}

	@Override
	public SfsContentView inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.doc_order_list_item_view, this);
		return this;
	}

	@Override
	public void fill() {
		final TextView v_doc_date = (TextView) findViewById(R.id.doc_date);
		final TextView v_doc_descr = (TextView) findViewById(R.id.doc_descr);
		final TextView v_doc_totals = (TextView) findViewById(R.id.doc_totals);
		final TextView v_doc_info = (TextView) findViewById(R.id.doc_info);
		final TextView v_accepted = (TextView) findViewById(R.id.doc_state);
		final TextView v_cfr = (TextView) findViewById(R.id.doc_cfr);
		String doc_date = "";
		String doc_descr = "";
		String doc_totals = "";
		String doc_info = "";
		String doc_cfr = "";
		float cfr = 0f;

		if (_doc != null) {
			if (_doc.CreateDate != null)
				doc_date = (String) DateFormat.format("dd.MM.yy",
						_doc.CreateDate);
			doc_totals = new DecimalFormat("0.00").format(_doc.mAmount);// Float.toString(_doc.Amount);
			if (_doc.ShipmentDate != null)
				doc_info = "Отгрузка: "
						+ (String) DateFormat.format("dd.MM.yy",
								_doc.ShipmentDate);

			doc_descr = _doc.toString();
			if (_doc.Outlet != null)
				doc_info += " " + _doc.Outlet.toString();
			cfr = _doc.CFR;
			if (cfr>0){
				doc_cfr = "КУЗ "+NumberFormat.getPercentInstance(new Locale("ru")).format(cfr);
			}
				  
		}
		v_doc_date.setText(doc_date);
		v_doc_descr.setText(doc_descr);
		v_doc_totals.setText(doc_totals);
		v_doc_info.setText(doc_info);
		v_cfr.setText(doc_cfr);
		v_accepted.setText("");
		if (_doc.IsAccepted == null)
			return;
		_foreColor = R.color.grey;
		if (!_doc.IsMark) {
			if (_doc.CancelDate!=null) _foreColor = R.color.gold;
			else if (_doc.AcceptDate!=null) _foreColor = R.color.green;
			else if (_doc.ImportDate!=null) _foreColor = R.color.blue;
			else if (_doc.ReceiveDate!=null) _foreColor = android.R.color.primary_text_light;
			else _foreColor = R.color.red;
			//_foreColor = (_doc.IsAccepted) ? android.R.color.primary_text_light
			//		: R.color.red;
		}
		setForeColor(this);
		if (cfr>0){
			Resources res = getContext().getResources();
			v_cfr.setTextColor(res.getColor(android.R.color.primary_text_light));
			v_cfr.setClickable(true);
			v_cfr.setOnClickListener(new CfrDetailsListener(_doc));
			if (cfr<0.95)
				v_cfr.setBackgroundColor(res.getColor(R.color.red));
			else if (cfr<0.98)
				v_cfr.setBackgroundColor(res.getColor(R.color.yellow));
			else if (cfr<1.01)
				v_cfr.setBackgroundColor(res.getColor(R.color.green));
			else
				v_cfr.setBackgroundColor(res.getColor(R.color.yellow));
				
		}
	}
	
	class CfrDetailsListener implements OnClickListener{
		final DocOrderEntity _order;
		public CfrDetailsListener(DocOrderEntity order){
			_order = (DocOrderEntity) order.clone();
		}
		@Override
		public void onClick(View v) {
			Dialogs.createDialog("Удовлетворенность заказа", "", (new OrderCfrInfoView(MainActivity.getInstance(),_order)).inflate(), null, null, null).show();
		}
		
	}
}
