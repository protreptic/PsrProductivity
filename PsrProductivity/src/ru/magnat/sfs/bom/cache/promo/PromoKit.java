package ru.magnat.sfs.bom.cache.promo;

import java.util.Arrays;
import java.util.HashMap;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.MainActivity;

public class PromoKit {
		public int getMinOrderQuantity() {
			return mMinOrderQuantity;
		}

		public void setMinOrderQuantity(int value) {
			this.mMinOrderQuantity = value;
		}

		public int getMinCskuQuantity() {
			return mMinCskuQuantity;
		}

		public void setMinCskuQuantity(int value) {
			this.mMinCskuQuantity = value;
		}

		public int getMinKitQuantity() {
			return mMinKitQuantity;
		}

		public void setMinKitQuantity(int value) {
			this.mMinKitQuantity = value;
		}
		
		public int getQuantity() {
			return mQuantity;
		}
/*
		public void _setQuantity(int quantity) {
			this.mQuantity = quantity;
		}
*/
		public HashMap<Long, PromoCsku> getPromoCskus() {
			return mItems;
		}

		public long getmKitId() {
			return mKitId;
		}

		
		final private HashMap<Long, PromoCsku> mItems;
		final private long mKitId;
		private int mMinOrderQuantity;
		private int mMinCskuQuantity;
		private int mMinKitQuantity;
		private int mQuantity;
		
		public PromoKit(long kitId) {
			mKitId = kitId;
			mItems = new HashMap<Long, PromoCsku>();
		}
		
		@Override
		public String toString() {
			String res = "";
			if (mMinOrderQuantity==1 && mMinCskuQuantity==1){
				
				if (mItems.size()>1)
					if (mMinKitQuantity>1) {
						res = "Купи как минимум "+mMinKitQuantity+" шт любых CSKU из пакета";
					}
					else {
						res = "Купи как минимум одну штуку любого CSKU из пакета";
					}
				else {
					res = "Купи как минимум одну штуку";
				}
			} 
			else if (mMinCskuQuantity>1) {
				res = "Купи "+mMinCskuQuantity+" разных CSKU из пакета";
				if (mMinOrderQuantity>1) res+=" по "+mMinOrderQuantity+" шт каждого";
			}
			else {
				if (mItems.size()>1)
					res = "Купи как минимум "+mMinOrderQuantity+" шт одного из CSKU";
				else
					res = "Купи как минимум "+mMinOrderQuantity+" шт";
			}
			return res;
		}
		public PromoCsku addPromoCsku(long cskuId,Boolean mustHave, int freeProductSize,long brand){
			PromoCsku promoCsku = new PromoCsku(cskuId,mustHave,freeProductSize,brand);
			mItems.put(cskuId, promoCsku);
			return promoCsku;
		}
		public PromoCsku getPromoCsku(long cskuId){
			return mItems.get(cskuId);
		}
		
