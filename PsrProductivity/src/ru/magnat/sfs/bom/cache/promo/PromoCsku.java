package ru.magnat.sfs.bom.cache.promo;

import java.util.HashMap;

import ru.magnat.sfs.android.MainActivity;

import android.util.Log;


public class PromoCsku implements Comparable<PromoCsku> {
	final private HashMap<Long, PromoItem> mItems;
	public HashMap<Long, PromoItem> getmItems() {
		return mItems;
	}
	private int mQuantity;
	private int mItemQuantity;
	private int mBonusQuantity;
	private final long mBrand;

	private int mTempQuantity;
	
	public int getTempQuantity() {
		return mTempQuantity;
	}
	public void setTempQuantity(int value) {
		mTempQuantity = value;
	}
	
	final long mCskuId;
	final Boolean mMustHave;
	final int mFreeProductSize;
	private int mHistorySalesQuantity;
	
	public int getItemQuantity() {
		return mItemQuantity;
	}
	
	public void setItemQuantity(int value) {
		this.mItemQuantity = value;
	}
	
	public Boolean getMustHave() {
		return mMustHave;
	}
	
	public long getCskuId(){
		return mCskuId;
	}
	
	public PromoCsku(long cskuId, Boolean mustHave, int freeProductSize, long brand){
		mItems = new HashMap<Long, PromoItem>();
		mCskuId = cskuId;
		mMustHave = mustHave;
		mFreeProductSize = freeProductSize;
		mBrand = brand;
		if (mFreeProductSize>0)
			Log.d(MainActivity.LOG_TAG, "Free product: "+cskuId);
	}
	
	public PromoItem addPromoItem(long itemId,int quantity){
		PromoItem item = new PromoItem(itemId,quantity);
		mItems.put(itemId, item);
		return item;
	}
	
	public PromoItem getPromoItem(long itemId){
		return mItems.get(itemId);
	}
	
	public void changePromo(long itemId, int order, int minFactor) {
		PromoItem oldItem =  null;
		PromoItem newItem = null;
		newItem = new PromoItem(itemId,order);
		oldItem  = mItems.put(itemId, newItem);
		if (oldItem!=null) mItemQuantity-=oldItem.getQuantity();
		if (newItem!=null) mItemQuantity+=newItem.getQuantity();
		mQuantity = (minFactor>0) ? (mItemQuantity+mHistorySalesQuantity)/minFactor:0;	
	}
	
	public int getQuantity() {
		return mQuantity;
	}
	
	public void setQuantity(int quantity) {
		mQuantity = quantity;
	}
	
	public void setBonusQuantity(int quantity) {
		mBonusQuantity = quantity;
		for (PromoItem item:mItems.values()){
			int order = item.getQuantity();
			int value = 0;
			if (order<quantity){
				value = order;
			}
			else {
				value = quantity;
			}
			quantity-=value;
			item.setPromoQuantity(value);
		}
	}
	
	public int getBonusQuantity() {
		return mBonusQuantity;
		
	}
	
	@Override
	public int compareTo(PromoCsku another) {
		if (another==null) return 1;
		int diff = (this.mMustHave)?0:1;
		diff-= (another.mMustHave)?0:1; //по MustHave
		if (diff == 0) {
			//diff = (this.mFreeProductSize>0)?0:1; //по бонусным Csku
			//diff-= (another.mFreeProductSize>0)?0:1;
			//if (diff==0) {
				diff = this.mQuantity - another.mQuantity; //по количеству
				if (diff == 0){
					diff = (int) (this.mCskuId - another.mCskuId); //по CskuId
				}
			//}
		}
		return diff;
	}
	public Integer getBonusedProductQuantity(long itemId) {
		PromoItem item = mItems.get(itemId);
		if (item == null) return 0;
		return item.getPromoQuantity();
	}
	public Integer getFreeProductQuantity(long itemId) {
		PromoItem item = mItems.get(itemId);
		if (item == null) return 0;
		return item.getPromoFreeProductQuantity();
	}
	public void setFreeProduct(int quantity) {
		for (PromoItem item:mItems.values()){
			//int order = item.getQuantity();
			//if (order>0) {
				item.setPromoFreeProductQuantity(quantity);
				quantity  = 0;
				break;
			//}
		}
	
	}
	public int getFreeProductSize() {
		return mFreeProductSize;
	}
	public int getFreeProduct() {
		int free = 0;
		for (PromoItem item:mItems.values()){
			return item.getPromoFreeProductQuantity();
				
		}
		return free;
	}
	public long getBrand() {
		return mBrand;
	}
	public void setHistorySales(int quantity) {
		mHistorySalesQuantity = quantity;
		
	}
	public int getHistorySales() {
		return mHistorySalesQuantity;
		
	}
}