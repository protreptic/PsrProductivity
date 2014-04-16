package ru.magnat.sfs.bom.cache.promo;

import java.util.HashMap;

public class PromoBrand {
		
		
		public int getQuantity() {
			return mQuantity;
		}
/*
		public void _setQuantity(int quantity) {
			this.mQuantity = quantity;
		}
*/
		public HashMap<Long, PromoItem> getPromoItem() {
			return mItems;
		}

		public long getmBrandId() {
			return mBrandId;
		}

		
		final private HashMap<Long, PromoItem> mItems;
		final private long mBrandId;
		
		private int mQuantity;
		
		public PromoBrand(long brandId) {
			mBrandId = brandId;
			mItems = new HashMap<Long, PromoItem>();
		}
		
		public PromoItem addPromoItem(long itemId,int order, float amount){
			PromoItem promoItem = new PromoItem(itemId,order,amount);
			mItems.put(itemId, promoItem);
			return promoItem;
		}
		public PromoItem getPromoItem(long itemId){
			return mItems.get(itemId);
		}
		
		public Boolean changePromo(long itemId, int order, float amount) {
			PromoItem item = mItems.get(itemId);
			if (item == null) {
				addPromoItem(itemId,order, amount);
				return true;
			}
			else {
				if (item.getAmount()!=amount){
					item.setQuantity(order);
					item.setAmount(amount);
					return true;
				}
			}
			return false;
			
			
		}

		//функция возвращает количество заказанных товаров
		//используется в расчете степени выполнения условий акции
		public int getOrderedQuantity() {
			int order = 0;
			for (PromoItem item: mItems.values()){
				order+=item.getQuantity();	
			}
			
			return order;
		}
		//используется в расчете степени выполнения условий акции
		public float getOrderedAmount() {
			float order = 0;
			for (PromoItem item: mItems.values()){
				order+=item.getAmount();	
			}
			
			return order;
		}
		
	}