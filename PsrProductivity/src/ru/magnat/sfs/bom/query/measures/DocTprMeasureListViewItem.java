package ru.magnat.sfs.bom.query.measures;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;

public final class DocTprMeasureListViewItem extends GenericListItemView<DocTprMeasureJournal, DocTprMeasureEntity> {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	private DocTprMeasureEntity mTprMeasureEntity;
	
	private TextView mDocDate;
	private TextView mDocDescr;
	private TextView mDocTotals;
	private TextView mDocInfo;
	private TextView mAccepted;
	
	public DocTprMeasureListViewItem(Context context, DocTprMeasureJournal journal, ListView list, DocTprMeasureEntity entity) {
		super(context, journal, list);
		
		mContext = context;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTprMeasureEntity = entity;
		
		mDocDate = (TextView) findViewById(R.id.doc_date);
		mDocDescr = (TextView) findViewById(R.id.doc_descr);
		mDocTotals = (TextView) findViewById(R.id.doc_totals);
		mDocInfo = (TextView) findViewById(R.id.doc_info);
		mAccepted = (TextView) findViewById(R.id.doc_state);
	}

	@Override
	public SfsContentView inflate() {
		mLayoutInflater.inflate(R.layout.doc_marketing_measure_list_item_view, this);

		return this;
	}

	@Override
	public void fill() {
		String docDate = "";
		String docDescr = "";
		String docTotals = "";
		String docInfo = "";

		if (mTprMeasureEntity != null) {
			if (mTprMeasureEntity.CreateDate != null) {
				docDate = (String) DateFormat.format("dd.MM.yy", mTprMeasureEntity.CreateDate);
			}
			if (mTprMeasureEntity.Outlet != null) {
				docInfo += " " + mTprMeasureEntity.Outlet.toString();
			}
			docDescr = mTprMeasureEntity.toString();
		}

		mDocDate.setText(docDate);
		mDocDescr.setText(docDescr);
		mDocTotals.setText(docTotals);
		mDocInfo.setText(docInfo);
		mAccepted.setText("");

		if (mTprMeasureEntity.IsAccepted == null) {
			return;
		}
		
		_foreColor = R.color.grey;
		if (!mTprMeasureEntity.IsMark) {
			_foreColor = (mTprMeasureEntity.IsAccepted) ? android.R.color.primary_text_light : R.color.red;
		}
		
		setForeColor(this);
	}

}
