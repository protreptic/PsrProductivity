package ru.magnat.sfs.ui.android.doc.order;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OnValueChangedListener;
import ru.magnat.sfs.bom.cache.promo.Promo;
import ru.magnat.sfs.bom.cache.promo.PromoTotalChangedListener;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderLine;
import ru.magnat.sfs.bom.order.DocOrderLineEntity;
import ru.magnat.sfs.bom.order.OnChangeOrderQuantityListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnLineChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnTradeRuleChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnWarehouseChangedListener;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickList;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListEntity;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListOrdered;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListSiblings;
import ru.magnat.sfs.bom.ref.productitem.RefProductItem;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRuleEntity;
import ru.magnat.sfs.bom.ref.warehouse.RefWarehouseEntity;
import ru.magnat.sfs.logging.PsrProductivityLogger;
import ru.magnat.sfs.promo.OnPromotionStateListener;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.controls.OnCommitListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class DocOrderPickupTab extends SfsContentView 
implements OnValueChangedListener
//, OnSeekBarChangeListener

, OnCommitListener
, Closeable, OnStockControlResultListener, OnLineChangedListener, OnOrderPickListSelectingListener, OnPromotionStateListener, PromoTotalChangedListener {
	final QueryGetOrderPickList _catalog;
	ListView _lv;
	public String _synchronizedOrderKey = "";
	DocOrderEntity _entity;
	ExpandableListAdapter mAdapter;
	PickupType _filter_type;
	
	Long _currentItemId = null;
	DocOrderEntityView _owner;
	Boolean _inflated = false;
	AlertDialog _dialog = null;

	public DocOrderPickupTab(Context context) {
		super(context);
		_catalog = null;
	}

	public DocOrderPickupTab(Context context, DocOrderEntity entity, PickupType filter_type, DocOrderEntityView owner, QueryGetOrderPickList catalog) {
		super(context);
		this._catalog = catalog;
		this._entity = entity;
		this._filter_type = filter_type;
		this._owner = owner;
	
		this._entity.addOnLineChangedListener(this);
		_currentItemId = owner.getCurrentCsku();
		_synchronizedOrderKey = owner.getSynchronizedOrderKey();
	}
	public PickupType getPickupType(){
		return this._filter_type;
	}
	public void refreshFields() {}

	RefProductItem _products = null;
	MotionEvent _lv_touch_event = null;
	void createPickupList() {
		if (_lv != null) {
			_lv = null;
		}

		_lv = (ListView) findViewById(R.id.list);
		_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	
		
		_lv.setAdapter(_catalog);
		_lv.setSelector(R.drawable.promo_list_selector);
	
		_lv.setFastScrollEnabled(true);
		_lv.setScrollBarStyle(android.view.View.SCROLLBARS_INSIDE_INSET);
		_lv.setFastScrollAlwaysVisible(true);
		
		_lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> listview, View view,
					int position, long id) {
				 
				String productName = (String) ((TextView) view.findViewById(R.id.csku_name)).getText();
				//Log.v(SFSActivity.LOG_TAG,"first visible position:"+Integer.toString(_lv.getFirstVisiblePosition()));
			
				//DocOrderPickupTab.this.mCurrentPosition = position;
				DocOrderPickupTab.this.setCurrentPosition(position,"on click");
				
				if (DocOrderPickupTab.this.getCurrentPosition() != DocOrderPickupTab.this.getPreviousPosition() && DocOrderPickupTab.this.mCommiteState == -1) {

					

					Dialogs.createDialog(
							"Внимание",
							"Для подтверждения ввода необходимо нажимать на кнопку \"Подтвердить ввод\" (Кнопка с дискетой). \n\nПодтвердить ввод текущего значения \""
									+ productName
									+ "\", "
									+ DocOrderPickupTab.this.mCurrentValue
									+ "?", new Command() {

								@Override
								public void execute() {
									//DocOrderPickupTab.this.fixSelection(DocOrderPickupTab.this.getPreviousPosition());
									if (_currentValue!=null) DocOrderPickupTab.this.commitValue(getCurrentPosition(),_currentValue);
								}
							}, new Command() {

								@Override
								public void execute() {
									DocOrderPickupTab.this.setPreviousPosition(DocOrderPickupTab.this.getCurrentPosition(),"after confirm saving");
									DocOrderPickupTab.this.mCommiteState = 0;
								}
							}).show();
				} else {
					fixSelection(position);
				}
				
			}
		});
		_lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				showLineInfo(position);
				return false;
			}

		});
		this._entity.addOnPromoStateChangedListener(this);
		

	}
	
	
	
	public void showLineInfo(int position){
		selectedPickItem = _catalog.getItem(position);
		if (selectedPickItem != null)
			
			if (selectedPickItem.ProductItemId != 0) {
				startOpeningLineDetails();
				
			}
		
	}
	QueryGetOrderPickListEntity selectedPickItem=null;
	DocOrderLineInfoView _lineInfoView = null;
	Float _current_baseprice = null;
	Float _current_price = null;
	Float _current_discount = null;
	DocOrderLineEntity _current_line = null;
	
	public void finishOpeningLineDetails(){
		 
		RefProductItem ref = new RefProductItem(MainActivity.getInstance());
		RefProductItemEntity product = (RefProductItemEntity) ref.FindById(selectedPickItem.ProductItemId); //BUGFIX #59 USMANOV 18/09/2013
		ref.close();
		if (product==null) return;
		_lineInfoView = new DocOrderLineInfoView(MainActivity.sInstance, product, _entity, _current_line, _current_baseprice, _current_price, _current_discount);
		_dialog = Dialogs.createDialog("", "", _lineInfoView.inflate(), null, null, null);
		_dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				_lineInfoView.dispose();
				_lineInfoView = null;
			}
		});
		_dialog.show();
	}
	
	public void processOpeningLineDetails() {
		try {
			_current_baseprice = null;
			_current_price = null;
			_current_discount = null;
			_current_line = null;
			
			_current_baseprice = DocOrderEntity.getPrice(_entity, selectedPickItem.ProductItemId);

			
			_current_price = DocOrderEntity.getAmountLine(_entity, _entity.AmountBase, _entity.Delay, selectedPickItem.ProductItemId,selectedPickItem.CskuId,selectedPickItem.GcasState, _current_baseprice,selectedPickItem.VAT);
			
			DocOrderLine lines = (DocOrderLine) _entity.getLines(MainActivity.sInstance);
			if (lines != null) {
				_current_line = lines.getLine(MainActivity.sInstance, selectedPickItem.ProductItemId);
				if (_current_line!=null)
					if (_current_line.Quantity>0)
						_current_price = _current_line.Amount/_current_line.Quantity;
					
			}
			if (_current_baseprice != null && _current_price != null && _current_baseprice != 0) {
				_current_discount = _current_price / _current_baseprice * 100f - 100f;
			}
		} catch (Exception e) {
			PsrProductivityLogger.e("Ошибка расчета цены!", e);
		}
	}
	
	private ProgressDialog progress;
	public void startOpeningLineDetails(){
		this.progress = ProgressDialog.show(getContext(), "", "Расчет цены товара...");
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				processOpeningLineDetails();
				progress.dismiss();
				MainActivity.sInstance.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							finishOpeningLineDetails();
						}
					});
				}
			});
		t.start();
	}

	private void commitValue(Integer position, Integer value) {
		fixSelection(getPreviousPosition());
		mCommiteState = 1;
		
		if (position == -1) {
			return;
		}
		
		new StockControl(this, position, value, _owner.getUnitLevel(), false).execute();
		_currentValue = null;
	}

	private Integer _currentValue;
	private TextView _currentTextView;

	@Override
	public void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
			if (visibility == VISIBLE) {
				_catalog.notifyDataSetChanged();
				scrollToOrderKey();
				Log.w(MainActivity.LOG_TAG, this.getClass().getName()+" now VISIBLE");
			}
		
	}

	private void initCurrentTextView() {
		View v = _lv.getChildAt(getPreviousPosition() - _lv.getFirstVisiblePosition());
		_currentTextView = (TextView) v.findViewById(R.id.pick_list_order2);
	}

	@Override
	public SfsContentView inflate() {
		Log.w(MainActivity.LOG_TAG, this.getClass().getName()+" inflating...");
		_invalidList = true;
		RefTradeRuleEntity rule = _entity.TradeRule;
		if (rule == null) {
			Dialogs.MessageBox("Не удалось найти торговые условия, ввести заказ будет невозможно!");
		} else {
			_entity.addOnWarehouseChangedListener(new OnWarehouseChangedListener() {

				@SuppressWarnings("rawtypes")
				public void onWarehouseChanged(GenericEntity sender,
						RefWarehouseEntity old_value,
						RefWarehouseEntity new_value) {
					_invalidList = true;
					refresh();
				}
			});
			_entity.addOnTradeRuleChangedListener(new OnTradeRuleChangedListener() {

				@Override
				public void onTradeRuleChanged(
						@SuppressWarnings("rawtypes") GenericEntity sender,
						RefTradeRuleEntity old_value,
						RefTradeRuleEntity new_value) {
					_invalidList = true;
					refresh();
				}

			});
		}
		
		layoutInflater.inflate(R.layout.generic_list_view, this);
		createPickupList();
		_lv.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				_owner.getNumericPad().setVisibility(VISIBLE);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

				
			}
			
		});
		_lv.setOnScrollListener(new OnScrollListener(){

			private long _beginFly;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					_beginFly = 0;
		
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					_beginFly = (new Date()).getTime();
					
					break;
				}
				
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (_beginFly>0){
					if ((new Date()).getTime()-_beginFly>1000) {
						_owner.getNumericPad().setVisibility(GONE);
						_beginFly = 0;
					}
				}
				
			}
			
		});
		if (_catalog instanceof QueryGetOrderPickListSiblings){
			refresh();
		}
		scrollToOrderKey();
		Log.w(MainActivity.LOG_TAG, this.getClass().getName()+" inflated.");

		return this;
	}


	public enum PickupType {
		REFILL, LIST_A, LIST_B, LIST_OTHER, ORDER, SIBLINGS, REFILL_RECOMMENDED
	}
	
	private boolean _invalidList;

	@Override
	public void refresh() {
		if (_catalog instanceof QueryGetOrderPickListOrdered){
			_invalidList = true;
		}
		if (_catalog instanceof QueryGetOrderPickListSiblings) {
			((QueryGetOrderPickListSiblings) _catalog).setFilterByProduct(_owner.getCurrentCsku());
		} else {
			if (_invalidList){
				_catalog.asyncSelect();
			}
		}
	}
	
	protected ProgressDialog _selectingProgress = null;
	
	@Override
	public void fill() {}

	@Override
	public void moveTaskToBack(Boolean handled) {}

	private String mCurrentValue = "";
	private short mCommiteState = 0;
	//private int mLastPosition = 0;
	private int _current_position = 0;
	private int _previous_position = -1;
	
	public int getCurrentPosition(){
		return _current_position;
	}
	
	public void setCurrentPosition(int position,String source){
		Log.v(MainActivity.LOG_TAG,source+": current position changed "+Integer.toString(position));
		_current_position = position;
	}
	
	public int getPreviousPosition(){
		return _previous_position;
	}
	
	public void setPreviousPosition(int position,String source){
		Log.v(MainActivity.LOG_TAG,source+": previous position changed "+Integer.toString(position));
		_previous_position = position;
	}
	
	public void onValueChanged(Object sender, Object value) {
	//	moveFocusToList();
		_currentValue = (Integer) value;
		
		View v = _lv.getChildAt(getPreviousPosition() - _lv.getFirstVisiblePosition());

		
		
		if (v == null) {
			return;
		}
		_currentTextView = (TextView) v.findViewById(R.id.pick_list_order2);
		if (_currentTextView.getVisibility() == View.GONE) {
			_currentTextView.setVisibility(View.VISIBLE);
		}
		
		String t = "";
		if (_currentValue > 0) {
			t = Integer.toString(_currentValue);
			switch (_owner.getUnitLevel()) {
			case 1:
				t += " КОР";
				break;
			default:
				t += " ШТ";
			}
		}
		mCurrentValue = t;
		if (_currentTextView == null) {
			initCurrentTextView();
		}
		if (_currentTextView != null) {
			Utils.setEditedText(_currentTextView, t);
		}

		this.setPreviousPosition(this.getCurrentPosition(), "on value changed");
		if (!t.equals("")) {
			this.mCommiteState = -1;
		} else {
			this.mCommiteState = 0;
		}
	}
	
	int _seek_position = 0;

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (!fromUser)
			return;
		_seek_position = (int) (((float) _lv.getCount()) * (float) progress / (float) seekBar
				.getMax());
		_lv.smoothScrollToPosition(_seek_position);
	}

	void moveFocusToList(){
		_lv.requestFocusFromTouch();
	}
	void fixSelection(int position) {
		setPreviousPosition(position,"fixSelection") ;

		int top = 0;
		int firstVisiblePosition = _lv.getFirstVisiblePosition();
		int lastVisiblePosition = _lv.getLastVisiblePosition();
		if (!(position < firstVisiblePosition || position > lastVisiblePosition)) {
			View v = _lv.getChildAt(position - firstVisiblePosition);
			top = (v == null) ? 0 : v.getTop();
			moveFocusToList();
			Log.v(MainActivity.LOG_TAG,"fixSelection: setSelectionFromTop from "+Integer.toString(top)+" to "+Integer.toString(getPreviousPosition()));
			_lv.setSelectionFromTop(getPreviousPosition(), top);
		} else {
			moveFocusToList();
			_lv.setSelection(getPreviousPosition());
		}
		fireCurrentChanged(getPreviousPosition());
		_owner.getNumericPad().resetValue(0);
	}

	void restoreSelection() {
		
		try {
			Log.w(MainActivity.LOG_TAG, "Restoring selection...");
			if (getPreviousPosition() > _catalog.getCount()) {
				setPreviousPosition(_catalog.getCount() - 1,"restore selection");
			}

			if (_lv == null)
				return;
			
			int top = 0;

			int firstVisiblePosition = _lv.getFirstVisiblePosition();
			int lastVisiblePosition = _lv.getLastVisiblePosition();
			Log.v(MainActivity.LOG_TAG
					,"last_position="
					+ Integer.toString(getPreviousPosition())
					+" firstVisiblePosition="
					+ Integer.toString(firstVisiblePosition)
					+" lastVisiblePosition="
					+ Integer.toString(lastVisiblePosition)		
							);	
			if (!(getPreviousPosition() < firstVisiblePosition || getPreviousPosition() > lastVisiblePosition)) {
				View v = _lv.getChildAt(getPreviousPosition() - firstVisiblePosition);
				top = (v == null) ? 0 : v.getTop();
				moveFocusToList();
				_lv.setSelectionFromTop(getPreviousPosition(), top);
				Log.v(MainActivity.LOG_TAG,"restoreSelection: setSelectionFromTop from "+Integer.toString(top)+" to "+Integer.toString(getPreviousPosition()));
			} else {
				moveFocusToList();
				Log.v(MainActivity.LOG_TAG,"setSelection to "+Integer.toString(getPreviousPosition()));
				_lv.setSelection(getPreviousPosition());
			}
		} catch (Exception e) {
			Log.v(MainActivity.LOG_TAG, "restore selection: " + e.getMessage());
		}
		finally {
			Log.w(MainActivity.LOG_TAG, "Restored selection.");
		}
	}

	public void scrollToOrderKey() {
		String synchronizedOrderKey = _owner.getSynchronizedOrderKey();
		
		
		
		Log.v(MainActivity.LOG_TAG,"scroll to order "+synchronizedOrderKey + " (" + _catalog.getClass().getName() + ", count="+_catalog.getCount()+")");
		_synchronizedOrderKey = synchronizedOrderKey;
		if (this.getVisibility() == View.VISIBLE) {
			
			if (_catalog != null) {
				if (_catalog.getCount()==0)
					return;
				Log.w(MainActivity.LOG_TAG,"Searching position...");
				int position = _catalog.searchByOrderKey(_synchronizedOrderKey);
				Log.w(MainActivity.LOG_TAG,"Founded position");
				if (position>_lv.getFirstVisiblePosition()-1 && position<_lv.getLastVisiblePosition())
					return;
			
					_lv.setSelection(position);
					_lv.clearFocus();
					removeSelection();		
		
			
			
			}
		}
		if (synchronizedOrderKey.equals("")) restoreSelection();
		
		
	}

	void removeSelection() {
		setPreviousPosition(-1,"remove selection");
	}
