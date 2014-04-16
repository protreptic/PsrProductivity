package ru.magnat.sfs.ui.android.doc.marketingmeasure;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.OnBackPressedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureEntity;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureJournal;
import ru.magnat.sfs.bom.query.measures.QueryGetMarketingMeasures;
import ru.magnat.sfs.bom.query.measures.QueryGetMarketingMeasuresEntity;
import ru.magnat.sfs.bom.ref.generic.RefGeneric.HierarchyMode;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasure;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasureEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class QueryMarketingMeasureListView extends GenericListView<QueryGetMarketingMeasures, QueryGetMarketingMeasuresEntity, TaskVisitEntity> {
	
	public QueryMarketingMeasureListView(Context context) {
		super(context, null, null);
	}

	@Override
	public SfsContentView inflate() {
		super.сatalogInflate(R.layout.doc_marketing_measure_list_view, "Маркетинговые измерения");
		
		Button date = (Button) findViewById(R.id.date_button1);
		date.setVisibility(View.INVISIBLE); 
		
		ImageButton add = (ImageButton) findViewById(R.id.caption_action);
		add.setVisibility(View.INVISIBLE); 
		
		return this;
	}
	
	protected void CreateMarketingMeasure(RefMarketingMeasureEntity measure, TaskVisitEntity visit) {
		DocMarketingMeasureJournal docs = new DocMarketingMeasureJournal(getContext());
		docs.NewEntity();
		DocMarketingMeasureEntity doc = docs.Current();
	
		if (visit == null) {
			Dialogs.MessageBox("Заказы можно создавать только в рамках визита");
			return;
		} else {
			doc.setDefaults(getContext(), visit);
			if (measure==null){
				Dialogs.MessageBox("Не выбрано измерение");
				return;
			}
			doc.MarketingMeasure = (RefMarketingMeasureEntity) measure.clone();
			doc.MasterTask = visit;
			if (docs.save()) {
				doc = docs.Current();
			} else {
				Dialogs.MessageBox("Не удалось записать созданный заказ");
				return;
			}
		}

		OpenEntity(docs, doc);
	}
	RefMarketingMeasure _refMM = null;
	@Override
	protected void createEntity() {
		_refMM =  new RefMarketingMeasure(getContext());
		_refMM.Select(false, false, HierarchyMode.OnlyEntity);
		int i = _refMM.getCount();
		switch (i) {
			case 0:
				Dialogs.MessageBox("Измерения для пользователя не назначены");
				_refMM.close();
				break;
			case 1:
				CreateMarketingMeasure((RefMarketingMeasureEntity) _refMM.getItem(0), _owner);
				_refMM.close();
				break;
			default:
				AlertDialog dlg = Dialogs.createSelectFromListDialog(_refMM, "Выбор измерения", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						RefMarketingMeasureEntity measure = null;
						try {
							measure = (RefMarketingMeasureEntity) _refMM.getItem(which);
						} catch (Exception e) {}
						
						if (measure!=null) measure = (RefMarketingMeasureEntity) measure.clone();
						_refMM.close();
						if (measure == null) {
							Dialogs.MessageBox("Не выбрано измерение");
							return;
						}
						CreateMarketingMeasure(measure, _owner);
					}
				}
			);
			dlg.setOnDismissListener(new OnDismissListener(){

				@Override
				public void onDismiss(DialogInterface dialog) {
					_refMM.close();
				}
				
			});
			dlg.show();
		}
		
	}

	@Override
	protected void OpenEntity(QueryGetMarketingMeasuresEntity entity) {
		DocMarketingMeasureJournal docs = new DocMarketingMeasureJournal(getContext());
		DocMarketingMeasureEntity order = docs.FindById(entity.Id);
		
		OpenEntity(docs, order);
	}
	
	protected void OpenEntity(DocMarketingMeasureJournal journal, DocMarketingMeasureEntity entity) {
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
		if (this._catalog != null) {
			_catalog.close();
		}
		if (this._owner != null) {
			this._catalog = new QueryGetMarketingMeasures(getContext(), _owner.Outlet.Id, null);
		} else {
			this._catalog = new QueryGetMarketingMeasures(getContext(), null, (mDate != null ? mDate : new DateTime(mCalendar.getTimeInMillis())));
		}
		this._catalog.SetListType(SfsListType.EXTENDED_LIST);
		this._catalog.Select();
		if (lv != null)
			lv.setAdapter(this._catalog);
		this._catalog.notifyDataSetChanged();
	}
	
	public static class QueryMarketingMeasureViewItem extends GenericListItemView<QueryGetMarketingMeasures, QueryGetMarketingMeasuresEntity> {
		private Context mActivityContext;
		private LayoutInflater mLayoutInflater;
		private QueryGetMarketingMeasuresEntity mEntity;
		private String mUserId;
		
		private TextView mNumber;
		private TextView mCustomer;
		private TextView mAddress;
		private ImageView mStatus;
		
		public QueryMarketingMeasureViewItem(Context context, QueryGetMarketingMeasures list, ListView lv, QueryGetMarketingMeasuresEntity entity) {
			super(context, list, lv);
			
			mActivityContext = context;
			mLayoutInflater = (LayoutInflater) mActivityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mEntity = entity;
			mUserId = String.valueOf(MainActivity.getInstance().mCurrentUser.Id);
		}

		@Override
		public SfsContentView inflate() {
			mLayoutInflater.inflate(R.layout.marketing_measure_item, this);
			
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
