package ru.magnat.sfs.ui.android.doc.order;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.LongTaskExecutor;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.OnOrderMenuEventListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.Globals.CCS;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderEntity.AsyncLinesCalculator;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.bom.order.DocOrderLineEntity;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnAmountBaseChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnAmountChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnLineChangedListener;
import ru.magnat.sfs.bom.query.order.PickFilter;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickList;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListNotSoldGolden;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListNotSoldOther;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListNotSoldPriority;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListOrdered;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListRefill;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListSiblings;
import ru.magnat.sfs.bom.query.targets.QueryGetDayTargets;
import ru.magnat.sfs.bom.query.targets.QueryGetDayTargetsEntity;
import ru.magnat.sfs.bom.ref.csku.RefCsku;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.generic.RefGeneric.HierarchyMode;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.promo.PromoActivity;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.controls.NumericPadLayout;
import ru.magnat.sfs.ui.android.doc.order.DocOrderPickupTab.PickupType;
import ru.magnat.sfs.ui.android.report.target.TargetView;
import ru.magnat.sfs.ui.android.task.visit.VisitTargetTab;
import ru.magnat.sfs.util.OnTreeControlEventsListener;
import ru.magnat.sfs.util.TreeNode;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DocOrderEntityView extends GenericEntityView<DocOrderJournal, DocOrderEntity> implements OnTreeControlEventsListener, OnOrderMenuEventListener, OnLineChangedListener, OnAmountChangedListener, OnAmountBaseChangedListener {

	private static final int LT_APPLY_FILTER = 1;
	private static final int LT_APPLY_PRODUCT_FILTER = 2;
	
	private static final String ALL_PRODUCTS = "ВСЕ ТОВАРЫ";
	private static final String TAB_HEADER = "HEADER";
	private static final String TAB_REFILL = "REFILL";
	private static final String TAB_POWER = "LIST_A";
	private static final String TAB_INITIATIVE = "LIST_B";
	private static final String TAB_LIST_OTHER = "LIST_OTHER";
	private static final String TAB_ORDER = "ORDER";
	private static final String TAB_SIBLINGS = "SIBLINGS";
	
	//private Context mContext;
	//private SFSActivity mSfsActivity;
	//private LayoutInflater mLayoutInflater;
	//private Typeface mTypeface;
	
	private PickFilter mPickFilter;
	private DocOrderFilterView mDocOrderFilterView;
	private RefCskuEntity mProductFilterSelected;
	
	/* Элементы управления */
	private ToggleButton mChangeUnitButton;
	private NumericPadLayout mNumericPadLayout;
	private TextView mStatusBar;
	
	private int mUnitLevel = -1;
	
	private List<View> mPickupControls = new ArrayList<View>();
	private String mSynchronizedOrderKey = "";
	private Map<String, Float> mDayTargets = new HashMap<String, Float>();
	private Long mCurrentCskuId;
	private VisitTargetTab mCurrentTargetTab;
	
	private Command mCancelCommand = new Command() {
		@Override
		public void execute() {
			cancelCloseView();
		}
	};
	
	private Command mCancelCloseView = new Command() {
		@Override
		public void execute() {
			cancelCloseView();
		}
	};
	
	private Command mExitCommand = new Command() {
		// если согласились, то покажем шапку из которой можно продолжить выход или отменить его
		public void execute() {
			if (!_entity.isReadOnly())_entity.applyTradeRules("Закрытие заказа");
			AlertDialog dlg = Dialogs.createDialog("", "", new DocOrderHeaderTab(getContext(), _catalog, _entity).inflate(), null, 
				new Command() {
					@Override
					public void execute() {
						showPromotionSummaryReport();
						if (_tabFactory instanceof Closeable){
							try {
								((Closeable)_tabFactory).close();
								MainActivity.getInstance().enablePromo(false);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						closeView();
						if (_entity.ReceiveDate == null) {
							TargetView<RefOutletEntity> view = new TargetView<RefOutletEntity>(MainActivity.getInstance(), _entity.Outlet, Calendar.DAY_OF_MONTH);
							AlertDialog dlg = Dialogs.createDialog("Цели визита", "", view.inflate(), null, null, null);
							dlg.setOnDismissListener(new OnDismissReportDialogListener(view));
							dlg.show();
						}
					}
				}, mCancelCloseView);
			// вешаем обработчик, чтобы заказ сохранился (вдруг в шапке чегой-то ппоменяли)
			dlg.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					_catalog.setCurrent(_entity);
					if (_catalog.save()) {
						_catalog.notifyDataSetChanged();
					}
				}
			});
			dlg.show();
		}
	};
	
	private Set<IEventListener> mEventNaviListeners = new CopyOnWriteArraySet<IEventListener>();
	
	/**
	 * @author petr_bu
	 *
	 */
	private class DayTargetsLoad extends LongTaskExecutor {

		/**
		 * @param context
		 */
		public DayTargetsLoad(Context context) {
			super(context);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			mDayTargets.clear();
			mDayTargets.put("ДистрибьюцияPOWER", 0f);
			mDayTargets.put("ДистрибьюцияЗолотыхCSKU", 0f);
			mDayTargets.put("ДистрибьюцияНакопленная", 0f);
			mDayTargets.put("ТоварооборотИнициативы", 0f);
			mDayTargets.put("Товарооборот", 0f);
			
			QueryGetDayTargets dayTargets = new QueryGetDayTargets(getContext(), _entity.Outlet.Id);
			dayTargets.Select();
			QueryGetDayTargetsEntity entity = null;
			while ((entity = dayTargets.next()) != null) {
				mDayTargets.put(entity.Descr, entity.Target);
			}
			dayTargets.close();
			dayTargets = null;
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			refreshStatusBar();
		}
		
	}
	
	private class OnDismissReportDialogListener implements OnDismissListener{
		
		private Closeable mReportView;
		private Command mPostCommand = Command.NO_OP;
		public OnDismissReportDialogListener(Closeable reportView, Command postCommand){
			mReportView = reportView;
			mPostCommand = postCommand;
		}
		public OnDismissReportDialogListener(Closeable reportView){
			mReportView = reportView;
			
		}
		@Override
		public void onDismiss(DialogInterface dialog) {
			try {
				mReportView.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			mPostCommand.execute();
		}
		
	};
	
	/**
	 * @author petr_bu
	 *
	 */
	private class OrderLongTaskExecutor extends LongTaskExecutor {
		
		/**
		 * @param context
		 */
		public OrderLongTaskExecutor(Context context) {
			super(context);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			super.doInBackground(params);
			switch(_action) {
				case LT_APPLY_FILTER: {
					mPickFilter.setFilter(mDocOrderFilterView.getFilter());
					mDocOrderFilterView = null;
				} break;
				case LT_APPLY_PRODUCT_FILTER: {
					MainActivity.getInstance().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							setMenuProductFilterText(((DocOrderEntityView.this.mProductFilterSelected == null) ? ALL_PRODUCTS : DocOrderEntityView.this.mProductFilterSelected.getCaption()));
						}
					});	
					mPickFilter.setProduct(DocOrderEntityView.this.mProductFilterSelected);		
				} break;
			}
			
			return null;
		}

	}
	
	private class DocOrderTabContentFactory implements TabHost.TabContentFactory, OnCurrentChangedListener, Closeable {

		private DocOrderEntity mEntity;
		private DocOrderJournal mCatalog;
		private Map<String, SfsContentView> mTabs = new HashMap<String, SfsContentView>();
		private Map<PickupType,QueryGetOrderPickList> mCatalogs = new HashMap<PickupType,QueryGetOrderPickList>();
		private DocOrderEntityView mOwner;

		public DocOrderTabContentFactory(DocOrderJournal journal, DocOrderEntity entity, DocOrderEntityView entityView) {
			mCatalog = journal;
			mEntity = entity;
			mOwner = entityView;
			
			MainActivity.getInstance().mCurrentOrder = entity;
			MainActivity.getInstance().mCurrentCatalog = journal;
			
			mCatalogs.put(PickupType.LIST_B, createCatalog(PickupType.LIST_B));
			mCatalogs.put(PickupType.LIST_A, createCatalog(PickupType.LIST_A));
			mCatalogs.put(PickupType.REFILL, createCatalog(PickupType.REFILL));
			mCatalogs.put(PickupType.LIST_OTHER, createCatalog(PickupType.LIST_OTHER));
			mCatalogs.put(PickupType.ORDER, createCatalog(PickupType.ORDER));
			mCatalogs.put(PickupType.SIBLINGS, createCatalog(PickupType.SIBLINGS));
			
			mCatalogs.get(PickupType.LIST_B).asyncSelect();
			mCatalogs.get(PickupType.LIST_A).asyncSelect();
			mCatalogs.get(PickupType.REFILL).asyncSelect();
			mCatalogs.get(PickupType.LIST_OTHER).asyncSelect();
			 
			createTabView(TAB_INITIATIVE);
			createTabView(TAB_POWER);
			createTabView(TAB_REFILL);
			createTabView(TAB_LIST_OTHER);
			createTabView(TAB_ORDER);
			createTabView(TAB_SIBLINGS);
		}
		
		/**
		 * NEED_A_COMMENT
		 *
		 * @param pickupType
		 * @return
		 * 
		 * @author petr_bu
		 */
		private QueryGetOrderPickList createCatalog(PickupType pickupType) {
			QueryGetOrderPickList catalog = null;
		
			switch (pickupType) {
				case REFILL: {
					catalog = new QueryGetOrderPickListRefill(getContext(), _entity, mOwner.getFilter());
				} break;
				case LIST_A: {
					catalog = new QueryGetOrderPickListNotSoldGolden(getContext(),_entity, mOwner.getFilter());
				} break;
				case LIST_B: {
					catalog = new QueryGetOrderPickListNotSoldPriority(getContext(), _entity, mOwner.getFilter());
				} break;
				case LIST_OTHER: {
					catalog = new QueryGetOrderPickListNotSoldOther(getContext(), _entity, mOwner.getFilter());
				} break;
				case SIBLINGS: {
					catalog = new QueryGetOrderPickListSiblings(getContext(), _entity, mOwner.getFilter());
				} break;
				default: {
					catalog = new QueryGetOrderPickListOrdered(getContext(), _entity, mOwner.getFilter());
				} break;
			}
			
			return catalog;
		}
		
		/**
		 * NEED_A_COMMENT
		 *
		 * @param cskuId
		 * 
		 * @author petr_bu
		 */
		private void setSiblingsData(Long cskuId){
			mCurrentCskuId = cskuId;
			((QueryGetOrderPickListSiblings) mCatalogs.get(PickupType.SIBLINGS)).setFilterByProduct(mCurrentCskuId);
		}
		
		/**
		 * NEED_A_COMMENT
		 *
		 * @param tag
		 * @return
		 * 
		 * @author petr_bu
		 */
		private View createTabView(String tag) {
			SfsContentView v = null;
			if (tag == TAB_HEADER) {
				v = new DocOrderHeaderTab(getContext(), mCatalog, mEntity);
			} else if (tag == TAB_REFILL) {
				v = new DocOrderPickupTab(getContext(), mEntity, PickupType.REFILL, mOwner,mCatalogs.get(PickupType.REFILL));
			} else if (tag == TAB_POWER) {
				v = new DocOrderPickupTab(getContext(), mEntity, PickupType.LIST_A, mOwner,mCatalogs.get(PickupType.LIST_A));
			} else if (tag == TAB_INITIATIVE) {
				v = new DocOrderPickupTab(getContext(), mEntity, PickupType.LIST_B, mOwner,mCatalogs.get(PickupType.LIST_B));
			} else if (tag == TAB_LIST_OTHER) {
				v = new DocOrderPickupTab(getContext(), mEntity, PickupType.LIST_OTHER, mOwner,mCatalogs.get(PickupType.LIST_OTHER));
			} else if (tag == TAB_ORDER) {
				v = new DocOrderPickupTab(getContext(), mEntity, PickupType.ORDER, mOwner,mCatalogs.get(PickupType.ORDER));
			} else if (tag == TAB_SIBLINGS) {
				v = new DocOrderPickupTab(getContext(), mEntity, PickupType.SIBLINGS, mOwner,mCatalogs.get(PickupType.SIBLINGS));
			}
			if (v != null) {
				v.inflate();
				mTabs.put(tag, v);
			}
			
			return v;
		}
		
		/**
		 * NEED_A_COMMENT
		 *
		 * @param t
		 * 
		 * @author petr_bu
		 */
		private void attachSelectCallback(DocOrderPickupTab t) {
			PickupType filter_type = t._filter_type;
			for (QueryGetOrderPickList cat:mCatalogs.values()) {
				cat.detachSelectCallback();
			}
			QueryGetOrderPickList cat = mCatalogs.get(filter_type);
			if (cat != null) {
				cat.attachSelectCallback(t);
			}
			for (SfsContentView tab:mTabs.values()) {
				if (tab instanceof DocOrderPickupTab) {
					if (!tab.equals(t)) {
						((DocOrderPickupTab)tab).detachAdapter();
					}
				}
			}
			t.attachAdapter(cat);
			t.scrollToOrderKey();
		}
		
		/**
		 * NEED_A_COMMENT
		 *
		 * @param type
		 * 
		 * @author petr_bu
		 */
		private void refreshTabData(PickupType type){
			QueryGetOrderPickList cat = mCatalogs.get(type);
			cat.asyncSelect();
		}
		
		/**
		 * NEED_A_COMMENT
		 *
		 * @param tag
		 * 
		 * @author petr_bu
		 */
		private void refreshTab(String tag) {
			SfsContentView v = mTabs.get(tag);
			if (v instanceof DocOrderPickupTab) {
				DocOrderPickupTab t = (DocOrderPickupTab) v;
				if (tag.equals(PickupType.REFILL.name())) {
					t._filter_type = PickupType.REFILL;
				}
				attachSelectCallback(t);
				_entity.setOnPromoTotalChangedListener(t);
				mOwner.getNumericPad().setOnValueChangedListener(t);
				mOwner.getNumericPad().setOnCommitListener(t);
				if (tag == TAB_ORDER) {
					mCatalogs.get(PickupType.ORDER).asyncSelect();
				}
			}
		}
		
		public View createTabContent(String tag) {
			SfsContentView v = mTabs.get(tag);
			if (v instanceof DocOrderPickupTab) {
				((DocOrderPickupTab) v).setOnCurrentChangedListener(this);
			}
			
			return v;
		}
	
		public void onCurrentChanged(Object sender, Object object, String orderkey) {
			mSynchronizedOrderKey = orderkey;
			if (object != null) {
				mCurrentCskuId = (Long) object;
			} else { 
				mCurrentCskuId = null;
			}
			if (getCurrentPickView() instanceof DocOrderPickupTab) {
				if (((DocOrderPickupTab) getCurrentPickView()).getPickupType() != PickupType.SIBLINGS) {
					setSiblingsData(mCurrentCskuId);
				}
			}
		}
		
		@Override
		public void close() {
			for (View v: mTabs.values()) {
				if (v instanceof Closeable){
					try {
						((Closeable) v).close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * @param context
	 */
	public DocOrderEntityView(Context context) {
		super(context);

		init();
	}
	
	/**
	 * @param context
	 * @param journal
	 * @param entity
	 */
	public DocOrderEntityView(Context context, DocOrderJournal journal, DocOrderEntity entity) {
		super(context, journal, entity);

		init();
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * @author petr_bu
	 */
	private void init() {
		//mSfsActivity = SFSActivity.getInstance();
		mPickFilter = new PickFilter();
				
		showMenu();
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * 
	 * @author petr_bu
	 */
	private void initControls() {
		mStatusBar = (TextView) findViewById(R.id.info_text);
		mStatusBar.setTypeface(typeface);
		mNumericPadLayout = ((NumericPadLayout) findViewById(R.id.numeric_pad));
		mChangeUnitButton = (ToggleButton) findViewById(R.id.change_unit_button);
		mTabHost = (TabHost) findViewById(R.id.tabHost);
		mTabHost.setup();
		mTabHost.setOnTabChangedListener(this);

		mChangeUnitButton.setAlpha(100);
		mChangeUnitButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				changeUnitLevel();
			}
		});

		mPickupControls.add(mNumericPadLayout);
		mPickupControls.add(mChangeUnitButton);
		changeUnitLevel();
		
		// Отобразить в ActionBar`ре кнопку перехода в меню работы с промо-акциями
		MainActivity.getInstance().enablePromo(true);
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * @return
	 * 
	 * @author petr_bu
	 */
	private Set<Integer> getContextFilter() {
		Set<Integer> checkboxes = new CopyOnWriteArraySet<Integer>();
		String tag = mTabHost.getCurrentTabTag();
		if (tag == TAB_REFILL) {
			checkboxes.add(R.id.checkBoxInStock);
			checkboxes.add(R.id.checkBoxRecommended);
			checkboxes.add(R.id.checkBoxOnlyInitiative);
			checkboxes.add(R.id.checkBoxOnlyPriority);
			checkboxes.add(R.id.checkBoxOnlyListA);
			checkboxes.add(R.id.checkBoxOnlyListC);
			checkboxes.add(R.id.checkBoxOnlyTpr);
			checkboxes.add(R.id.checkBoxOnlyTemporary);
			checkboxes.add(R.id.checkBoxOnlyKits);
		} else if (tag == TAB_POWER) {
			checkboxes.add(R.id.checkBoxInStock);
			checkboxes.add(R.id.checkBoxOnlyListA);
			checkboxes.add(R.id.checkBoxOnlyListC);
			checkboxes.add(R.id.checkBoxOnlyTpr);
			checkboxes.add(R.id.checkBoxOnlyTemporary);
			checkboxes.add(R.id.checkBoxOnlyKits);
		} else if (tag == TAB_INITIATIVE) {
			checkboxes.add(R.id.checkBoxInStock);
			checkboxes.add(R.id.checkBoxOnlyInitiative);
			checkboxes.add(R.id.checkBoxOnlyPriority);
			checkboxes.add(R.id.checkBoxOnlyTpr);
			checkboxes.add(R.id.checkBoxOnlyTemporary);
			checkboxes.add(R.id.checkBoxOnlyKits);
		} else if (tag == TAB_LIST_OTHER) {
			checkboxes.add(R.id.checkBoxInStock);
			checkboxes.add(R.id.checkBoxOnlyTpr);
			checkboxes.add(R.id.checkBoxOnlyTemporary);
			checkboxes.add(R.id.checkBoxOnlyKits);
		} else if (tag == TAB_SIBLINGS) {
			checkboxes.add(R.id.checkBoxInStock);
		}
		
		return checkboxes;
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * @return
	 * 
	 * @author petr_bu
	 */
	private View getCurrentPickView(){
		return mTabHost.getCurrentView();
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * 
	 * @author petr_bu
	 */
	private void changeProductFilter() {
		RefCsku ref = (RefCsku) Globals.createOrmObject(RefCsku.class);
		ref.SetParent(null);
		ref.Select(false, true, HierarchyMode.OnlyGroup);
		Dialogs.createSelectFromTreeDialog((RefCsku) Globals.createOrmObject(RefCsku.class), "Товары", this).show();
	}

	/**
	 * NEED_A_COMMENT
	 *
	 * 
	 * @author petr_bu
	 */
	private void changeUnitLevel() {
		mUnitLevel++;
		if (mUnitLevel > 1) {
			mUnitLevel = 0;
		}
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * @param filterText
	 * 
	 * @author petr_bu
	 */
	private void setMenuProductFilterText(String filterText) {
		Menu menu = MainActivity.getInstance().getMainMenu();
		MenuItem item = menu.findItem(R.id.main_order_menu_action_product_filter);
		if (item != null) {
			if (filterText.length() > 15) { 
				item.setTitle(filterText.substring(0, 15));
			} else {
				item.setTitle(filterText);
			}
		}	
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * @author petr_bu
	 */
	private void refreshStatusBar() {
		if (mDayTargets.size() == 0) {
			loadDayTargets();
		}
		try {
			float[] targets = {
				mDayTargets.get("ДистрибьюцияPOWER"), 
				mDayTargets.get("ДистрибьюцияЗолотыхCSKU"),
				mDayTargets.get("ДистрибьюцияНакопленная"),
				mDayTargets.get("Товарооборот")
			};
			mStatusBar.setText(
				String.format("ППД: %d/%d, ЗПД: %d/%d, ОПД: %d/%d, Сумма: %,.2fр/%,.2fр",
					Math.round(targets[0]), _entity.getPowerDistribution(),
					Math.round(targets[1]), _entity.getGoldenDistribution(),
					Math.round(targets[2]), _entity.getTotalDistribution(),
					targets[3], _entity.getTurnover())
			);
		} catch (Exception e){
			Log.e(MainActivity.LOG_TAG,"Ошибка обновоения стаусной строки заказа:"+e.getMessage()+"("+e.getStackTrace().toString()+")");
		}
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * 
	 * @author petr_bu
	 */
	private void loadDayTargets() {
		new DayTargetsLoad(getContext()).execute();
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * 
	 * @author petr_bu
	 */
	private void showPromotionSummaryReport() {
		
		
		Intent intent = new Intent(getContext(), PromoActivity.class);
		intent.putExtra("order_id", _entity.Id);
		intent.putExtra("store_id", _entity.Outlet.Id);
		intent.putExtra("on_date",  _entity.ShipmentDate.getTime());
		intent.putExtra("promotion_summary_report", true);
		getContext().startActivity(intent); 
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * 
	 * @author petr_bu
	 */
	private void showWarningDialog() {
		Dictionary<String,Float> details = new Hashtable<String,Float>();
		CCS res = Globals.creditControl(_entity.getContractor(), _entity.getPaymentType(), _entity.getShipmentDate(), _entity.getAmount(),details);
		Float debt = details.get("Debt");
		Float limit = details.get("Limit");
		Float pdz = details.get("Overdue");
		if (debt == null) {
			debt = 0f;
		}
		if (limit == null) {
			limit = 0f;
		}
		if (pdz == null) {
			pdz = 0f;
		}
		String message = "";
		
		switch (res) {
			case Overdue: {
				message = String.format(Locale.getDefault(),"Имеется ПДЗ в размере %.2f", pdz);
			} break;
			case LimitExceeded: {
				message = String.format(Locale.getDefault(),"Кредитный лимит исчерпан (Заказ=%.2f, Долг= %.2f, Лимит=%.2f)",_entity.getAmount(),debt,limit);
			} break;
			case NoCreditLine: {
				message = "Кредитная линия не установлена";
			} break;
			case Forbidden: {
				message = "Выбранное кредитное условие не доступно";
			} break;
			default: {} break;
		}
		if (!message.isEmpty()) {
			message = "Обнаружено нарушение кредитной политики:\n" + message + "!";
		}
		message += "\nЗакрыть заказ?";
		
		Dialogs.createDialog("", message, new Command() {
			
			@Override
			public void execute() {
				if (!_entity.isReadOnly())_entity.applyTradeRules("Закрытие заказа");
				AlertDialog dlg = Dialogs.createDialog("", "", new DocOrderHeaderTab(getContext(), _catalog, _entity).inflate(), null, 
					new Command() {
						@Override
						public void execute() {
							showPromotionSummaryReport();
							if (_tabFactory instanceof Closeable){
								try {
									((Closeable)_tabFactory).close();
									MainActivity.getInstance().enablePromo(false);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							closeView();
							if (_entity.ReceiveDate == null) {
								TargetView<RefOutletEntity> view = new TargetView<RefOutletEntity>(MainActivity.getInstance(), _entity.Outlet, Calendar.DAY_OF_MONTH);
								AlertDialog dlg = Dialogs.createDialog("Цели визита", "", view.inflate(), null, null, null);
								dlg.setOnDismissListener(new OnDismissReportDialogListener(view));
								dlg.show();
							}
						}
					}, mCancelCloseView);
				// вешаем обработчик, чтобы заказ сохранился (вдруг в шапке чегой-то ппоменяли)
				dlg.setOnDismissListener(new OnDismissListener() {
					public void onDismiss(DialogInterface dialog) {
						_catalog.setCurrent(_entity);
						if (_catalog.save()) {
							_catalog.notifyDataSetChanged();
						}
					}
				});
				dlg.show();
			}
		}, mCancelCommand).show();
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * 
	 * @author petr_bu
	 */
	@Override
	protected void showMenu() {
		MainActivity.getInstance().getMainMenu().findItem(R.id.main_order_menu_action_show_properties).setVisible(true);
		MainActivity.getInstance().getMainMenu().findItem(R.id.main_order_menu_action_show_filters).setVisible(true);
		MainActivity.getInstance().getMainMenu().findItem(R.id.main_order_menu_action_show_targets).setVisible(true);
		MainActivity.getInstance().getMainMenu().findItem(R.id.main_order_menu_action_product_filter).setVisible(true);
		MainActivity.getInstance().getMainMenu().findItem(R.id.main_order_menu_action_recalc_order).setVisible(true);
		MainActivity.getInstance().setOnOrderMenuEventListener(this);
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * 
	 * @author petr_bu
	 */
	@Override
	protected void hideMenu() {
		MainActivity.getInstance().getMainMenu().findItem(R.id.main_order_menu_action_show_properties).setVisible(false);
		MainActivity.getInstance().getMainMenu().findItem(R.id.main_order_menu_action_show_filters).setVisible(false);
		MainActivity.getInstance().getMainMenu().findItem(R.id.main_order_menu_action_show_targets).setVisible(false);
		MainActivity.getInstance().getMainMenu().findItem(R.id.main_order_menu_action_product_filter).setVisible(false);
		MainActivity.getInstance().getMainMenu().findItem(R.id.main_order_menu_action_recalc_order).setVisible(false);
	}

	Stack<Command> mExitCommandStack;
	Command mCloseCommand = new Command() {
		@Override
		public void execute() {
			
			if (_tabFactory instanceof Closeable){
				try {
					((Closeable)_tabFactory).close();
					MainActivity.getInstance().enablePromo(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			closeView();
	
		}
	};
	Command mPromoReportCommand = new Command(){

		@Override
		public void execute() {
			showPromotionSummaryReport();
			mStackCommandProcessor.execute();
		}
		
	};
	Command mTargetReportCommand = new Command() {
		@Override
		public void execute() {
			if (_entity.ReceiveDate == null) {
				TargetView<RefOutletEntity> view = new TargetView<RefOutletEntity>(MainActivity.getInstance(), _entity.Outlet, Calendar.DAY_OF_MONTH);
				AlertDialog dlg = Dialogs.createDialog(getResources().getString(R.string.dialog_order_targets), "", view.inflate(), null, null, null);
				dlg.setOnDismissListener(new OnDismissReportDialogListener(view,mStackCommandProcessor)); 
		
				dlg.show();
			} else {
				mStackCommandProcessor.execute();
			}
		
		}
	};
	Command mStackCommandProcessor = new Command(){

		@Override
		public void execute() {
			try {
				if (mExitCommandStack!=null) {
					Command cmd = mExitCommandStack.pop();
					cmd.execute();
				}
			} catch (EmptyStackException e){
				throw new RuntimeException("EmptyStackException: " + mExitCommandStack.size()); 
			}
			
		}
	};
	
	private class CreditControlAsyncTask extends AsyncTask<String,String,CCS> implements Command {
		private ProgressDialog mProgressDialog;
		private Dictionary<String,Float> details = new Hashtable<String,Float>();
		
		@Override
		protected CCS doInBackground(String... params) {
			return Globals.creditControl(_entity.getContractor(), _entity.getPaymentType(), _entity.getShipmentDate(), _entity.getAmount(),details);
		}
		
		@Override
		protected void onPostExecute(CCS res) {
			Float debt = details.get("Debt");
			Float limit = details.get("Limit");
			Float pdz = details.get("Overdue");
			if (debt == null) {
				debt = 0f;
			}
			if (limit == null) {
				limit = 0f;
			}
			if (pdz == null) {
				pdz = 0f;
			}
			String message = "";
			switch (res) {
				case Overdue: {
					message = String.format(Locale.getDefault(),"Имеется ПДЗ в размере %.2f", pdz);
				} break;
				case LimitExceeded: {
					message = String.format(Locale.getDefault(),"Кредитный лимит исчерпан (Заказ=%.2f, Долг= %.2f, Лимит=%.2f)",_entity.getAmount(),debt,limit);
				} break;
				case NoCreditLine: {
					message = "Кредитная линия не установлена";
				} break;
				case Forbidden: {
					message = "Выбранное кредитное условие не доступно";
				} break;
				default: {} break;
			}
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			if (!message.isEmpty()) {
				Dialogs.createDialog(getContext(), "", "Обнаружено нарушение кредитной политики:\n" + message + "! Закрыть заказ?"
						, mStackCommandProcessor 
						, Command.NO_OP
						,null).show();
				
			}
			else {

				mStackCommandProcessor.execute();
			}
			super.onPostExecute(res);
		}
		
		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(MainActivity.getInstance(),android.R.style.Theme_DeviceDefault_Light_Panel);
			mProgressDialog.setMessage(getResources().getString(R.string.dialog_order_check_trade_rules_msg)); 
			if (!mProgressDialog.isShowing()) {
				mProgressDialog.show();
			}
		}

		@Override
		public void execute() {
			this.execute("");
		}
	}
	
	private class PriorityReportAsyncTask extends AsyncTask<DocOrderEntity, String, List<Map<String, Object>>> implements Command {
		private ProgressDialog mProgressDialog;
		private DocOrderEntity mDoc;
		
		public PriorityReportAsyncTask(DocOrderEntity doc){
			mDoc = doc;
		}
	
		@Override
		protected List<Map<String, Object>> doInBackground(DocOrderEntity... docs) {
			if (docs.length == 1) {
				QueryGetNotSoldPriority notSoldPriority = new QueryGetNotSoldPriority(getContext(), docs[0]);
				return notSoldPriority.executeForResult(); 
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(List<Map<String, Object>> res) {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			
			if (!res.isEmpty()) {
				Dialogs.createDialog(
					getResources().getString(R.string.dialog_order_check_proirities), 
					"", 
					new NotSoldPrioritiesDialog(getContext(), res).inflate(), 
					Command.NO_OP, 
					mStackCommandProcessor, 
					null
				).show();
			} else {
				mStackCommandProcessor.execute();
			}
			
			super.onPostExecute(res);
		}
		
		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(MainActivity.getInstance(),android.R.style.Theme_DeviceDefault_Light_Panel);
			mProgressDialog.setMessage(getResources().getString(R.string.dialog_order_check_proirities_msg));
			if (!mProgressDialog.isShowing())
				mProgressDialog.show();
		}

		@Override
		public void execute() {
			this.execute(mDoc);
			
		}
	}
	
	private Command mShowOrderInfo = new Command() {
		
		@Override
		public void execute() {
			_entity.applyTradeRules(getResources().getString(R.string.dialog_order_check_order_property));
			AlertDialog dlg = Dialogs.createDialog("", "", new DocOrderInfoView(getContext(), _catalog, _entity).inflate(), null, new Command() {
				
				@Override
				public void execute() {
					mStackCommandProcessor.execute();
				}
			}, null);
			dlg.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					_catalog.setCurrent(_entity);
					if (_catalog.save()) {
						_catalog.notifyDataSetChanged();
					}
				}
			});
			dlg.show();
		}
	};
	
	public Boolean onBackPressed() {
		onRemove();
		
		return true;
	};
	
	@Override
	protected Boolean onRemove() {
		mExitCommandStack = new Stack<Command>();
		mExitCommandStack.push(mCloseCommand);
		if (!_entity.isReadOnly()){
			mExitCommandStack.push(mShowOrderInfo);
			mExitCommandStack.push(mPromoReportCommand);
			mExitCommandStack.push(mTargetReportCommand);
			mExitCommandStack.push(new CreditControlAsyncTask());
			mExitCommandStack.push(new PriorityReportAsyncTask(_entity));
		}
		mStackCommandProcessor.execute();
		return false;
	}
	
	@Override
	public SfsContentView inflate() {
		if (_entity == null) {
			return null;
		}
		
		layoutInflater.inflate(R.layout.doc_order_entity_view, this);

		initControls();
		
		_tabFactory = new DocOrderTabContentFactory(_catalog, _entity, this); 
		
		Utils.AddTab(mTabHost, _tabFactory, TAB_INITIATIVE, getResources().getString(R.string.order_tab_initiatives));
		Utils.AddTab(mTabHost, _tabFactory, TAB_POWER, getResources().getString(R.string.order_tab_power));
		Utils.AddTab(mTabHost, _tabFactory, TAB_REFILL, getResources().getString(R.string.order_tab_refill));
		Utils.AddTab(mTabHost, _tabFactory, TAB_LIST_OTHER, getResources().getString(R.string.order_tab_other));
		Utils.AddTab(mTabHost, _tabFactory, TAB_SIBLINGS, getResources().getString(R.string.order_tab_siblings));
		Utils.AddTab(mTabHost, _tabFactory, TAB_ORDER, getResources().getString(R.string.order_tab_order));

		((DocOrderTabContentFactory) _tabFactory).refreshTab(TAB_INITIATIVE);
		_entity.initLineCache();
		_entity.loadPromo();
		if (_entity.isReadOnly()) { 
			Dialogs.createDialog("", getResources().getString(R.string.dialog_order_order_open_violaion), Command.NO_OP).show();  
		} else {
			_entity.applyTradeRules(getResources().getString(R.string.dialog_order_initialization)); 
		}

		_entity.addOnLineChangedListener(this);
		_entity.addOnAmountChangedListener(this);
		_entity.addOnAmountBaseChangedListener(this);
		
		loadDayTargets();
		
		return this;
	}
	
	public void showFilter() {
		mDocOrderFilterView = new DocOrderFilterView(getContext(), mPickFilter);
		Set<Integer> contextFilter = getContextFilter();
		if (contextFilter.isEmpty()){
			Dialogs.MessageBox(getResources().getString(R.string.dialog_order_filter_unavailable)); 
			return;
		}
		mDocOrderFilterView.setContextFilter(contextFilter);
		AlertDialog dlg = Dialogs.createDialog("", "", mDocOrderFilterView.inflate(), null, null, null);
		dlg.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				new OrderLongTaskExecutor(getContext()).execute(LT_APPLY_FILTER);
			}				
		});
		dlg.show();
	}
	
	@Override
	public void onTabChanged(String tabId) {
		super.onTabChanged(tabId);
		((DocOrderTabContentFactory) _tabFactory).refreshTab(tabId);
	}
	
	public void onTreeNodeSelected(Object sender, TreeNode value) {
		mProductFilterSelected = (RefCskuEntity) value;
		MainActivity.getInstance().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setMenuProductFilterText(((mProductFilterSelected == null) ? ALL_PRODUCTS : mProductFilterSelected.getCaption()));
				mPickFilter.setProduct((RefCskuEntity) mProductFilterSelected);
			}
		});
	}

	public void onCancel(Object sender) {}
	
	@Override
	public void closeView() {
		_entity.removeOnLineChangedListener(this);
		_entity.removeOnAmountChangedListener(this);
		_entity.removeOnAmountBaseChangedListener(this);
		
		super.closeView();
	}

	@Override
	public void onLineChanged(DocOrderEntity sender, DocOrderLineEntity oldValue, DocOrderLineEntity newValue) {
		refreshStatusBar();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onAmountBaseChanged(GenericEntity sender, float oldValue, float newValue) {
		refreshStatusBar();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onAmountChanged(GenericEntity sender, float oldValue, float newValue) {
		refreshStatusBar();
	}
	
	public void showProductFilter() {
		changeProductFilter();
		mNumericPadLayout.resetValue(0);
	}
	
	public void showOrderInfo() {
		_entity.applyTradeRules(getResources().getString(R.string.dialog_order_check_order_property)); 
		AlertDialog dlg = Dialogs.createDialog("", "", new DocOrderInfoView(getContext(), _catalog, _entity).inflate(), null, null, null);
		dlg.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				_catalog.setCurrent(_entity);
				if (_catalog.save()) {
					_catalog.notifyDataSetChanged();
				}
			}
		});
		dlg.show();
	}
	
	/**
	 * Запускает активность для работы с промо-акциями
	 * 
	 * @author petr_bu
	 */
	public void showPromo() {
		Intent intent = new Intent(getContext(), PromoActivity.class);
		intent.putExtra("order_id", _entity.Id);
		intent.putExtra("store_id", _entity.Outlet.Id);
		intent.putExtra("on_date", _entity.ShipmentDate.getTime());
		getContext().startActivity(intent);
	}
	
	/**
	 * Показывает диалог с целями
	 * 
	 * @author petr_bu
	 */
	public void showTarget() {
		if (_entity.MasterTask == null) {
			Dialogs.MessageBox(getResources().getString(R.string.dialog_order_visit_undefined)); 
			return;
		}
		_entity.applyTradeRules(getResources().getString(R.string.dialog_order_target_recounting_msg)); 
		mCurrentTargetTab = new VisitTargetTab(getContext(), (TaskVisitEntity) _entity.MasterTask);
		AlertDialog dlg = Dialogs.createDialog("", "", mCurrentTargetTab.inflate(), null, Command.NO_OP, null);
		dlg.setOnDismissListener(new OnDismissListener(){

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mCurrentTargetTab!=null)
					mCurrentTargetTab.dismiss();
				mCurrentTargetTab = null;
			}
			
		});
		dlg.show();
	}

	public void recalcOrder() {
		AsyncLinesCalculator task = _entity.new AsyncLinesCalculator();
		task.useFullRecalc(true); 
		task.execute(getResources().getString(R.string.dialog_order_order_recounting_msg)); 
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * @param cskuId
	 * 
	 * @author petr_bu
	 */
	public void setSiblingsData(Long cskuId) {
		((DocOrderTabContentFactory) _tabFactory).setSiblingsData(cskuId); 
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * @return
	 * 
	 * @author petr_bu
	 */
	public NumericPadLayout getNumericPad() {
		return mNumericPadLayout;
	}

	/**
	 * NEED_A_COMMENT
	 *
	 * @return
	 * 
	 * @author petr_bu
	 */
	public int getUnitLevel() {
		return mUnitLevel;
	}

	/**
	 * NEED_A_COMMENT
	 *
	 * @return
	 * 
	 * @author petr_bu
	 */
	public Long getCurrentCsku() {
		return mCurrentCskuId;
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * @return
	 * 
	 * @author petr_bu
	 */
	public String getSynchronizedOrderKey() {
		return mSynchronizedOrderKey;
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * @return
	 * 
	 * @author petr_bu
	 */
	public PickFilter getFilter() {
		return mPickFilter;
	}

	/**
	 * NEED_A_COMMENT
	 * 
	 * @author petr_bu
	 */
	public void refreshOrderTab() {
		((DocOrderTabContentFactory)_tabFactory).refreshTabData(PickupType.ORDER);
	}
	
	/**
	 * NEED_A_COMMENT
	 *
	 * @param eventListener
	 * 
	 * @author petr_bu
	 */
	public void setOnNaviListener(OnNaviListener eventListener) {
		EventListenerSubscriber.setListener(mEventNaviListeners, eventListener);
	}

	/**
	 * NEED_A_COMMENT
	 *
	 * @param eventListener
	 * 
	 * @author petr_bu
	 */
	public void addOnNaviListener(OnNaviListener eventListener) {
		EventListenerSubscriber.addListener(mEventNaviListeners, eventListener);
	}
	
}
