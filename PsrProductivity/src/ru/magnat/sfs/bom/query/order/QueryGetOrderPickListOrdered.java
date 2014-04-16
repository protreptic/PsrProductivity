package ru.magnat.sfs.bom.query.order;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import android.content.Context;
import ru.magnat.sfs.android.Log;

import com.ianywhere.ultralitejni12.ULjException;

public final class QueryGetOrderPickListOrdered extends QueryGetOrderPickList {

	static final String _query = "SELECT distinct l.Id as Id "
			+ ", i.id as prodItem "
			+ ", l.Amount/l.Quantity as prodPrice "
			+ ", COALESCE(s.ABC,0) as abc "
			+ ", COALESCE(s.IsPriority,0) as isPriority "
			+ ", COALESCE(s.IsDrive,0) as isDrive "
			+ ", COALESCE(h.Quantity,0) as Recommended "
			+ ", COALESCE(w.Quantity,0) as Stock "
			+ ", c.OrderKey as OrderKey "
			+ ", c.Id as Csku "
			+ ", h.LastSale as LastSale "
			+ ", COALESCE(h.Novetly,0) as Novetly "
			+ ", COALESCE(h.Turnover,0) as Turnover "
			+ ", COALESCE(h.Cyclicity,0) as Cyclicity "
			+ ", i.Descr as prodItemDescr "
			+ ", c.Descr as CskuDescr "
			+ ", i.UnitFactor1 as UnitFactor1 "
			+ ", i.UnitFactor2 as UnitFactor2 "
			+ ", i.VAT as VAT "
			+ ", g.Descr as GcasState "
			+ ", i.ProfitDescription as ProfitDescription"
			+ ", c.ExtCode as ExtCode "
			+ ", COALESCE(s.IsNextPriority,0) as isNextPriority "
			+ ", COALESCE(s.IsNextDrive,0) as isNextDrive "
			+ ", COALESCE(c.Brand,0) as Brand"
			+ " FROM DocOrderLine l " 
			+ " INNER JOIN RefProductItem i on l.Item=i.Id"
			+ " INNER JOIN RefGcasState g on i.GcasState=g.Id "
			+ " INNER JOIN RegPrice p on i.Id = p.ProductItem "
			+ " INNER JOIN RefCsku c on i.ParentExt=c.Id "
			+ " LEFT JOIN RegCskuState s on c.Id=s.Csku "
			+ " and s.StoreChannel=? "
			+ " LEFT JOIN RegStock w on i.Id = w.ProductItem and w.Warehouse=? "
			//+ " INNER JOIN RefPriceType pt on p.PriceType=pt.Id "
			+ " LEFT JOIN RegOrderHelper h on c.Id=h.Csku "
			+ " and (1 = 1 OR h.Employee=?)  " + " and h.Outlet=? "
			+ " WHERE l.MasterDocId=?"
			+ " AND  l.MasterDocAuthor=?"
			+ " AND l.Quantity>0 "
			// Условия-фильтры
			//+ " AND (? = 0 OR COALESCE(w.Quantity, 0) > 0) " // Нет на складе
			+ " AND c.Fullpath LIKE ? "
			+ " ORDER BY c.OrderKey ";

	public QueryGetOrderPickListOrdered(Context context, DocOrderEntity entity,
			PickFilter filter) {

		super(context, entity, filter, _query);

	}

	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		int p = 0;
		try {
			if (_cursor != null)
				_cursor.close();
		} catch (ULjException e) {

		}
		try {
			
			if (_channel == null)
				_select_statement.setNull(++p);
			else
				_select_statement.set(++p, _channel.Id);
			
			if (_doc.Warehouse != null) {
				_select_statement.set(++p, _doc.Warehouse.Id);
			} else
				_select_statement.setNull(++p);
			_select_statement.set(++p, _doc.Employee.Id);
			_select_statement.set(++p, _doc.Outlet.Id);
			_select_statement.set(++p, _doc.Id);
			_select_statement.set(++p, _doc.Author.Id);
			/*
			if (_doc.TradeRule == null)
				_select_statement.setNull(++p); //BUGFIX #251,252 ANALOGUE USMANOV
			if (_doc.TradeRule.Assortment != null)
				_select_statement.set(++p, _doc.TradeRule.Assortment.Id);
			else if (_employee.Assortment != null)
				_select_statement.set(++p, _employee.Assortment.Id);
			else
				_select_statement.setNull(++p);
				*/
			/*
			if (_doc.TradeRule == null)
				_select_statement.setNull(++p); //BUGFIX #251,252 ANALOGUE USMANOV
			if (_doc.TradeRule.PriceType != null) {
				if (_doc.TradeRule.PriceType.BaseType != null)
					_select_statement.set(++p,
							_doc.TradeRule.PriceType.BaseType.Id);
				else
					_select_statement.set(++p, _doc.TradeRule.PriceType.Id);
			} else
				_select_statement.setNull(++p);
			*/
	/*		// Если установлен фильтр по товарам которые есть в наличии
			if (_pickFilter.getInStock()) {
				_select_statement.set(++p, 1);
			} else {
				_select_statement.set(++p, 0);
			}*/
						
			if (_pickFilter.getProduct() != null) {
				_select_statement.set(++p, _pickFilter.getProduct().Fullpath + "%");
			} else
				_select_statement.set(++p, "%");
			
		} catch (ULjException e) {
			Log.v(TAG, this.getClass().getName() + ":error on set " + p + "param: " + e.getMessage());
			return false;
		}

		return true;
	}

	//private boolean mOutOfStockFilterIsActive = false;
	
	
}
