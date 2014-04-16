package ru.magnat.sfs.bom.query.order;
import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import android.content.Context;
import ru.magnat.sfs.android.Log;

import com.ianywhere.ultralitejni12.ULjException;

public final class QueryGetOrderPickListNotSoldGolden extends QueryGetOrderPickList
{
	static final String _query = "SELECT distinct ra.Id as Id "
			+ ", i.id as prodItem "
			+ ", p.Price*(1+(1-pt.IncludeVAT)*i.VAT) as prodPrice "
			+ ", COALESCE(s.ABC,0) as abc "
			+ ", COALESCE(s.IsPriority,0) as isPriority "
			+ ", COALESCE(s.IsDrive,0) as isDrive "
			+ ", 0 as Recommended "
			+ ", COALESCE(w.Quantity,0) as Stock "
			+ ", c.OrderKey as OrderKey "
			+ ", c.Id as Csku "
			+ ", h.LastSale as LastSale "
			+ ", 0 as Novetly "
			+ ",  s.ABC"
			+ ", COALESCE(h.Turnover,0) as Turnover "
			+ ", COALESCE(h.Cyclicity,0) as Cyclicity "
			+ ", i.Descr as prodItemDescr "
			+ ", c.Descr as CskuDescr "
			+ ", i.UnitFactor1 as UnitFactor1 "
			+ ", i.UnitFactor2 as UnitFactor2 "
			+ ", i.VAT as VAT "
			+ ", g.Descr as GcasState "
			+ ", i.ProfitDescription as ProfitDescription "
			+ ", c.ExtCode as ExtCode "
			+ ", COALESCE(s.IsNextPriority,0) as isNextPriority "
			+ ", COALESCE(s.IsNextDrive,0) as isNextDrive "
			+ ", COALESCE(c.Brand,0) as Brand"
			+ " FROM RegAssortment ra "
			+ " INNER JOIN RefProductItem i on ra.ProductItem=i.Id "
			+ " INNER JOIN RefGcasState g on i.GcasState=g.Id "
			+ " INNER JOIN RegPrice p on i.Id = p.ProductItem "
			+ " INNER JOIN RefCsku c on i.ParentExt=c.Id "
			+ " INNER JOIN RegCskuState s on c.Id=s.Csku AND s.ABC in (1,3)"
			+ " and s.StoreChannel=? " 
			+ " LEFT JOIN RegOrderHelper h on c.Id=h.Csku "
			+ " and (1 = 1 OR h.Employee=?) " //это мы так по-быстрому отвязались от агента
			+ " and h.Outlet=? "
			//+ " and h.CurrentPeriod=1 "
			+ " LEFT JOIN RegStock w on ra.ProductItem = w.ProductItem and w.Warehouse=? "
			//+ " LEFT JOIN RegTprDiscountByOutlet tpr on tpr.Outlet = ? and (i.Id = tpr.ProductItem or c.Id = tpr.Csku)"
			+ " INNER JOIN RefPriceType pt on p.PriceType=pt.Id "
			+ " WHERE ra.Assortment=? "
			+ " AND p.PriceType=? "
			+ " AND c.Fullpath LIKE ? "
			//+ " AND h.Id IS NULL "
			+ " AND COALESCE(h.CurrentPeriod,0) = 0  "
			+" AND NOT NULLIF(p.Price,0) IS NULL "
			// Условия-фильтры
			+ " AND (? = 0 OR COALESCE(w.Quantity, 0) > 0) " // Нет на складе
			+ " AND (? = 0 OR COALESCE(s.ABC, 0) = 1) " // Лист А
			+ " AND (? = 0 OR COALESCE(s.ABC, 0) = 3) " // Лист С
			//+ " AND (? = 0 OR COALESCE(tpr.Id,0) > 0  OR not nullif(trim(ProfitDescription),'') is null)" //TPR скидка
			+ " AND (? = 0 OR not nullif(trim(ProfitDescription),'') is null)" //Выгода
			/*+ " AND (? = 0 OR COALESCE(s.IsPriority, 0) = 1) " // Приоритеты
			+ " AND (? = 0 OR COALESCE(s.IsDrive, 0) = 1) " // Инициативы
			
*/			+ " AND (? = 0 OR COALESCE(g.Id, 0) = 2 OR COALESCE(g.Id, 0) = 3) " //нерегулярные товары
			+ " AND (? = 0 OR COALESCE(c.IsKit, 0) = 1) " //набор
			+ " ORDER BY c.OrderKey ";
	public QueryGetOrderPickListNotSoldGolden(Context context, DocOrderEntity entity, PickFilter filter) {
		super(context, entity,filter, _query);
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
			
			if (_channel == null)
				_select_statement.setNull(++param);
			else
				_select_statement.set(++param, _channel.Id);
			_select_statement.set(++param, _employee.Id);
			_select_statement.set(++param, _outlet.Id);
			
			if (_doc.Warehouse!=null){
				_select_statement.set(++param,_doc.Warehouse.Id);
			} else _select_statement.setNull(++param);
			//_select_statement.set(++param, _outlet.Id);
			if (_doc.TradeRule == null)
				_select_statement.setNull(++param); //BUGFIX #251 USMANOV
			else if (_doc.TradeRule.Assortment != null)
				_select_statement.set(++param, _doc.TradeRule.Assortment.Id);
			else if (_employee.Assortment != null)
				_select_statement.set(++param, _employee.Assortment.Id);
			else
				_select_statement.setNull(++param);
			if (_doc.TradeRule == null)
				_select_statement.setNull(++param); //BUGFIX #251 USMANOV
			else if (_doc.TradeRule.PriceType != null) {
				if (_doc.TradeRule.PriceType.BaseType != null)
					_select_statement.set(++param, _doc.TradeRule.PriceType.BaseType.Id);
				else
					_select_statement.set(++param, _doc.TradeRule.PriceType.Id);
			} else
				_select_statement.setNull(++param);
			if (_pickFilter.getProduct()!=null){
				_select_statement.set(++param,_pickFilter.getProduct().Fullpath+"%");
			} else _select_statement.set(++param,"%");
			//this.mOutOfStockFilterIsActive = true;
			// Если установлен фильтр по товарам которые есть в наличии
			if (_pickFilter.getInStock()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}
			// Фильтр для листа А
			if (_pickFilter.getOnlyListA()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}
			// Фильтр для листа С
			if (_pickFilter.getOnlyListC()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}
			// Фильтр по TPR скидкам
			if (_pickFilter.getOnlyTpr()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}
		/*	// Фильтр для приоритетов
			if (_pickFilter.getOnlyPriority()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}
			// Фильтр для инициатив
			if (_pickFilter.getOnlyInitiative()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}*/
			// Фильтр по нерегулярным товарам
			if (_pickFilter.getTemporary()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}
			// Фильтр по наборам
			if (_pickFilter.getKits()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}
		} catch (ULjException e) {
			Log.v(TAG, this.getClass().getName()+":error on set "+param+" param: "+e.getMessage());
			return false;
		}
		return true;
	}

	

	//private boolean mOutOfStockFilterIsActive = false;
	

}
