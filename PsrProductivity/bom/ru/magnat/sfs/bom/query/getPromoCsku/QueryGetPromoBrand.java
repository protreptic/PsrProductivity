package ru.magnat.sfs.bom.query.getPromoCsku;

import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

import com.ianywhere.ultralitejni12.ULjException;

public class QueryGetPromoBrand extends QueryGeneric<QueryGetPromoBrandEntity> 
//implements IBO<QueryGetPromoCsku, QueryGetPromoCskuEntity, RefOutletEntity> 
{

	final static String _query = "select " +
			"distinct pb.Id" +
			", pb.Brand" +
			", a.Promo" +
			", a.PromoType" +
			", d.Discount" +
			", p.DescriptionSfa as Descr" +
			", a.IsAvailable " +
			", a.CompensationType " +
			", a.PrefferedCompensationType as PrefferedCompensationType " +
			", pt.Target as Target " +
			", pt.Fact as Fact " +
			"  from RegPromoAvailability a"  
			+" inner join RefOutlet o on a.Outlet = o.Id"
			+" inner join RefPromoDetails d on a.Promo = d.Id"
			+" inner join RefPromo p on d.ParentExt = p.Id"
			+" inner join RefPromoBrand pb  on p.Id = pb.ParentExt"
			+" inner join RegPromoTarget pt on p.Id=pt.Promo and pt.Customer=o.ParentExt"
			+" where a.Outlet = ?"
			+" and datediff(day,a.StartOfPromo,?)>=0" +
			"  and datediff(day,?,a.EndOfPromo)>=0"
			+" and not a.IsAvailable is null" 
			;
	

	final RefOutletEntity _outlet;
	final Date _ondate;
	public QueryGetPromoBrand(Context context, RefOutletEntity entity, Date ondate) {
		super(context, QueryGetPromoBrandEntity.class, _query);
		_outlet = entity;
		_ondate = ondate;
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
			if (_ondate == null) {
				_select_statement.setNull(++param);
				_select_statement.setNull(++param);
			} else {
				_select_statement.set(++param, _ondate);
				_select_statement.set(++param, _ondate);
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
