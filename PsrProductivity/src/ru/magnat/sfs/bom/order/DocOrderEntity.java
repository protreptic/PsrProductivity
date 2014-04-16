package ru.magnat.sfs.bom.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.InternalStorage;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.OrmObject.OperationContext;
import ru.magnat.sfs.bom.SfsEnum;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.cache.promo.Promo;
import ru.magnat.sfs.bom.cache.promo.PromoBrand;
import ru.magnat.sfs.bom.cache.promo.PromoCsku;
import ru.magnat.sfs.bom.cache.promo.PromoItem;
import ru.magnat.sfs.bom.cache.promo.PromoKit;
import ru.magnat.sfs.bom.cache.promo.PromoTotalChangedListener;
import ru.magnat.sfs.bom.cache.promo.Promos;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.order.DocOrderLine.Units;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnAmountBaseChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnAmountChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnClosingListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnContractorChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnDelayChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnLineChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnOutletChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnPaymentTypeChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnShipmentDateChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnShipmentTimeChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnTradeRuleChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnWarehouseChangedListener;
import ru.magnat.sfs.bom.query.distribution.QueryGetDistributionListFromHistory;
import ru.magnat.sfs.bom.query.distribution.QueryGetDistributionListFromTodayOrder;
import ru.magnat.sfs.bom.query.distribution.QueryGetTurnoverFromTodayOrder;
import ru.magnat.sfs.bom.query.getItemByCsku.QueryGetItemByCsku;
import ru.magnat.sfs.bom.query.getItemByCsku.QueryGetItemByCskuEntity;
import ru.magnat.sfs.bom.query.getPrices.QueryGetPrices;
import ru.magnat.sfs.bom.query.getPrices.QueryGetPricesEntity;
import ru.magnat.sfs.bom.query.getPromoCsku.QueryGetPromoBrand;
import ru.magnat.sfs.bom.query.getPromoCsku.QueryGetPromoBrandEntity;
import ru.magnat.sfs.bom.query.getPromoCsku.QueryGetPromoCsku;
import ru.magnat.sfs.bom.query.getPromoCsku.QueryGetPromoCskuEntity;
import ru.magnat.sfs.bom.query.getPromoCsku.QueryGetPromoSales;
import ru.magnat.sfs.bom.query.getPromoCsku.QueryGetPromoSalesEntity;
import ru.magnat.sfs.bom.query.updatepromoavailability.QueryUpdatePromoAvailability;
import ru.magnat.sfs.bom.query.updatepromoavailability.QueryUpdatePromoCompensationType;
import ru.magnat.sfs.bom.ref.assortment.RefAssortmentEntity;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.bom.ref.discount.RefDiscountEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.paymenttype.RefPaymentTypeEntity;
import ru.magnat.sfs.bom.ref.pricetype.RefPriceTypeEntity;
import ru.magnat.sfs.bom.ref.productitem.RefProductItem;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.ref.promo.CompensationType;
import ru.magnat.sfs.bom.ref.promo.PromoType;
import ru.magnat.sfs.bom.ref.promo.RefPromoDetails;
import ru.magnat.sfs.bom.ref.promo.RefPromoDetailsEntity;
import ru.magnat.sfs.bom.ref.promo.RefPromoKit;
import ru.magnat.sfs.bom.ref.promo.RefPromoKitEntity;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRule;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRuleEntity;
import ru.magnat.sfs.bom.ref.warehouse.RefWarehouseEntity;
import ru.magnat.sfs.bom.reg.assortment.RegAssortment;
import ru.magnat.sfs.bom.reg.discount.RegDiscount;
import ru.magnat.sfs.bom.reg.discount.RegDiscountEntity;
import ru.magnat.sfs.bom.reg.goldendiscount.RegGoldenDiscount;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.promo.OnPromotionStateListener;
import ru.magnat.sfs.util.Apps;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.SparseArray;

@SuppressWarnings("rawtypes")
@OrmEntityOwner(owner = DocOrderJournal.class)
public final class DocOrderEntity extends DocGenericEntity<DocOrderJournal, DocOrderLineEntity> implements OnChangeQuantityListener, OnPromotionStateListener, PromoTotalChangedListener {

