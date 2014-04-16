package ru.magnat.sfs.bom.query.measures;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.OnBackPressedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class QueryTprMeasureListView extends GenericListView<QueryGetTprMeasures, QueryGetTprMeasureEntity, TaskVisitEntity> {

	private Context mContext;
	
	public QueryTprMeasureListView(Context context) {
		super(context, null, null);
		
		mContext = context;
	}

	@Override
	public SfsContentView inflate() {
		return super.сatalogInflate(R.layout.doc_tpr_measure_list_view, "  Контроль TPR");
	}
	
	protected void CreateTprMeasure(TaskVisitEntity visit) {
		if (visit == null) {
			Dialogs.MessageBox("Измерения можно создавать только в рамках визита");
			return;
		}
		DocTprMeasureJournal docs = new DocTprMeasureJournal(mContext);
		docs.NewEntity();
		DocTprMeasureEntity doc = docs.Current();
		doc.setDefaults(getContext(), visit);
		doc.MasterTask = visit;
		if (docs.save()) {
			doc = docs.Current();
		} else {
			Dialogs.MessageBox("Не удалось записать созданное измерение");
			return;
		}
		OpenEntity(docs, doc);
	}

	@Override
	protected void createEntity() {
		CreateTprMeasure(_owner);
	}

	@Override
	protected void OpenEntity(QueryGetTprMeasureEntity entity) {
		DocTprMeasureJournal docs = new DocTprMeasureJournal(mContext);
		DocTprMeasureEntity order = docs.FindById(entity.Id);
		
		OpenEntity(docs, order);
	}
	
	protected void OpenEntity(DocTprMeasureJournal journal, DocTprMeasureEntity entity) {
		SfsContentView v = journal.GetEditView(entity);
		journal.close();
		if (v == null) {
			return;
		}
		v.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
			public void onViewAttachedToWindow(View v) {
				suspend();
			}

			public void onViewDetachedFromWindow(View v) {
				wakeup();
				requery();
			}
		});

		MainActivity.sInstance.addToFlipper(v, this.getClass().getSimpleName());
		MainActivity.sInstance.addOnBackPressedListener((OnBackPressedListener) v);
	}
	
	@Override
	protected void requery() {
		if (_catalog != null) {
			_catalog.close();
		}
		if (_owner != null) {
			_catalog = new QueryGetTprMeasures(mContext, _owner.Outlet.Id, null);
		} else {
			_catalog = new QueryGetTprMeasures(mContext, null, (mDate != null ? mDate : new DateTime(mCalendar.getTimeInMillis())));
		}
		_catalog.SetListType(SfsListType.EXTENDED_LIST);
		_catalog.Select();
		if (lv != null) {
			lv.setAdapter(this._catalog);
		}
		_catalog.notifyDataSetChanged();
	}
	
	public static class QueryTprMeasureViewItem extends GenericListItemView<QueryGetTprMeasures, QueryGetTprMeasureEntity> {
		private Context mContext;
		private LayoutInflater mLayoutInflater;
		private QueryGetTprMeasureEntity mEntity;
		private String mUserId;
		
		private TextView mNumber;
		private TextView mCustomer;
		private TextView mAddress;
		private ImageView mStatus;
		
		public QueryTprMeasureViewItem(Context context, QueryGetTprMeasures list, ListView lv, QueryGetTprMeasureEntity entity) {
			super(context, list, lv);
			
			mContext = context;
			mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mEntity = entity;
			mUserId = String.valueOf(MainActivity.getInstance().mCurrentUser.Id);
		}

		@Override
		public SfsContentView inflate() {
			mLayoutInflater.inflate(R.layout.tpr_measure_item, this);
			
			return this;
		}

		@Override
		public void fill() {
			mStatus = (ImageView) findViewById(R.id.status_icon);		 
			switch (mEntity.Draft) {
				case 0: {
					if (mEntity.IsAccepted == 0) {
						mStatus.setImageDrawable(getResources().getDrawable(R.drawable.not_sent)); 
					}
					if(mEntity.IsAccepted == 1) {
						mStatus.setImageDrawable(getResources().getDrawable(R.drawable.approved)); 
					}
				} break;
				case 1: {
					mStatus.setImageDrawable(getResources().getDrawable(R.drawable.draft)); 
				} break;
				default: {
					mStatus.setImageDrawable(getResources().getDrawable(R.drawable.not_sent)); 
				} break;
			}
			
			mNumber = (TextView) findViewById(R.id.number);
			DateTime createDate = new DateTime(mEntity.CreateDate.getTime());
			mNumber.setText("№ " + mUserId + "/" + mEntity.Id + " от " + createDate.toString(DateTimeFormat.forPattern("dd.MM.yyyy")));
 			
			mCustomer = (TextView) findViewById(R.id.customer);
			mCustomer.setText(mEntity.CustomerLegalName);
			
			mAddress = (TextView) findViewById(R.id.address);
			mAddress.setText(Utils.prepareAddress(mEntity.CustomerAddress));
		} 
	}

}
