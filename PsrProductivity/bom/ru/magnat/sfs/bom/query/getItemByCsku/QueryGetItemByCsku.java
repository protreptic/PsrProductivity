package ru.magnat.sfs.bom.query.getItemByCsku;

import java.util.ArrayList;

import com.ianywhere.ultralitejni12.ULjException;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetItemByCsku extends
		QueryGeneric<QueryGetItemByCskuEntity> {

	final long _cskuId;
	final long _channel;
	
	
	static final String query = 
			"select top 1 " +
			"  i.Id" +
			", s.abc " +
			", c.brand " +
			"from RefProductItem i" +
			"  inner join RefCsku c on i.ParentExt = c.Id" +
			" left join RegCskuState s on c.Id = s.Csku and s.StoreChannel = ?" +
			" where c.Id = ?" +
			" order by i.GcasState, i.Id desc"
	;
	public QueryGetItemByCsku(Context context, long cskuId, long channel) {
		super(context, QueryGetItemByCskuEntity.class, query);
		_cskuId = cskuId;
		_channel = channel;
		
		

	}

	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		int param = 0;
		try {
			if (_cursor != null) {
				_cursor.close();
			}
		} catch (ULjException e) {}
		try {
			_select_statement.set(++param, _channel);
			_select_statement.set(++param, _cskuId);
		} catch (ULjException e) {
			Log.v(TAG, this.getClass().getName()+":error on set "+param+" param: "+e.getMessage());
			return false;
		}
		return true;
	}
@Override
	
	public Boolean Select() {
		if (prepareSelect(new ArrayList<SqlCriteria>(), "")) {
			setSelectParameters(new ArrayList<SqlCriteria>());
		}
		Boolean result = super.Select();
		return result;
	}
	public QueryGetItemByCskuEntity execute() {
		QueryGetItemByCskuEntity result = null;
		if (this.Select()) {
			if (this.Next()) {

				result = (QueryGetItemByCskuEntity) this.Current().clone();
			}
			this.close();
		}
		return result;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
