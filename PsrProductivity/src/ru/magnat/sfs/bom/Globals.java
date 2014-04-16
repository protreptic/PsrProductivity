package ru.magnat.sfs.bom;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import ru.magnat.sfs.MainActivity;
import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.CommandWithParameters;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.OnDismissCloseableListener;
import ru.magnat.sfs.android.OnJobFinishedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.SalesForceSolution;
import ru.magnat.sfs.android.SyncMaster;
import ru.magnat.sfs.android.UlFileTransferDownload;
import ru.magnat.sfs.android.UlFileTransferUpload;
import ru.magnat.sfs.android.UlRegisterSync;
import ru.magnat.sfs.android.UlRegularSync;
import ru.magnat.sfs.android.UlSync;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.creditrequest.DocCreditRequestEntity;
import ru.magnat.sfs.bom.creditrequest.DocCreditRequestJournal;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.invoice.DocInvoiceEntity;
import ru.magnat.sfs.bom.invoice.DocInvoiceJournal;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.query.getOutletSalesOfDay.QueryGetCumulativeOutletTarget;
import ru.magnat.sfs.bom.query.getOutletSalesOfDay.QueryGetOutletInitiativeSalesOfDay;
import ru.magnat.sfs.bom.query.getOutletSalesOfDay.QueryGetOutletNewCskuOfDay;
import ru.magnat.sfs.bom.query.getOutletSalesOfDay.QueryGetOutletNewGoldenCskuOfDay;
import ru.magnat.sfs.bom.query.getOutletSalesOfDay.QueryGetOutletNewInitiativeCskuOfDay;
import ru.magnat.sfs.bom.query.getOutletSalesOfDay.QueryGetOutletNewPowerCskuOfDay;
import ru.magnat.sfs.bom.query.getOutletSalesOfDay.QueryGetOutletSalesOfDay;
import ru.magnat.sfs.bom.query.getOutletSalesOfDay.QueryGetOutletSuSalesOfDay;
import ru.magnat.sfs.bom.query.getOutletSalesOfDay.QueryGetVisibilityOutletTarget;
import ru.magnat.sfs.bom.query.getOutletSalesOfDay.QueryGetWorkdaysProgress;
import ru.magnat.sfs.bom.query.getRoute.QueryGetRoute;
import ru.magnat.sfs.bom.query.getRoute.QueryGetRouteEntity;
import ru.magnat.sfs.bom.query.getSalesOfDay.QueryGetCumulativeInitiativeDistribution;
import ru.magnat.sfs.bom.query.getSalesOfDay.QueryGetCumulativeInitiativeTurnover;
import ru.magnat.sfs.bom.query.getSalesOfDay.QueryGetDistribution;
import ru.magnat.sfs.bom.query.getSalesOfDay.QueryGetGoldenSalesOfDay;
import ru.magnat.sfs.bom.query.getSalesOfDay.QueryGetInitiativeDistribution;
import ru.magnat.sfs.bom.query.getSalesOfDay.QueryGetInitiativeTurnover;
import ru.magnat.sfs.bom.query.getSalesOfDay.QueryGetSalesOfDay;
import ru.magnat.sfs.bom.query.getTprDiscounts.QueryGetTprDiscounts.TprDiscountVariant;
import ru.magnat.sfs.bom.query.getTprDiscounts.QueryGetTprDiscountsListView;
import ru.magnat.sfs.bom.query.getTradeRule.QueryGetTradeRule;
import ru.magnat.sfs.bom.query.getTradeRule.QueryGetTradeRuleEntity;
import ru.magnat.sfs.bom.query.order.PickFilter;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListEntity;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListNotSoldPriority;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListOrdered;
import ru.magnat.sfs.bom.query.targets.QueryGetEmployeeTarget;
import ru.magnat.sfs.bom.query.targets.QueryGetKpiByMatrix;
import ru.magnat.sfs.bom.query.targets.QueryGetKpiByMatrixEntity;
import ru.magnat.sfs.bom.query.targets.QueryGetTargetEntity;
import ru.magnat.sfs.bom.query.targets.QueryUpdateKpiCombine;
import ru.magnat.sfs.bom.query.targets.QueryUpdateKpiReplace;
import ru.magnat.sfs.bom.query.targets.QueryGetEmployeeTarget.Mode;
import ru.magnat.sfs.bom.ref.approvestate.RefApproveState;
import ru.magnat.sfs.bom.ref.approvestate.RefApproveStateEntity;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.contractor.RefContractor;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployee;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.generic.RefGeneric.HierarchyMode;
import ru.magnat.sfs.bom.ref.initiative.RefInitiative;
import ru.magnat.sfs.bom.ref.initiative.RefInitiativeEntity;
import ru.magnat.sfs.bom.ref.kpi.RefKpiEntity;
import ru.magnat.sfs.bom.ref.kpimatrix.RefKpiMatrixEntity;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasureEntity;
import ru.magnat.sfs.bom.ref.mediafiles.RefMediaFiles;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.paymenttype.RefPaymentTypeEntity;
import ru.magnat.sfs.bom.ref.pricetype.RefPriceTypeEntity;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRuleEntity;
import ru.magnat.sfs.bom.ref.user.RefUser;
import ru.magnat.sfs.bom.ref.user.RefUserEntity;
import ru.magnat.sfs.bom.ref.warehouse.RefWarehouse;
import ru.magnat.sfs.bom.ref.warehouse.RefWarehouseEntity;
import ru.magnat.sfs.bom.reg.assortment.RegAssortment;
import ru.magnat.sfs.bom.reg.balance.RegBalance;
import ru.magnat.sfs.bom.reg.balance.RegBalanceEntity;
import ru.magnat.sfs.bom.reg.discount.RegDiscount;
import ru.magnat.sfs.bom.reg.discount.RegDiscountEntity;
import ru.magnat.sfs.bom.reg.kpiinterpretation.RegKpiInterpretation;
import ru.magnat.sfs.bom.reg.marketingmeasure.RegMarketingMeasure;
import ru.magnat.sfs.bom.reg.paymenttype.RegPaymentType;
import ru.magnat.sfs.bom.reg.paymenttype.RegPaymentTypeEntity;
import ru.magnat.sfs.bom.reg.price.RegPrice;
import ru.magnat.sfs.bom.reg.stock.RegStock;
import ru.magnat.sfs.bom.reg.welcomeoffersubmitted.RegWelcomeOfferSubmitted;
import ru.magnat.sfs.bom.requestonchangecontractor.DocRequestOnChangeContractorEntity;
import ru.magnat.sfs.bom.requestonchangecontractor.DocRequestOnChangeContractorJournal;
import ru.magnat.sfs.bom.requestonchangeoutlet.DocRequestOnChangeOutletEntity;
import ru.magnat.sfs.bom.requestonchangeoutlet.DocRequestOnChangeOutletJournal;
import ru.magnat.sfs.bom.requestonchangeroute.DocRequestOnChangeRouteEntity;
import ru.magnat.sfs.bom.requestonchangeroute.DocRequestOnChangeRouteJournal;
import ru.magnat.sfs.bom.task.generic.TaskGeneric;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitJournal;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayJournal;
import ru.magnat.sfs.bom.track.QueryGetLastLocation;
import ru.magnat.sfs.ui.android.doc.creditrequest.DocRequestOnChangeCreditView;
import ru.magnat.sfs.ui.android.doc.onchangeroute.DocRequestOnChangeRouteView;
import ru.magnat.sfs.ui.android.doc.requestonchangecontractor.DocRequestOnChangeContractorView;
import ru.magnat.sfs.ui.android.doc.requestonchangeoutlet.DocRequestOnChangeOutletView;
import ru.magnat.sfs.ui.android.extras.SfsExtrasActivity;
import ru.magnat.sfs.ui.android.extras.SfsExtrasFilesActivity;
import ru.magnat.sfs.ui.android.task.workday.SfsTrackActivity;
import ru.magnat.sfs.util.Network;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.widget.ArrayAdapter;

import com.google.android.maps.GeoPoint;
import com.ianywhere.ultralitejni12.ULjException;

/**
 * @author petr_bu
 * 
 */
public class Globals {
	public static final MainActivity CONTEXT = MainActivity.getInstance();

	public static void addEvent(final String title, long endTime) {
		Calendar cal = Calendar.getInstance();
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", cal.getTimeInMillis());
		intent.putExtra("allDay", false);
		intent.putExtra("rrule", "FREQ=DAILY");
		intent.putExtra("endTime", endTime);
		intent.putExtra("title", title);
		CONTEXT.startActivity(intent);
	}