/*
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			if (!_seek_progress) {
				if (getPreviousPosition() < _lv.getFirstVisiblePosition()) {
					View v = _lv.getChildAt(0);
					int offset = 0;
					if (v.getHeight() > v.getTop())
						offset = 1;
					fixSelection(_lv.getFirstVisiblePosition() + offset);
				} else if (getPreviousPosition() > _lv.getLastVisiblePosition()) {
					View v = _lv.getChildAt(_lv.getLastVisiblePosition()
							- _lv.getFirstVisiblePosition());
					if (v!=null) {
						int offset = 0;
						if (v.getHeight() > _lv.getHeight() - v.getTop())
							offset = 1;
						fixSelection(_lv.getLastVisiblePosition() - offset);
					}
				} 
			
			}
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			break;
		}
		
	}
	*/

	private final Set<IEventListener> _eventCurrentChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnCurrentChangedListener(OnCurrentChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventCurrentChangedListeners, eventListener);
	}

	public void addOnCurrentChangedListener(
			OnCurrentChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventCurrentChangedListeners,
				eventListener);
	}

	protected void fireCurrentChanged(int position) {
		QueryGetOrderPickListEntity entity = _catalog.getItem(position);
		if (entity != null) {
			this._currentItemId = entity.CskuId;
			this._synchronizedOrderKey = entity.OrderKey;
			//Toast.makeText(SFSActivity.Me, "fireCurrentChanged: " + this._synchronizedOrderKey, Toast.LENGTH_LONG).show();
			for (IEventListener eventListener : _eventCurrentChangedListeners) {
				((OnCurrentChangedListener) eventListener).onCurrentChanged(this, _currentItemId, this._synchronizedOrderKey);
			}
		}
	}
	

	
	@Override
	public void onCommit(Object sender, Integer value) {
		commitValue(getCurrentPosition(), value);
	}
		
		class StockControl extends AsyncTask<Void,Void,Integer> {
			Boolean _needProgress = false;
			final OnStockControlResultListener _callback;
			protected ProgressDialog mProgressDialog;
			
			public StockControl(OnStockControlResultListener callback, Integer position, Integer value, Integer unitlevel, Boolean needProgress){
				_callback = callback;
				_needProgress = needProgress;
				_position = position;
				_value = value;
				_unitlevel = unitlevel;
			}
			
			Integer _position;
			Integer _value;
			Integer _unitlevel;
			
			@Override
			protected Integer doInBackground(Void... params) {
				QueryGetOrderPickListEntity entity = _catalog.getItem(_position);
				if (entity == null) {
					return null;
				}
				int inStock = (int) entity.Stock;

				int unitFactor = (int) entity.UnitFactor1;
				if (_unitlevel == null) _unitlevel = _owner.getUnitLevel();
				if (_value == null) {
					if (entity.Recommended == 0) {
						return 0;
					}
					_unitlevel = 0;
					_value =(int) entity.Recommended;
				}
				int outOfStock = 0;
				
				if (_unitlevel == 0) {
					outOfStock = _value - inStock;
				} else {
					outOfStock = (_value * unitFactor) - inStock;
				}
				return outOfStock;
			}
			
			@Override
			protected void onPreExecute() {
				if (_needProgress) {
					mProgressDialog = new ProgressDialog(MainActivity.getInstance(),android.R.style.Theme_DeviceDefault_Light_Panel);
					mProgressDialog.setMessage("Сохранение заказа.\nПожалуйста, подождите...");
					if (!mProgressDialog.isShowing())
						mProgressDialog.show();
				}
				
			} 
			
			@Override
			protected void onPostExecute(Integer result){
				if (_needProgress){
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
				}
				if (_callback!=null) _callback.onStockControlResult(_position,_value, _unitlevel, result);
			}
			
		}
		/*
		public class QuantityOfPosition{
			final public Integer Position;
			final public Integer Quantity;
			final public Integer UnitLevel;
			public QuantityOfPosition(Integer position,Integer quantity,){
				Position = position;
				Quantity = quantity;
			}
		}
		*/
		class CommitTask extends AsyncTask<Void,Void,Void>{
			int _value = 0;
			Integer _abc = 0;
			Integer _unitlevel = 0;
			Integer _unitFactor = 1;
			Integer _position = -1;
			//RefProductItemEntity _product = null;
			Long _productId = null;
			Long _cskuId = null;
			Long _brandId = null;
			protected ProgressDialog mProgressDialog;
			final Boolean _needProgress;
			public CommitTask(Boolean needProgress,Integer position, int quantity, Integer unitlevel){
				_needProgress = needProgress;
				_unitlevel = unitlevel;
				_value = quantity;
				_position = position;
				
			}
			@Override
			protected Void doInBackground(Void... params) {
				mCommiteState = 1;
				if (_position==-1) return null;
				
			
				QueryGetOrderPickListEntity e = _catalog.getItem(_position);
				if (e==null) return null;
				_abc = (int) e.ABC;
				_productId = e.ProductItemId;
				_cskuId = e.CskuId;
				_brandId = e.Brand;
				switch (_unitlevel) {
				case 1:
					_unitFactor = e.UnitFactor1;
					break;
				case 2:
					_unitFactor = e.UnitFactor2;
					break;
					default:
						_unitFactor = 1;
				}
				_currentValue = null;
				_currentTextView = null;
				return null;
			}
		
			@Override
			protected void onPostExecute(Void param) {
				_entity.setOnQuantityChangedListener(new OnChangeOrderQuantityListener(){

					@Override
					public void onChangeOrderQuantity() {
						_catalog.notifyDataSetChanged();
					}
					
				});
				_entity.changeQuantity(_productId, _value, _unitlevel,_abc,_cskuId,_unitFactor,_brandId);
				
				if (_needProgress) {
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
				}
				
			} 
			
			@Override
			protected void onPreExecute() {
				if (_needProgress) {
					mProgressDialog = new ProgressDialog(MainActivity.getInstance(),android.R.style.Theme_DeviceDefault_Light_Panel);
					mProgressDialog.setMessage("Сохранение заказа.\nПожалуйста, подождите...");
					if (!mProgressDialog.isShowing()) {
						mProgressDialog.show();
					}
				}
			} 
		}
		
		@Override
		public void close() throws IOException {
			_entity.removeOnPromoStateChangedListener(this);
			if (_catalog!=null){
				_catalog.close();
			}
			
		}

		public class CommitCommand implements Command
		{

			final Integer _position;
			final Integer _quantity;
			final Integer _unitlevel;
			public  CommitCommand(Integer position,Integer quantity,Integer unitlevel){
				_position = position;
				_quantity = quantity;
				_unitlevel = unitlevel;
			}
			@Override
			public void execute() {
				new CommitTask(false,_position,_quantity,_unitlevel).execute();
				
			}
			
		}

		@Override
		public void onStockControlResult(Integer position, Integer quantity,Integer unitlevel, Integer outOfStock) {
			if (quantity==null) {
				_catalog.notifyDataSetChanged();
				return;
			}
			if (outOfStock == null) return;
			if (outOfStock > 0) {
				Dialogs.createDialog(
						"Внимание",
						"На складе не хватает товара в количестве " + outOfStock + " штук. Подтвердить ввод?", 
						
						new CommitCommand(position,quantity,unitlevel)
								//DocOrderPickupTab.this.commitValue();
						
						, new CommitCommand(position,0,0)).show();
			} else {	
				new CommitCommand(position,quantity,unitlevel).execute();
			}
			
		}

		@Override
		public void onLineChanged(DocOrderEntity sender, DocOrderLineEntity old_value, DocOrderLineEntity new_value) {
			
		}

		@Override
		public void onBeforeSelect() {
			
			
		}

		@Override
		public void onSelect(String... messages) {
			if (_selectingProgress==null){
				_selectingProgress = new ProgressDialog(MainActivity.getInstance(),0);
			}
			
			String msg = "Отбор товаров";
			for (String m: messages){
				msg+="\n"+m;
			}
			msg+="\nПожалуйста подождите...";
			_selectingProgress.setMessage(msg);
			
			if (!_selectingProgress.isShowing())
				_selectingProgress.show();
			
		}

		@Override
		public void onAfterSelect(Boolean result) {
			if (_selectingProgress!=null) {
				_selectingProgress.dismiss();
				_selectingProgress = null;
			}
			if (result) {
				_invalidList = false;
				_owner.setSiblingsData(0l);
				scrollToOrderKey();
				Log.v(MainActivity.LOG_TAG, "Список подбора для "+_catalog.getClass().getName()+" обновлен");
			}
			
		}

		public void detachAdapter() {
			_lv.setAdapter(null);
			
		}

		public void attachAdapter(QueryGetOrderPickList cat) {
			_lv.setAdapter(cat);
			
		}

		@Override
		public void onPromotionCompensationTypeChanged(int compensationType,
				Long promotionId) {
			_catalog.notifyDataSetChanged();
		}

		@Override
		public void onPromotionTypeAvailabilityChanged(Boolean availability,
				int promotionType) {
			_catalog.notifyDataSetChanged();
		}

		@Override
		public void onPromotionAvailabilityChanged(Boolean availability,
				Long promotionId) {
			_catalog.notifyDataSetChanged();
		}

		@Override
		public void onPromotionCounterChanged(Integer counter, Long promotionId) {
			_catalog.notifyDataSetChanged();
		}

		@Override
		public void onPromoTotalChanged(Promo promo) {
			_catalog.notifyDataSetChanged();
			
		}
	
}