		public Boolean changePromo(long cskuId, long itemId, int order) {
			Boolean handled = false;
			PromoCsku csku = mItems.get(cskuId);
			if (csku == null) return handled;
			handled = true;
			csku.changePromo(itemId,order,mMinOrderQuantity);
	//		mItems.put(cskuId,csku);
			updateMultiplicity();
			return handled;
			
		}
		
	
		private void updateMultiplicity() {
			PromoCsku[] cskus = mItems.values().toArray(new PromoCsku[0]);
     		Arrays.sort(cskus);
			
			//Массив отсортирован следующим образом:
			// MustHave, Количество, Код CSKU
			// Логика: сначала пробегаем обязательные и определяем какое максимальное количество кратности может быть 
			// (это самый маленький заказ среди обязательных CSKU)
			//  если в результате одна из обязательных не заказана (то есть самый малький заказ = 0), то смысла что-то проверять дальше нет
			//  если количество наименований заказанных обязательных csku равно минимальному количеству наименований (например купи два любых наименования чего-то и обязательно 
			//  включи туда два наименования), то значит речь идет о приобретении конкретных наименований и кратность равна определенной ранее максимальной
			//  если же количество обязательных наименований меньше чем количество необходимых наименований, то нам нужно посчитать чем можно дополнить наш обязательный набор,
			//  для этого идем дальше по массиву с ненулевым заказом считывая порциями по столько наименований, сколько нам не хватает до дополнения набора
			//  количество в текущем наименовании используем для увеличения вычисляемой кратности, при этом уменьшаем на эту величину количество последующих строках
			//  для остальных наименований, входящих в комплект, если в процессе этого вылезли за пределы пакета, то прекращаем расчет (не хватило наименований)
			//  перед увеличением кратности проверяем не превышена ли максимальная величина, полученная при обходе обязательных, если превышена, то
			//  приравниваем кратность максимальной величине, 
     		// 27.12.2013 по логике Михаила Казакова, если для пакета задано одно CSKU, то минимальное количество для CSKU
     		// приравнено 1, а значение минимального заказа относится ко всему пакету, поэтому в конце расчета делим на цело на
     		// минимальный заказ пакета и прекращаем рассчет
     		// 
     		int tempQuantity = 0;
			int maxDistr = mMinCskuQuantity;
			int maxQuantity = Integer.MAX_VALUE;
			Boolean overflow = false;
			//очищаем бонусное количество
			for (int i = 0; i<cskus.length;i++){
				PromoCsku csku = cskus[i]; 
				csku.setBonusQuantity(0);
				csku.setTempQuantity(csku.getQuantity());
			}
			for (int i = 0; i<cskus.length;i++)	{
				PromoCsku currentCsku = cskus[i];
				
				Log.d(MainActivity.LOG_TAG, "Свойства промо CSKU: Id = " + currentCsku.mCskuId + ", MustHave = " + currentCsku.mMustHave + ", mQuantity = " + currentCsku.getQuantity());
				if (currentCsku.mMustHave) {
					maxDistr--;
					if (currentCsku.getQuantity() < maxQuantity ) maxQuantity = currentCsku.getQuantity();
					continue;
				}
				if (maxQuantity == 0) {
					tempQuantity = 0;
					break;
				}
				if (maxDistr == 0){
					tempQuantity = maxQuantity;
					break;		
				}
				int quantity = currentCsku.getTempQuantity();
     			if (quantity == 0) continue;
     			
				for (int j = i + 1;j<i + maxDistr;j++){
					 
					if (j<cskus.length){
						PromoCsku csku = cskus[j];
						csku.setTempQuantity(csku.getTempQuantity()-quantity);
					}
					else {
						overflow = true;
						break;
					}
				}
				if (overflow){
					break;
				}
				for (int j = i;j < i + maxDistr;j++){
					PromoCsku csku = cskus[j];
					csku.setBonusQuantity(csku.getBonusQuantity()+quantity);
					if (csku.getBonusQuantity()>maxQuantity) {
						csku.setBonusQuantity(maxQuantity);
					}
				}
				tempQuantity += quantity;
				if (tempQuantity>maxQuantity) {
					tempQuantity = maxQuantity;
					break;
				}
			}
			if (maxDistr == 0){
				tempQuantity = maxQuantity;
				
			}
			if (mMinKitQuantity > 0)
				mQuantity = tempQuantity/mMinKitQuantity;
			else
				mQuantity = Integer.MAX_VALUE; //Это бонусный пакет его кратность не важна
			Log.d(MainActivity.LOG_TAG,"Кратность пакета "+this.mKitId+" = "+mQuantity+" ("+this.hashCode()+")");
			
		}
		
