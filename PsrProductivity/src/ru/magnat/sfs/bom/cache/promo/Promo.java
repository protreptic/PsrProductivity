package ru.magnat.sfs.bom.cache.promo;

import java.util.HashMap;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.MainActivity;


public class Promo {
	final private HashMap<Long, PromoKit> mItems;
	final private HashMap<Long, PromoBrand> mBrands;
	final private long mPromoId;
	final private int mPromoType;
	final private float mPromoTarget;
	final private float mPromoFact;
	private float mPromoTotalFact;
	private int mQuantity;
	
	public Promo(long promoId, int promoType,float discount){
		this(promoId, promoType,discount, 0f,0f);
	}
	public Promo(long promoId, int promoType,float discount,float target,float fact){
		mPromoId = promoId;
		mItems = new HashMap<Long, PromoKit>();
		mBrands = new HashMap<Long, PromoBrand>();
		mPromoType = promoType;
		mPromoTarget = target;
		mPromoFact = fact;
		mPromoTotalFact = fact;
		mDiscount = discount;
	}
	private PromoTotalChangedListener mTotalChangedListener;
	final private Float mDiscount;
	public void setOnPromoTotalChangedListener(PromoTotalChangedListener listener){
		mTotalChangedListener = listener;
	}
	public long getId(){
		return mPromoId;
	}
	public int getQuantity() {
		return mQuantity;
	}
	public float getPromoTarget(){
		return mPromoTarget;
	}
	private float getPromoFact(){
		float order = 0f;
		for(PromoBrand brand:mBrands.values()){
			order+=brand.getOrderedAmount();
		}
		return mPromoFact + order;
	}
	public float getPromoTotalFact(){
		
		return mPromoTotalFact;
	}
	public PromoKit addPromoKit(long kitId){
		PromoKit promoKit = new PromoKit(kitId);
		mItems.put(kitId, promoKit);
		return promoKit;
	}
	public PromoKit removePromoKit(long kitId){
		return mItems.remove(kitId);
	}
	
	public PromoKit getPromoKit(long kitId){
		return mItems.get(kitId);
	}
	
	public PromoBrand addPromoBrand(long brandId){
		PromoBrand promoBrand = new PromoBrand(brandId);
		mBrands.put(brandId, promoBrand);
		return promoBrand;
	}
	public PromoBrand getPromoBrand(long brandId){
		return mBrands.get(brandId);
	}
	public boolean changePromo(long cskuId, long itemId, int order) {
		return changePromo(0, cskuId, itemId, order, 0f);
	}
	public boolean changePromo(long brandId, long cskuId, long itemId, int order, float amount) {
		Boolean handled = false;
		Boolean handledBrand = false;
		PromoBrand brand = mBrands.get(brandId);
		if (brand!=null){
			handledBrand = brand.changePromo(itemId,order,amount);
			if (handledBrand) {
			 float fact = this.getPromoFact();
				 if (fact!=this.mPromoTotalFact){
					 this.mPromoTotalFact = fact;
					 if (mTotalChangedListener!=null)
						 mTotalChangedListener.onPromoTotalChanged(this);
				 }
				 Log.d(MainActivity.LOG_TAG, "Поменялся бренд промо");
			}
		}
		
		//Обходим все пакеты и пытаемся зансунуть в него наш товар, 
		//если получилось, то handled будет true
		for (PromoKit kit: mItems.values()){
			handled = kit.changePromo(cskuId,itemId,order);
			if (handled) 
				break;
		}
		if (!handled) //не нашлось пакета с таким csku (хотя такого не должно бы быть, вызывать надо только для строк с заполненным promo2) 
			return handledBrand||false;
		//Теперь пересчитаем кратность, она равна минимальной среди всех пакетов
		int quantity = Integer.MAX_VALUE;
		for (PromoKit kit: mItems.values()){
			if (kit.getQuantity()<quantity)
				quantity = kit.getQuantity();
		}
		int multiplicity = mQuantity;
		if (quantity == Integer.MAX_VALUE)
			mQuantity = 0;
		else
			mQuantity = quantity;
		//Теперь пересчитаем бонусное количество
		return handledBrand||chargeBonus()||(multiplicity!=mQuantity);
	}
	public boolean chargeBonus(){
		Log.d(MainActivity.LOG_TAG,"АКЦИЯ "+mPromoId+" НАЧИСЛЕНИЕ БОНУСА x"+mQuantity);
		boolean changed = false;
		for (PromoKit kit: mItems.values()){
			changed|=kit.chargeBonus(mQuantity);
		}
		return changed;
	}
	public Integer getBonusProductQuantity(long cskuId, long itemId) {
		Integer quantity = 0;
		for (PromoKit kit: mItems.values()){
			quantity = kit.getBonusProductQuantity(cskuId,itemId);
			if (quantity>-1) 
				break;
		}
		if (quantity==-1) return 0;
		return quantity;
	}
	public Integer getFreeProductQuantity(long cskuId, long itemId) {
		Integer quantity = 0;
		for (PromoKit kit: mItems.values()){
			quantity = kit.getFreeProductQuantity(cskuId,itemId);
			if (quantity>-1) 
				break;
		}
		if (quantity==-1) return 0;
		return quantity;
	}
	public int getPromoType() {
		return mPromoType;
	}
	public int getMinOrderQuantity(long cskuId) {
		Integer quantity = 0;
		for (PromoKit kit: mItems.values()){
			quantity = kit.getMinOrderQuantity(cskuId);
			if (quantity>-1) 
				break;
		}
		if (quantity==-1) return 0;
		return quantity;
	}
	
	public Short getPromoProgress() {
		int fact = 0;
		int plan = 0;

		for (PromoKit kit: mItems.values()){
			fact += kit.getOrderedQuantity();
			plan += kit.getMinCskuQuantity() * kit.getMinKitQuantity() * kit.getMinOrderQuantity();
			
		}
		
		Short progress = 0;
		if (plan > 0) {
		    progress = (short) Math.round((float) fact / (float) plan * 100f);
		}
		Log.d(MainActivity.LOG_TAG, "Promo fact = "+fact+", Promo plan = "+plan+", Promo progress = " + progress+"%");
		return progress;
	}
	public PromoKit getPromoKitByCsku(long cskuId) {
		for (PromoKit kit:mItems.values()){
			if (kit.getPromoCsku(cskuId)!=null)
				return kit;
		}
		return null;
	}
	public HashMap<Long, PromoKit> getPromoKits() {
		
		return mItems;
	}
	public Float getDiscount() 
	{
		return mDiscount;
	}
	public void addPromoSales(long promo, long cskuId, int quantity) {
		for (PromoKit kit:mItems.values()){
			PromoCsku csku = kit.getPromoCsku(cskuId);
			if (csku!=null){
				csku.setHistorySales(quantity);
				break;
			}
		}
		
	}
	
	public int getPromoSales(long promo, long cskuId) {
		for (PromoKit kit:mItems.values()){
			PromoCsku csku = kit.getPromoCsku(cskuId);
			if (csku!=null){
				return csku.getHistorySales();
			}
		}
		return 0;
	}
	
	
}