	@EntityCardField(DisplayName = "торговая точка", Sortkey = 3, SelectMethod = "changeOutlet")
	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "Outlet")
	public RefOutletEntity Outlet;
	@EntityCardField(DisplayName = "дата доставки", Sortkey = 1, SelectMethod = "changeShipmentDate", format = "dd MMMM yyyy")
	@OrmEntityField(DisplayName = "Дата поставки", isPrimary = 0, fields = "ShipmentDate")
	public Date ShipmentDate;
	@EntityCardField(DisplayName = "время доставки", Sortkey = 2, SelectMethod = "changeShipmentTime", format = "kk:mm")
	@OrmEntityField(DisplayName = "Время поставки", isPrimary = 0, fields = "ShipmentTime")
	public String ShipmentTime;
	@EntityCardField(DisplayName = "тип кредита", Sortkey = 6, SelectMethod = "changePaymentType")
	@OrmEntityField(DisplayName = "Тип кредита", isPrimary = 0, fields = "PaymentType")
	public RefPaymentTypeEntity PaymentType;
	@EntityCardField(DisplayName = "контрагент", Sortkey = 4, SelectMethod = "changeContactor")
	@OrmEntityField(DisplayName = "Контрагент", isPrimary = 0, fields = "Contractor")
	public RefContractorEntity Contractor;
	@EntityCardField(DisplayName = "сумма продажи", Sortkey = 8, SelectMethod = "refreshAmount", format = "money")
	@OrmEntityField(DisplayName = "Сумма со скидкой", isPrimary = 0, fields = "Amount")
	public float mAmount; //##,###,###.## рублей
	@EntityCardField(DisplayName = "cумма в базовых ценах", Sortkey = 9, SelectMethod = "refreshAmount", format = "money")
	@OrmEntityField(DisplayName = "Сумма по базе", isPrimary = 0, fields = "AmountBase")
	public float AmountBase;
	@EntityCardField(DisplayName = "объем в SU", Sortkey = 10, SelectMethod = "", format = "##,###,###.##")
	@OrmEntityField(DisplayName = "Объем в SU", isPrimary = 0, fields = "Su")
	public float Su;
	@EntityCardField(DisplayName = "торговые условия", Sortkey = 7, SelectMethod = "changeTradeRule")
	@OrmEntityField(DisplayName = "ТУ", isPrimary = 0, fields = "TradeRule")
	public RefTradeRuleEntity TradeRule;
	@EntityCardField(DisplayName = "склад", Sortkey = 5, SelectMethod = "changeWarehouse")
	@OrmEntityField(DisplayName = "Склад", isPrimary = 0, fields = "Warehouse")
	public RefWarehouseEntity Warehouse;
	@EntityCardField(DisplayName = "комментарий", Sortkey = 11, SelectMethod = "changeDocComment")
	@OrmEntityField(DisplayName = "Комментарий", isPrimary = 0, fields = "DocComment")
	public String DocComment;
	@OrmEntityField(DisplayName = "Отсрочка", isPrimary = 0, fields = "Delay")
	public int Delay = 0;
	@EntityCardField(DisplayName = "Предоставить скидку WelcomeOffer", Sortkey = 130, SelectMethod = "changeWelcomeOfferDiscount")
	@OrmEntityField(DisplayName = "Предоставить скидку WelcomeOffer", isPrimary = 0, fields = "WelcomeOfferDiscount")
	public Boolean WelcomeOfferDiscount;
	//@EntityCardField(DisplayName = "Не предоставлять TPR-скидку", Sortkey = 130, SelectMethod = "changeTprDiscountDisabled")
	@OrmEntityField(DisplayName = "Не предоставлять TPR-скидку", isPrimary = 0, fields = "TprDiscountDisabled")
	public Boolean TprDiscountDisabled;
	@EntityCardField(DisplayName = "время сохранения", Sortkey = 131, SelectMethod = "", format = "dd MMMM kk:mm")
	@OrmEntityField(DisplayName = "Время сохранения", isPrimary = 0, fields = "SaveDate")
	public Date SaveDate;
	@EntityCardField(DisplayName = "время отправки", Sortkey = 132, SelectMethod = "", format = "dd MMMM kk:mm")
	@OrmEntityField(DisplayName = "Время отправки", isPrimary = 0, fields = "ReceiveDate")
	public Date ReceiveDate;
	@EntityCardField(DisplayName = "время импорта в 1С", Sortkey = 133, SelectMethod = "", format = "dd MMMM kk:mm")
	@OrmEntityField(DisplayName = "Время импорта", isPrimary = 0, fields = "ImportDate")
	public Date ImportDate;
	@EntityCardField(DisplayName = "время проведения в 1С", Sortkey = 134, SelectMethod = "", format = "dd MMMM kk:mm")
	@OrmEntityField(DisplayName = "Время проведения", isPrimary = 0, fields = "AcceptDate")
	public Date AcceptDate;
	@EntityCardField(DisplayName = "время отмены в 1С", Sortkey = 135, SelectMethod = "", format = "dd MMMM kk:mm")
	@OrmEntityField(DisplayName = "Время отмены", isPrimary = 0, fields = "CancelDate")
	public Date CancelDate;
	@EntityCardField(DisplayName = "КУЗ", Sortkey = 136, SelectMethod = "", format="percent")
	@OrmEntityField(DisplayName = "КУЗ", isPrimary = 0, fields = "CFR")
	public float CFR;
	@OrmEntityField(DisplayName = "Количество", isPrimary = 0, fields = "Quantity")
	public int Quantity;
	@OrmEntityField(DisplayName = "Версия", isPrimary = 0, fields = "ClientVersion")
	public String ClientVersion;
	
	private Context mContext;
	private Float _goldenDiscount = null;
	private Float _zoneDiscount1 = null;
	private Float _zoneDiscount2 = null;
	final protected DistributionCalculator _powerDistribution;
	final protected DistributionCalculator _goldenDistribution;
	final protected DistributionCalculator _initiativesDistribution;
	final protected DistributionCalculator _totalDistribution;
	
	final TurnoverCalculator 	 _totalTurnover;
	public HashMap<Long, QueryGetPromoCskuEntity> _promoPricing = new HashMap<Long, QueryGetPromoCskuEntity>();
	public HashMap<Long, QueryGetPromoCskuEntity> _promoBuyAndGetOrder = new HashMap<Long, QueryGetPromoCskuEntity>();
	public HashMap<Long, QueryGetPromoCskuEntity> _promoBuyAndGetPeriod = new HashMap<Long, QueryGetPromoCskuEntity>();
	public HashMap<Long, QueryGetPromoCskuEntity> _promoGrowMustCsku = new HashMap<Long, QueryGetPromoCskuEntity>();
	public HashMap<Long, QueryGetPromoBrandEntity>_promoVolumeBrand = new HashMap<Long, QueryGetPromoBrandEntity>();
	public boolean _promoPrepared = false;
	final protected Promos mPromos;
	private boolean mPromoFilling = false;
	
	//final public SumCalculator _amountCalculator;
	public DocOrderEntity() {

		this.mContext = MainActivity.sInstance;
		_powerDistribution = new DistributionCalculator(0); 
		_goldenDistribution = new DistributionCalculator(1);
		_totalDistribution = new DistributionCalculator(2);
		_initiativesDistribution = new DistributionCalculator(3);
		_totalTurnover = new TurnoverCalculator();
		mPromos = new Promos();
		mPromos.setOnPromoTotalChangedListener(this);
		
		//_amountCalculator = new SumCalculator();
	}
	
	private PromoTotalChangedListener mPromoTotalChangedListener;
	public void setOnPromoTotalChangedListener(PromoTotalChangedListener listener){
		mPromoTotalChangedListener = listener;
	}
	
	@Override
	final protected Class<?> getLinesContainer() {
		return DocOrderLine.class;
	}

	public float getAmount() {
		return this.mAmount;
	}

	public void setAmountBase(float val) {
		if (AmountBase == val)
			return;
		float old = AmountBase;
		AmountBase = val;
		onAmountBaseChanged(old, AmountBase);
	}

	public int getPowerDistribution(){
		return (Integer) _powerDistribution.getValue();
	}
	
	public int getGoldenDistribution(){
		return (Integer) _goldenDistribution.getValue();
	}
	public int getTotalDistribution(){
		return (Integer) _totalDistribution.getValue();
	}
	public int getInitiativesDistribution(){
		return (Integer) _initiativesDistribution.getValue();
	}
	public float getTurnover() {
		
		return (Float) _totalTurnover.getValue();
	}
	public void setAmount(float val) {
		if (mAmount == val)
			return;
		float old = mAmount;
		mAmount = val;
		onAmountChanged(old, mAmount);

	}

	public float getSu() {
		return Su;
	}

	public void setSu(float val) {
		if (Su == val) {
			return;
		}
		Su = val;
	}

	public RefWarehouseEntity getWarehouse() {
		return Warehouse;
	}

	public void setWarehouse(RefWarehouseEntity val) {
		if (val.equals(Warehouse))
			return;
		RefWarehouseEntity old = Warehouse;
		Warehouse = val;
		onWarehouseChanged(old, Warehouse);

	}

	public RefOutletEntity getOutlet() {
		return Outlet;
	}

	public void setOutlet(RefOutletEntity val) {
		if (Outlet.equals(val))
			return;
		RefOutletEntity old = Outlet;
		Outlet = val;
		onOutletChanged(old, Outlet);

	}

	public RefContractorEntity getContractor() {
		return Contractor;
	}

	public void setContractor(RefContractorEntity val) {
		if (Contractor.equals(val))
			return;
		RefContractorEntity old = Contractor;
		Contractor = old;
		onContractorChanged(old, Contractor);
		invalidateSum();

	}

	public Date getShipmentDate() {
		return ShipmentDate;
	}

	public void setShipmentDate(Date val) {
		
		Date old = ShipmentDate;
		
		if ((old!=null && !old.equals(val)) || (val!=null && !val.equals(old))) {
			ShipmentDate = val;
			onShipmentDateChanged(old, val);
		}

	}

	public String getShipmentTime() {
		return ShipmentTime;
	}

	public void setShipmentTime(String val) {
		if ((ShipmentTime == null) && (val == null))
			return;
		if (ShipmentTime != null)
			if (ShipmentTime.equals(val))
				return;
			else if (val.equals(ShipmentTime))
				return;
		String old = ShipmentTime;
		ShipmentTime = val;
		onShipmentTimeChanged(old, ShipmentTime);

	}
	
	public RefPaymentTypeEntity getPaymentType() {
		return PaymentType;
	}

	public void setPaymentType(RefPaymentTypeEntity val) {
		if ((PaymentType == null) && (val == null))
			return;
		if (PaymentType != null)
			if (PaymentType.equals(val))
				return;
			else if (val.equals(PaymentType))
				return;
		RefPaymentTypeEntity old = PaymentType;
		PaymentType = val;
		onPaymentTypeChanged(old, PaymentType);
		invalidateSum();

	}

	public void setPayDelay(Integer val) {
		Integer old = Delay;
		Delay = val;
		if (old != Delay) {
			applyTradeRules("Изменена глубина кредита");
		}
		onDelayChanged(old, Delay);
	}

	public void setTradeRule(RefTradeRuleEntity val) {
		if ((TradeRule == null) && (val == null))
			return;
		if (TradeRule != null)
			if (TradeRule.equals(val))
				return;
			else if (val.equals(TradeRule))
				return;
		RefTradeRuleEntity old = TradeRule;
		TradeRule = val;
	
		onTradeRuleChanged(old, TradeRule);

		if (TradeRule != null) {

			loadDiscounts();
			loadPrices(TradeRule.PriceType);
		}
		invalidateSum();
		applyTradeRules("Изменена Welcome скидка");
	}

	public void setWelcomeOfferDiscount(boolean val) {
		this.WelcomeOfferDiscount = val;
		invalidateSum();
		applyTradeRules("Изменена Welcome скидка");
	}
	
	public void setTprDiscountDisabled(boolean val) {
		this.TprDiscountDisabled = val;
		invalidateSum();
		applyTradeRules("Изменена TPR скидка");
	}

	@Override
	public String toString() {
		if (Author == null)
			return "Некорректный заказ";
		return ((IsMark) ? "Черновик" : "Заказ") + " №"
				+ String.format("%03d/%09d", Author.Id, Id);
	}
	
	private RefPaymentTypeEntity getPaymentTypeFromPreviousOrder(RefOutletEntity store) {
		RefPaymentTypeEntity result = null;
		DocOrderJournal journal = new DocOrderJournal(MainActivity.getInstance());
		journal.selectByOutlet(store);
		DocOrderEntity current  = journal.next();
		if (current!=null && current.PaymentType!=null)
			result = (RefPaymentTypeEntity) current.PaymentType.clone(); //BUGFIX #39 USMANOV 18/09/2013
		journal.close();
		return result;
	}
	
	@Override
	public void setDefaults(Context context, GenericEntity owner) {
		CreateDate = new Date();
		ShipmentDate = Globals.getNextWorkingDate(new Date());
		ClientVersion = Apps.getVersionName(mContext);
		IsAccepted = false;
		IsMark = false;
		WelcomeOfferDiscount = false;
		TprDiscountDisabled = false;
		Employee = Globals.getEmployee();
		TaskVisitEntity task = (TaskVisitEntity) owner;
		
		if (task != null) {
			Author = task.Author;
			MasterTask = (TaskVisitEntity) task;
			Outlet = task.Outlet;
			
			Contractor = Globals.getDefaultContractor(task.Outlet);
		
			Log.v(MainActivity.LOG_TAG, "setDefaults Amount: " + mAmount + " AmountBase: " + AmountBase);

			Warehouse = Globals.getWarehouse();
			invalidateSum();

			// найдем что было в предыдущем заказе
			RefPaymentTypeEntity paymentType = getPaymentTypeFromPreviousOrder(task.Outlet);
			
			//проверим разрешен ли предущий тип и опредним какой присвоить
			Object[] payterms = Globals.getDefaultPaymentType(Contractor, paymentType);
			
			PaymentType = (RefPaymentTypeEntity) payterms[0];
			Delay = (Integer) payterms[1];
						
			// Получить ТУ из последнего заказа в эту точку
			RefTradeRuleEntity tradeRule;
			// Если такое условие есть, то проверить доступно ли это торговое условия для выбора
			if ((tradeRule = getLastTradeRuleFromOrder(Outlet)) != null && isTradeRuleAvailable(tradeRule)) {
				// Условие доступно, выбираем его
				TradeRule = tradeRule;
			
			// Получить последние выбранные агентом торговые условия
			} else if ((tradeRule = getLastUsedTradeRule()) != null && isTradeRuleAvailable(tradeRule)) {
				// Условие доступно, выбираем его
				TradeRule = tradeRule;
				
			// Если последнего выбора не было, то выбираем по умолчанию, 
			// первые торговые условия из всех доступных для точки 	
			} else {
				if (Contractor != null) { 
					TradeRule = Globals.getTradeRule((RefCustomerEntity) Contractor.ParentExt);
				}
			}
		}
		
		if (CreateDate == null) {
			
		}
		if (ShipmentDate == null) {
			
		}
		if (ClientVersion == null) {
			
		}
		if (IsAccepted == null) {
			
		}
		if (IsMark == null) {
			
		}
		if (WelcomeOfferDiscount == null) {
			
		}
		if (TprDiscountDisabled == null) {
			
		}
		if (Employee == null) {
			
		}
	}

	/**
	 * Возвращает ТУ из последнего сделанного заказа в эту точку
	 *
	 * @param Физ.ТТ
	 * @return ТУ из последнего заказа
	 * 
	 * @author petr_bu
	 */
	private RefTradeRuleEntity getLastTradeRuleFromOrder(RefOutletEntity store) {
		RefTradeRuleEntity result = null;
		
		DocOrderJournal journal = new DocOrderJournal(MainActivity.getInstance());
		journal.selectByOutlet(store);
		DocOrderEntity current  = journal.next();
		if (current != null && current.TradeRule != null) {
			result = (RefTradeRuleEntity) current.TradeRule.clone();
		}
		journal.close();
		journal = null;
		
		return result;
	}
	
	/**
	 * Функция проверяет доступность ТУ для выбора
	 *
	 * @param entity Торговые условия
	 * @return истина если ТУ представлены в списке ТУ агента или ФИЗ.ТТ
	 * 
	 * @author petr_bu
	 */
	private boolean isTradeRuleAvailable(RefTradeRuleEntity entity) {
		boolean result = false;
		
		if (entity != null) {
			List<RefTradeRuleEntity> tradeRules = Globals.getTradeRulesAsList(Outlet.ParentExt);
			for (Iterator iterator = tradeRules.iterator(); iterator.hasNext();) {
				RefTradeRuleEntity refTradeRuleEntity = (RefTradeRuleEntity) iterator.next();
				if (refTradeRuleEntity.Id == entity.Id) {
					result = true;
					break;
				}
			}
		} else {
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Функция возвращает последнее выбранное ТУ агентом, берется из SharedPreferences
	 *
	 * @return последнее выбранное ТУ
	 * 
	 * @author petr_bu
	 */
	@SuppressWarnings("unchecked")
	private RefTradeRuleEntity getLastUsedTradeRule() {
		RefTradeRuleEntity result = null;
		
		SharedPreferences settings = mContext.getSharedPreferences(MainActivity.PREFS_NAME, 0);
	    Long tradeRuleId = settings.getLong(mContext.getResources().getString(R.string.options_order_last_used_trade_rule), 0);
	    
	    // Если агент еще не выбирал торговые условия
	    if (tradeRuleId == 0) {
	    	return result;
	    }
	    
	    RefTradeRule refTradeRule = new RefTradeRule(mContext);
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Id", tradeRuleId));
		if (refTradeRule.Select(criteria)) {
			if (refTradeRule.Next()) {
				result = (RefTradeRuleEntity) refTradeRule.Current();
			}	
		}
		refTradeRule.close();
		refTradeRule = null;
	    
		return result;
	}
	
	public DocOrderLineEntity getOrder(Context context, RefProductItemEntity productItem) {
		DocOrderLine lines = (DocOrderLine) getLines(context);
		return (DocOrderLineEntity) lines.getLine(context, productItem);
	}
	
	public void initLineCache(){
		if (_linescache==null) _linescache = new DocOrderLineCache();
	}
	public DocOrderLineCache getLinesCache(){
		return _linescache;
	}
	public class AsyncLinesCalculator extends AsyncTask<String,String,Float>{
		ProgressDialog _progress = null;
		@Override
		protected void onPreExecute() {
			Log.v(MainActivity.LOG_TAG,"Начало асинхронного пересчета");
			_progress = new ProgressDialog(MainActivity.getInstance());;
			_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			_progress.setIndeterminate(true);
			_progress.setCancelable(false);
			_progress.setMessage("Перерасчет документа");
			_progress.show();
		}
		
		private boolean mUseFullRecalc;
		
		public void useFullRecalc(boolean mode) {
			mUseFullRecalc = mode;
		}
		
		@Override
		protected Float doInBackground(String... reason) {
			if (DocOrderEntity.this.isReadOnly()) {
				publishProgress("Исправлять заказ нельзя.");
				return 0f;
			}
			if (DocOrderEntity.this.TradeRule==null){
				publishProgress("Не выбраны торговые условия");
				return 0f;
			}
			publishProgress(reason[0]+". Идет перерасчет документа. Пожалуйста подождите...");
			initLineCache();
			
			RefPriceTypeEntity pricetype = getBasePrice();
			if (prices.size() == 0) 
				loadPrices(pricetype);
		
			if (discounts.size() == 0) {
				loadDiscounts();
			}
			
			float su = 0.0f;
			float amount = 0.0f;
			
			priceTypeIncludeVAT = false;
			if (DocOrderEntity.this.TradeRule.PriceType != null) {
				priceTypeIncludeVAT = DocOrderEntity.this.TradeRule.PriceType.IncludeVAT;
			}
			for (DocOrderLineEntity line: _linescache.values()) {
				if (mUseFullRecalc) { 
					recalcBaseOrderLine(line);
				}
				DocOrderLineEntity calculated = recalcLine(true, line);
				su += line.Su;
				amount += calculated.Amount;
			}

			setSu(su);
			_invalidSum = false;
			return amount;
		}
		@Override
		 protected void onProgressUpdate(String... progress) {
			_progress.setMessage(progress[0]);
		}
		protected void onPostExecute(Float amount) {
			if (!DocOrderEntity.this.isReadOnly()) {
				setAmount(amount);
				DocOrderEntity.this.save();
			}
			if (_progress.isShowing()) {
				_progress.dismiss();
			} 
			onAmountUpdated();
			Log.v(MainActivity.LOG_TAG,"Конец асинхронного пересчета");
		}
	}
	public class AsyncLineCalculator extends AsyncTask<DocOrderLineEntity,Void,DocOrderLineEntity> {
		
		@Override
		protected DocOrderLineEntity doInBackground(DocOrderLineEntity... lines) {
			mIsBonusChanged = false; // считаем что бонус не менялся
			DocOrderLineEntity line = lines[0];
			if (line==null) return line;
			if (DocOrderEntity.this.isReadOnly()) return line;
			 return recalcLine(!line.IsRemoved,line); // здесь бонус может измениться
		}

		@Override
		protected void onPostExecute(DocOrderLineEntity line) {
			if (line!=null && !line.IsRemoved) DocOrderEntity.this.setAmount(DocOrderEntity.this.mAmount + line.Amount);
			onAmountUpdated();
			if ((DocOrderEntity.this._invalidSum || mIsBonusChanged) && !DocOrderEntity.this.isReadOnly()){
				(new AsyncLinesCalculator()).execute(DocOrderEntity.this._invalidSumReason);
			}
			
		}
	}

public class ChangeQuantityTask extends AsyncTask<Void,Void,Void> {
	
	final Long _productItemId;
	final int _value;
	final int _unitLevel;
	final int _unitFactor;
	final int _abc;
	final Long _cskuId;
	final Long _brandId;
	final OnChangeQuantityListener _callback;
	DocOrderLineEntity _oldline = null;
	DocOrderLineEntity _newline = null;
	final QueryGetPromoCskuEntity _promo1;
	final Promo _promo2;
	final Promo _promo3;
	final Promo _promo4;
	boolean _invalidPromo = false;
	public ChangeQuantityTask(OnChangeQuantityListener callback, Long productItemId, Integer value, int unitLevel, int abc, Long cskuId, int unitFactor, long brand) {
		_callback = callback;
		_productItemId = productItemId;
		_value = value;
		_unitLevel = unitLevel;
		_unitFactor = unitFactor;
		_abc = abc;
		_cskuId = cskuId; 
		_brandId = brand;
		_promo1 = _promoPricing.get(_cskuId);
		_promo2 = mPromos.getPromoByCsku(_cskuId,PromoType.BAG_ORDER);
		_promo3 = mPromos.getPromoByCsku(_cskuId,PromoType.BAG_PERIOD);
		_promo4 = mPromos.getPromoByBrand(brand);
		_invalidPromo = false;
	}
	
	@Override
	protected void onPostExecute(Void param) {
		if (_callback!=null) _callback.OnChangeQuantity(_oldline, _newline);
		if (_invalidPromo) applyTradeRules("Изменились промоданные");
	} 
	
	@Override
	protected Void doInBackground(Void... caches) {
		if (_linescache==null) _linescache = new DocOrderLineCache();
		DocOrderLineEntity newentity = null;
		DocOrderLineEntity oldentity = _linescache.get(_productItemId);
		if (oldentity!=null) oldentity = (DocOrderLineEntity) oldentity.clone();
		
		float amountbasecorrection = 0f;
		float amountcorrection = 0f;
		
		if (oldentity!=null){
			amountbasecorrection = - oldentity.AmountBase;
			amountcorrection = - oldentity.Amount;
		}
	
			
			if (oldentity == null) {
				newentity = new DocOrderLineEntity(DocOrderEntity.this.Id,DocOrderEntity.this.Author.Id,_productItemId,_abc);
				
				if (_cskuId!=null){
					if (_value>0) {
					switch (_abc){
					case 1:
						_goldenDistribution.addOrderProduct(_cskuId);
						_powerDistribution.addOrderProduct(_cskuId);
						break;
					case 3:
						_powerDistribution.addOrderProduct(_cskuId);
						break;
					default:
						break;
					}
					
					_totalDistribution.addOrderProduct(_cskuId);
					}
					if (!_promoPrepared  ) {
						loadPromo();
						while (mPromoFilling){
							try {
								Thread.sleep(100, 0);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
					}
					RefPromoDetails ref = new RefPromoDetails(mContext);
					if (_promo1!=null) newentity.Promo1 =  ref.FindById(_promo1.Promo);
					if (_promo2!=null) newentity.Promo2 =  ref.FindById(_promo2.getId());
					if (_promo3!=null)	newentity.Promo3 =  ref.FindById(_promo3.getId());
					if (_promo4!=null) newentity.Promo4 =  ref.FindById(_promo4.getId());
					ref.close();
				}
			} else {
				if (_value==0) {
					_goldenDistribution.removeOrderProduct(_cskuId);
					_powerDistribution.removeOrderProduct(_cskuId);
					_totalDistribution.removeOrderProduct(_cskuId);
				}
				newentity = oldentity;
			}
			newentity.setQuantity(_value, (_unitLevel == 1) ? Units.Case : Units.Piece);
			recalcBaseOrderLine(newentity);
			amountbasecorrection+=newentity.AmountBase;
			
			_linescache.put(_productItemId, newentity);
			if (_promo2!=null)
				if (addItemToPromo(_promo2.getId(),_cskuId,_productItemId,_value*_unitFactor,_brandId,newentity.AmountBase)) {
					_invalidSum = true;
					_invalidPromo = true;
					Log.d(MainActivity.LOG_TAG,"ИЗМЕНИЛАСЬ КРАТНОСТЬ ПРОМО 2!");
				}
			if (_promo3!=null)
				if (addItemToPromo(_promo3.getId(),_cskuId,_productItemId,_value*_unitFactor,_brandId,newentity.AmountBase)) {
					_invalidSum = true;
					_invalidPromo = true;
					Log.d(MainActivity.LOG_TAG,"ИЗМЕНИЛАСЬ КРАТНОСТЬ ПРОМО 3!");
				}
			if (_promo4!=null)
				if (addItemToPromo(_promo4.getId(),_cskuId,_productItemId,_value*_unitFactor,_brandId,newentity.AmountBase)) {
				//	_invalidSum = true;
				//	_invalidPromo = true;
					Log.d(MainActivity.LOG_TAG,"ИЗМЕНИЛОСЬ ПРОМО 4!");
				}
			_oldline = oldentity;
			_newline = newentity;
		
		//}
		DocOrderEntity.this.AmountBase += amountbasecorrection;
		DocOrderEntity.this.mAmount += amountcorrection;
		return null;
	}
	
}
public void changeQuantity(Long productItemId, int value, int unitLevel, int abc, Long cskuId, int unitFactor,long brand) {
	new ChangeQuantityTask(this, productItemId, value, unitLevel, abc, cskuId,unitFactor,brand).execute();
	
	}


	
long _currentPromoId = 0;
public void removeItemFromPromo(long promoId, long cskuId, long itemId, long brandId)
{
	addItemToPromo(promoId, cskuId, itemId, 0, brandId, 0f); 
}
public boolean addItemToPromo(long promoId, long cskuId, long itemId, int quantity, long brandId, float amount) {
	while (mPromoFilling ){
		
	}
	boolean changed = mPromos.changePromo(promoId, cskuId, itemId, quantity, brandId, amount);
	_currentPromoId = promoId;
	MainActivity.sInstance.runOnUiThread(new Runnable() {
		@Override
		public void run() {
			
			onPromotionCounterChanged(mPromos.getCounter(_currentPromoId), _currentPromoId);
		}
	});
	return changed;
}

/*
	private void loadLines() {
		DocOrderLine lines = (DocOrderLine) getLines(this.mContext);
		lines.Select();
		int count = (int) lines.Count();
		for (long i = 0; i < count; i++) {
			if (lines.To(i)) {
				this._lines.add(lines.Current());
			}
		}
		lines.close();
	}
	*/
	/*
	static protected Boolean invalideSum = false;
	static protected Boolean updateProgress = false;
	static protected AsyncTask<String,String,String> calculator = null;
	public void removeTask(){
		 calculator = null;
	}
	*/
	public void applyTradeRules(String reason){
		if (this._invalidSum)
			(new AsyncLinesCalculator()).execute(reason);
	}

	public float priceTypeMargin = 0f;
	public boolean priceTypeIncludeVAT = false;
	boolean _invalidSum = false;
	
	public void invalidateSum(){
		_invalidSum = true;
	}
	
	public RefPriceTypeEntity getBasePrice()
	{
		priceTypeMargin = 0f;
		
		if (TradeRule==null){
			Log.v(MainActivity.LOG_TAG,"ТУ не заданы");
			return null;
		}
		RefPriceTypeEntity pricetype = TradeRule.PriceType;
		
		if (pricetype == null) {
			Log.v(MainActivity.LOG_TAG,"В ТУ "+TradeRule.Descr + " не задан тип цен");
			return null;
		}
		if (pricetype.BaseType!=null){
			priceTypeMargin = - pricetype.Margin;
			return pricetype.BaseType;
			
		}
		return pricetype;
	}
	public static float getAmountLine(DocOrderEntity doc, float orderAmountBase, int delay, RefProductItemEntity product, float amountbaseline) {
		if (product==null) return 0f; // BUGFIX #41 Усманов
		if (product.ParentExt==null) return 0f; // BUGFIX #41 Усманов
		if (product.GcasState==null) return 0f; // BUGFIX #41 Усманов
		return getAmountLine(doc, orderAmountBase, delay, product.Id,product.ParentExt.Id,product.GcasState.Descr,amountbaseline,product.VAT);
	}
	public static int BONUS_AMOUNT = 1;
	public static int BONUS_DISCOUNT = 2;
	public static int BONUS_QUANTITY = 3;
	public static SparseArray<Float> getPricingOfLine(DocOrderEntity doc,
			float amountline, long productItemId, long cskuId) {
		SparseArray<Float> result = new SparseArray<Float>();
		Float pricingDiscount=doc.getPricingDiscount(cskuId);
		result.put(BONUS_DISCOUNT, pricingDiscount);
		result.put(BONUS_AMOUNT, Math.round(amountline*pricingDiscount)*0.01f);
		return result;
	}
	public static SparseArray<Float> getBuyAndGetOfLine(DocOrderEntity doc,
			float amountline, float priceofline, long productItemId,long cskuId) {
		SparseArray<Float> result = doc.getBuyAndGetBonus(cskuId, productItemId);
		Float discount = result.get(BONUS_DISCOUNT);
		Float quantity = result.get(BONUS_QUANTITY);
		if (priceofline == 0)
			priceofline = getPrice(doc, productItemId);
		float amount = 0f;
		if (discount>0) 
			amount = Math.round(priceofline*quantity*discount)*0.01f; //монетартная коменсация
		else
			amount = priceofline*quantity; //товарная компенсация
		result.put(BONUS_AMOUNT, amount); //монетартная коменсация
		Log.d(MainActivity.LOG_TAG, "Определение бонуса для товара "+productItemId+": Скидка="+discount+",Количество="+quantity+",Сумма="+amount);
		return result;
	}
	public SparseArray<Float> getBuyAndGetBonus(long cskuId,long itemId) {
		SparseArray<Float> result = new SparseArray<Float>();
		//QueryGetPromoCskuEntity e = _promoBAG.get(cskuId);
		Promo promo = mPromos.getPromoByCsku(cskuId, PromoType.BAG_ORDER);
		
		Float discount = 0f;
		Float quantity = 0f;
		
		if (promo!=null){
			//int compensationType = e.PrefferedCompensationType;
			int compensationType = getPromoPrefferedCompensationType(promo.getId());
			if (compensationType == 0)
				compensationType = this.getPromoCompensationType(promo.getId());
				//compensationType = e.CompensationType;
			boolean isAvailable = this.getPromoAvailabilty(promo.getId());
			if (isAvailable){
				switch (compensationType){
				case 2:
					//discount = e.Discount;
					discount = promo.getDiscount();
					quantity = (float) this.mPromos.getBonusProductQuantity(promo.getId(),cskuId,itemId);
					//quantity = (float) this.mPromos.getBonusProductQuantity(e.Promo,cskuId,itemId);
					break;
				case 1:
					quantity = (float) this.mPromos.getFreeProductQuantity(promo.getId(), cskuId, itemId);
					
					Log.d(MainActivity.LOG_TAG, "Free product for "+itemId+" = "+quantity);
					if (quantity>0)
						discount  = 100f;
					break;
				}
			}
		}
		result.put(BONUS_DISCOUNT, discount);
		result.put(BONUS_QUANTITY, quantity);
		return result;
	}
	
	public Promos getPromos() {
		return mPromos;
	}
	
	public boolean IsBonusCsku(long promoId, long kitId, long cskuId) {
		Promo promo = mPromos.getPromo(promoId);
		if (promo==null) return false;
		PromoKit kit = promo.getPromoKit(kitId);
		if (kit==null) return false;
		PromoCsku csku = kit.getPromoCsku(cskuId);
		if (csku==null) return false;
		return csku.getFreeProductSize()>0;
	}

	public Float _currentBaseAmountBorder = null;
	public static float getAmountLine(DocOrderEntity doc, float orderAmountBase, int delay, Long product,Long csku,String gcasstate, float amountbaseline,float vat) {
		float discount = doc.getDiscount(doc.TradeRule, product,csku,gcasstate, orderAmountBase, delay,doc.TprDiscountDisabled,true);
		float margin =  (1.0f - 0.01f * doc.priceTypeMargin);
		float lineSumBeforeTax = amountbaseline; //она по любому с НДС
		if (!doc.priceTypeIncludeVAT) //если тип цен без НДС, то скидки будем считать от сумм без НДС, а в конце его опять прикрутим 
			lineSumBeforeTax=amountbaseline/(1+vat);
		//float amountline = lineSumBeforeTax * (1.0f - 0.01f * discount);
		float amountline = lineSumBeforeTax * margin * discount;
		if (!doc.priceTypeIncludeVAT){
			amountline += amountline * vat;
		}
		return amountline;
	}
/*
	public void applyTradeRules() {
		applyTradeRules(true);
	}
	*/
	DocOrderLineCache _linescache = null;
	class DocOrderLineCache extends HashMap<Long,DocOrderLineEntity>  {
		public DocOrderLineCache(){
			DocOrderLine lines = (DocOrderLine) DocOrderEntity.this.getLines(MainActivity.getInstance());
			if (lines.Select()){
				while (lines.Next()){
					DocOrderLineEntity line = lines.Current(); 
					if (line==null)
						break;
					if (line.ProductItem==null)
						continue;
					super.put(line.ProductItem.Id, line);
				}
			}
			lines.close();
		}
		@Override
		public DocOrderLineEntity put(Long uid,DocOrderLineEntity entity){
			if (DocOrderEntity.this.IsAccepted &&  !DocOrderEntity.this.IsMark)
				return null;
			DocOrderLineEntity old = super.get(uid);
			if (old==null || !entity.equals(old)){
				DocOrderLine lines = (DocOrderLine) DocOrderEntity.this.getLines(MainActivity.getInstance());
				lines.setCurrent(entity);
				if (old==null){
					lines.ChangeContext(OperationContext.INSERTING);
				}
				else {
					lines.ChangeContext(OperationContext.EDITING);
				}
				if (lines.save())
					super.put(uid, entity);
				lines.close();
			}
			return old;
		}
	
		@Override
		public DocOrderLineEntity remove(Object uid){
			DocOrderLineEntity old = super.get(uid);
			if (old!=null){
				DocOrderLine lines = (DocOrderLine) DocOrderEntity.this.getLines(MainActivity.getInstance());
				if (lines.delete(old)) 
						super.remove(uid);
				old.IsRemoved = true;
			}
			return old;
			
		}
		private static final long serialVersionUID = 1L;
	
		
		
	}

	public DocOrderLineEntity getOrderLine(long productItemId) {
		if (_linescache==null) _linescache = new DocOrderLineCache();
		return _linescache.get(productItemId);
	}
	/*
	public void applyTradeRules(Boolean changeLines) {
		if (!_invalidSum) return;
		if (_linescache==null) _linescache = new DocOrderLineCache();
		Log.v(SFSActivity.LOG_TAG,"Начало пересчета");
		RefPriceTypeEntity pricetype = getBasePrice();
		if (prices.size() == 0) 
			loadPrices(pricetype);
	
		if (discounts.size() == 0) {
			loadDiscounts();
		}

		float amountbase = 0.0f;
		float su = 0.0f;

		float amount = 0.0f;
		priceTypeIncludeVAT = false;
		if (this.TradeRule.PriceType!=null)
			priceTypeIncludeVAT = this.TradeRule.PriceType.IncludeVAT;
		for (DocOrderLineEntity line: _linescache.values()){
			DocOrderLineEntity calculated = recalcLine(changeLines, line);
			su += line.Su;
			amount += calculated.Amount;
			amountbase += line.AmountBase;
		}
	
		setAmount(amount);
		setAmountBase(amountbase);
		setSu(su);
		_invalidSum = false;
		Log.v(SFSActivity.LOG_TAG,"Конец пересчета");
		
	}
*/
	public DocOrderLineEntity recalcLine(Boolean changeLines, DocOrderLineEntity line) {
		float amountbaseline;
		float amountline;
		amountbaseline = line.getAmountBase();
		RefProductItemEntity item = line.ProductItem;
		long itemId = 0;
		long cskuId = 0;
		long brandId = 0;
		if (item == null){
			return line;
		}
		itemId = item.Id;
		if (item.ParentExt!=null){
			cskuId = item.ParentExt.Id;
			if (item.ParentExt.Brand!=null)
				brandId = item.ParentExt.Brand.Id;
		}
		amountline = getAmountLine(this,this.AmountBase,this.Delay,item,amountbaseline);
		
		Float   pricingDiscount = 0f;
		Float   pricingAmount = 0f;
		
		Integer bagQuantity = 0;
		Float bagDiscount = 0f;
		Float bagAmount = 0f;
		int promo3comensationType = 0;
		int promo4comensationType = 0;
		Boolean bonus1changed = false;
		
		if (line.Promo1 == null){
			 QueryGetPromoCskuEntity p = _promoPricing.get(cskuId);
			 RefPromoDetails ref = new RefPromoDetails(mContext);
			 if (p!=null) {
				 line.Promo1 = ref.FindById(p.Promo); 
			 }
			 ref.close();
			 
		}
		if (line.Promo1!=null) {
			SparseArray<Float> result = getPricingOfLine(this,amountline,itemId,cskuId);
			pricingDiscount = result.get(BONUS_DISCOUNT);
			pricingAmount = result.get(BONUS_AMOUNT);
			if (pricingDiscount>0f){
				amountline-=pricingAmount;
			}
			bonus1changed = line.Bonus1Amount != pricingAmount
					|| line.Bonus1Discount !=pricingDiscount
					; 
		}
		Boolean bonus2changed = false;
		if (line.Promo2!=null) {
			
			float priceOfLine = 0f;
			if (line.Quantity > 0) priceOfLine = Math.round(amountline/(0.01f*line.Quantity))*0.01f;
			SparseArray<Float> result = getBuyAndGetOfLine(this,amountline,priceOfLine, itemId,cskuId);
			bagDiscount = result.get(BONUS_DISCOUNT);
			bagAmount = result.get(BONUS_AMOUNT);
			bagQuantity =  Math.round(result.get(BONUS_QUANTITY));
			Log.d(MainActivity.LOG_TAG, "Buy & Get bonus = "+bagAmount+"x"+bagQuantity+" ("+itemId+")");
			
			if (bagDiscount>0f && bagDiscount<99.99f){
				amountline-=bagAmount;
			}
		
			
			bonus2changed = line.Bonus2Amount != bagAmount
					|| line.Bonus2Discount !=bagDiscount
					|| line.Bonus2Count!=bagQuantity;
					 
		}
		boolean bonus3changed = false;
		if (line.Promo3!=null) {
			QueryGetPromoCskuEntity entity = this._promoBuyAndGetPeriod.get(cskuId);
			if (entity!=null) {
				Integer compensationType = entity.CompensationType; 
				if (compensationType==null || compensationType==0) compensationType = entity.PrefferedCompensationType;
				if (compensationType==null) compensationType = 0;
				promo3comensationType =compensationType;
				bonus3changed = line.Promo3CompensationType!=promo3comensationType;
			}
		}
		
		boolean bonus4changed = false;
		if (line.Promo4!=null) {
			QueryGetPromoBrandEntity entity = this._promoVolumeBrand.get(brandId);
			promo4comensationType = (entity.IsAvailable)?2:0;
			bonus4changed = line.Promo4CompensationType!=promo4comensationType;
		}
		if (bonus1changed || bonus2changed || bonus3changed|| bonus4changed) {
			mIsBonusChanged = true;
		}
		
		if (line.Amount != amountline || mIsBonusChanged ){
			if  (changeLines){
				if (_linescache==null) _linescache = new DocOrderLineCache();
				DocOrderLineEntity newline = (DocOrderLineEntity) line.clone();
				newline.setAmount(amountline);
				newline.Bonus1Amount = pricingAmount;
				newline.Bonus1Discount = pricingDiscount;
				newline.Bonus2Amount = bagAmount;
				newline.Bonus2Discount = bagDiscount;
				newline.Bonus2Count =bagQuantity;
				newline.Promo4CompensationType = promo4comensationType;
				newline.Promo3CompensationType = promo3comensationType;
				_linescache.put(item.Id, newline);
				if (newline.Promo4!=null) 
					adjustPromo4(newline);
				return newline;
			}
			else{
				line.setAmount(amountline);
				if (line.Promo4!=null) 
					adjustPromo4(line);
				return line;
			}
			
		}
		
		return line;
	}

	private void adjustPromo4(DocOrderLineEntity line) {
		Promo promo = mPromos.getPromo(line.Promo4.Id);
		long brand_id = 0;
		long csku_id = 0;
		long item_id = 0;
		if (line.ProductItem!=null){
			item_id = line.ProductItem.Id;
			if (line.ProductItem.ParentExt!=null){
				csku_id = line.ProductItem.ParentExt.Id;
				if (line.ProductItem.ParentExt.Brand!=null){
					brand_id = line.ProductItem.ParentExt.Brand.Id;
				}
			}
		}
			
		promo.changePromo(brand_id, csku_id, item_id, line.Quantity, line.Amount);
		Log.d(MainActivity.LOG_TAG, "Promo 4 adjusted for "+brand_id+" on "+ line.Amount);
	}

	
private boolean mIsBonusChanged;

	//private List<DocOrderLineEntity> _lines = new ArrayList<DocOrderLineEntity>();
	private List<RegDiscountEntity> discounts = new ArrayList<RegDiscountEntity>();
	protected String _invalidSumReason;
	private void loadPersonalDiscounts() {
		RegDiscount reg = new RegDiscount(this.mContext);
		RegDiscountEntity entity;
		if (reg.Select(this.TradeRule,this.Outlet.ParentExt)) {
			while ((entity = reg.next())!=null) {
				discounts.add(entity);
			}
		}
		reg.close();
	}
	public void loadPromo(){
			if (_promoPrepared) return;
			new AsyncPromoLoader().execute();
	}
	public class AsyncPromoLoader extends AsyncTask<Integer,String,String>{
		ProgressDialog _progress = null;
		Boolean mNeedRecalc = false;
		@Override
		protected void onPreExecute() {
			Log.d(MainActivity.LOG_TAG, "Начало обновления кэша промо");
			_promoPrepared = true;
			DocOrderEntity.this.mPromoFilling = true;
			Log.v(MainActivity.LOG_TAG,"Начало загрузки деталей промо");
			_progress= new ProgressDialog(MainActivity.getInstance());;
			_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			_progress.setIndeterminate(true);
			_progress.setCancelable(false);
			_progress.setMessage("Загрузка деталей промо...");
			_progress.show();
		}
		@Override
		protected String doInBackground(Integer... params) {
			if (params.length>0){
				mNeedRecalc = params[0]>0;
			}
			publishProgress("Идет загрузка деталей промо. Пожалуйста подождите...");
			clearPromo();
			
			fillPromoCskus();
			//Для третьей заполняем историю отгрузок
			fillPromoSales();
			//Для четвертой механики надо также заполнить массив акционных брендов
			fillPromoBrands();
			_promoPrepared = true;
			
			if (DocOrderEntity.this._linescache!=null){
				for (DocOrderLineEntity line:_linescache.values()){
					if (line.Promo2==null && line.Promo3==null && line.Promo4==null)
						continue;
				
					long itemId = 0;
					long cskuId = 0;
					long brandId = 0;
					
					float amount = line.Amount;
					int order = line.Quantity;
					switch (line.UnitLevel){
					case 1:
						order*=line.ProductItem.UnitFactor1;
						break;
					case 2:
						order*=line.ProductItem.UnitFactor2;
						break;
					default:
					}
					if (line.ProductItem!=null) {
						itemId = line.ProductItem.Id;
						if (line.ProductItem.ParentExt!=null){
							cskuId = line.ProductItem.ParentExt.Id;
							if (line.ProductItem.ParentExt.Brand!=null){
								brandId = line.ProductItem.ParentExt.Brand.Id;
							}
						}
					}
				
					if (line.Promo2!=null)
						mPromos.changePromo(line.Promo2.Id, cskuId, itemId, order, brandId, amount);
					if (line.Promo3!=null)
						mPromos.changePromo(line.Promo3.Id, cskuId, itemId, order, brandId, amount);
					if (line.Promo4!=null)
						mPromos.changePromo(line.Promo4.Id, cskuId, itemId, order, brandId, amount);
				}
			}
			return "OK";
		}
		private void fillPromoCskus() {
			QueryGetPromoCsku query = new QueryGetPromoCsku(mContext,Outlet,ShipmentDate);
			if (query.Select()) {
				QueryGetPromoCskuEntity entity;
				while ((entity = query.next())!=null) {
					switch (entity.PromoType){
					case 1:
					{
						_promoPricing.put(entity.CskuId, entity);
						Promo promo = mPromos.getPromo(entity.Promo);
						if (promo==null) promo = mPromos.addPromo(entity.Promo,entity.PromoType,entity.Discount);
					}
					break;
					case 2: 
					case 3: 
					{
						
						if (entity.PromoType==2) 
							_promoBuyAndGetOrder.put(entity.CskuId, entity);
						else 
							_promoBuyAndGetPeriod.put(entity.CskuId, entity);
						Promo promo = mPromos.getPromo(entity.Promo);
						if (promo==null) {
							promo = mPromos.addPromo(entity.Promo,entity.PromoType,entity.Discount);
							if (entity.FreeProductSize>0) {
								PromoKit bonusKit = promo.addPromoKit(0);
								bonusKit.addPromoCsku(entity.BonusCsku,false,entity.FreeProductSize,entity.Brand);
								addBonusCskuToOrder(entity.BonusCsku);
							}
						}
						PromoKit kit = promo.getPromoKit(entity.KitId);
						if (kit==null) kit = promo.addPromoKit(entity.KitId);
						kit.setMinCskuQuantity(entity.MinCsku);
						if (entity.MinCsku==1) {
							kit.setMinOrderQuantity(1);
							kit.setMinKitQuantity(entity.MinOrder);
						}
						else {
							kit.setMinKitQuantity(1);
							kit.setMinOrderQuantity(entity.MinOrder);
						}
						PromoCsku csku = kit.getPromoCsku(entity.CskuId);
						
						int freeProductSize = 0;
						if (entity.FreeProductSize>0) {
							if (entity.CskuId==entity.BonusCsku){
								//Если бонусная csku в наборе, то фейковую убираем
								freeProductSize = entity.FreeProductSize;
								promo.removePromoKit(0);
							}
						}
						if (csku==null) csku = kit.addPromoCsku(entity.CskuId,entity.MustHave,freeProductSize,entity.Brand);
						
						//if (entity.KitId==470)
						Log.d(MainActivity.LOG_TAG,"Промо: "+entity.Promo+" Пакет: "+entity.KitId+" CSKU:"+entity.CskuId+" FreeProductSize: "+entity.FreeProductSize);
					}
					break;
					case 4:
					{
						_promoGrowMustCsku.put(entity.CskuId, entity);
						Promo promo = mPromos.getPromo(entity.Promo);
						if (promo==null) promo = mPromos.addPromo(entity.Promo,entity.PromoType,entity.Discount); 
						PromoKit kit = promo.getPromoKit(entity.KitId);
						if (kit==null) kit = promo.addPromoKit(entity.KitId);
						kit.setMinCskuQuantity(1);
						kit.setMinKitQuantity(1);
						kit.setMinOrderQuantity(entity.MinOrder);
						
						PromoCsku csku = kit.getPromoCsku(entity.CskuId);
						if (csku==null) csku = kit.addPromoCsku(entity.CskuId,true,0,entity.Brand);
						
						Log.d(MainActivity.LOG_TAG,"Промо: "+entity.Promo+" Пакет: "+entity.KitId+" CSKU:"+entity.CskuId+" FreeProductSize: "+entity.FreeProductSize);
					}
					break;
				default:
					break;
					}
					
				}
			}
			query.close();
			
		}
		private void addBonusCskuToOrder(long cskuId) {
			if (DocOrderEntity.this._linescache!=null){
				boolean found = false;
				for (DocOrderLineEntity line:_linescache.values()){
					if (line.ProductItem.ParentExt.Id==cskuId) {
						found = true;
						break;
					}
				}
				if (!found){
					addEmptyLine(cskuId);
					Log.d(MainActivity.LOG_TAG, "Добавляем в заказ бонусную csku "+cskuId);
				}
			}
		}
		private void fillPromoBrands() {
			QueryGetPromoBrand query = new QueryGetPromoBrand(mContext,Outlet, ShipmentDate);
			if (query.Select()) {
				QueryGetPromoBrandEntity entity;
				while ((entity = query.next())!=null) {
					switch (entity.PromoType){
					case 1:
						break;
					case 2:
						break;
					case 4:
					{
						_promoVolumeBrand.put(entity.BrandId, entity);
						Promo promo = mPromos.getPromo(entity.Promo);
						if (promo==null) promo = mPromos.addPromo(entity.Promo,entity.PromoType,entity.Discount,entity.Target,entity.Fact); 
						PromoBrand brand = promo.getPromoBrand(entity.BrandId);
						if (brand==null) brand = promo.addPromoBrand(entity.BrandId);
						Log.d(MainActivity.LOG_TAG,"Промо: "+entity.Promo+" Бренд: "+entity.BrandId+" Цель: "+entity.Target+" Факт: "+entity.Fact);
						
					}
					break;
						default:
							break;
					}
					
				}
			}
			query.close();
		
		}
		
		private void fillPromoSales() {
			QueryGetPromoSales query = new QueryGetPromoSales(mContext,Outlet);
			if (query.Select()) {
				QueryGetPromoSalesEntity entity;
				while ((entity = query.next())!=null) {
					switch (entity.PromoType){
					case 1:
						break;
					case 2:
						break;
					case 3:{
						
						Promo promo = mPromos.getPromo(entity.Promo);
						if (promo==null) 
							continue;
						promo.addPromoSales(entity.Promo,entity.Csku,entity.Quantity); 
						Log.d(MainActivity.LOG_TAG,"Промо: "+entity.Promo+" Csku: "+entity.Csku+" Продано: "+entity.Quantity);		
						
					}
						break;
					case 4:
						break;
					default:
							break;
					}
					
				}
			}
			query.close();
		
		}
		
		@Override
		 protected void onProgressUpdate(String... progress) {
			_progress.setMessage(progress[0]);
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (_progress.isShowing()) {
				_progress.dismiss();
			} 
			DocOrderEntity.this.mPromoFilling = false;
			Log.v(MainActivity.LOG_TAG,"Конец загрузки деталей промо");
			Log.d(MainActivity.LOG_TAG, "Конец обновления кэша промо: прайсинг - "+_promoPricing.size()+", купи/получи - "+_promoBuyAndGetOrder.size()+", период - "+_promoBuyAndGetPeriod.size()+", объем - "+_promoVolumeBrand.size());
			if (mNeedRecalc) {
				invalidateSum();
				applyTradeRules("Обновлено промо");
			}
		}
	}
	
	public QueryGetPromoCskuEntity getPromoCskuEntity (long csku, int promoType){
		if (!this._promoPrepared) loadPromo();
		switch (promoType){
		case PromoType.PRICING:
			return _promoPricing.get(csku);
		case PromoType.BAG_ORDER:
			return _promoBuyAndGetOrder.get(csku);
		case PromoType.BAG_PERIOD:
			return _promoBuyAndGetPeriod.get(csku);	
		case PromoType.BAG_VOLUME:
			return _promoGrowMustCsku.get(csku);
		default:
				return null;
		}
		
	}

	public void clearPromo() {
		 mPromos.clear();
		_promoPricing.clear();
		_promoBuyAndGetOrder.clear();
		_promoBuyAndGetPeriod.clear();	
		_promoGrowMustCsku.clear();
		
	}


	/**
	 * NEED_A_COMMENT
	 *
	 * @param brand
	 * @param bagVolume
	 * @return
	 * 
	 * @author petr_bu
	 */
	public QueryGetPromoBrandEntity getPromoBrandEntity(long brand, int promoType) {
		if (!this._promoPrepared) loadPromo();
		switch (promoType) {
			case PromoType.PRICING:
				return null;
			case PromoType.BAG_ORDER:
				return null;
			case PromoType.BAG_PERIOD:
				return null;	
			case PromoType.BAG_VOLUME:
				return _promoVolumeBrand.get(brand);
			default:
					return null;
		}
	}
	

	public Promo getPromo(long csku, long brand, int promoType){
		if (!this._promoPrepared) loadPromo();
		switch (promoType){
		case PromoType.PRICING:
			return null;
		case PromoType.BAG_ORDER:
			return null;
		case PromoType.BAG_PERIOD:
			return null;	
		case PromoType.BAG_VOLUME:
			return mPromos.getPromoByBrand(brand);
		default:
				return null;
		}
		
	}
	public void addEmptyLine(long cskuId) {
		long channel = 0;
		if (this.Outlet!=null && this.Outlet.Channel!=null)
			channel = this.Outlet.Channel.Id;
		QueryGetItemByCsku getItemByCsku = new QueryGetItemByCsku(MainActivity.getInstance(),cskuId, channel);
		QueryGetItemByCskuEntity entity = getItemByCsku.execute();
		if (entity!=null) {
			changeQuantity(entity.Id, 0, 0, entity.ABC, cskuId, 0,entity.Brand);
			Log.v(MainActivity.LOG_TAG,"добавлена пустая строка для бонуса itemId="+entity.Id);
		}
		
	}

	private void loadCommonDiscounts() {
		RegDiscount reg = new RegDiscount(this.mContext);
		RegDiscountEntity entity;
		if (reg.Select(this.TradeRule)) {
			while ((entity = reg.next())!=null) {
				discounts.add(entity);
			}
		}
		reg.close();
	}
	
	public int loadDiscounts() {
		discounts.clear();
		loadPersonalDiscounts();
		if (discounts.size()==0) loadCommonDiscounts();
	
		return discounts.size();
	}
	/*@SuppressWarnings("unused")
	private  float getDiscount(RefTradeRuleEntity tradeRule, RefProductItemEntity productItem, float amountbase, long delay, boolean tpr_disabled){
		return getDiscount(tradeRule, productItem, amountbase, delay, tpr_disabled, false);
	}*/
	public  float getDiscount(RefTradeRuleEntity tradeRule, Long productItem, Long csku, String gcasstate, float amountbase, long delay, boolean tpr_disabled, boolean multiple) {
		if (discounts.size()==0)
			loadDiscounts();
		// Размер скидки
		float discount = (!multiple)?0f:1f;

		//
		int currentDiscount = -1;

		//
		RegDiscountEntity entity;
		
		for (Iterator<RegDiscountEntity> i = discounts.iterator(); i.hasNext();) {

			entity = i.next();
			if (entity==null) continue; //BUGFIX #112 USMANOV
			if (entity.Discount == null) {
				// Перемножать ли скидки
				return (!multiple) ? 0f : 1f;
			}
			if (entity.Discount.Id == currentDiscount)
				continue;

			if (entity.Assortment != null) {
				if (!productInAssortment(productItem, entity.Assortment.Id)) {
					continue;
				}
			}
			float discountValue = entity.DiscountValue;
			switch (entity.DiscountType) {
			case 1: // Транзакционная (за объем или единовременный заказ)
				if (amountbase < entity.Border) {
					continue;
				}
				if (_currentBaseAmountBorder==null){
					_currentBaseAmountBorder = entity.Border; 
					_invalidSum = true;
					_invalidSumReason = "Инициализация скидки за объем.";
					Log.v(MainActivity.LOG_TAG, "Граница непонятная. Требуется полный пересчет заказа");
				}
				else {
					if (_currentBaseAmountBorder != entity.Border){
						_currentBaseAmountBorder = entity.Border;
						_invalidSum = true;
						_invalidSumReason = "Изменилась скидка за объем";
						Log.v(MainActivity.LOG_TAG, "Изменилась граница. Требуется полный пересчет заказа");
					}
				}
				
				break;
			case 2: // За кредит
				if (delay < entity.Border) {
					continue;
				}
				break;
			case 3: //фиксированная скидка
				break;
			case 4: //Наценка на закупку
				break;	
			case 5:
				discountValue = getZoneDiscount1();
				if (discountValue==0) 
					continue;
				break;
			case 6:
				discountValue = getZoneDiscount2();
				if (discountValue==0) 
					continue;
				break;
			case 7: //TPR
			//	if (tpr_disabled)
			//		continue;
			//	discountValue = getTprDiscountValue(productItem,csku,gcasstate);
			//	if (discountValue==0) 
			//		continue;
				break;
			case 8:
				discountValue = getGoldenDiscount(entity.Discount);
				break;
			case 9:
				if (this.WelcomeOfferDiscount==null || !this.WelcomeOfferDiscount)
					continue;
				break;
			default:
				continue;
			}
			
			Log.v(MainActivity.LOG_TAG,"Найдена скидка для "+productItem+" "+entity.Discount.Descr+" "+discountValue);
			if (multiple)
				discount*=(1f-discountValue*0.01f);
			else
				discount += discountValue;
			currentDiscount = (int) entity.Discount.Id;
		}
		
		//discount*=(1f-getPricingDiscount(csku)*0.01f);
		
		return discount;
	}
	protected float getPricingDiscount(long cskuId){
			QueryGetPromoCskuEntity e = _promoPricing.get(cskuId);
			if (e!=null){
				if (e.IsAvailable){
					return e.Discount;
				}
			}
		
		return 0f;
	}
	/*
	Dictionary<Long,Float> _tprCacheProduct = null;
	Dictionary<Long,Float> _tprCacheCsku = null;
	
	public float getTprDiscountValue(RefProductItemEntity productItem) {
		Float v = null;
		if (!productItem.isRegular()) return 0;
		if (_tprCacheProduct==null || _tprCacheCsku==null)
			fillTprCache();
		if (_tprCacheProduct!=null && _tprCacheProduct.size()>0){
			v = _tprCacheProduct.get(productItem.Id);
		}
		if (v!=null)
			return v;
		if (_tprCacheCsku!=null  && _tprCacheCsku.size()>0){
			v = _tprCacheCsku.get(productItem.ParentExt.Id);
		}
		if (v!=null)
			return v;
		return 0;
	}
	*/
	/*
	public float getTprDiscountValue(Long productItem, Long csku, String gcasstate ) {
		Float v = null;
		if (!RefProductItemEntity.isRegular(gcasstate)) return 0;
		if (_tprCacheProduct==null || _tprCacheCsku==null)
			fillTprCache();
		if (_tprCacheProduct!=null && _tprCacheProduct.size()>0){
			v = _tprCacheProduct.get(productItem);
		}
		if (v!=null)
			return v;
		if (_tprCacheCsku!=null  && _tprCacheCsku.size()>0){
			v = _tprCacheCsku.get(csku);
		}
		if (v!=null)
			return v;
		return 0;
	}
	*/
	/*
	public RegTprDiscountEntity  getTprDiscount(RefProductItemEntity productItem) {
		RegTprDiscountEntity founded = null;
		RegTprDiscount reg = new RegTprDiscount(SFSActivity.sInstance);
		try{
			boolean isGolden = this.Outlet.isGolden();
			if (reg.Select(this.Outlet.ISISCode,productItem)){
				while (reg.Next()){
					RegTprDiscountEntity entity = reg.Current();
					if (!isGolden)
						if (entity.ForGoldenStoreOnly)
							continue;
					if (entity.ServiceType!=null)
						if (!entity.ServiceType.equals(this.Outlet.ServiceType))
							continue;
					founded = (RegTprDiscountEntity) entity.clone();
					reg.close();
					return founded;
					
				}
			}
			if (reg.Select(this.Outlet.ISISCode,productItem.ParentExt)){
				while (reg.Next()){
					RegTprDiscountEntity entity = reg.Current();
					if (!isGolden)
						if (entity.ForGoldenStoreOnly)
							continue;
					if (entity.ServiceType!=null)
						if (!entity.ServiceType.equals(this.Outlet.ServiceType))
							continue;
					founded = (RegTprDiscountEntity) entity.clone();
					reg.close();
					return founded;
					
				}
			}
		}
		catch (Exception e){
			Log.v(SFSActivity.LOG_TAG,"Ошибка выборки TPR скидок: "+e.getMessage());
		}
		finally{
			reg.close();
		}
		return null;
	}
	*/
	/*
	class OrderDataPreparing extends AsyncTask<Void,Void,Void>{
		ProgressDialog _progress= new ProgressDialog(SFSActivity.getInstance());;
		@Override
		protected void onPreExecute()
		{
		
			_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			_progress.setIndeterminate(true);
			_progress.setCancelable(false);
			_progress.setMessage("Подготовка данных...");
			_progress.show();
		}
		@Override
		protected Void doInBackground(Void... params) {
			fillTprCache();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			
			
			if (_progress.isShowing()) {
				_progress.dismiss();
			}
		}
		
	} 
	public void prepareData(){
		new OrderDataPreparing().execute(null,null,null);
	}
	*/
	/*
	protected void fillTprCache() {
		_tprCacheProduct = new Hashtable<Long,Float>();
		_tprCacheCsku = new Hashtable<Long,Float>();
		RegTprDiscount reg = new RegTprDiscount(SFSActivity.sInstance);
		try{
			boolean isGolden = this.Outlet.isGolden();
			if (reg.Select(this.Outlet.ISISCode)){
				while (reg.Next()){
					RegTprDiscountEntity entity = reg.Current();
					if (!isGolden)
						if (entity.ForGoldenStoreOnly)
							continue;
					if (entity.ServiceType!=null)
						if (!entity.ServiceType.equals(this.Outlet.ServiceType))
							continue;
					if (entity.Csku!=0)
						_tprCacheCsku.put(entity.Csku, entity.DiscountValue);
					else if (entity.ProductItem!=0)
						_tprCacheProduct.put(entity.ProductItem, entity.DiscountValue);
					
				}
			}
			
		}
		catch (Exception e){
			Log.v(SFSActivity.LOG_TAG,"Ошибка выборки TPR скидок: "+e.getMessage());
		}
		finally{
			reg.close();
		}
		
	}
	*/

	private float getZoneDiscount1() {
		if (this._zoneDiscount1==null){
			defineZoneDiscounts();
		}
		
		return this._zoneDiscount1;
	}
	private float getZoneDiscount2() {
		if (this._zoneDiscount2==null){
			defineZoneDiscounts();
		}
		
		return this._zoneDiscount2;
	}
	private void defineZoneDiscounts() {
		_zoneDiscount1 = 0f;
		_zoneDiscount2 = 0f;
		
		if (this.Contractor!=null){
			if (this.Contractor.DiscountZone!=null){
				_zoneDiscount1 = this.Contractor.DiscountZone.PrimaryDiscount;
				_zoneDiscount2 = this.Contractor.DiscountZone.SecondaryDiscount;
				
			}
		}
	
	}

	private float getGoldenDiscount(RefDiscountEntity discountEntity){
		if (_goldenDiscount == null) {
			float discount = 0;
			if (Outlet != null) {
				if (Outlet.Channel != null) {
					if (Outlet.Channel.GoldenProgramType != null) {
						if (!Outlet.Channel.GoldenProgramType.equals(SfsEnum.NOTHING)) { 
							if (Outlet.isGolden() && !Outlet.GoldenDiscountByPayment) {
								try {
									RegGoldenDiscount reg = new RegGoldenDiscount(mContext);
									if (reg.Select(discountEntity, Outlet.ServiceType, Outlet.Channel.GoldenProgramType)){
										if (reg.Next()) {
											discount = reg.Current().DiscountValue;
										}
									}
									reg.close();
									reg = null;
								} catch(Exception e){
									Log.v(MainActivity.LOG_TAG,"Ошибка запроса золотой скидки: "+e.getMessage());
								}
							}
						}
					}
				}
			}
			
			_goldenDiscount = discount;
		}
		
		return _goldenDiscount;
	}
	
	public Boolean productInAssortment(RefProductItemEntity item, RefAssortmentEntity assortment) {
		RegAssortment reg = new RegAssortment(mContext);
		Boolean inAssortment = reg.inAssortment(assortment, item);
		reg.close();

		return inAssortment;
	}
	
	public Boolean productInAssortment(Long item, Long assortment) {
		RegAssortment reg = new RegAssortment(mContext);
		Boolean inAssortment = reg.inAssortment(assortment, item);
		reg.close();

		return inAssortment;
	}
	
	protected boolean _calculator;

	private boolean recalcBaseOrderLine(DocOrderLineEntity line) {
		
		Log.v("", "recalcBaseOrderLine start");
		
		if (line == null) return false;
		
		try {
			float price = 0.0f;
			long pieces = line.Quantity;
			
			switch (line.getUnit()) {
				case Case: {
					pieces *= line.ProductItem.UnitFactor1;
				} break;
				case Block: {
					pieces *= line.ProductItem.UnitFactor2;
				}
				default: {} break;
			}

			if (TradeRule != null) {
				if (line.ProductItem != null) {
					price = getPrice(this,line.ProductItem.Id);
				}
				/*if (!TradeRule.PriceType.IncludeVAT) {
					price = this.getPrice(line.ProductItem);
				}*/
			}
			
			line.setSu(pieces * line.ProductItem.SuFactor);
			line.setAmountBase(pieces * price);

			return true;
		} catch (Exception e) {}

		Log.v("", "recalcBaseOrderLine stop");

		return false;
	}
	
	//@SuppressWarnings("unused")
	/*
	private boolean recalcOrderLine(DocOrderLineEntity line) {
		
		Log.v(SFSActivity.LOG_TAG, "recalcOrderLine start");
		
		if (line == null) return false;
		try {
			line.setAmount(getAmountLine(this, this.AmountBase,this.Delay,line.ProductItem, line.AmountBase));
		
			return true;
		} catch (Exception e) {
			Log.v(SFSActivity.LOG_TAG, "error during amount calculation: "+e.getMessage());
		}

		Log.v(SFSActivity.LOG_TAG, "recalcOrderLine stop");

		return false;
	}
	*/
	private HashMap<Long, Float> prices = new HashMap<Long, Float>();
	private long _currentPriceType = 0;

	public void loadPrices(RefPriceTypeEntity priceType) {

		if (priceType==null) return;
		if (_currentPriceType == priceType.Id) return;

		_currentPriceType = priceType.Id;
		

		QueryGetPrices query = new QueryGetPrices(mContext, _currentPriceType);

		query.Select();

		QueryGetPricesEntity entity;
		prices.clear();
		
		while ((entity = query.next())!=null) {

			

			prices.put(entity.ProductItem, entity.Price);
		}

		query.close();
	}

	//public static float getPrice(DocOrderEntity doc, RefProductItemEntity productItem) {
	public static float getPrice(DocOrderEntity doc, Long productItemId) {

		Log.v(MainActivity.LOG_TAG, "ProductItem: " + productItemId + " Prices size: " + doc.prices.size());
		if (doc.prices.size() == 0)
			doc.loadPrices(doc.getBasePrice());

		if (doc.prices.containsKey(productItemId)) {
			float price = doc.prices.get(productItemId).floatValue();
			Log.v(MainActivity.LOG_TAG, "Id: " + productItemId + " Price: " + price);
			return price;
		}

		return 0.0f;
	}

	

	// listeners

	private final Set<IEventListener> _eventAmountChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnAmountChangedListener(OnAmountChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventAmountChangedListeners,
				eventListener);
	}

	public void addOnAmountChangedListener(OnAmountChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventAmountChangedListeners,
				eventListener);
	}

	public void removeOnAmountChangedListener(
			OnAmountChangedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventAmountChangedListeners,
				eventListener);
	}
	

	public void onAmountChanged(Float old_value, Float new_value) {
		_catalog.setCurrent(this);
		if (_catalog.save())
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventAmountChangedListeners)
				((OnAmountChangedListener) eventListener).onAmountChanged(this,
						old_value, new_value);
	}

	//
	private final Set<IEventListener> _eventAmountBaseChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	private final Set<IEventListener> _eventAmountUpdatedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnAmountUpdatedListener(OnAmountUpdatedListener eventListener) {
		EventListenerSubscriber.setListener(_eventAmountUpdatedListeners,
				eventListener);
	}

	public void addOnAmountUpdatedListener(OnAmountUpdatedListener eventListener) {
		EventListenerSubscriber.addListener(_eventAmountUpdatedListeners,
				eventListener);
	}

	public void removeOnAmountUpdatedListener(
			OnAmountUpdatedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventAmountUpdatedListeners,
				eventListener);
	}
	
	private void onAmountUpdated(){
		for (IEventListener eventListener : _eventAmountUpdatedListeners)
			((OnAmountUpdatedListener) eventListener)
					.onAmountUpdated();
	}
	public void setOnAmountBaseChangedListener(
			OnAmountBaseChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventAmountBaseChangedListeners,
				eventListener);
	}

	public void addOnAmountBaseChangedListener(
			OnAmountBaseChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventAmountBaseChangedListeners,
				eventListener);
	}

	public void removeOnAmountBaseChangedListener(
			OnAmountBaseChangedListener eventListener) {
		EventListenerSubscriber.removeListener(
				_eventAmountBaseChangedListeners, eventListener);
	}

	public void onAmountBaseChanged(Float old_value, Float new_value) {
		_catalog.setCurrent(this);
		if (_catalog.save())
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventAmountBaseChangedListeners)
				((OnAmountBaseChangedListener) eventListener)
						.onAmountBaseChanged(this, old_value, new_value);
	}

	//

	private final Set<IEventListener> _eventPaymentTypeChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnPaymentTypeChangedListener(
			OnPaymentTypeChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventPaymentTypeChangedListeners,
				eventListener);
	}

	public void onPaymentTypeChanged(RefPaymentTypeEntity old_value,
			RefPaymentTypeEntity new_value) {
		_catalog.setCurrent(this);
		if (_catalog.save())
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventPaymentTypeChangedListeners)
				((OnPaymentTypeChangedListener) eventListener)
						.onPaymentTypeChanged(this, old_value, new_value);
	}

	private final Set<IEventListener> _eventDelayChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnDelayChangedListener(OnDelayChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventDelayChangedListeners,
				eventListener);
	}

	public void onDelayChanged(Integer old_value, Integer new_value) {
		_catalog.setCurrent(this);
		if (_catalog.save())
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventDelayChangedListeners)
				((OnDelayChangedListener) eventListener).onDelayChanged(this,
						old_value, new_value);
	}

	private final Set<IEventListener> _eventTradeRuleChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnTradeRuleChangedListener(
			OnTradeRuleChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventTradeRuleChangedListeners,
				eventListener);
	}
	
	public void addOnTradeRuleChangedListener(
			OnTradeRuleChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventTradeRuleChangedListeners,
				eventListener);
	}

	public void onTradeRuleChanged(RefTradeRuleEntity old_value,
			RefTradeRuleEntity new_value) {
		_catalog.setCurrent(this);
		if (_catalog.save())
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventTradeRuleChangedListeners)
				((OnTradeRuleChangedListener) eventListener)
						.onTradeRuleChanged(this, old_value, new_value);
	}

	private final Set<IEventListener> _eventShipmentTimeChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnShipmentTimeChangedListener(
			OnShipmentTimeChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventShipmentTimeChangedListeners,
				eventListener);
	}

	public void onShipmentTimeChanged(String old_value, String new_value) {
		_catalog.setCurrent(this);
		if (_catalog.save())
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventShipmentTimeChangedListeners)
				((OnShipmentTimeChangedListener) eventListener)
						.onShipmentTimeChanged(this, old_value, new_value);
	}

	private final Set<IEventListener> _eventShipmentDateChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnShipmentDateChangedListener(
			OnShipmentDateChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventShipmentDateChangedListeners,
				eventListener);
	}

	public void onShipmentDateChanged(Date old_value, Date new_value) {
		_catalog.setCurrent(this);
		
		if (_catalog.save()) {
			_promoPrepared = false;
			new AsyncPromoLoader().execute(1);
			
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventShipmentDateChangedListeners)
				((OnShipmentDateChangedListener) eventListener)
						.onShipmentDateChanged(this, old_value, new_value);
		}
	}

	private final Set<IEventListener> _eventContractorChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnContractorChangedListener(
			OnContractorChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventContractorChangedListeners,
				eventListener);
	}

	public void onContractorChanged(RefContractorEntity old_value,
			RefContractorEntity new_value) {
		_catalog.setCurrent(this);
		if (_catalog.save())
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventContractorChangedListeners)
				((OnContractorChangedListener) eventListener)
						.onContractorChanged(this, old_value, new_value);
	}

	private final Set<IEventListener> _eventOutletChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnOutletChangedListener(OnOutletChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventOutletChangedListeners,
				eventListener);
	}

	public void onOutletChanged(RefOutletEntity old_value,
			RefOutletEntity new_value) {
		_catalog.setCurrent(this);
		if (_catalog.save())
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventOutletChangedListeners)
				((OnOutletChangedListener) eventListener).onOutletChanged(this,
						old_value, new_value);
	}

	private final Set<IEventListener> _eventWarehouseChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnWarehouseChangedListener(
			OnWarehouseChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventWarehouseChangedListeners,
				eventListener);
	}

	public void addOnWarehouseChangedListener(
			OnWarehouseChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventWarehouseChangedListeners,
				eventListener);
	}
	public void removeOnWarehouseChangedListener(
			OnWarehouseChangedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventWarehouseChangedListeners,
				eventListener);
	}

	public void onWarehouseChanged(RefWarehouseEntity old_value,
			RefWarehouseEntity new_value) {
		_catalog.setCurrent(this);
		if (_catalog.save())
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventWarehouseChangedListeners)
				((OnWarehouseChangedListener) eventListener)
						.onWarehouseChanged(this, old_value, new_value);
	}

	private final Set<IEventListener> _eventQuantityChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnQuantityChangedListener(OnChangeOrderQuantityListener eventListener) {
		EventListenerSubscriber.setListener(_eventQuantityChangedListeners,
				eventListener);
	}

	public void addOnQuantityChangedListener(OnChangeOrderQuantityListener eventListener) {
		EventListenerSubscriber.addListener(_eventQuantityChangedListeners,
				eventListener);
	}
	public void removeOnQuantityChangedListener(OnChangeOrderQuantityListener eventListener) {
		EventListenerSubscriber.removeListener(_eventQuantityChangedListeners,
				eventListener);
	}
	public void onQuantityChanged() {
	
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventQuantityChangedListeners)
				((OnChangeOrderQuantityListener) eventListener).onChangeOrderQuantity();
	}
	
	private final Set<IEventListener> _eventLineChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnLineChangedListener(OnLineChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventLineChangedListeners,
				eventListener);
	}

	public void addOnLineChangedListener(OnLineChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventLineChangedListeners,
				eventListener);
	}
	public void removeOnLineChangedListener(OnLineChangedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventLineChangedListeners,
				eventListener);
	}

	public void onLineChanged(DocOrderLineEntity old_value,
			DocOrderLineEntity new_value) {
		this.openCatalog().setCurrent(this);
		
		if (_catalog.save())
			// if (_catalog.save(this))
			for (IEventListener eventListener : _eventLineChangedListeners)
				((OnLineChangedListener) eventListener).onLineChanged(this,
						old_value, new_value);
	}

	private final Set<IEventListener> _eventClosingListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnClosingListener(OnClosingListener eventListener) {
		EventListenerSubscriber.setListener(_eventClosingListeners, eventListener);
	}

	public void addOnClosingListener(OnClosingListener eventListener) {
		EventListenerSubscriber.addListener(_eventClosingListeners, eventListener);
	}
	
	public void removeOnClosingListener(OnClosingListener eventListener) {
		EventListenerSubscriber.removeListener(_eventClosingListeners,
				eventListener);
	}

	public void onClosing() {
		for (IEventListener eventListener : _eventClosingListeners)
			((OnClosingListener) eventListener).onClosing(this);
	}
	
	
	class TurnoverCalculator implements OnlineKpiCalculator {
		private Float _fixed_turnover = 0f;
		
		
		Boolean _dataReady = false;
		public TurnoverCalculator(){
			
		
		}
		public void initializeData(){
			if (_dataReady) return;
			if (DocOrderEntity.this.Outlet==null) return;
			
			{
				//прочие сегодняшние заказы
				QueryGetTurnoverFromTodayOrder query = new QueryGetTurnoverFromTodayOrder(DocOrderEntity.this.mContext,DocOrderEntity.this.Outlet.Id,DocOrderEntity.this.Id);
				if (query.Select()){
					if (query.Next()){
						_fixed_turnover = query.Current().Value;
					}
				}
				query.close();
			}
			
			
			_dataReady = true;
		}
		public Object getValue(){
			initializeData();
			return _fixed_turnover + DocOrderEntity.this.mAmount;
		}
		
	}
	
	class DistributionCalculator implements OnlineKpiCalculator {
		private final Set<Long> _fixed_distribution = new CopyOnWriteArraySet<Long>();
		private final Set<Long> _order_distribution = new CopyOnWriteArraySet<Long>();
		final int _distribution_type; //0 - power, //1 - golden //2- total
		Boolean _dataReady = false;
		public DistributionCalculator(int distribution_type){
			_distribution_type = distribution_type;
		
		}
		public void initializeData(){
			GenericEntity<?> entity;
			if (_dataReady) return;
			if (DocOrderEntity.this.Outlet==null) return;
			if (DocOrderEntity.this.Outlet.Channel==null) return;
			{
				QueryGetDistributionListFromHistory query = new QueryGetDistributionListFromHistory(DocOrderEntity.this.mContext,DocOrderEntity.this.Outlet.Id,_distribution_type);
				if (query.Select()){
					while ((entity = query.next())!=null){
						_fixed_distribution.add(entity.Id);
					}
				}
				query.close();
			}
			{
				//прочие сегодняшние заказы
				QueryGetDistributionListFromTodayOrder query = new QueryGetDistributionListFromTodayOrder(DocOrderEntity.this.mContext,DocOrderEntity.this.Outlet.Id,DocOrderEntity.this.Id,_distribution_type,false);
				if (query.Select()){
					
					while ((entity = query.next())!=null){
						_fixed_distribution.add(entity.Id);
					}
				}
				query.close();
			}
			{
				//текущий сегодняшний заказ
				QueryGetDistributionListFromTodayOrder query = new QueryGetDistributionListFromTodayOrder(DocOrderEntity.this.mContext,DocOrderEntity.this.Outlet.Id,DocOrderEntity.this.Id,_distribution_type,true);
				if (query.Select()){
					while ((entity = query.next())!=null){
						Long id = entity.Id;
						if (_fixed_distribution.contains(id))
							continue;
						_order_distribution.add(id);
					}
				}
				query.close();
			}
			
			_dataReady = true;
		}
		public Object getValue(){
			initializeData();
			return _fixed_distribution.size()+_order_distribution.size();
		}
		public void addOrderProduct(Long id){
			if (_fixed_distribution.contains(id)) return;
			if (_order_distribution.contains(id)) return;
			_order_distribution.add(id);
		}
		public void removeOrderProduct(Long id){
			if (!_order_distribution.contains(id)) return;
			_order_distribution.remove(id);
		}
	}
	HashMap<String,Boolean> _pictures_availability = new HashMap<String,Boolean>();
	public boolean pictureAvailable(RefProductItemEntity product) {
		Boolean availability = false;
		if (product==null) return false;
		if (product.ParentExt==null) return false;
		if (product.ParentExt.ExtCode==null) return false;
		if (_pictures_availability.containsKey(product.ParentExt.ExtCode))
			availability = _pictures_availability.get(product.ParentExt.ExtCode);
		else {
			availability = Globals.findDownloadedFile(product.ParentExt.ExtCode.trim()+".jpg");
			_pictures_availability.put(product.ParentExt.ExtCode, availability);
		}	
		//Log.v(SFSActivity.LOG_TAG,product.ParentExt.ExtCode+": "+((availability)?"фото есть":"фото нет"));
		return availability;
	}
	public boolean pictureAvailable(String extcode) {
		Boolean availability = false;
		
		if (_pictures_availability.containsKey(extcode))
			availability = _pictures_availability.get(extcode);
		else {
			availability = Globals.findDownloadedFile(extcode.trim()+".jpg");
			_pictures_availability.put(extcode, availability);
		}	
		
		return availability;
	}
	

	public boolean isReadOnly() {
		if (this.IsAccepted && !this.IsMark) return true;
		return false;
	}

	@Override
	public void OnChangeQuantity(DocOrderLineEntity oldline, DocOrderLineEntity newline) {
		onQuantityChanged();
		if (newline != null && newline.ProductItem!=null) {
			(new AsyncLineCalculator()).execute(newline);
		}
	}
	
	// Возвращает кратность выполенения акции по ее идентификатору
	
    /**
     * Возвращает кратность выполнения акции
     *
     * @param promoId - идентификатор промо-акции
     * 
     * @return - кратность выполнения акции
     * 
     * @author petr_bu
     * 
     * @see {@link RefPromoDetails}
     * @see {@link RefPromoDetailsEntity}
     * 
     */
    public Integer getPromotionMultiplicity(long promoId) {
	return mPromos.getCounter(promoId);
    }
    
    /**
     * Возвращает кратность выполнения акции
     *
     * @param promoId - идентификатор промо-акции
     * @param promoKitId
     * 
     * @return - кратность выполнения акции
     * 
     * @author petr_bu
     * 
     * @see {@link RefPromoDetails}
     * @see {@link RefPromoDetailsEntity}
     * @see {@link RefPromoKit}
     * @see {@link RefPromoKitEntity}
     * 
     */
    public Integer getPromotionMultiplicity(long promoId, long promoKitId) {
	return mPromos.getCounter(promoId, promoKitId);
    }
    /**
     * Возвращает описание промо набора
     *
     * @param promoId - идентификатор промо-акции
     * @param kitId
     * 
     * @return - текстовое представление набора
     * 
     * @author alex_us
     * 
     * @see {@link RefPromoDetails}
     * @see {@link RefPromoDetailsEntity}
     * @see {@link RefPromoKit}
     * @see {@link RefPromoKitEntity}
     * 
     */
    public String getPromoKitDescription(Long promoId, Long kitId) {
		Promo promo = mPromos.getPromo(promoId);
		if (promo!=null){
			PromoKit kit = promo.getPromoKit(kitId);
			if (kit!=null)
				return kit.toString();
		}
		return "Набор";
	}
    /**
     * Возвращает кратность выполнения акции
     *
     * @param promoId - идентификатор промо-акции
     * @param promoKitId - идентификатор набора CSKU
     * @param productId - идентификатор товара
     *
     * @return - кратность выполнения акции
     * 
     * @author petr_bu
     * 
     * @see {@link RefPromoDetails}
     * @see {@link RefPromoDetailsEntity}
     * @see {@link RefPromoKit}
     * @see {@link RefPromoKitEntity}
     * @see {@link RefProductItem}
     * @see {@link RefProductItemEntity}
     * 
     */
    public Integer getPromotionMultiplicity(long promoId, long promoKitId, long productId) {
	return mPromos.getCounter(promoId, promoKitId, productId);
    }
	
	private final Set<IEventListener> _eventPromoStateChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnPromoStateChangedListener(OnPromotionStateListener eventListener) {
		EventListenerSubscriber.setListener(_eventPromoStateChangedListeners, eventListener);
	}

	public void addOnPromoStateChangedListener(OnPromotionStateListener eventListener) {
		EventListenerSubscriber.addListener(_eventPromoStateChangedListeners, eventListener);
	}
	
	public void removeOnPromoStateChangedListener(OnPromotionStateListener eventListener) {
		EventListenerSubscriber.removeListener(_eventPromoStateChangedListeners, eventListener);
	}
	
	public void firePromoCompensationTypeChanged(int compensationType, Long promotionId) {
		for (IEventListener eventListener : _eventPromoStateChangedListeners)
			((OnPromotionStateListener) eventListener).onPromotionCompensationTypeChanged(compensationType, promotionId);
	}
	
	public void firePromoTypeAvailabilityChanged(Boolean availability, int promotionType) {
		for (IEventListener eventListener : _eventPromoStateChangedListeners)
			((OnPromotionStateListener) eventListener).onPromotionTypeAvailabilityChanged(availability, promotionType);
	}
	
	public void firePromoAvailabilityChanged(Boolean availability, Long promotionId) {
		for (IEventListener eventListener : _eventPromoStateChangedListeners)
			((OnPromotionStateListener) eventListener).onPromotionAvailabilityChanged(availability, promotionId);
	}
	
	public void firePromoCounterChanged(Integer counter, Long promotionId) {
		for (IEventListener eventListener : _eventPromoStateChangedListeners)
			((OnPromotionStateListener) eventListener).onPromotionCounterChanged(counter, promotionId);
	}
	
    /**
     * Событие наступает при изменении фгентом типа компенсации 
     * 
     * @param compensationType - тип компенсации
     * @param promotionId - идентификатор промо-акции
     * 
     * @author petr_bu
     * 
     * @see {@link CompensationType}
     * @see {@link RefPromoDetails}
     * @see {@link RefPromoDetailsEntity}
     * 
     */
    public void onPromotionCompensationTypeChanged(int compensationType, Long promotionId) {
	Boolean handled = false;

	for (QueryGetPromoCskuEntity entity : _promoBuyAndGetOrder.values()) {
	    if (entity.Promo == promotionId) {
		entity.PrefferedCompensationType = compensationType;
		handled = true;

	    }
	}
	if (!handled){
		for (QueryGetPromoCskuEntity entity : _promoBuyAndGetPeriod.values()) {
		    if (entity.Promo == promotionId) {
			entity.PrefferedCompensationType = compensationType;
			handled = true;

		    }
		}
	}
	if (!handled){
		for (QueryGetPromoBrandEntity entity : this._promoVolumeBrand.values()) {
		    if (entity.Promo == promotionId) {
			entity.PrefferedCompensationType = compensationType;
			handled = true;

		    }
		}
	}
	if (handled) {
		
		//this.setPromoPrefferedCompensationType(promotionId, compensationType.getId());
	    new QueryUpdatePromoCompensationType(InternalStorage.getConnection(), Outlet.Id, promotionId, compensationType).execute();
	    _invalidSum = true;
	    applyTradeRules("Изменилcя тип компенсации промо-скидок");
	    firePromoCompensationTypeChanged(compensationType, promotionId);
	}
    }

    /**
     * Функция возвращает текущее выбранное агентом значение типа компенсации,
     * если агент еще не выбрал тип компенсации, то возвращается 0
     * 
     * @param promoId - идентификатор промо-акции
     * 
     * @return - текущее выбранное агентом значение типа компенсации
     * 
     * @author petr_bu
     * 
     * @see {@link RefPromoDetails}
     * @see {@link RefPromoDetailsEntity}
     * 
     */
    public int getPromoPrefferedCompensationType(long promoId) {
	for (QueryGetPromoCskuEntity entity : _promoBuyAndGetOrder.values()) {
	    if (entity.Promo == promoId) {
		return entity.PrefferedCompensationType;
	    }
	}
	for (QueryGetPromoCskuEntity entity : _promoBuyAndGetPeriod.values()) {
	    if (entity.Promo == promoId) {
		return entity.PrefferedCompensationType;
	    }
	}
	for (QueryGetPromoBrandEntity entity : this._promoVolumeBrand.values()) {
	    if (entity.Promo == promoId) {
		return entity.PrefferedCompensationType;
	    }
	}
	return 0;
    }

    /**
     * Функция задает текущее значение типа компенсации выбранное агентом
     * 
     * @param promoId - дентификатор промо-акции
     * @param type - тип компенсации промо-акции
     * 
     * @author petr_bu
     * 
     * @see {@link RefPromoDetails}
     * @see {@link RefPromoDetailsEntity}
     * 
     */
    /*
    public void setPromoPrefferedCompensationType(long promoId, int type) {
		for (QueryGetPromoCskuEntity entity : _promoBAG.values()) {
		    if (entity.Promo == promoId) {
		    	entity.PrefferedCompensationType = type;
			
		    }
		    
		}
	
    }
    */

    /**
     * Функция возвращает значение типа компенсации для акции заданое в регистре (в механике).
     * 
     * Например, если тип компенсации жестко задан для акции, то функция вернет
     * 1 (монетарная), либо 2 (товарная), если логикой акции допускается выбор
     * агентом типа компенсации, то функция вернет 0.
     * 
     * @param promoId - дентификатор промо-акции
     * 
     * @return - значение типа компенсации для акции
     * 
     * @author petr_bu
     * 
     * @see {@link RefPromoDetails}
     * @see {@link RefPromoDetailsEntity}
     * 
     */
    public int getPromoCompensationType(long promoId) {
	for (QueryGetPromoCskuEntity entity : _promoBuyAndGetOrder.values()) {
	    if (entity.Promo == promoId) {
		return entity.CompensationType;
	    }
	}
	for (QueryGetPromoCskuEntity entity : _promoBuyAndGetPeriod.values()) {
	    if (entity.Promo == promoId) {
		return entity.CompensationType;
	    }
	}
	for (QueryGetPromoBrandEntity entity : this._promoVolumeBrand.values()) {
	    if (entity.Promo == promoId) {
		return entity.CompensationType;
	    }
	}
	return 0;
    }
    
    /**
     * Функция возвращает текущий прогресс выполнения акции выраженный в процентах (от 0 до 100)
     *
     * @param promotionId - дентификатор промо-акции
     * 
     * @return - текущий прогресс выполнения акции выраженный в процентах (от 0 до 100)
     * 
     * @author petr_bu
     * 
     * @see {@link RefPromoDetails}
     * @see {@link RefPromoDetailsEntity}
     */
    public Short getPromoProgress(Long promotionId) {
    	return mPromos.getPromoProgress(promotionId);
    }
    
    public void onPromotionTypeAvailabilityChanged(Boolean availability, int promotionType) {
	Set<Long> promos = new CopyOnWriteArraySet<Long>();
	switch (promotionType) {
	case PromoType.PRICING:
	    for (QueryGetPromoCskuEntity entity : _promoPricing.values()) {
		if (entity.IsAvailable != availability) {
		    entity.IsAvailable = availability;
		    promos.add(entity.Promo);
		}

	    }
	    break;
	case PromoType.BAG_ORDER:
	    for (QueryGetPromoCskuEntity entity : _promoBuyAndGetOrder.values()) {
		if (entity.IsAvailable != availability) {
		    entity.IsAvailable = availability;
		    promos.add(entity.Promo);
		}
	    }
	    break;
	
	case PromoType.BAG_PERIOD: 
		for (QueryGetPromoCskuEntity entity : _promoBuyAndGetPeriod.values()){
		if (entity.IsAvailable != availability) {
		    entity.IsAvailable = availability;
		    promos.add(entity.Promo);
		}
		}
	    break;
	case PromoType.BAG_VOLUME:
		for (QueryGetPromoBrandEntity entity : _promoVolumeBrand.values()){
			if (entity.IsAvailable != availability) {
			    entity.IsAvailable = availability;
			    promos.add(entity.Promo);
			}
			}
	    break;
	default:
	    break;
	}

	if (!promos.isEmpty()) {
	    for (Long promo : promos) {
		new QueryUpdatePromoAvailability(
			InternalStorage.getConnection(), this.Outlet.Id, promo,
			availability).execute();
		mPromos.chargeBonus(promo);
	    }
	    _invalidSum = true;

	    applyTradeRules("Изменилась доступность групп промо-скидок");
	    firePromoTypeAvailabilityChanged(availability, promotionType);
	}
    }

    public Boolean getPromoAvailabilty(Long promoId) {
	for (QueryGetPromoCskuEntity entity : _promoPricing.values()) {
	    if (entity.Promo == promoId) {
	    	return entity.IsAvailable;
	    }
		
	}
	for (QueryGetPromoCskuEntity entity : _promoBuyAndGetOrder.values()) {
	    if (entity.Promo == promoId)
		return entity.IsAvailable;
	}
	for (QueryGetPromoCskuEntity entity : _promoBuyAndGetPeriod.values()) {
	    if (entity.Promo == promoId)
		return entity.IsAvailable;
	}
	for (QueryGetPromoBrandEntity entity : _promoVolumeBrand.values()) {
	    if (entity.Promo == promoId)
		return entity.IsAvailable;
	}
	return false;
    }

    public boolean getPromoTypeAvailabilty(Integer promoType) {
	Map<Long, QueryGetPromoCskuEntity> map = null;
	switch (promoType) {
	case 1:
	    map = _promoPricing;
	    break;
	case 2:
	    map = _promoBuyAndGetOrder;
	    break;
	case 3:
	    map = _promoBuyAndGetPeriod;
	    break;
	default:
	    map = null;
	}
	if (map == null) {
	    return false;
	}
	for (QueryGetPromoCskuEntity entity : map.values()) {
	    if (entity.IsAvailable == null)
		continue;
	    if (entity.IsAvailable)
		return true;
	}
	if (promoType==4){
		for (QueryGetPromoBrandEntity entity : this._promoVolumeBrand.values()) {
		    if (entity.IsAvailable == null)
			continue;
		    if (entity.IsAvailable)
			return true;
		}	
	}
	return false;
    }
    
	@Override
	public void onPromotionAvailabilityChanged(Boolean availability, Long promotionId) {
		Boolean handled = false;
			for (QueryGetPromoCskuEntity entity: _promoPricing.values()){
				if (entity.Promo==promotionId) { 
					entity.IsAvailable = availability;
					
					handled = true;
					
				}
			}
		if (!handled) {
			for (QueryGetPromoCskuEntity entity: _promoBuyAndGetOrder.values()){
				if (entity.Promo==promotionId) {
					entity.IsAvailable = availability;
					handled = true;
				}
			}
		}
		if (!handled) {
			for (QueryGetPromoCskuEntity entity: _promoBuyAndGetPeriod.values()){
				if (entity.Promo==promotionId) {
					entity.IsAvailable = availability;
					handled = true;
				}
			}
		}
		if (!handled) {
		
			
			for (QueryGetPromoBrandEntity entity: this._promoVolumeBrand.values()){
				if (entity.Promo==promotionId) {
					entity.IsAvailable = availability;
					handled = true;
				}
			}
		}
		if (handled) {
			new QueryUpdatePromoAvailability(InternalStorage.getConnection(), this.Outlet.Id, promotionId, availability).execute();
			_invalidSum = true;
			applyTradeRules("Изменилась доступность промо-скидок");
			firePromoAvailabilityChanged(availability, promotionId);
		}
	}

	@Override
	public void onPromotionCounterChanged(Integer counter, Long promotionId) {
		//эксалация
		firePromoCounterChanged(counter, promotionId);
		
	}
	public static int PROMO_COUNT = 1;
	public static int PROMO_SOLD = 2;
	public static int PROMO_AMOUNT = 3;
	
	public SparseArray<Float> getPromosTotals(int promoType){
		int count = 0;
		int sold = 0;
		float amount = 0;
		
		List<Promo> promoList = mPromos.getPromosByType(promoType);
		for (Promo promo : promoList) {
			SparseArray<Float> t = getPromoTotals(promo.getId());
			
			amount += t.get(PROMO_AMOUNT); 
			if (t.get(PROMO_SOLD) > 0) {
				sold++;
			}
		}

		count = promoList.size();
		
		SparseArray<Float> result = new SparseArray<Float>();
		result.put(PROMO_COUNT, (float) count);
		result.put(PROMO_SOLD, (float) sold);
		result.put(PROMO_AMOUNT, amount);
		return result;
		
	}

	public SparseArray<Float> getPromoTotals(long promoId) {
		int count = 0;
		int sold = 0;
		float amount = 0;
		
		Promo promo = mPromos.getPromo(promoId);
		if (promo!=null) {
			switch (promo.getPromoType()) {
				case PromoType.PRICING: {
					for (DocOrderLineEntity line : _linescache.values()) {
						if (line.Promo1 != null){
							if (line.Promo1.Id == promoId){
								amount += line.Bonus1Amount;
							}
						}
					}
					sold = amount > 0 ? 1 : 0;
				} break;
				case PromoType.BAG_ORDER: {
					sold = promo.getQuantity();
					for (DocOrderLineEntity line : _linescache.values()) {
						if (line.Promo2 != null){
							if (line.Promo2.Id == promoId){
								amount += line.Bonus2Amount;
							}
						}
					}
				} break;
				case PromoType.BAG_PERIOD: {
					sold = getPromoAvailabilty(promoId) ? 1 : 0;
					// TODO Пока не реализовано
				} break;
				case PromoType.BAG_VOLUME: {
					sold = getPromoAvailabilty(promoId) ? 1 : 0;
					float fact = promo.getPromoTotalFact();
					float plan = promo.getPromoTarget();
					
					if (fact < plan) { 
						amount = 0f;
					} else {
						amount = fact * (promo.getDiscount() / 100);
					}
				} break;
				default: {
					
				} break;
			}
		}
		SparseArray<Float> result = new SparseArray<Float>();
		result.put(PROMO_COUNT, (float) count);
		result.put(PROMO_SOLD, (float) sold);
		result.put(PROMO_AMOUNT, amount);
		
		return result;
	}
	
	
	public void changePromoCounter(long promoId, int desiredCount) {
		if (promoId==0) return;
		if (desiredCount==0) return;
		Log.d(MainActivity.LOG_TAG, "Установка кратности "+desiredCount+" начата...");
		Promo promo = mPromos.getPromo(promoId);
		if (promo!=null){
			for (PromoKit kit: promo.getPromoKits().values()){
				if (kit.getQuantity()==desiredCount)
					continue;
				if (kit.getMinKitQuantity()<2){
					//пропорциональная акция
					for (PromoCsku csku: kit.getPromoCskus().values()){
						if (csku.getItemQuantity()==0) //увеличиваем только уже заказанное
							continue;
						int desiredQuantity = kit.getMinOrderQuantity() * desiredCount;
						for (PromoItem item: csku.getmItems().values()){
							if (item.getQuantity()>0){
								changeQuantity(item.getItemId(), desiredQuantity,0, 0, csku.getCskuId(),1,csku.getBrand());
								desiredQuantity = 0;
							}
						}
					}
				}
				else {
					int currentQuantity = kit.getQuantity() * kit.getMinKitQuantity();
					if (currentQuantity>0) {
						int desiredKitQuantity = kit.getMinKitQuantity() * desiredCount;
						float index = ((float) desiredKitQuantity) / ((float) currentQuantity);
						//сначала поймем как сейчас распределено количество по товарам
						 
						for (PromoCsku csku: kit.getPromoCskus().values()){
							if (csku.getItemQuantity()==0) //не заказано
								continue;
							int desiredQuantity = (int) Math.ceil(index * (float) csku.getItemQuantity());
							if (desiredQuantity>desiredKitQuantity)
								desiredQuantity = desiredKitQuantity;
							for (PromoItem item: csku.getmItems().values()){
								if (item.getQuantity()>0){
									changeQuantity(item.getItemId(), desiredQuantity,0, 0, csku.getCskuId(),1,csku.getBrand());
									desiredKitQuantity-=desiredQuantity;
									desiredQuantity = 0;
									
								}
							}
							
						}
					}
				}
				
			}
		}
		/*
		for (DocOrderLineEntity line: _linescache.values()){
			if (line.Promo2==null) continue;
			if (line.Promo2.Id!=promoId) continue;
		
			int promoFactor = mPromos.getMinOrderQuantity(line.Promo2.Id,line.ProductItem.ParentExt.Id);
			
			changeQuantity(line.ProductItem.Id, desiredCount*promoFactor,0, 0, line.ProductItem.ParentExt.Id,1);
		}
		*/
		Log.d(MainActivity.LOG_TAG, "Установка кратности "+desiredCount+" закончена");
		
	}
	
	public HashMap<Long,Set<DocOrderLineEntity>> getPromoBonuses() {
		HashMap<Long, Set<DocOrderLineEntity>> result = new HashMap<Long, Set<DocOrderLineEntity>>();
		
		for (DocOrderLineEntity line: _linescache.values()){
			
			if (line.Promo1 != null && line.Bonus1Amount > 0) {
				Set<DocOrderLineEntity> set = result.get(line.Promo1.Id);
				if (set == null) {
					set = new CopyOnWriteArraySet<DocOrderLineEntity>();
					result.put(line.Promo1.Id, set);
				}
				set.add(line);
			}
			
			if (line.Promo2 != null && line.Bonus2Amount > 0){
				Set<DocOrderLineEntity> set = result.get(line.Promo2.Id);
				if (set == null) {
					set = new CopyOnWriteArraySet<DocOrderLineEntity>();
					result.put(line.Promo2.Id, set);
				}
				set.add(line);
			}
			
			if (line.Promo4 != null && line.Promo4CompensationType > 0) {
				Set<DocOrderLineEntity> set = result.get(line.Promo4.Id);
				if (set == null) {
					set = new CopyOnWriteArraySet<DocOrderLineEntity>();
					result.put(line.Promo4.Id, set);
				}
				set.add(line);
			}
		}
		
		return result;
	}

	@Override
	public void onPromoTotalChanged(Promo promo) {
		//Эскалация
		if (this.mPromoTotalChangedListener!=null){
			this.mPromoTotalChangedListener.onPromoTotalChanged(promo);
		}
	}


	
}