		public boolean chargeBonus(int selledPromo) {
			int multiplicity = selledPromo;
			PromoCsku[] cskus = mItems.values().toArray(new PromoCsku[0]);
			Arrays.sort(cskus);
			selledPromo*=this.mMinKitQuantity*this.mMinOrderQuantity;
			//Массив отсортирован следующим образом:
			// MustHave, Количество, Код CSKU
			// Логика: сначала пробегаем обязательные и списываем бонус, попутно определяем размер пакета необязательного ассортимента
			// Если размер пакета необязательного ассортимента равен 0, то заканчиваем раздачу слонов
			// Идем по необязательному ассортименту списывая количество бонуса равное количеству для текущего товара и уменьшая на это же количество количество во всех остальных
			// товарах пакета, заканчиваем когда бонус спишется в ноль
			int maxDistr = mMinCskuQuantity;
			boolean overflow = false;
			//Очищаем бонус
			for (PromoCsku csku:cskus){
				csku.setBonusQuantity(0);
				csku.setTempQuantity(csku.getQuantity());
				csku.setFreeProduct(0);
			}
			for (int i = 0; i<cskus.length && selledPromo > 0 && maxDistr>0;i++){
				PromoCsku currentCsku = cskus[i];

				if (currentCsku.mMustHave) {
					maxDistr--;
					currentCsku.setTempQuantity(currentCsku.getTempQuantity()-selledPromo); //для обязательных начисляем безусловно, особо проверять ничего не надо, так как при
					currentCsku.setBonusQuantity(selledPromo); //расчете бонуса для каждого пакета бонус меньше или равен самому маленькому заказу обязательного
					continue; //товара, а в начисление должно передаваться минимальное значение бонуса из всех пакетов
				}
				int  quantity = currentCsku.getTempQuantity();
				if (quantity == 0) 
					continue;
				int charge =  (selledPromo > quantity) ? quantity : selledPromo; //начисляемое количество бонуса
				selledPromo -= charge; //уменьшаем баланс бонуса
				for (int j = i; j < i + maxDistr;j++){ //идем по всем csku в пределах обязательной диситрибьюции, памятуя о том что массив отсортирован   
					if (j < cskus.length) {  //по количеству, т.е. каждое следующее количество больше предыдущего
						PromoCsku csku = cskus[j];
						csku.setBonusQuantity(csku.getBonusQuantity()+charge); //начисляем бонус
						csku.setTempQuantity(csku.getTempQuantity()-charge);  //уменьшаем заказанное количество
					}
					else {
						overflow = true; //вылезли за пределы массива (при начислении бонуса такого по идее быть не должно)
						break;
					}
				}
				if (overflow) 
					break;


			}
			boolean changed = false;
			//просчитали массив, теперь надо записать результат в пакет
			for (PromoCsku csku:cskus){
				 int free  = multiplicity * csku.getFreeProductSize();
				 int bonus = csku.getBonusQuantity()*this.mMinOrderQuantity;
					 Log.i(MainActivity.LOG_TAG, "АКЦИЯ Выполнений = "+csku.getBonusQuantity()+" Со скидкой "+ bonus +" Бесплатно ="+free);
					 if (mItems.get(csku.mCskuId).getBonusQuantity()!=bonus){
						 Log.d(MainActivity.LOG_TAG, "Бонус на товар изменился "+bonus);
						 changed = true;
						 mItems.get(csku.mCskuId).setBonusQuantity(bonus);
					 }
					 if (mItems.get(csku.mCskuId).getFreeProduct()!=free) {
						 Log.d(MainActivity.LOG_TAG, "Бесплатный товар изменился "+free);
						 changed = true;
						 mItems.get(csku.mCskuId).setFreeProduct(free);
					 }
			}
			return changed;
			
		}

		public Integer getBonusProductQuantity(long cskuId, long itemId) {
			PromoCsku csku = mItems.get(cskuId);
			if (csku==null) return -1; //нет в пакете
			return csku.getBonusedProductQuantity(itemId);
		}

		public Integer getFreeProductQuantity(long cskuId, long itemId) {
			PromoCsku csku = mItems.get(cskuId);
			if (csku==null) return -1; //нет в пакете
			return csku.getFreeProductQuantity(itemId);
		}

		public Integer getMinOrderQuantity(long cskuId) {
			PromoCsku csku = mItems.get(cskuId);
			if (csku==null) return -1; //нет в пакете
			return this.mMinOrderQuantity;
		}
		//функция возвращает количество заказанных товаров
			//используется в расчете степени выполнения условий акции
		public int getOrderedQuantity() {
			int order = 0;
			for (PromoCsku csku: mItems.values()){
				order+=csku.getItemQuantity();	
			}
			
			return order;
		}
	
		
	}