	/**
	 * Функция проверяет есть ли в данном заказе непроданные приоритетные
	 * позиции, если есть то возвращает истина, иначе ложь
	 * 
	 * @param entity
	 *            - ссылка на документ
	 * @return - если есть непроданные инициативные позиции то возвращает
	 *         истина, иначе ложь
	 * 
	 * @author petr_bu
	 * 
	 * @see DocOrderEntity
	 */
	public static boolean areWhetherNotSoldPriorities(DocOrderEntity entity) {
		boolean result = false;

		DocOrderEntity mDocOrderEntity = entity;
		PickFilter mPickFilter;
		QueryGetOrderPickListNotSoldPriority mGetOrderPickListNotSoldPriority;
		QueryGetOrderPickListOrdered mGetOrderPickListOrdered;
		Map<Long, QueryGetOrderPickListEntity> mNotSoldInitiatives = new HashMap<Long, QueryGetOrderPickListEntity>();
		Map<Long, QueryGetOrderPickListEntity> mSoldInitiatives = new HashMap<Long, QueryGetOrderPickListEntity>();

		mNotSoldInitiatives.clear();
		mSoldInitiatives.clear();

		mPickFilter = new PickFilter();
		mPickFilter.setOnlyPriority(true);

		mGetOrderPickListNotSoldPriority = new QueryGetOrderPickListNotSoldPriority(
				CONTEXT, mDocOrderEntity, mPickFilter);
		mGetOrderPickListNotSoldPriority.Select();
		while (mGetOrderPickListNotSoldPriority.Next()) {
			QueryGetOrderPickListEntity entity2;
			if ((entity2 = mGetOrderPickListNotSoldPriority.Current()) != null) {
				mNotSoldInitiatives.put(entity2.CskuId, entity2);
			}
		}
		mGetOrderPickListNotSoldPriority.close();
		mGetOrderPickListNotSoldPriority = null;

		mGetOrderPickListOrdered = new QueryGetOrderPickListOrdered(CONTEXT,
				mDocOrderEntity, mPickFilter);
		mGetOrderPickListOrdered.Select();
		while (mGetOrderPickListOrdered.Next()) {
			QueryGetOrderPickListEntity entity3;
			if ((entity3 = mGetOrderPickListOrdered.Current()) != null) {
				mSoldInitiatives.put(entity3.CskuId, entity3);
			}
		}
		mGetOrderPickListOrdered.close();
		mGetOrderPickListOrdered = null;

		Iterator<Entry<Long, QueryGetOrderPickListEntity>> it = mNotSoldInitiatives
				.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, QueryGetOrderPickListEntity> item = (Entry<Long, QueryGetOrderPickListEntity>) it
					.next();
			if (mSoldInitiatives.containsKey(item.getValue().CskuId)) {
				it.remove();
			}
		}

		result = mNotSoldInitiatives.isEmpty() ? false : true;

