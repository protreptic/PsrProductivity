package ru.magnat.sfs.bom.cache.promo;

public class PromoItem {
	public PromoItem(long itemId, int quantity)
	{
		this(itemId,quantity,0f);
	}
	public PromoItem(long itemId, int quantity,float amount){
		mItemId = itemId;
		mQuantity = quantity;
		mPromoQuantity = 0;
		mAmount = amount;
	}
	private final long mItemId;
	
	public long getItemId() {
		return mItemId;
	}
	private int mPromoQuantity;
	public Integer getPromoQuantity() {
		return mPromoQuantity;
	}
	public void setPromoQuantity(int value) {
		this.mPromoQuantity = value;
	}
	private int mPromoFreeProductQuantity;
	public Integer getPromoFreeProductQuantity() {
		return mPromoFreeProductQuantity;
	}
	public void setPromoFreeProductQuantity(int value) {
		this.mPromoFreeProductQuantity = value;
	}
	private int mQuantity; 
	public int getQuantity(){
		return mQuantity;
	}
	public void setQuantity(int quantity){
		mQuantity  = quantity;
	}
	private float mAmount; 
	public float getAmount(){
		return mAmount;
	}
	public void setAmount(float amount){
		mAmount  = amount;
	}
}	