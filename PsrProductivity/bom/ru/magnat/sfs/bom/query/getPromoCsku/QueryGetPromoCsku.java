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

public class QueryGetPromoCsku extends QueryGeneric<QueryGetPromoCskuEntity> 
//implements IBO<QueryGetPromoCsku, QueryGetPromoCskuEntity, RefOutletEntity> 
{

	final static String _query = "select " +
			"distinct kl.Id" +
			", kl.Csku as Csku" +
			", kl.ParentExt as Kit" +
			", a.Promo" +
			", a.PromoType" +
			", d.Discount" +
			", p.DescriptionSfa as Descr" +
			", a.IsAvailable " +
			", a.CompensationType " +
			", a.PrefferedCompensationType as PrefferedCompensationType " +
			", coalesce(nullif(k.MinOrderQuantity,0),1) as MinOrderQuantity " +
			", coalesce(nullif(k.MinCskuQuantity,0),1) as MinCskuQuantity" +
			", kl.MustHave" +
			", coalesce(bc.Quantity,0) as FreeProductSize" +
			", coalesce(csku.Brand,0) as Brand" +
			", coalesce(bc.Csku,0) as bonusCsku" +
			"  from RegPromoAvailability a"  
			+" inner join RefPromoDetails d on a.Promo = d.Id"
			+" inner join RefPromoKit k  on d.Id = k.ParentExt"
			+" inner join RefPromoKitList kl on k.Id = kl.ParentExt"
			+" inner join RefCsku csku on kl.Csku = csku.Id"
			+" inner join RefPromo p on d.ParentExt = p.Id"
			+" left join RefPromoBonusCsku bc on a.Promo=bc.ParentExt"
			+" where Outlet = ?"
			+" and datediff(day,a.StartOfPromo,?)>=0" +
			"  and datediff(day,?,a.EndOfPromo)>=0"
			+" and not a.IsAvailable is null" +
			" order by kl.Csku,a.PromoType"
			;
	

	final RefOutletEntity _outlet;
	final Date _ondate;
	public QueryGetPromoCsku(Context context, RefOutletEntity entity, Date ondate) {
		super(context, QueryGetPromoCskuEntity.class, _query);
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
			if (_ondate == null){
				_select_statement.set(++param, new Date());
				_select_statement.set(++param, new Date());
			}
			else {
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
