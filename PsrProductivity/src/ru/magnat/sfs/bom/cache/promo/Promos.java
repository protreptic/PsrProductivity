package ru.magnat.sfs.bom.cache.promo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Promos implements PromoTotalChangedListener {
	final private HashMap<Long, Promo> mItems;
	private int mQuantity;
	
	private PromoTotalChangedListener mTotalChangedListener;
	public void setOnPromoTotalChangedListener(PromoTotalChangedListener listener){
		mTotalChangedListener = listener;
	}
	
	public Promos(){
		mItems = new HashMap<Long, Promo>();
		mQuantity = 0;
	}
	
	public Promo addPromo(long promoId, int promoType,float discount){
		Promo promo = new Promo(promoId,promoType,discount);
		mItems.put(promoId, promo);
		return promo;
	}
	
	public Promo addPromo(long promoId, int promoType,float discount, float target,float fact){
		Promo promo = new Promo(promoId,promoType,discount, target,fact);
		if (promoType==4){
			promo.setOnPromoTotalChangedListener(this);
		}
		mItems.put(promoId, promo);
		return promo;
	}
	
	public Promo getPromo(long promoId){
		return mItems.get(promoId);
	}
	
	public List<Promo> getPromosByType(int promoType) {
		List<Promo> result = new ArrayList<Promo>();
		for (Promo promo : mItems.values()) {
			if (promo.getPromoType() == promoType) {
				result.add(promo);
			}
		}
		
		return result;
	}
	
	public int getQuantity() {
		return mQuantity;
	}
	
	public boolean changePromo(long promoId, long cskuId, long itemId, int order, long brandId, float amount){
		boolean changed = false;
		{
			Promo promo = mItems.get(promoId);
			if (promo!=null){
				changed|=promo.changePromo(brandId, cskuId, itemId,order,amount);
			}
		}
		
		//ѕросчитаем сколько раз применились все акции
		mQuantity = 0;
		for (Promo promo: mItems.values()){
			mQuantity+=promo.getQuantity();
		}
		return changed;
		
	}

	public int getCounter(long promoId) {
		return getCounter(promoId,null,null);
	}

	public int getCounter(long promoId, long kitId) {
		return getCounter(promoId,kitId,null);
	}
	
	public int getCounter(Long promoId, Long kitId, Long cskuId) {
		int counter = 0;
		Promo promo = getPromo(promoId);
		if (promo!=null) {
			if (kitId==null)
				counter = promo.getQuantity();
			else {
				PromoKit kit = promo.getPromoKit(kitId);
				if (kit!=null){
					if (cskuId==null)
						counter = kit.getQuantity();
					else  {
						PromoCsku csku = kit.getPromoCsku(cskuId);
						if (csku!=null)
							counter = csku.getQuantity();
					}
						
				}
			}
		}
		return counter;
	}
	
	public int getBonusProductQuantity(long promoId, long cskuId, long itemId) {
		Promo promo = getPromo(promoId);
		if (promo==null) return 0;
		else 
			return promo.getBonusProductQuantity(cskuId,itemId);
	}

	public int getFreeProductQuantity(long promoId, long cskuId, long itemId) {
		Promo promo = getPromo(promoId);
		if (promo==null) return 0;
		else 
			return promo.getFreeProductQuantity(cskuId,itemId);
	}
	
	public int getMinOrderQuantity(long promoId, long cskuId) {
		Promo promo = getPromo(promoId);
		if (promo==null) return 0;
		else 
			return promo.getMinOrderQuantity(cskuId);
	}
	/*
	public Integer getFreeProductQuantity(long promoId, long kitId, long cskuId,
			long itemId) {
		Promo promo = getPromo(promoId);
		if (promo==null) return null;
		PromoKit kit = promo.getPromoKit(kitId);
		
		
		return bonus;
	}
*/
	/*
	public int getFreeProductQuantity(long promoId, long cskuId, long itemId) {
		Promo promo = getPromo(promoId);
		if (promo==null) return 0;
		PromoKit kit = promo.getPromoKitByCsku(cskuId);
		if (kit==null)
			return 0;
		PromoCsku csku = kit.getPromoCsku(cskuId);
		if (csku==null) return 0;
		int bonusFactor = csku.getFreeProductSize();
		if (bonusFactor==0) return 0;
		int bonus = 0;
		PromoItem item = csku.getPromoItem(itemId);
		if (item!=null){
			bonus = item.getPromoFreeProductQuantity();
		}
		return bonus;
	}
	*/
	
	public float getCount() {
	    return mItems.size();
	}

	public int getCount(int promoType) {
		int result = 0;
		for (Promo promo:mItems.values()){
			if (promo.getPromoType()==promoType) result++;
		}
		
		return result;
	}

	public void chargeBonus(Long promoId) {
		Promo promo = mItems.get(promoId);
		if (promo==null) return;
		promo.chargeBonus();
		
	}

	public Short getPromoProgress(Long promotionId) {
		Promo promo = mItems.get(promotionId);
		if (promo == null) {
		    return 0;
		}
		
		return promo.getPromoProgress();
	}

	public Promo getPromoByBrand(long brand) {
		for (Promo promo:mItems.values()){
			if (promo.getPromoBrand(brand)!=null)
				return promo;
		}
		return null;
	}
	public Promo getPromoByCsku(long cskuId, int promoType) {
		for (Promo promo:mItems.values()){
			if (promo.getPromoType()!=promoType)
				continue;
			if (promo.getPromoKitByCsku(cskuId)==null)
				continue;
			return promo;
		}
		return null;
	}
	@Override
	public void onPromoTotalChanged(Promo promo) {
		//Ёскалаци€
		if (this.mTotalChangedListener!=null){
			this.mTotalChangedListener.onPromoTotalChanged(promo);
		}
		
	}

	public void clear() {
		mItems.clear();
		
	}

	



	
}
