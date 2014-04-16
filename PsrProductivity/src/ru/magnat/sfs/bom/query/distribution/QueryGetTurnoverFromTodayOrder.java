package ru.magnat.sfs.bom.query.distribution;

import java.util.ArrayList;

import com.ianywhere.ultralitejni12.ULjException;

import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import ru.magnat.sfs.android.Log;
import android.widget.ListView;

public final class QueryGetTurnoverFromTodayOrder extends QueryGeneric<QueryGetTurnoverEntity> {

	static final String query = "SELECT count(j.Id) as Id, sum(Amount) as Value " +
			" from DocOrderJournal j " +
			" where Outlet = ? and datediff(day,CreateDate,now()) = 0 and j.IsMark = 0 and j.Id<>?";
	final long _outlet;
	final long _current_order;
	
	

	public QueryGetTurnoverFromTodayOrder(Context context, long outlet,long current_order) {
		super(context, QueryGetTurnoverEntity.class, query);
		_outlet = outlet;
	
		_current_order = current_order;
	
		if (prepareSelect(new ArrayList<SqlCriteria>(), "")) {
			setSelectParameters(new ArrayList<SqlCriteria>());
		}
	}
	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		int param = 0;
		try {
			
			if (_cursor != null)
				_cursor.close();
			
			_select_statement.set(++param, _outlet);
			_select_statement.set(++param, _current_order);
			
		} catch (ULjException e) {
			Log.v(MainActivity.LOG_TAG, this.getClass().getName() + ":error on set " + param + " param: " + e.getMessage());
			return false;
		}

		return true;
	}
	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		
		Log.v(MainActivity.LOG_TAG,"Начало выбора продаж дня");
		Boolean res =  super.Select(criteria, "");
		Log.v(MainActivity.LOG_TAG,"Конец выбора продаж дня");
		return res;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
