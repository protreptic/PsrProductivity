package ru.magnat.sfs.bom.query.order;

import java.util.ArrayList;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannelEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.order.DocOrderPickListItemView;
import ru.magnat.sfs.ui.android.doc.order.OnOrderPickListSelectingListener;
import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public abstract class QueryGetOrderPickList extends QueryGeneric<QueryGetOrderPickListEntity> implements PickupInterface {

	protected final DocOrderEntity _doc;
	protected final RefEmployeeEntity _employee;
	protected final RefStoreChannelEntity _channel;
	protected PickFilter _pickFilter;

	String _orderKey = "";
	int _pos = -1;
	protected final RefOutletEntity _outlet;

	public QueryGetOrderPickList(Context context, DocOrderEntity entity, PickFilter filter, String query) {
	
		super(context, QueryGetOrderPickListEntity.class, query);
		_doc = entity;
		_employee = Globals.getEmployee(); //BUGFIX #54 USMANOV 18/09/2013 
		_pickFilter = filter;
		_outlet = entity.Outlet;
		if (_outlet != null) {
			_channel = _outlet.Channel;
		} else {
			_channel = null;
		}
		if (_pickFilter!=null) { 
			_pickFilter.addOnFilterChanged(new OnPickFilterChangedListener() {
				@Override
				public void onFilterChanged(PickFilter filter) {
					_pickFilter = filter;
					beforeFilterChanged();
					asyncSelect();
					
				}		
			});
		}
	}
	
	protected void beforeFilterChanged() {}
	
	OnOrderPickListSelectingListener _selectCallback;
	public void attachSelectCallback(OnOrderPickListSelectingListener callback){
		_selectCallback = callback;
	}
	public void detachSelectCallback(){
		_selectCallback = null;
	}
	class SelectAsync extends AsyncTask<Void,String,Boolean>
	{
		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
			if (_selectCallback!=null) _selectCallback.onBeforeSelect();
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			
			while (_selecting){
				try {
					this.publishProgress("ожидание завершения");
					Thread.sleep(300);
					Log.v(MainActivity.LOG_TAG,"Ожидаем завершение предыдущего запроса");
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}
			this.publishProgress("выполнение запроса");
			Boolean result = Select();
			return result;
		}
		@Override
		protected void onProgressUpdate(String... values) {
			
			super.onProgressUpdate(values);
			if (_selectCallback!=null) _selectCallback.onSelect(values);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			
			super.onPostExecute(result);
			if (_selectCallback!=null) _selectCallback.onAfterSelect(result);
		}
		
	}
	Boolean _selecting = false;
	public void asyncSelect(){
		(new SelectAsync()).execute();
	}
	@Override
	
	public Boolean Select() {
		if (_selecting) {
			Log.w(MainActivity.LOG_TAG, "Не могу запросить данные, запрос уже выполняется: "+this.getClass().getName());
			return false;
		}
		Log.w(MainActivity.LOG_TAG, "Начало выборки данных для списка подбора: "+this.getClass().getName());
	
		if (prepareSelect(new ArrayList<SqlCriteria>(), "")) {
			setSelectParameters(new ArrayList<SqlCriteria>());
		}
		_selecting = true;
		Boolean result = super.Select();
		_selecting = false;
		Log.w(MainActivity.LOG_TAG, "Конец выборки данных для списка подбора: "+this.getClass().getName());
		return result;
	}
	
	
	
	@Override
	abstract public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria);
	
	@Override
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return new DocOrderPickListItemView(_context, this, lv, _doc);
	}

	@Override
	public
	 SfsContentView GetExtendedListItemView(ListView lv){
		return new DocOrderPickListItemView(MainActivity.getInstance(),this,lv,_doc);
	}
	
	SparseArray<QueryGetOrderPickListEntity> _cache = new SparseArray<QueryGetOrderPickListEntity>();
	QueryGetOrderPickListEntity _currentListItem = null;
	public QueryGetOrderPickListEntity getCurrentListEntity(){
		return _currentListItem;
	}
	
	@Override
	public QueryGetOrderPickListEntity getItem(int at) {
		
		QueryGetOrderPickListEntity found = null; 
		Boolean fetched = this.To(at);
		if (fetched){
			this._invalid = true;
			this.ReadEntity();
			found = this.Current();
		}
		return found;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DocOrderPickListItemView v = null;
		Boolean neednew = false;
		if (convertView == null) {
			_currentViewGroup = parent;
			neednew = true;
		} else {
			if (!parent.equals(_currentViewGroup))
				neednew = true;
		}

		neednew = true;
		
		if (neednew) {
			v = (DocOrderPickListItemView) GetExtendedListItemView((ListView) parent);
			v.inflate();
			v.fill();
		} else {
			v = (DocOrderPickListItemView) convertView;
		}

		return v;
	}
	
	/**
	 * TODO Написать JavaDoc для этой функции
	 * 
	 * @param orderKey
	 * @return
	 */
	public int searchByOrderKey(final String orderKey) {
		// Если 
		if (orderKey==null || orderKey.isEmpty()) {
			return 0;
		}
		// 
		if (!orderKey.equals(_orderKey)) {
			_orderKey = orderKey;
			int a = 0;
			int b = this._count;
			int pos =  ((a + b) / 2);
			while (true) {
				if (b - a < 2) break;
				if (this.To(pos)) {
					QueryGetOrderPickListEntity entity = this.Current(); 
					if (entity==null) break;
					int cmp = orderKey.compareTo(entity.OrderKey); //BUG 1095
					if (cmp == 0) break;
					if (cmp < 0) {
						b = pos;
					} else {
						a = pos;
					}
					pos = ((a + b) / 2);
				}
			}
			_pos = (int) pos;
		}
		
		return _pos;
	}
}
