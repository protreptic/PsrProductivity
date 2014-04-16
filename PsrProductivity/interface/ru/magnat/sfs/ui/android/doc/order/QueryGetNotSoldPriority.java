package ru.magnat.sfs.ui.android.doc.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListEntity;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.pricetype.RefPriceTypeEntity;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannelEntity;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRuleEntity;
import ru.magnat.sfs.bom.ref.user.RefUserEntity;
import ru.magnat.sfs.bom.ref.warehouse.RefWarehouseEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;
import ru.magnat.sfs.android.Log;

import com.ianywhere.ultralitejni12.ULjException;

public final class QueryGetNotSoldPriority extends QueryGeneric<QueryGetNotSoldPriorityEntity> {
	final DocOrderEntity _entity;
	static final String _query = "SELECT distinct ra.Id as Id "
			+ ", i.id as prodItem "
			+ ", i.Descr as prodItemDescr "
			+ ", COALESCE(s.IsPriority,0) as IsPriority "
			+ ", COALESCE(s.IsNextPriority,0) as IsNextPriority "
			+ ", c.Id as CskuId"
			+ " FROM RegAssortment ra "
			+ " INNER JOIN RefProductItem i on ra.ProductItem=i.Id "
			+ " INNER JOIN RefGcasState g on i.GcasState=g.Id "
			+ " INNER JOIN RegPrice p on i.Id = p.ProductItem "
			+ " INNER JOIN RefCsku c on i.ParentExt=c.Id "
			+ " INNER JOIN RegCskuState s on c.Id=s.Csku AND (s.ABC=2 OR s.IsPriority=1 OR s.IsDrive=1 OR s.IsNextPriority=1 OR s.IsNextDrive=1) "
			+ " and s.StoreChannel=? " 			// #1
			+ " INNER JOIN RegStock w on ra.ProductItem = w.ProductItem and w.Warehouse=? " // #2
			+ " INNER JOIN RefPriceType pt on p.PriceType=pt.Id "
			+ " LEFT JOIN RegOrderHelper h on c.Id=h.Csku "
			+ " and h.Outlet=? " 				// #3
			+ " LEFT JOIN DocOrderLine l ON l.MasterDocId=? " //#4
			+ " AND l.MasterDocAuthor=? " //#5
			+ " AND l.Item=i.Id"
			+ " AND l.Quantity>0"
			+ " WHERE ra.Assortment=? " // #6 
			+ " AND p.PriceType=? " // #7
			+ " AND COALESCE(h.InitiativePeriod,0) = 0  "
			// Условия-фильтры
			+ " AND COALESCE(w.Quantity, 0) > 0 "
			+ " AND COALESCE(NULLIF(s.IsPriority, 0),NULLIF(s.IsNextPriority, 0),0) = 1 " 
			+ " AND NOT NULLIF(p.Price,0) IS NULL " 
			+ " AND l.Id IS NULL"
			+ " ORDER BY c.OrderKey ";

	public QueryGetNotSoldPriority(Context context, DocOrderEntity entity) {
		super(context, QueryGetNotSoldPriorityEntity.class,_query);
		_entity = entity;
	}

	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		Log.v(TAG, "Подготовка запроса "+_query);
		int param = 0;
		try {
			if (_cursor != null)
				_cursor.close();
		} catch (ULjException e) {

		}
		
		try {
			RefOutletEntity outlet = null;
			RefStoreChannelEntity channel = null;
			RefWarehouseEntity warehouse =null;
			RefUserEntity user = null;
			RefTradeRuleEntity rule = null;
			RefAssortmentEntity assortment = null;
			RefPriceTypeEntity pricetype = null;
			RefEmployeeEntity employee = null;
			
			if (_entity!=null){
				outlet		 = _entity.Outlet;
				warehouse	 = _entity.Warehouse;
				rule		 = _entity.TradeRule;
				employee	 = _entity.Employee;
				user		 = _entity.Author;
			}
			
			if (outlet!=null){
				channel = outlet.Channel;
			}
			
			if (rule!=null){
				assortment = rule.Assortment;
				pricetype = rule.PriceType;
			}
			if (assortment == null) {
				assortment = employee.Assortment;
			}
			if (pricetype!=null){
				if (pricetype.BaseType!=null) {
					pricetype = pricetype.BaseType;
				}
			}
			
			// #1
			if (channel == null) {
				_select_statement.setNull(++param); 
			} else {
				_select_statement.set(++param, channel.Id);
			} // #1
			
			
		
			
			// #2
			if (warehouse != null) {
				_select_statement.set(++param, warehouse.Id);
			} else {
				_select_statement.setNull(++param);
			}
			
			// #3
			if (outlet != null) {
				_select_statement.set(++param, outlet.Id);
			} else {
				_select_statement.setNull(++param);
			}
			
			// #4
			if (_entity != null) {
				_select_statement.set(++param, _entity.Id);
			} else {
				_select_statement.setNull(++param);
			}
			//#5
			if (user != null) {
				_select_statement.set(++param, user.Id);
			} else {
				_select_statement.setNull(++param);
			}
			
						
			// #6 
			if (assortment != null)
				_select_statement.set(++param, assortment.Id); 
			else
				_select_statement.setNull(++param);
			
			
			// #7
			if (pricetype != null) {
				_select_statement.set(++param, pricetype.Id); 
			}
			else {
				_select_statement.setNull(++param);
			}
			
		} catch (ULjException e) {
			Log.v(TAG,
					this.getClass().getName() + ":error on set " + param + "param: "
							+ e.getMessage());
			return false;
		}

		return true;
	}
	
	@Override
	
	public Boolean Select() {
		
	
		if (prepareSelect(new ArrayList<SqlCriteria>(), "")) {
			setSelectParameters(new ArrayList<SqlCriteria>());
		}
		
		return super.Select();
	}

	public List<Map<String, Object>> executeForResult(){
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(); 
		CopyOnWriteArraySet<Long> added = new CopyOnWriteArraySet<Long>(); 
		if (Select()) {
			QueryGetNotSoldPriorityEntity entity; 
			while ((entity = next())!=null) {
				if (added.add(entity.CskuId)){
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("first_line", entity.ProductItemDescr);
					result.add(item);
				}
				
			}
		}
		added.clear();
		close();
		return result;
	}
	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}
}
