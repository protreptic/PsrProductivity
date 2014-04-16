package ru.magnat.sfs.bom.query.getPromoCsku;

import java.util.ArrayList;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

import com.ianywhere.ultralitejni12.ULjException;

public class QueryGetPromoSales extends QueryGeneric<QueryGetPromoSalesEntity> 

{

	final static String _query = "select " +
			"  a.Id" +
			", a.Promo" +
			", a.PromoType" +
			", c.Csku" +
			", c.Quantity" +
			"  from RegPromoAvailability a" +
			" inner join RefPromoDetails b on a.Promo = b.Id" + 
			" inner join RegPromoSales c on b.ParentExt = c.Promo and a.Outlet = c.Outlet"	
			+" where a.Outlet = ?"
			;
	

	final RefOutletEntity _outlet;

	public QueryGetPromoSales(Context context, RefOutletEntity entity) {
		super(context, QueryGetPromoSalesEntity.class, _query);
		_outlet = entity;
		if (prepareSelect(new ArrayList<SqlCriteria>(), "")) {
			setSelectParameters(new ArrayList<SqlCriteria>());
		}
	}

	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		int param = 0;
		try {
			if (_cursor != null) _cursor.close();
			if (_outlet == null) {
				_select_statement.setNull(++param);
			} else {
				_select_statement.set(++param, _outlet.Id);
			}
			
		} catch (ULjException e) {
			Log.v(TAG,
			getClass().getName() + ":error on set params: " + e.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}
	
}
