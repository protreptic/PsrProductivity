package ru.magnat.sfs.ui.android.doc.creditrequest;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.creditrequest.DocCreditRequestEntity;
import ru.magnat.sfs.bom.creditrequest.DocCreditRequestJournal;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;

public class DocRequestOnChangeCreditListViewItem extends
		GenericListItemView<DocCreditRequestJournal, DocCreditRequestEntity> {

	final DocCreditRequestEntity _doc;
	TextView _order_text;

	public DocRequestOnChangeCreditListViewItem(Context context,
			DocCreditRequestJournal list, ListView lv,
			DocCreditRequestEntity doc) {
		super(context, list, lv);
		
		_doc = doc;
	}

	@Override
	public SfsContentView inflate() {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.doc_credit_request_view, this);

		// }

		return this;
	}

	@Override
	public void fill() {
		final TextView v_doc_date = (TextView) findViewById(R.id.doc_date);
		final TextView v_doc_descr = (TextView) findViewById(R.id.doc_descr);
		final TextView v_doc_totals = (TextView) findViewById(R.id.doc_totals);
		final TextView v_doc_info = (TextView) findViewById(R.id.doc_info);
		final TextView v_accepted = (TextView) findViewById(R.id.doc_state);
		String doc_date = "";
		String doc_descr = "";
		String doc_totals = "";
		String doc_info = "";

		if (_doc != null) {
			if (_doc.CreateDate != null)
				doc_date = (String) DateFormat.format("dd.MM.yy",
						_doc.CreateDate);

			doc_descr = _doc.toString();
			if (_doc.Contractor != null)
				doc_info += " " + _doc.Contractor.toString();
		}

		v_doc_date.setText(doc_date);
		v_doc_descr.setText(doc_descr);
		v_doc_totals.setText(doc_totals);
		v_doc_info.setText(doc_info);
		v_accepted.setText("");
		if (_doc.IsAccepted == null)
			return;
		_foreColor = R.color.grey;
		if (!_doc.IsMark)
			_foreColor = (_doc.IsAccepted) ? android.R.color.primary_text_light
					: R.color.red;
		setForeColor(this);
	}
}