		return result;
	}

	/**
	 * NEED_A_COMMENT
	 *
	 * @param pattern
	 * @param value
	 * @return
	 * 
	 * @author petr_bu
	 */
	public static String customFormat(final String pattern, final double value) {
		NumberFormat formatter;
		if (pattern.equalsIgnoreCase("percent"))
			formatter = NumberFormat.getPercentInstance();
		else if (pattern.equalsIgnoreCase("money"))
			formatter = NumberFormat.getCurrencyInstance();
		else
			formatter = new DecimalFormat(pattern);
		String output = formatter.format(value);

		return output;
	}

	public static Date getNextWorkingDay() {
		Date result = new Date();
		Calendar calendar = Calendar.getInstance(new Locale("ru", "RU"));
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.get(Calendar.DAY_OF_WEEK);

		return result;
	}

	public static Date getNextWorkingDate(final Date date) {
		TaskVisitJournal tasks = new TaskVisitJournal(CONTEXT);
		Date date1 = Utils.AddDay(date, 1);
		if (tasks.Select(date1, null, TaskGeneric.DateVariant.BEGIN, false)) {
			if (tasks.Next())
				date1 = tasks.Current().TaskBegin;
		}
		tasks.close();
		return date1;
	}

	public static float getPrice(RefPriceTypeEntity priceType, RefProductItemEntity productItem) {
		RegPrice reg = (RegPrice) createRelatedClass(CONTEXT, RegPrice.class);
		GenericEntity<?> ds[] = { null, priceType, productItem };
		if (reg.Select(ds)) {
			if (reg.Next())
				return reg.Current().Price;
		}
		reg.close();
		return 0;
	}

	public static TaskWorkdayEntity FindWorkdayTask() {
		TaskWorkdayJournal tasks = (TaskWorkdayJournal) createRelatedClass(CONTEXT, TaskWorkdayJournal.class);
		TaskWorkdayEntity task = null;
		Date today = new Date();
		today = Utils.RoundDate(today);
		if (tasks.Select(today, today, TaskGeneric.DateVariant.BEGIN, false)) {
			if (tasks.Next())
				task = tasks.Current();
		}
		tasks.close();
		return task;
	}

	public static RefContractorEntity getDefaultContractor(RefOutletEntity outlet) {
		RefContractor ref = (RefContractor) createOrmObject(RefContractor.class);
		ref.SetOwner(outlet.ParentExt);
		if (ref.Select(true, false, HierarchyMode.OnlyEntity)) {
			if (ref.Next())
				return ref.Current();
		}
		ref.close();
		return null;
	}

	public static RefMarketingMeasureEntity getDefaultMarketingMeasure(RefOutletEntity outlet) {
		RefMarketingMeasureEntity result = null;
		RegMarketingMeasure reg = (RegMarketingMeasure) createOrmObject(RegMarketingMeasure.class);

		if (reg.SelectLast(new Date(), getEmployee(), outlet, null, null))
			if (reg.Next())
				result = reg.Current().MarketingMeasure;
		reg.close();
		return result;
	}

	public static Object[] getDefaultPaymentType(
			RefContractorEntity contractor,
			RefPaymentTypeEntity prefferedPaymentType) {
		RefPaymentTypeEntity pt = null;

		Integer delay = 0;
		RegPaymentType reg = new RegPaymentType(CONTEXT);
		GenericEntity<?> ds[] = { contractor, getEmployee(), null };

		// вместо того чтобы вернуть первый попавшийся нужно перебрать все
		// записи регистра в поисках
		// условия из последнего заказа и если найдено, то использовать его,
		// если нет, то первого
		if (reg.Select(ds)) {

			while (reg.Next()) {
				RegPaymentTypeEntity entity = reg.Current();
				if (entity != null) {
					if (entity.PaymentType != null)
						pt = (RefPaymentTypeEntity) entity.PaymentType.clone();

					delay = entity.Delay;
					if (prefferedPaymentType == null)
						break;
					else if (prefferedPaymentType.equals(pt))
						break;
				}
			}
		}
		reg.close();
		Object[] results = { pt, delay };
		return results;
	}

	public static ArrayAdapter<RefTradeRuleEntity> getTradeRules(
			RefEmployeeEntity employee, RefCustomerEntity customer) {
		ArrayAdapter<RefTradeRuleEntity> array = new ArrayAdapter<RefTradeRuleEntity>(
				CONTEXT, android.R.layout.select_dialog_item);
		QueryGetTradeRule qry = new QueryGetTradeRule(CONTEXT, customer,
				getEmployee());
		QueryGetTradeRuleEntity entity;
		if (qry.Select()) {

			while ((entity = qry.next()) != null) {
				{
					if (entity.TradeRule != null) {
						array.add(qry.Current().TradeRule);
					}
				}
			}
		}
		qry.close();

		qry = new QueryGetTradeRule(CONTEXT, null, getEmployee());
		if (qry.Select()) {
			while ((entity = qry.next()) != null) {
				{
					if (entity.TradeRule != null) {
						array.add(qry.Current().TradeRule);

					}
				}
			}
		}
		qry.close();

		return array;
	}

	/**
	 * Возвращает список торговых условий доступных агенту
	 * 
	 * @param customer
	 * @return
	 * 
	 * @author petr_bu
	 */
	public static List<RefTradeRuleEntity> getTradeRulesAsList(
			RefCustomerEntity customer) {
		List<RefTradeRuleEntity> result = new ArrayList<RefTradeRuleEntity>();

		QueryGetTradeRule qry = new QueryGetTradeRule(CONTEXT, customer,
				getEmployee());
		QueryGetTradeRuleEntity entity;
		if (qry.Select()) {
			while ((entity = qry.next()) != null) {
				if (entity.TradeRule != null) {
					result.add(qry.Current().TradeRule);
				}
			}
		}
		qry.close();
		qry = null;

		qry = new QueryGetTradeRule(CONTEXT, null, getEmployee());
		if (qry.Select()) {
			while ((entity = qry.next()) != null) {
				if (entity.TradeRule != null) {
					result.add(qry.Current().TradeRule);
				}
			}
		}
		qry.close();
		qry = null;

		return result;
	}

	public static RefTradeRuleEntity getTradeRule(RefCustomerEntity customer) {
		RefTradeRuleEntity result = null;
		{
			QueryGetTradeRule qry = new QueryGetTradeRule(CONTEXT, customer,
					getEmployee());
			if (qry.Select()) {

				while (qry.Next()) {
					{
						if (qry.Current().TradeRule != null) {
							result = (RefTradeRuleEntity) qry.Current().TradeRule.clone();
							break;
						}
					}
				}
			}
			qry.close();
		}
		if (result == null) {
			QueryGetTradeRule qry = new QueryGetTradeRule(CONTEXT, null,
					getEmployee());
			if (qry.Select()) {
				while (qry.Next()) {
					{
						if (qry.Current().TradeRule != null) {
							result = (RefTradeRuleEntity) qry.Current().TradeRule
									.clone();
							break;
						}
					}
				}
			}
			qry.close();
		}
		if (result == null) {
			QueryGetTradeRule qry = new QueryGetTradeRule(CONTEXT, null, null);
			if (qry.Select()) {
				while (qry.Next()) {
					{
						if (qry.Current().TradeRule != null) {
							result = (RefTradeRuleEntity) qry.Current().TradeRule
									.clone();
							break;
						}
					}
				}
			}
			qry.close();
		}

		return result;
	}

	public static RefUserEntity getUser() {

		RefUser users = (RefUser) createOrmObject(RefUser.class);
		RefUserEntity user = null;
		if (!users.Select(false, false, HierarchyMode.Both)) {
			return null;
		}

		if (users.Next())
			user = (RefUserEntity) users.Current().clone();
		users.close();

		return user;
	}

	// public static void TestCode() {
	// String query = "SELECT ra.Id as Id"
	// + " , i.id as prodItem "
	// + " , p.Price as prodPrice "
	// + " , COALESCE(s.ABC,0) as abc "
	// + " , COALESCE(s.IsPriority,0) as isPriority "
	// + " , COALESCE(s.IsDrive,0) as isDrive  "
	// + " FROM RegAssortment ra  "
	// + " INNER JOIN RefProductItem i on ra.ProductItem=i.Id  "
	// + " INNER JOIN RegPrice p on i.Id = p.ProductItem  "
	// + " INNER JOIN RefCsku c on i.ParentExt=c.Id  "
	// + " LEFT JOIN RegCskuState s on c.Id=s.Csku  and s.StoreChannel=3  "
	// + " LEFT JOIN RegOrderHelper h on c.Id=s.Csku  "
	// + " and h.Employee=919  " + " and h.Outlet=1094  "
	// + " WHERE ra.Assortment=1  " + " AND p.PriceType=1060  "
	// + " AND c.Fullpath LIKE '%'  " + " AND s.ABC IS NULL  "
	// + " AND h.Id IS NULL  " + " ORDER BY c.OrderKey";
	// QueryGetOrderPickListFreeStyle qry = new QueryGetOrderPickListFreeStyle(
	// CONTEXT, query);
	// qry.Select();
	// }
	public static Boolean productInAssortment(RefProductItemEntity item,
			RefAssortmentEntity assortment) {
		RegAssortment reg = new RegAssortment(CONTEXT);
		Boolean inAssortment = reg.inAssortment(assortment, item);
		reg.close();
		return inAssortment;

	}

	@Deprecated
	public static float getDiscount(RefTradeRuleEntity tradeRule,
			RefProductItemEntity productItem, float oramountbase, long delay) {
		float discount = 0;
		RegDiscount reg = new RegDiscount(CONTEXT);
		{
			if (reg.Select(tradeRule)) {
				long currentDiscount = -1;
				while (reg.Next()) {
					RegDiscountEntity rde = reg.Current();
					if (rde.Discount.Id == currentDiscount)
						continue;

					if (rde.Assortment != null)
						if (!productInAssortment(productItem, rde.Assortment))
							continue;
					switch (rde.DiscountType) {
					case 1: // Транзакционная\
						if (oramountbase < rde.Border)
							continue;
						break;
					case 2: // За кредит
						if (delay < rde.Border)
							continue;
						break;
					default:
						continue;
					}
					discount += rde.DiscountValue;
					currentDiscount = rde.Discount.Id;
				}

			}
		}
		reg.close();
		return discount;
	}

	private static RefEmployeeEntity _employee = null;

	public static RefEmployeeEntity defineEmployee() {
		RefEmployee cat = new RefEmployee(CONTEXT);
		if (cat.Select(false, false, HierarchyMode.OnlyEntity)) {
			_employee = cat.next();
			if (_employee != null)
				_employee = (RefEmployeeEntity) _employee.clone();
		}
		cat.close();

		return _employee;
	}

	public static RefEmployeeEntity getEmployee() { // BUGFIX #54 USMANOV
													// 18/09/2013
		if (_employee != null)
			return _employee;

		return defineEmployee();

	}

	public static RefWarehouseEntity getWarehouse() {

		RefWarehouse cat = (RefWarehouse) createOrmObject(RefWarehouse.class);
		if (!cat.Select(false, false, HierarchyMode.OnlyEntity)) {
			return null;
		}

		if (cat.Next())
			return cat.Current();
		return null;

	}

	@SuppressWarnings("rawtypes")
	private static final HashMap<Class<?>, OrmObject> _relatedClass = new HashMap<Class<?>, OrmObject>();

	public static final OrmObject<?> createRelatedClass(Context context,
			Class<?> cls, OrmObject<?> ormObject) {
		Boolean can_cache = !ormObject.getClass().equals(cls);

		return createRelatedClass(context, cls, can_cache, null);
	}

	public static final OrmObject<?> createOrmObject(Class<?> cls) {
		return createRelatedClass(CONTEXT, cls, true, null);
	}

	public static final OrmObject<?> createKidOrmObject(Class<?> cls,
			GenericEntity<?> owner) {
		return createRelatedClass(CONTEXT, cls, true, owner);
	}

	public static final OrmObject<?> createRelatedClass(Context context,
			Class<?> cls) {
		return createRelatedClass(context, cls, true, null);
	}

	public static final OrmObject<?> createRelatedClass(Context context,
			Class<?> cls, boolean can_cache, GenericEntity<?> owner) {

		OrmObject<?> o = null;
		if (can_cache)
			o = _relatedClass.get(cls);
		if (o != null) {
			if (owner == null)
				return o;
			else if (owner == o.getOwner())
				return o;
			o.close();

		}

		try {
			if (owner == null) {
				Class<?> partypes[] = { Context.class };
				Object arglist[] = { context };
				Constructor<?> ct = cls.getConstructor(partypes);
				o = (OrmObject<?>) ct.newInstance(arglist);
			} else {
				Class<?> partypes[] = { Context.class, owner.getClass() };
				Object arglist[] = { context, owner };
				Constructor<?> ct = cls.getConstructor(partypes);
				o = (OrmObject<?>) ct.newInstance(arglist);
			}
		} catch (Exception e) {
			Log.e(MainActivity.LOG_TAG, e.getMessage());

		}
		if (can_cache) {
			_relatedClass.put(cls, o);
		}
		return o;

	}

	public static long getStock(RefProductItemEntity productItem,
			RefWarehouseEntity warehouse) {
		if (productItem == null || warehouse == null)
			return 0;
		RegStock reg = (RegStock) createOrmObject(RegStock.class);
		if (!reg.Select(new Object[] { warehouse, productItem }))
			return 0;

		if (!reg.Next())
			return 0;

		return reg.Current().Quantity;

	}

	public static final String getDeviceId() {
		String deviceID = null;
		String serviceName = Context.TELEPHONY_SERVICE;
		TelephonyManager m_telephonyManager = (TelephonyManager) CONTEXT
				.getSystemService(serviceName);
		int deviceType = m_telephonyManager.getPhoneType();
		switch (deviceType) {
		case (TelephonyManager.PHONE_TYPE_GSM):
			break;
		case (TelephonyManager.PHONE_TYPE_CDMA):
			break;
		case (TelephonyManager.PHONE_TYPE_NONE):
			break;
		default:
			break;
		}
		deviceID = m_telephonyManager.getDeviceId();
		return deviceID;
	}

	public static GeoPoint getCenterPoint() {
		QueryGetRoute track = (QueryGetRoute) createOrmObject(QueryGetRoute.class);
		if (track.Select()) {
			if (track.Next()) {
				QueryGetRouteEntity location = track.Current();
				return Utils.getGeoPoint(location.Latitude, location.Longitude);
			}
		}
		return null;
	}

	public static TaskWorkdayEntity getCurrentWorkday() {
		return CONTEXT.mCurrentWorkday;
	}

	public static int getColor(int colorId) {

		return CONTEXT.getResources().getColor(colorId);
	}

	public static void finalizeVisit(TaskVisitEntity visit) {
		TaskVisitJournal j = new TaskVisitJournal(MainActivity.getInstance());
		if (j.Find(visit)) {
			TaskVisitEntity e = j.Current();
			Boolean wasCompleted = e.IsCompleted;
			e.IsCompleted = visit.IsCompleted;
			e.VisitType = visit.VisitType;
			e.Result = visit.Result;
			if (wasCompleted == null || !wasCompleted) {
				e.TaskEnd = new Date(System.currentTimeMillis());
			}
			j.save();
		}
		j.close();
		j = null;
	}

	public static RefApproveStateEntity getFirstApproveState() {
		RefApproveStateEntity entity = null;
		RefApproveState ref = new RefApproveState(CONTEXT);
		ref.Select(false, false, HierarchyMode.OnlyEntity);
		// if(ref.Next()) entity = (RefApproveStateEntity)
		// ref.Current().clone();
		if (ref.Next())
			entity = (RefApproveStateEntity) ref.FindById(1).clone(); // Changed
																		// Selyanin
																		// D.V.
																		// 2012-09-11
		ref.close();
		return entity;
	}

	// Credit SDV 2012-09-10
	public static void openOrCreateChangeCreditRequest(
			RefContractorEntity entity) {
		DocCreditRequestJournal j = new DocCreditRequestJournal(CONTEXT);
		j.NewEntity();
		DocCreditRequestEntity dce = j.Current();
		dce.setDefaults(CONTEXT, CONTEXT.mCurrentWorkday);
		dce.Contractor = entity;
		dce.Approved = getFirstApproveState();
		j.save();
		dce = j.Current();
		DocRequestOnChangeCreditView v = new DocRequestOnChangeCreditView(
				CONTEXT, j, dce);
		Dialogs.createDialog("", "", v.inflate(), null,
				createRequestSaveAndExitCommand(j), createRequestExitCommand(j))
				.show();
	}

	// Route SDV 2012-09-10
	public static void openOrCreateChangeRouteRequest(RefOutletEntity entity) {
		if (request > 0)
			return;
		request++;
		DocRequestOnChangeRouteJournal j = new DocRequestOnChangeRouteJournal(
				CONTEXT);
		j.NewEntity();
		DocRequestOnChangeRouteEntity dce = j.Current();
		dce.setDefaults(CONTEXT, CONTEXT.mCurrentWorkday);
		if (entity != null)
			dce.Outlet = (RefOutletEntity) entity.clone();
		dce.VisitCyclicity = 0;// разовое посещение
		dce.VisitDate = Utils.AddDay(new Date(), 1);// следующая дата
		dce.VisitKindId = 0; // тип - заказ
		dce.VisitTime = new Date();
		dce.Approved = getFirstApproveState();// Added SDV 2012-09-11
		j.save();
		dce = j.Current();

		DocRequestOnChangeRouteView v = new DocRequestOnChangeRouteView(
				CONTEXT, j, dce);
		Dialogs.createDialog("", "", v.inflate(), null,
				createRequestSaveAndExitCommand(j), createRequestExitCommand(j))
				.show();
	}

	// Customer
	public static void openOrCreateChangeCustomerRequest(
			RefCustomerEntity entity) {
		if (request > 0)
			return;
		request++;
		DocRequestOnChangeContractorJournal j = new DocRequestOnChangeContractorJournal(
				CONTEXT);
		j.NewEntity();
		DocRequestOnChangeContractorEntity dce = j.Current();
		dce.setDefaults(CONTEXT, CONTEXT.mCurrentWorkday);
		if (entity != null) {
			RefContractor refContractor = new RefContractor(CONTEXT);
			refContractor.SetOwner(entity);
			refContractor.Select(true, false, HierarchyMode.OnlyEntity);
			if (refContractor.Next()) {
				dce.Contractor = (RefContractorEntity) refContractor.Current()
						.clone();
				if (dce.Contractor != null) {
					RefContractorEntity contractor = dce.Contractor;
					dce.INN_KPP = (contractor.INN != null) ? contractor.INN
							: "";
					dce.Address = contractor.LegalAddress;
					dce.DeliveryAddress = contractor.RealAddress;
					dce.NumberAccount = contractor.AccountInfo;
					dce.Approved = getFirstApproveState();// Added SDV
															// 2012-09-11
				}
			}
			refContractor.close();// закрыл курсор
		}
		j.save();
		dce = j.Current();

		// Запрос на изменение/добавление контрагента
		DocRequestOnChangeContractorView v = new DocRequestOnChangeContractorView(
				CONTEXT, j, dce);
		Dialogs.createDialog("", "", v.inflate(), null,
				createRequestSaveAndExitCommand(j), createRequestExitCommand(j))
				.show();

	}

	private static int request = 0; // Singletone

	// Outlet
	public static void openOrCreateChangeOutletRequest(RefOutletEntity entity) {
		if (request > 0) {
			return;
		}
		request++;
		DocRequestOnChangeOutletJournal j = new DocRequestOnChangeOutletJournal(
				CONTEXT);
		j.NewEntity();
		DocRequestOnChangeOutletEntity dce = j.Current();
		dce.setDefaults(CONTEXT, CONTEXT.mCurrentWorkday);
		if (entity != null) {
			dce.Outlet = (RefOutletEntity) entity.clone();
			dce.Address = entity.Address;
			dce.Channel = entity.Channel;
			dce.IsDC = entity.IsDC;
			dce.IsHQ = entity.IsHQ;
			dce.IsOP = entity.IsOP;
			dce.IsShop = entity.IsShop;
			dce.LocationLat = entity.LocationLat;
			dce.LocationLon = entity.LocationLon;
			dce.ServiceType = entity.ServiceType;
			dce.StoreType = entity.StoreType;
			dce.Descr = entity.Descr;
			dce.Approved = getFirstApproveState();
		}

		j.save();
		dce = j.Current();
		DocRequestOnChangeOutletView v = new DocRequestOnChangeOutletView(
				CONTEXT, j, dce);
		Dialogs.createDialog("", "", v.inflate(), null,
				createRequestSaveAndExitCommand(j), createRequestExitCommand(j))
				.show();
	}

	private static CommandWithParameters createRequestSaveAndExitCommand(
			DocGeneric<?, ?> journal) {
		CommandWithParameters cmd = new CommandWithParameters() {
			final Map<String, Object> _params = new HashMap<String, Object>();

			public void execute() {
				DocGeneric<?, ?> journal = (DocGeneric<?, ?>) _params
						.get("journal");
				if (journal != null) {
					journal.save();
					journal.close();
					request--;
				}

			}

			public Map<String, Object> getParameters() {

				return _params;
			}

		};
		cmd.getParameters().put("journal", journal);
		return cmd;
	}

	private static CommandWithParameters createRequestExitCommand(
			DocGeneric<?, ?> journal) {
		CommandWithParameters cmd = new CommandWithParameters() {
			final Map<String, Object> _params = new HashMap<String, Object>();

			public void execute() {
				DocGeneric<?, ?> journal = (DocGeneric<?, ?>) _params
						.get("journal");
				if (journal != null) {
					journal.close();
					request--;
				}

			}

			public Map<String, Object> getParameters() {

				return _params;
			}

		};
		cmd.getParameters().put("journal", journal);
		return cmd;
	}

	public static Location getLastLocation() {
		Location loc = null;
		QueryGetLastLocation query = new QueryGetLastLocation(CONTEXT);
		query.Select();
		if (query.Next()) {
			loc = query.Current().getLocation();
		}
		query.close();
		return loc;
	}

	// public static boolean refreshTargets(){
	// float shipments = new QueryGetSalesOfDay(CONTEXT, new Date()).execute();
	// int daydistribution = new QueryGetTotalDistributionOfDay(CONTEXT, new
	// Date()).execute();
	// int monthdistribution = new QueryGetTotalDistributionOfMonth(CONTEXT, new
	// Date()).execute();
	// return new QueryUpdateTurnoverOfMonthEmployee(CONTEXT.storage.Connection
	// ,shipments
	// ).execute()&&
	// new QueryUpdateTurnoverOfDayEmployee(CONTEXT.storage.Connection
	// , shipments
	// ).execute()&&
	// new
	// QueryUpdateTotalDistributionOfMonthEmployee(CONTEXT.storage.Connection
	// , monthdistribution
	// ).execute()&&
	// new QueryUpdateTotalDistributionOfDayEmployee(CONTEXT.storage.Connection
	// , daydistribution
	// ).execute();
	// }
	public static boolean refreshEmployeeTargets() {

		Boolean result = true;
		Log.v(MainActivity.LOG_TAG, "Обновление целей агента");
		if (getEmployeeTargetUpdated()) {
			Log.v(MainActivity.LOG_TAG, "Обновление целей агента не требуется");
			return true;
		}

		// RefKpi kpis = new RefKpi(CONTEXT);
		// kpis.Select(false, false, HierarchyMode.OnlyEntity);
		RefEmployeeEntity employee = getEmployee();
		if (employee == null)
			return false;
		QueryGetKpiByMatrix kpis = new QueryGetKpiByMatrix(CONTEXT,
				employee.GoalMatrix);
		kpis.Select(new ArrayList<SqlCriteria>());
		float correction = 0;
		float corrections[] = new float[] { 0, 0, 0 };
		Date today = Utils.RoundDate(new Date(System.currentTimeMillis()));
		Date period = Utils.FirstDateOfMonth(today);
		QueryGetKpiByMatrixEntity matrixkpi;
		while ((matrixkpi = kpis.next()) != null) {
			if (matrixkpi.KPI == null)
				continue; // BUGFIX #57,192 USMANOV 18/09/2013
			RefKpiEntity kpi = (RefKpiEntity) matrixkpi.KPI.clone();
			Log.v(MainActivity.LOG_TAG, "Расчет фактов " + kpi.Descr);
			if (kpi.KpiKind.equalsIgnoreCase("Товарооборот")) {

				correction = new QueryGetSalesOfDay(CONTEXT, today).execute();
				Log.v(MainActivity.LOG_TAG, "Корректировка " + correction);
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								employee.GoalMatrix, kpi, Calendar.MONTH)
								.execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								employee.GoalMatrix, kpi, Calendar.DAY_OF_MONTH)
								.execute();
			} else if (kpi.KpiKind
					.equalsIgnoreCase("ТоварооборотПерваяПоловина")) {
				if (Utils.isFirstPartOfMonth(today))
					correction = new QueryGetSalesOfDay(CONTEXT, today)
							.execute();
				else
					correction = 0f;
				Log.v(MainActivity.LOG_TAG, "Корректировка " + correction);
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								employee.GoalMatrix, kpi, Calendar.MONTH)
								.execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								employee.GoalMatrix, kpi, Calendar.DAY_OF_MONTH)
								.execute();
			} else if (kpi.KpiKind
					.equalsIgnoreCase("ТоварооборотВтораяПоловина")) {
				if (!Utils.isFirstPartOfMonth(today))
					correction = new QueryGetSalesOfDay(CONTEXT, today)
							.execute();
				else
					correction = 0f;
				Log.v(MainActivity.LOG_TAG, "Корректировка " + correction);
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								employee.GoalMatrix, kpi, Calendar.MONTH)
								.execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								employee.GoalMatrix, kpi, Calendar.DAY_OF_MONTH)
								.execute();
			} else if (kpi.KpiKind.equalsIgnoreCase("ТоварооборотЗМ")) {
				correction = new QueryGetGoldenSalesOfDay(CONTEXT, today)
						.execute();
				Log.v(MainActivity.LOG_TAG, "Корректировка " + correction);
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								employee.GoalMatrix, kpi, Calendar.MONTH)
								.execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								employee.GoalMatrix, kpi, Calendar.DAY_OF_MONTH)
								.execute();
			} else if (kpi.KpiKind.equalsIgnoreCase("ДистрибьюцияНакопленная")
					|| kpi.KpiKind.equalsIgnoreCase("ДистрибьюцияPOWER")) {
				corrections = new QueryGetDistribution(CONTEXT, period,
						Calendar.MONTH, kpi).execute();
				Log.v(MainActivity.LOG_TAG, "Корректировка " + corrections[0]);
				result = result
						&& new QueryUpdateKpiReplace(
								InternalStorage.getConnection(),
								corrections[0], employee.GoalMatrix, kpi,
								Calendar.DAY_OF_MONTH).execute();

				// corrections = new QueryGetDistribution(CONTEXT, period,
				// Calendar.MONTH, kpi).execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(),
								corrections[0], employee.GoalMatrix, kpi,
								Calendar.MONTH).execute();
			}

			else if (kpi.KpiKind.equalsIgnoreCase("ДистрибьюцияИнициатив")) {
				RefInitiative ref = new RefInitiative(CONTEXT);
				ref.Select(false, false, HierarchyMode.OnlyEntity);
				RefInitiativeEntity initiative;
				while ((initiative = ref.next())!=null) {


					correction = new QueryGetInitiativeDistribution(CONTEXT,
							today, Calendar.DAY_OF_MONTH, kpi, initiative)
							.execute();
					Log.v(MainActivity.LOG_TAG, kpi.Descr + " (день) "
							+ initiative.Descr + " " + correction);
					result = result
							&& new QueryUpdateKpiCombine(
									InternalStorage.getConnection(),
									(int) correction, employee.GoalMatrix, kpi,
									Calendar.DAY_OF_MONTH, null, initiative)
									.execute();
					correction = new QueryGetInitiativeDistribution(CONTEXT,
							period, Calendar.MONTH, kpi, initiative).execute();
					Log.v(MainActivity.LOG_TAG, kpi.Descr + " (месяц) "
							+ initiative.Descr + " " + correction);
					result = result
							&& new QueryUpdateKpiCombine(
									InternalStorage.getConnection(),
									(int) correction, employee.GoalMatrix, kpi,
									Calendar.MONTH, null, initiative).execute();

				}
				ref.close();
				corrections = new QueryGetCumulativeInitiativeDistribution(
						CONTEXT, today, Calendar.DAY_OF_MONTH, kpi).execute();
				Log.v(MainActivity.LOG_TAG, kpi.Descr + " (день) кумулятивная "
						+ corrections[0]);
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(),
								corrections[0], employee.GoalMatrix, kpi,
								Calendar.DAY_OF_MONTH, null, null,
								corrections[2]).execute();
				corrections = new QueryGetCumulativeInitiativeDistribution(
						CONTEXT, period, Calendar.MONTH, kpi).execute();
				Log.v(MainActivity.LOG_TAG, kpi.Descr + " (месяц) кумулятивная "
						+ corrections[0]);
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(),
								corrections[0], employee.GoalMatrix, kpi,
								Calendar.MONTH, null, null, corrections[2])
								.execute();
			} else if (kpi.KpiKind.equalsIgnoreCase("ТоварооборотИнициативы")) {
				RefInitiative ref = new RefInitiative(CONTEXT);
				ref.Select(false, false, HierarchyMode.OnlyEntity);
				while (ref.Next()) {
					RefInitiativeEntity initiative = (RefInitiativeEntity) ref.Current();
					correction = new QueryGetInitiativeTurnover(CONTEXT, today,
							Calendar.DAY_OF_MONTH, kpi, initiative).execute();
					Log.v(MainActivity.LOG_TAG, kpi.Descr + " (день) "
							+ initiative.Descr + " " + correction);
					result = result
							&& new QueryUpdateKpiCombine(
									InternalStorage.getConnection(),
									correction, employee.GoalMatrix, kpi,
									Calendar.DAY_OF_MONTH, null, initiative)
									.execute();
					correction = new QueryGetInitiativeTurnover(CONTEXT,
							period, Calendar.MONTH, kpi, initiative).execute();
					Log.v(MainActivity.LOG_TAG, kpi.Descr + " (месяц) "
							+ initiative.Descr + " " + correction);
					result = result
							&& new QueryUpdateKpiCombine(
									InternalStorage.getConnection(),
									correction, employee.GoalMatrix, kpi,
									Calendar.MONTH, null, initiative).execute();

				}
				ref.close();
				corrections = new QueryGetCumulativeInitiativeTurnover(CONTEXT,
						today, Calendar.DAY_OF_MONTH, kpi).execute();
				Log.v(MainActivity.LOG_TAG, kpi.Descr + " (день) кумулятивная "
						+ corrections[0]);
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(),
								corrections[0], employee.GoalMatrix, kpi,
								Calendar.DAY_OF_MONTH, null, null,
								corrections[2]).execute();
				corrections = new QueryGetCumulativeInitiativeTurnover(CONTEXT,
						period, Calendar.MONTH, kpi).execute();
				Log.v(MainActivity.LOG_TAG, kpi.Descr + " (месяц) кумулятивная "
						+ corrections[0]);
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(),
								corrections[0], employee.GoalMatrix, kpi,
								Calendar.MONTH, null, null, corrections[2])
								.execute();
			}

		}
		kpis.close();
		setEmployeeTargetUpdated(result);
		return result;
	}

	public static boolean refreshOutletTargets(RefOutletEntity outlet) {
		if (outlet == null) {
			Log.v(MainActivity.LOG_TAG,
					"торговая точка для расчета целей равна NULL");
			return false;
		}
		Log.v(MainActivity.LOG_TAG, "Обновление целей " + outlet.Descr);
		if (getOutletTargetUpdated()) {
			Log.v(MainActivity.LOG_TAG, "Обновление целей не требуется");
			return true;
		}
		Boolean result = true;
		Date today = Utils.RoundDate(new Date(System.currentTimeMillis()));
		Date period = Utils.FirstDateOfMonth(today);
		QueryGetKpiByMatrix kpis = new QueryGetKpiByMatrix(CONTEXT,
				outlet.GoalMatrix);
		kpis.Select(new ArrayList<SqlCriteria>());
		float correction = 0;
		float[] corrections = new float[] { 0f, 0f, 0f };
		QueryGetKpiByMatrixEntity querykpi;
		while ((querykpi = kpis.next()) != null) {
			if (querykpi.KPI == null)
				continue;
			RefKpiEntity kpi = (RefKpiEntity) querykpi.KPI.clone();
			Log.v(MainActivity.LOG_TAG, "Расчет фактов " + kpi.Descr);
			if (kpi.KpiKind.equalsIgnoreCase("Товарооборот")) {
				correction = new QueryGetOutletSalesOfDay(CONTEXT, today,
						outlet).execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								outlet.GoalMatrix, kpi, Calendar.MONTH, outlet)
								.execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								outlet.GoalMatrix, kpi, Calendar.DAY_OF_MONTH,
								outlet).execute();
			} else if (kpi.KpiKind.equalsIgnoreCase("ТоварооборотЗМ")) {
				if (outlet.StoreType.Descr.contains("золотой")) {
					correction = new QueryGetOutletSuSalesOfDay(CONTEXT, today,
							outlet).execute();
				} else {
					correction = 0;
				}
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								outlet.GoalMatrix, kpi, Calendar.MONTH, outlet)
								.execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								outlet.GoalMatrix, kpi, Calendar.DAY_OF_MONTH,
								outlet).execute();
			} else if (kpi.KpiKind.equalsIgnoreCase("ДистрибьюцияНакопленная")) {
				correction = new QueryGetOutletNewCskuOfDay(CONTEXT, today,
						outlet).execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								outlet.GoalMatrix, kpi, Calendar.MONTH, outlet)
								.execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								outlet.GoalMatrix, kpi, Calendar.DAY_OF_MONTH,
								outlet).execute();

			} else if (kpi.KpiKind.equalsIgnoreCase("ДистрибьюцияЗолотыхCSKU")) {
				correction = new QueryGetOutletNewGoldenCskuOfDay(CONTEXT,
						today, outlet).execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								outlet.GoalMatrix, kpi, Calendar.MONTH, outlet)
								.execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								outlet.GoalMatrix, kpi, Calendar.DAY_OF_MONTH,
								outlet).execute();

			} else if (kpi.KpiKind.equalsIgnoreCase("ДистрибьюцияPOWER")) {
				correction = new QueryGetOutletNewPowerCskuOfDay(CONTEXT,
						today, outlet).execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								outlet.GoalMatrix, kpi, Calendar.MONTH, outlet)
								.execute();
				result = result
						&& new QueryUpdateKpiCombine(
								InternalStorage.getConnection(), correction,
								outlet.GoalMatrix, kpi, Calendar.DAY_OF_MONTH,
								outlet).execute();

			} else if (kpi.KpiKind.equalsIgnoreCase("Представленность")) {
				corrections = new QueryGetVisibilityOutletTarget(CONTEXT,
						today, outlet, kpi, Calendar.DAY_OF_MONTH).execute();
				result = result
						&& new QueryUpdateKpiReplace(
								InternalStorage.getConnection(),
								corrections[0], outlet.GoalMatrix, kpi,
								Calendar.MONTH, outlet, null, corrections[2])
								.execute();
				result = result
						&& new QueryUpdateKpiReplace(
								InternalStorage.getConnection(),
								corrections[0], outlet.GoalMatrix, kpi,
								Calendar.DAY_OF_MONTH, outlet, null,
								corrections[2]).execute();
			} else if (kpi.KpiKind.equalsIgnoreCase("ДистрибьюцияИнициатив")) {
				RefInitiative ref = new RefInitiative(CONTEXT);
				ref.Select(false, false, HierarchyMode.OnlyEntity);
				RefInitiativeEntity initiative;
				while ((initiative = ref.next()) != null) {
					correction = new QueryGetOutletNewInitiativeCskuOfDay(
							CONTEXT, today, outlet, initiative).execute();
					Log.v(MainActivity.LOG_TAG, kpi.Descr + " "
							+ initiative.Descr + " " + correction);
					result = result
							&& new QueryUpdateKpiCombine(
									InternalStorage.getConnection(),
									correction, outlet.GoalMatrix, kpi,
									Calendar.MONTH, outlet, initiative)
									.execute();
					result = result
							&& new QueryUpdateKpiCombine(
									InternalStorage.getConnection(),
									correction, outlet.GoalMatrix, kpi,
									Calendar.DAY_OF_MONTH, outlet, initiative)
									.execute();

				}
				ref.close();
				corrections = new QueryGetCumulativeOutletTarget(CONTEXT,
						today, outlet, Calendar.DAY_OF_MONTH, kpi).execute();
				Log.v(MainActivity.LOG_TAG, kpi.Descr + " (кумулятивная день) "
						+ " " + corrections[0] + " " + corrections[2]);
				result = result
						&& new QueryUpdateKpiReplace(
								InternalStorage.getConnection(),
								corrections[0], outlet.GoalMatrix, kpi,
								Calendar.DAY_OF_MONTH, outlet, null,
								corrections[2]).execute();
				corrections = new QueryGetCumulativeOutletTarget(CONTEXT,
						period, outlet, Calendar.MONTH, kpi).execute();
				Log.v(MainActivity.LOG_TAG, kpi.Descr + " (кумулятивная месяц) "
						+ " " + corrections[0] + " " + corrections[2]);
				result = result
						&& new QueryUpdateKpiReplace(
								InternalStorage.getConnection(),
								corrections[0], outlet.GoalMatrix, kpi,
								Calendar.MONTH, outlet, null, corrections[2])
								.execute();
			} else if (kpi.KpiKind.equalsIgnoreCase("ТоварооборотИнициативы")) {
				RefInitiative ref = new RefInitiative(CONTEXT);
				RefInitiativeEntity initiative;
				ref.Select(false, false, HierarchyMode.OnlyEntity);
				while ((initiative = ref.next()) != null) {
					correction = new QueryGetOutletInitiativeSalesOfDay(
							CONTEXT, today, outlet, initiative).execute();
					Log.v(MainActivity.LOG_TAG, kpi.Descr + " "
							+ initiative.Descr + " " + correction);
					corrections = new QueryGetCumulativeOutletTarget(CONTEXT,
							today, outlet, Calendar.DAY_OF_MONTH, kpi)
							.execute();
					result = result
							&& new QueryUpdateKpiCombine(
									InternalStorage.getConnection(),
									correction, outlet.GoalMatrix, kpi,
									Calendar.MONTH, outlet, initiative)
									.execute();
					result = result
							&& new QueryUpdateKpiCombine(
									InternalStorage.getConnection(),
									correction, outlet.GoalMatrix, kpi,
									Calendar.DAY_OF_MONTH, outlet, initiative)
									.execute();

				}
				ref.close();
				corrections = new QueryGetCumulativeOutletTarget(CONTEXT,
						today, outlet, Calendar.DAY_OF_MONTH, kpi).execute();
				Log.v(MainActivity.LOG_TAG, kpi.Descr + " (кумулятивная день) "
						+ " " + corrections[0] + " " + corrections[2]);
				result = result
						&& new QueryUpdateKpiReplace(
								InternalStorage.getConnection(),
								corrections[0], outlet.GoalMatrix, kpi,
								Calendar.DAY_OF_MONTH, outlet, null,
								corrections[2]).execute();

				corrections = new QueryGetCumulativeOutletTarget(CONTEXT,
						period, outlet, Calendar.MONTH, kpi).execute();
				Log.v(MainActivity.LOG_TAG, kpi.Descr + " (кумулятивная месяц) "
						+ " " + corrections[0] + " " + corrections[2]);
				result = result
						&& new QueryUpdateKpiReplace(
								InternalStorage.getConnection(),
								corrections[0], outlet.GoalMatrix, kpi,
								Calendar.MONTH, outlet, null, corrections[2])
								.execute();
			}

		}
		kpis.close();
		setOutletTargetUpdated(result);
		return result;
	}

	public static float recalcKpiValue(RefKpiMatrixEntity matrix,
			RefKpiEntity kpi, float sourceValue, float weight) {
		if (weight == 0)
			return 0;
		float val = 0;
		float a = 0, aa = 0; // a - верхняя граница, aa - знаечние для верхней
								// границы
		float b = 0, bb = 0; // b - нижняя граница, bb - значение для нижней
								// границы

		RegKpiInterpretation reg = new RegKpiInterpretation(CONTEXT);
		if (reg.Select(new Object[] { matrix, kpi, null })) {
			while (reg.Next()) {
				if (reg.Current().Border == sourceValue * 100) {
					val = reg.Current().Value;
					reg.close();
					return val;
				}
				if (reg.Current().Border > sourceValue * 100) {
					a = reg.Current().Border;
					aa = reg.Current().Value;
					continue;
				}
				b = reg.Current().Border;
				bb = reg.Current().Value;
				break;
			}
		}
		reg.close();
		if (a == b)
			return 0;

		float y = (aa - bb) * (sourceValue * 100 - b) / (a - b) + bb;
		return (float) (y * weight * 0.01);

	}

	public static float getIntegralIndex() {
		float iindex = 0;

		QueryGetEmployeeTarget query = new QueryGetEmployeeTarget(CONTEXT,
				new Date(), Calendar.MONTH/*
										 * , Globals.getWorkdaysProgress(
										 * "Товарооборот")
										 */);
		query.setMode(Mode.Salary);
		query.Select(new ArrayList<SqlCriteria>(), "");
		while (query.Next()) {
			float workdayProgress = 1;
			QueryGetTargetEntity entity = query.Current();
			float index = entity.Index;
			float forecast = entity.Fact;
			workdayProgress = Globals.getWorkdaysProgress(entity.KpiKind);
			if (workdayProgress != 0)
				forecast = forecast / workdayProgress;
			if (entity.Target != 0) {
				index = forecast / entity.Target;
			} else
				index = 0f;
			float kpiValue = Globals.recalcKpiValue(entity.KpiMatrix,
					entity.KPI, index, entity.Weight);
			Log.e(MainActivity.LOG_TAG, "" + entity.KPI.toString() + " = "
					+ Float.toString(kpiValue) + "(WD progress="
					+ workdayProgress + " Id=" + entity.KPI.Id + ", Weight="
					+ entity.Weight + ")");
			iindex += kpiValue;
		}
		query.close();
		return iindex;

	}

	public enum CCS {
		Ok, Overdue, LimitExceeded, NoCreditLine, Forbidden
	}

	static public void dropSummaryNotes() {
		try {
		File file = new File(getInboxPath(),SalesForceSolution.SUMMARY_FILE);
		if (file.exists()) file.delete();
		} catch (Exception e) {}
		
		
	}
	
	static public void showSummaryNotes(Context context) {
		try {
			File file = new File(getInboxPath() ,SalesForceSolution.SUMMARY_FILE);
			if (file.exists()) return;
			
			BufferedInputStream bis = new BufferedInputStream(context.getAssets().open("docs/"+SalesForceSolution.SUMMARY_FILE));
			FileOutputStream fos = new FileOutputStream(file);
			int byteCount = 0;
			byte[] buffer = new byte[1024 * 4];
			while ((byteCount = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, byteCount);
			}
			bis.close();
			fos.close();
			bis = null;
			fos = null;
			buffer = null;
		
			openExtrasFile(context,
					SalesForceSolution.SUMMARY_FILE,false);
		
		} catch (Exception e) {}
		
		
	}
	
	public static CCS creditControl(RefContractorEntity contractor,
			RefPaymentTypeEntity paymentType, Date shipDate, float sum,
			Dictionary<String, Float> details) {
		Date now = Utils.RoundDate(new Date());
		Date firstDate = null;
		// Ищем ПДЗ
		float pdz = 0;
		float dz = 0;
		{
			RegBalance reg = new RegBalance(CONTEXT);
			try {

				if (reg.Select(new Object[] { contractor, getEmployee(), null })) {
					while (reg.Next()) {
						RegBalanceEntity bal = reg.Current();
						if (bal.Debt < 0)
							continue;
						if (bal.PaymentDate == null)
							continue;
						if (bal.PaymentDate.compareTo(now) < 0) {
							pdz += bal.Debt;
							continue;
						}
						if (bal.PaymentType == null)
							continue;
						if (!bal.PaymentType.equals(paymentType))
							continue;
						if (firstDate == null)
							firstDate = bal.PaymentDate;
						dz += bal.Debt;
					}
				}

			} catch (Exception e) {
				Log.e(MainActivity.LOG_TAG,
						" Ошибка определения задолженности: " + e.getMessage());
			} finally {
				reg.close();
			}

		}
		if (details != null) {
			details.put("Overdue", pdz);
			details.put("Debt", dz);
		}

		if (pdz > 0)
			return CCS.Overdue;

		CCS res = CCS.Forbidden;
		{
			RegPaymentType reg = new RegPaymentType(CONTEXT);
			reg.Select(new Object[] { contractor, getEmployee(), paymentType });
			try {
				if (reg.Next()) {
					RegPaymentTypeEntity entity = reg.Current();
					if (entity.IsCommon) {
						res = ((dz > 1) ? CCS.NoCreditLine : CCS.Ok);
					} else {
						if (details != null)
							details.put("Limit", entity.Limit);
						if (entity.CreditLine) { // если кредитная линия то
													// проверяем только баланс
							dz += sum;
							res = (dz > entity.Limit) ? CCS.LimitExceeded
									: CCS.Ok;
						} else {
							if (firstDate == null) { // нет ДЗ
								res = (sum > entity.Limit) ? CCS.LimitExceeded
										: CCS.Ok;
							} else {
								if (firstDate.after(shipDate)) // если на момент
																// отгрузки он
																// что-то должен
									res = CCS.NoCreditLine;
								else
									res = (sum > entity.Limit) ? CCS.LimitExceeded
											: CCS.Ok;
							}
						}
					}
				}
			} catch (Exception e) {
				Log.e(MainActivity.LOG_TAG,
						" Ошибка кредитного контроля: " + e.getMessage());
			}
			reg.close();
		}
		return res;
	}

	private static HashMap<String, Float> _workdayProgressPart = new HashMap<String, Float>();

	public static float getWorkdaysProgress(String kpiKind) {
		float res = 0;

		if (kpiKind.equalsIgnoreCase("Товарооборот1"))
			res = getWorkdaysProgress("<", 16, "Первая половина", 0);
		else if (kpiKind.equalsIgnoreCase("Товарооборот2"))
			res = getWorkdaysProgress(">", 15, "Вторая половина", 0);
		else if (kpiKind.equalsIgnoreCase("ДистрибьюцияИнициатив"))
			res = 0;
		else if (kpiKind.equalsIgnoreCase("Покрытие"))
			res = 0;
		else if (kpiKind.equalsIgnoreCase("КоличествоЗМ"))
			res = 0;
		else if (kpiKind.equalsIgnoreCase("ДистрибьюцияPOWER"))
			res = getWorkdaysProgress(">", 0, "Квалификационный период", 2);
		else
			res = getWorkdaysProgress(">", 0, "Месяц", 0);

		return res;
	}

	private static float getWorkdaysProgress(String criteria, int i,
			String tag, int j) {
		if (_workdayProgressPart.containsKey(tag))
			return _workdayProgressPart.get(tag);

		Float progress = new QueryGetWorkdaysProgress(CONTEXT, j, i, criteria)
				.execute();
		_workdayProgressPart.put(tag, progress);

		return progress;
	}

	public static String getSfsString(int id) {
		return CONTEXT.getResources().getString(id);
	}

	public static void synchronizeDb(SyncMaster syncMaster) {
		if (!Network.isNetworkConnectionAvailable(MainActivity.getInstance())) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
			builder.setCancelable(false);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setMessage("Сеть Интернет недоступна! Обмен невозможен!");
			builder.setPositiveButton("Продолжить",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
				});
			builder.setNegativeButton("Выйти",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						MainActivity.getInstance().finish();
					}
				});
			builder.create().show();
			
			return;
		}

		RefUserEntity user = getUser();
		UlSync sync = null;
		if (user == null) {
			sync = new UlRegisterSync(MainActivity.getInstance());
		} else {
			sync = new UlRegularSync(MainActivity.getInstance());
		}
		sync.setSyncMaster(syncMaster);
		long userId = ((user == null) ? 0 : user.Id);
		String version = ((user == null) ? "SFSINI"
				: getSfsString(R.string.ml_version));
		String publication = ((user == null) ? "INITIAL" : null);

		String primaryServer = MainActivity.getInstance().getResources()
				.getString(R.string.ml_primary_host);
		int port = Integer.valueOf(MainActivity.getInstance().getResources()
				.getString(R.string.ml_port));

		sync.execute(new String[] {
				version,
				primaryServer,
				String.valueOf(port),
				String.valueOf(userId),
				MainActivity.getInstance().getResources()
						.getString(R.string.ml_pwd), publication,
				Globals.getDeviceId() });
	}

	public static final String PREFS_NAME = "SfsPreferences";
	public static final String PREF_EMPLOYEE_TARGET_UPDATED = "employeeTargetUpdated";
	public static final String PREF_OUTLET_TARGET_UPDATED = "outletTargetUpdated";

	public static Boolean getEmployeeTargetUpdated() {
		SharedPreferences settings = CONTEXT
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getBoolean(PREF_EMPLOYEE_TARGET_UPDATED, false);
	}

	public static void setEmployeeTargetUpdated(Boolean value) {
		SharedPreferences settings = CONTEXT
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PREF_EMPLOYEE_TARGET_UPDATED, value);
		editor.commit();

	}

	public static Boolean getOutletTargetUpdated() {
		SharedPreferences settings = CONTEXT
				.getSharedPreferences(PREFS_NAME, 0);
		return settings.getBoolean(PREF_OUTLET_TARGET_UPDATED, false);

	}

	public static void setOutletTargetUpdated(Boolean value) {
		SharedPreferences settings = CONTEXT
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PREF_OUTLET_TARGET_UPDATED, value);
		editor.commit();
	}

	public static String getSubmittedWelcomeOffer(RefOutletEntity outlet) {
		String submitted = null;
		RegWelcomeOfferSubmitted reg = new RegWelcomeOfferSubmitted(
				MainActivity.getInstance());
		try {
			if (reg.Select(outlet)) {
				if (reg.Next())
					submitted = reg.Current().Document;
			}
		} catch (Exception e) {
			Log.v(MainActivity.LOG_TAG,
					"Не удалось запросить список предоставленных Welcome скидок: "
							+ e.getMessage());
		} finally {
			reg.close();
		}
		return submitted;
	}

	static String _outbox = Environment.getExternalStorageDirectory()
			+ "/download/ru.magnat.sfs/outbox/";
	static String _inbox = Environment.getExternalStorageDirectory()
			+ "/download/ru.magnat.sfs/inbox/";

	static public long fileUploadWanted() {
		_totalSizeForUpload = 0;
		File catalog = new File(_outbox);
		if (catalog.exists()) {
			_filesForUpload = catalog.listFiles();
			for (File file : _filesForUpload) {
				if (file.isDirectory())
					continue;
				_totalSizeForUpload += file.length();
			}
		}
		_filesForUpload = null;

		return _totalSizeForUpload;
	}

	static File[] _filesForUpload = null;
	static RefMediaFiles _mediaFiles = null;
	static long _totalSizeForUpload = 0;
	static long _totalSizeForDownload = 0;
	static long _receivedSize = 0;
	static long _sendedSize = 0;
	static ProgressDialog _progressDialog = null;
	static int _currentUploadFile = 0;

	@SuppressWarnings("serial")
	static public long fileDownloadWanted() {
		if (_mediaFiles != null) {
			_mediaFiles.close();
		} else {
			_mediaFiles = new RefMediaFiles(MainActivity.getInstance());
		}
		_mediaFiles.Select(new ArrayList<SqlCriteria>() {
		});
		_totalSizeForDownload = 0;
		while (_mediaFiles.Next()) {
			if (!findDownloadedFile(_mediaFiles.Current().FileName)) {
				_totalSizeForDownload += _mediaFiles.Current().FileSize;
			}
		}
		_mediaFiles.close();
		_mediaFiles = null;
		return _totalSizeForDownload;
	}

	public static void uploadFiles(boolean escape) {
		if (_filesForUpload == null) {
			_sendedSize = 0;
			File catalog = new File(_outbox);
			if (catalog.exists()) {
				_filesForUpload = catalog.listFiles();
			}
			if (_filesForUpload == null)
				return;
			_currentUploadFile = 0;
			_progressDialog = new ProgressDialog(MainActivity.getInstance());
			_progressDialog.setMax((int) (_totalSizeForUpload / 1024));
			_progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}
		if (!escape) {
			while (true) {
				_currentUploadFile++;
				if (_filesForUpload.length < _currentUploadFile)
					break;
				File file = _filesForUpload[_currentUploadFile - 1];
				if (file.exists() && file.isFile()) {
					uploadFile(file, _totalSizeForUpload, _sendedSize,
							_progressDialog);
					_sendedSize += file.length();
				}

				return;
			}
		}

		String filename = Utils.getFirstFile(_outbox);
		if (filename.isEmpty()) {
			Dialogs.createDialog("SFS", "Все файлы успешно отправлены",
					Command.NO_OP);
			// return;
		} else {
			Dialogs.createDialog(
					"SFS",
					"Отправка завершена, но остались неотправленные файлы. Повторите попытку позже",
					Command.NO_OP);

		}

		_filesForUpload = null;
		if (_progressDialog.isShowing())
			_progressDialog.dismiss();
		_progressDialog = null;
		// uploadFile(filename, _outbox);
	}

	static void uploadFile(File file, long totalSize, long sendedSize,
			ProgressDialog progressDialog) {
		try {
			UlFileTransferUpload upload = new UlFileTransferUpload(
					MainActivity.getInstance(), file.getName(), file
							.getParentFile().getPath(), String.valueOf(Globals
							.getUser().Id), Globals.getDeviceId(), totalSize,
					sendedSize, progressDialog);
			upload.setOnJobFinished(new OnJobFinishedListener() {

				public void onJobFinished(Object sender) {
					if (sender instanceof UlFileTransferUpload) {
						UlFileTransferUpload upload = (UlFileTransferUpload) sender;
						if (upload.getResult() == "Ok") {
							if (Utils.deleteFile(upload.getFilePath()
									+ upload.getFileName()))
								uploadFiles(false);
						}

						else {
							Dialogs.createDialog(
									"SFS",
									"При отправке файла "
											+ upload.getFileName()
											+ " произошла ошибка "
											+ upload.getResult()
											+ " продолжить отправку?",
									new Command() {

										public void execute() {
											uploadFiles(false);
										}

									}, new Command() {

										public void execute() {
											uploadFiles(true);
										}

									}

							).show();
						}
					}

				}

			});
			upload.execute();

		} catch (ULjException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("serial")
	public static void downloadFiles(boolean escape) {
		if (_mediaFiles == null) {
			_mediaFiles = new RefMediaFiles(MainActivity.getInstance());
			_mediaFiles.Select(new ArrayList<SqlCriteria>() {
			});
			_receivedSize = 0;
			_progressDialog = new ProgressDialog(MainActivity.getInstance());
			_progressDialog.setMax((int) (_totalSizeForDownload / 1024));
			_progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}

		if (!escape) {
			while (_mediaFiles.Next()) {
				if (findDownloadedFile(_mediaFiles.Current().FileName))
					continue;
				downloadFile(_mediaFiles.Current().Descr,
						_mediaFiles.Current().FileName, _inbox,
						_totalSizeForDownload, _receivedSize, _progressDialog);
				_receivedSize += _mediaFiles.Current().FileSize;
				return;
			}
		}
		_mediaFiles.close();
		_mediaFiles = null;
		if (_progressDialog.isShowing())
			_progressDialog.dismiss();
		_progressDialog = null;

	}

	public static String definePictureName(RefProductItemEntity product) {
		String res = "";
		if (product == null)
			return "";
		if (product.ParentExt == null)
			return "";
		if (product.ParentExt.ExtCode == null)
			return "";
		res = product.ParentExt.ExtCode.trim();
		if (res.isEmpty())
			return "";
		res += ".jpg";
		if (!Globals.findDownloadedFile(res))
			return "";

		return res;
	}

	private static void downloadFile(String description, String fileName,
			String filePath, long totalSize, long totalReceived,
			ProgressDialog progressDialog) {
		try {
			UlFileTransferDownload download = new UlFileTransferDownload(
					MainActivity.getInstance(), fileName, filePath,
					String.valueOf(Globals.getUser().Id),
					Globals.getDeviceId(), description, totalSize,
					totalReceived, progressDialog);
			download.setOnJobFinished(new OnJobFinishedListener() {

				public void onJobFinished(Object sender) {
					if (sender instanceof UlFileTransferDownload) {
						UlFileTransferDownload download = (UlFileTransferDownload) sender;
						if (download.getResult() == "Ok") {
							downloadFiles(false);
						}

						else {
							Dialogs.createDialog(
									"SFS",
									"При загрузке файла "
											+ download.getFileName()
											+ " произошла ошибка "
											+ download.getResult()
											+ ". Продолжить загрузку других файлов?",
									new Command() {

										public void execute() {
											downloadFiles(false);
										}

									},

									new Command() {

										public void execute() {
											downloadFiles(true);
										}

									}

							).show();
						}
					}

				}

			});
			download.execute();

		} catch (ULjException e) {
			e.printStackTrace();
		}

	}

	public static boolean findDownloadedFile(String fileName) {
		if (!Utils.findOrCreateCatalog(_inbox))
			return false;
		String path = _inbox + fileName;
		File f = new File(path);
		return f.exists();
	}

	public static void openExtras() {
		Intent intent = new Intent(MainActivity.getInstance()
				.getApplicationContext(), SfsExtrasActivity.class);
		MainActivity.getInstance().startActivity(intent);

	}

	public static String getInboxPath() {
		return _inbox;
	}

	public static void openExtrasFile(Context context, String fileName, Boolean newtask) {
		if (!findDownloadedFile(fileName)) {
			Dialogs.createDialog("", "Извините, файл еще не загружен. Попробуйте провести обмен и загрузить файлы с сервера.", Command.NO_OP);
			return;
		}
		File file = new File(getInboxPath() + fileName);
		// Uri uri = Uri.fromFile(file);
		Intent intent = new Intent(Intent.ACTION_VIEW);// , uri);
		String ufn = fileName.toUpperCase();

		if (ufn.endsWith("PDF"))

			intent.setDataAndType(Uri.fromFile(file), "application/pdf");

		else if (ufn.endsWith("XLS") || ufn.endsWith("XLSX")
				|| ufn.endsWith("XLSB"))
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.ms-excel");
		else if (ufn.endsWith("TXT") || ufn.endsWith("LOG"))
			intent.setDataAndType(Uri.fromFile(file), "text/plain");

		else if (ufn.endsWith("PPT") || ufn.endsWith("PPTX"))
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.ms-powerpoint");
		else if (ufn.endsWith("DOC") || ufn.endsWith("DOCX"))
			intent.setDataAndType(Uri.fromFile(file), "application/msword");
		else if (ufn.endsWith("PNG") || ufn.endsWith("BMP")
				|| ufn.endsWith("JPG") || ufn.endsWith("JPEG")
				|| ufn.endsWith("GIF"))
			intent.setDataAndType(Uri.fromFile(file), "image/*");
		else if (ufn.endsWith("AVI") || ufn.endsWith("MPEG")
				|| ufn.endsWith("FLV") || ufn.endsWith("WMV")
				|| ufn.endsWith("MP4") || ufn.endsWith("MOV"))
			intent.setDataAndType(Uri.fromFile(file), "video/*");
		else if (ufn.endsWith("HTM") || ufn.endsWith("HTML"))
			intent.setDataAndType(Uri.fromFile(file), "text/html");
		if (newtask)
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

	}

	public static DocInvoiceEntity getDerivedInvoice(DocOrderEntity _entity) {
		if (_entity == null)
			return null;
		DocInvoiceEntity invoice = null;
		DocInvoiceJournal j = new DocInvoiceJournal(MainActivity.getInstance());
		j.SetMasterDoc(_entity);
		j.Select(null, null, true, false);
		if (j.Next()) {
			invoice = (DocInvoiceEntity) j.Current().clone();
		}
		j.close();
		return invoice;
	}

	public static void showTprNotifications(RefOutletEntity outlet) {
		if (outlet == null)
			return;
		{
			QueryGetTprDiscountsListView v = new QueryGetTprDiscountsListView(
					MainActivity.getInstance(), outlet, TprDiscountVariant.NEW);
			if (v.getCount() > 0) {
				AlertDialog dlg = Dialogs.createDialog("Новые промо акции", "",
						v.inflate(), null, Command.NO_OP, null);
				dlg.setOnDismissListener(new OnDismissCloseableListener(
						(Closeable) v, null));
				dlg.show();
			} else {
				v.close();
			}
		}
		{
			QueryGetTprDiscountsListView v = new QueryGetTprDiscountsListView(
					MainActivity.getInstance(), outlet,
					TprDiscountVariant.EXPIRING);
			if (v.getCount() > 0) {
				AlertDialog dlg = Dialogs.createDialog(
						"Заканчивающиеся промо акции", "", v.inflate(), null,
						Command.NO_OP, null);
				dlg.setOnDismissListener(new OnDismissCloseableListener(
						(Closeable) v, null));
				dlg.show();
			} else {
				v.close();
			}
		}

	}

	public static Location getCurrentLocation() {

		LocationManager locationManager = (LocationManager) MainActivity
				.getInstance().getSystemService(Context.LOCATION_SERVICE);
		if (locationManager == null)
			return null;
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_LOW);
		try {

			locationManager.requestSingleUpdate(criteria,
					new LocationListener() {

						@Override
						public void onLocationChanged(Location location) {
							Log.v(MainActivity.LOG_TAG, "Location changed: "
									+ location.toString());

						}

						@Override
						public void onProviderDisabled(String provider) {
							Log.v(MainActivity.LOG_TAG, provider + " disabled");

						}

						@Override
						public void onProviderEnabled(String provider) {
							Log.v(MainActivity.LOG_TAG, provider + " enabled");

						}

						@Override
						public void onStatusChanged(String provider,
								int status, Bundle extras) {
							Log.v(MainActivity.LOG_TAG,
									provider + " status changed: "
											+ Integer.toString(status));

						}

					}, null);

			return locationManager.getLastKnownLocation(locationManager
					.getBestProvider(criteria, true));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * NEED_A_COMMENT
	 * 
	 * 
	 * @author petr_bu
	 */
	public static void showTrack() {
		Intent intent = new Intent(MainActivity.getInstance(),
				SfsTrackActivity.class);
		MainActivity.getInstance().startActivity(intent);
	}

	/**
	 * Функция открывает активность с дополнительными материалами на закладке с
	 * руководством пользователя
	 * 
	 * @author petr_bu
	 */
	public static void showUserGuide() {
		Context context = MainActivity.getInstance();

		Intent intent = new Intent(context, SfsExtrasFilesActivity.class);
		intent.putExtra(SfsExtrasFilesActivity.CURR_TAB, "2");
		intent.putExtra(SfsExtrasFilesActivity.TYPE_ID, 8l);
		intent.putExtra(SfsExtrasFilesActivity.TYPE_NAME,
				"Инструкция к программе SFS");

		context.startActivity(intent);
	}

}
