package ru.magnat.sfs.ui.android.doc.contract;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.OnBackPressedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.contract.DocContractEntity;
import ru.magnat.sfs.bom.contract.DocContractJournal;
import ru.magnat.sfs.bom.contract.QueryGetContractors;
import ru.magnat.sfs.bom.contract.QueryGetContractorsEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class QueryContractorListView extends GenericListView<QueryGetContractors, QueryGetContractorsEntity, TaskWorkdayEntity> {
	
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	public static QueryContractorListView sInstance;
	private AlertDialog mFilterDialog;
	
	public QueryContractorListView(Context context) {
		super(context, null, null);
		
		mContext = context;
		sInstance = this;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFilterView = mLayoutInflater.inflate(R.layout.doc_contract_filter_layout, null);
		
		mFilterDialog = Dialogs.createDialog("", "", mFilterView, null, new Command() {
			@Override
			public void execute() {
				requery();	
			}
		}, null);
	}

	public void update() {
		requery();
	}
	private View mFilterView;
	@Override
	public SfsContentView inflate() {
		SfsContentView view = super.сatalogInflate(R.layout.doc_marketing_measure_list_view, "Стандартные договора");
		ImageButton button = (ImageButton) this.findViewById(R.id.add_button);
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createEntity();
			}
		});
		ImageButton filterButton = (ImageButton) this.findViewById(R.id.filter_action);
		filterButton.setVisibility(View.VISIBLE);
		
		filterButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckBox view = (CheckBox) mFilterView.findViewById(R.id.checkBox1);
				view.setChecked(mFilter[0]);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFilter[0] = (!mFilter[0]);
					}
				});
				view = (CheckBox) mFilterView.findViewById(R.id.checkBox2);
				view.setChecked(mFilter[1]);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFilter[1] = (!mFilter[1]);
					}
				});
				view = (CheckBox) mFilterView.findViewById(R.id.checkBox3);
				view.setChecked(mFilter[2]);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFilter[2] = (!mFilter[2]);
					}
				});
				view = (CheckBox) mFilterView.findViewById(R.id.checkBox4);
				view.setChecked(mFilter[3]);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFilter[3] = (!mFilter[3]);
					}
				});
				view = (CheckBox) mFilterView.findViewById(R.id.checkBox5);
				view.setChecked(mFilter[4]);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFilter[4] = (!mFilter[4]);
					}
				});
				view = (CheckBox) mFilterView.findViewById(R.id.checkBox6);
				view.setChecked(mFilter[5]);
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mFilter[5] = (!mFilter[5]);
					}
				});
				view = (CheckBox) mFilterView.findViewById(R.id.checkBox7);
				view.setChecked(mFilter[6]);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFilter[6] = (!mFilter[6]);
					}
				});
				view = (CheckBox) mFilterView.findViewById(R.id.checkBox8);
				view.setChecked(mFilter[7]);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFilter[7] = (!mFilter[7]);
					}
				});
				view = (CheckBox) mFilterView.findViewById(R.id.checkBox9);
				view.setChecked(mFilter[8]);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFilter[8] = (!mFilter[8]);
					}
				});
				view = (CheckBox) mFilterView.findViewById(R.id.checkBox10);
				view.setChecked(mFilter[9]);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFilter[9] = (!mFilter[9]);
					}
				});
				
				mFilterDialog.show();
			}
		});
		
		//Button dbutton = (Button) this.findViewById(R.id.date_button1);
		//dbutton.setVisibility(View.GONE);
		
		return view;
	}
	
	private Boolean[] mFilter = { true, true, true, true, true, true, false, true, true, true };
	
	protected void CreateContract(TaskWorkdayEntity workdayEntity) {
		DocContractJournal docs = new DocContractJournal(mContext);
		docs.NewEntity();
		DocContractEntity doc = docs.Current();
	
		doc.setDefaults(getContext(), workdayEntity);
		doc.MasterTask = Globals.getCurrentWorkday();
		if (docs.save()) {
			doc = docs.Current();
		} else {
			Dialogs.MessageBox("Не удалось записать созданный Стандартный договор");
			return;
		}

		OpenEntity(docs, doc);
	}

	@Override
	protected void createEntity() {
		CreateContract(_owner);	
	}

	@Override
	protected void OpenEntity(QueryGetContractorsEntity entity) {
		DocContractJournal docs = new DocContractJournal(mContext);
		DocContractEntity order = docs.FindById(entity.Id);
		
		OpenEntity(docs, order);
	}
	
	protected void OpenEntity(DocContractJournal journal, DocContractEntity entity) {
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
			this._catalog = new QueryGetContractors(mContext, Globals.getEmployee().Id, null, mFilter);
		} else {
			this._catalog = new QueryGetContractors(mContext, null, (mDate != null ? mDate : new DateTime(mCalendar.getTimeInMillis())), mFilter);
		}
		this._catalog.SetListType(SfsListType.EXTENDED_LIST);
		this._catalog.Select();
		if (lv != null)
			lv.setAdapter(this._catalog);
		this._catalog.notifyDataSetChanged();
	}

	public static class QueryContractorsViewItem extends GenericListItemView<QueryGetContractors, QueryGetContractorsEntity> {
		private Context mContext;
		private LayoutInflater mLayoutInflater;
		private QueryGetContractorsEntity mEntity;
		private String mUserId;
		
		private TextView mNumber;
		private TextView mCustomer;
		private TextView mDocStatus;
		private ImageView mStatus;
		
		public QueryContractorsViewItem(Context context, QueryGetContractors list, ListView lv, QueryGetContractorsEntity entity) {
			super(context, list, lv);
			
			mContext = context;
			mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			Typeface mTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
			
			mStatus = (ImageView) findViewById(R.id.status_icon);	
			int icon = R.drawable.canceled;
			
			if (mEntity.Draft == 0) {
				if (mEntity.Status == 1 && mEntity.IsAccepted == 0) {
					icon = R.drawable.not_sent; 
				}
				if (mEntity.Status == 1 && mEntity.IsAccepted == 1) {
					icon = R.drawable.imported; 
				}
				if (mEntity.Status == 2 && mEntity.IsAccepted == 1) {
					icon = R.drawable.imported; 
				}
				if (mEntity.Status == 3 && mEntity.IsAccepted == 1) {
					icon = R.drawable.imported; 
				}
				if (mEntity.Status == 4 && mEntity.IsAccepted == 1) {
					icon = R.drawable.imported; 
				}
				if (mEntity.Status == 5 && mEntity.IsAccepted == 1) {
					icon = R.drawable.imported; 
				}
				if (mEntity.Status == 6 && mEntity.IsAccepted == 1) {
					icon = R.drawable.approved; 
				}
				if (mEntity.Status == 7 && mEntity.IsAccepted == 1) {
					icon = R.drawable.imported; 
				}
				if (mEntity.Status == 8 && mEntity.IsAccepted == 1) {
					icon = R.drawable.imported; 
				}
				if (mEntity.Status == 9 && mEntity.IsAccepted == 1) {
					icon = R.drawable.imported; 
				}
				if (mEntity.Status == 10 && mEntity.IsAccepted == 1) {
					icon = R.drawable.canceled; 
				}
			}
			if (mEntity.Draft == 1) {
				icon = R.drawable.draft; 
			}
			
			mStatus.setImageDrawable(getResources().getDrawable(icon)); 
			
			mNumber = (TextView) findViewById(R.id.number);
			mNumber.setTypeface(mTypeface);
			DateTime createDate = new DateTime(mEntity.CreateDate.getTime());
			mNumber.setText("№ " + mUserId + "/" + mEntity.Id + " от " + createDate.toString(DateTimeFormat.forPattern("dd.MM.yyyy")));
 			
			mCustomer = (TextView) findViewById(R.id.customer);
			mCustomer.setTypeface(mTypeface);
			mCustomer.setText(mEntity.ContractorLegalName);
			
			mDocStatus = (TextView) findViewById(R.id.address);
			mDocStatus.setTypeface(mTypeface);
			mDocStatus.setText(new RequestStatusType(mEntity.Status).toString());
			mDocStatus.setTextSize(18f);
			switch (mEntity.Status) {
				case 6: {
					mDocStatus.setTextColor(getResources().getColor(R.color.green)); 
				} break;
				case 7: {
					mDocStatus.setTextColor(getResources().getColor(R.color.green)); 
				} break;
				case 10: {
					mDocStatus.setTextColor(getResources().getColor(R.color.red)); 
				} break;
				default: {
					
				} break;
			}
		} 
	}
}
