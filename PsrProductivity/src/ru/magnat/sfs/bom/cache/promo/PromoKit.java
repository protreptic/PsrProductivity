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
						res = "���� ��� ������� "+mMinKitQuantity+" �� ����� CSKU �� ������";
					}
					else {
						res = "���� ��� ������� ���� ����� ������ CSKU �� ������";
					}
				else {
					res = "���� ��� ������� ���� �����";
				}
			} 
			else if (mMinCskuQuantity>1) {
				res = "���� "+mMinCskuQuantity+" ������ CSKU �� ������";
				if (mMinOrderQuantity>1) res+=" �� "+mMinOrderQuantity+" �� �������";
			}
			else {
				if (mItems.size()>1)
					res = "���� ��� ������� "+mMinOrderQuantity+" �� ������ �� CSKU";
				else
					res = "���� ��� ������� "+mMinOrderQuantity+" ��";
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
			
			//������ ������������ ��������� �������:
			// MustHave, ����������, ��� CSKU
			// ������: ������� ��������� ������������ � ���������� ����� ������������ ���������� ��������� ����� ���� 
			// (��� ����� ��������� ����� ����� ������������ CSKU)
			//  ���� � ���������� ���� �� ������������ �� �������� (�� ���� ����� ������� ����� = 0), �� ������ ���-�� ��������� ������ ���
			//  ���� ���������� ������������ ���������� ������������ csku ����� ������������ ���������� ������������ (�������� ���� ��� ����� ������������ ����-�� � ����������� 
			//  ������ ���� ��� ������������), �� ������ ���� ���� � ������������ ���������� ������������ � ��������� ����� ������������ ����� ������������
			//  ���� �� ���������� ������������ ������������ ������ ��� ���������� ����������� ������������, �� ��� ����� ��������� ��� ����� ��������� ��� ������������ �����,
			//  ��� ����� ���� ������ �� ������� � ��������� ������� �������� �������� �� ������� ������������, ������� ��� �� ������� �� ���������� ������
			//  ���������� � ������� ������������ ���������� ��� ���������� ����������� ���������, ��� ���� ��������� �� ��� �������� ���������� ����������� �������
			//  ��� ��������� ������������, �������� � ��������, ���� � �������� ����� ������� �� ������� ������, �� ���������� ������ (�� ������� ������������)
			//  ����� ����������� ��������� ��������� �� ��������� �� ������������ ��������, ���������� ��� ������ ������������, ���� ���������, ��
			//  ������������ ��������� ������������ ��������, 
     		// 27.12.2013 �� ������ ������� ��������, ���� ��� ������ ������ ���� CSKU, �� ����������� ���������� ��� CSKU
     		// ���������� 1, � �������� ������������ ������ ��������� �� ����� ������, ������� � ����� ������� ����� �� ���� ��
     		// ����������� ����� ������ � ���������� �������
     		// 
     		int tempQuantity = 0;
			int maxDistr = mMinCskuQuantity;
			int maxQuantity = Integer.MAX_VALUE;
			Boolean overflow = false;
			//������� �������� ����������
			for (int i = 0; i<cskus.length;i++){
				PromoCsku csku = cskus[i]; 
				csku.setBonusQuantity(0);
				csku.setTempQuantity(csku.getQuantity());
			}
			for (int i = 0; i<cskus.length;i++)	{
				PromoCsku currentCsku = cskus[i];
				
				Log.d(MainActivity.LOG_TAG, "�������� ����� CSKU: Id = " + currentCsku.mCskuId + ", MustHave = " + currentCsku.mMustHave + ", mQuantity = " + currentCsku.getQuantity());
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
				mQuantity = Integer.MAX_VALUE; //��� �������� ����� ��� ��������� �� �����
			Log.d(MainActivity.LOG_TAG,"��������� ������ "+this.mKitId+" = "+mQuantity+" ("+this.hashCode()+")");
			
		}
		
		public boolean chargeBonus(int selledPromo) {
			int multiplicity = selledPromo;
			PromoCsku[] cskus = mItems.values().toArray(new PromoCsku[0]);
			Arrays.sort(cskus);
			selledPromo*=this.mMinKitQuantity*this.mMinOrderQuantity;
			//������ ������������ ��������� �������:
			// MustHave, ����������, ��� CSKU
			// ������: ������� ��������� ������������ � ��������� �����, ������� ���������� ������ ������ ��������������� ������������
			// ���� ������ ������ ��������������� ������������ ����� 0, �� ����������� ������� ������
			// ���� �� ��������������� ������������ �������� ���������� ������ ������ ���������� ��� �������� ������ � �������� �� ��� �� ���������� ���������� �� ���� ���������
			// ������� ������, ����������� ����� ����� �������� � ����
			int maxDistr = mMinCskuQuantity;
			boolean overflow = false;
			//������� �����
			for (PromoCsku csku:cskus){
				csku.setBonusQuantity(0);
				csku.setTempQuantity(csku.getQuantity());
				csku.setFreeProduct(0);
			}
			for (int i = 0; i<cskus.length && selledPromo > 0 && maxDistr>0;i++){
				PromoCsku currentCsku = cskus[i];

				if (currentCsku.mMustHave) {
					maxDistr--;
					currentCsku.setTempQuantity(currentCsku.getTempQuantity()-selledPromo); //��� ������������ ��������� ����������, ����� ��������� ������ �� ����, ��� ��� ���
					currentCsku.setBonusQuantity(selledPromo); //������� ������ ��� ������� ������ ����� ������ ��� ����� ������ ���������� ������ �������������
					continue; //������, � � ���������� ������ ������������ ����������� �������� ������ �� ���� �������
				}
				int  quantity = currentCsku.getTempQuantity();
				if (quantity == 0) 
					continue;
				int charge =  (selledPromo > quantity) ? quantity : selledPromo; //����������� ���������� ������
				selledPromo -= charge; //��������� ������ ������
				for (int j = i; j < i + maxDistr;j++){ //���� �� ���� csku � �������� ������������ �������������, ������� � ��� ��� ������ ������������   
					if (j < cskus.length) {  //�� ����������, �.�. ������ ��������� ���������� ������ �����������
						PromoCsku csku = cskus[j];
						csku.setBonusQuantity(csku.getBonusQuantity()+charge); //��������� �����
						csku.setTempQuantity(csku.getTempQuantity()-charge);  //��������� ���������� ����������
					}
					else {
						overflow = true; //������� �� ������� ������� (��� ���������� ������ ������ �� ���� ���� �� ������)
						break;
					}
				}
				if (overflow) 
					break;


			}
			boolean changed = false;
			//���������� ������, ������ ���� �������� ��������� � �����
			for (PromoCsku csku:cskus){
				 int free  = multiplicity * csku.getFreeProductSize();
				 int bonus = csku.getBonusQuantity()*this.mMinOrderQuantity;
					 Log.i(MainActivity.LOG_TAG, "����� ���������� = "+csku.getBonusQuantity()+" �� ������� "+ bonus +" ��������� ="+free);
					 if (mItems.get(csku.mCskuId).getBonusQuantity()!=bonus){
						 Log.d(MainActivity.LOG_TAG, "����� �� ����� ��������� "+bonus);
						 changed = true;
						 mItems.get(csku.mCskuId).setBonusQuantity(bonus);
					 }
					 if (mItems.get(csku.mCskuId).getFreeProduct()!=free) {
						 Log.d(MainActivity.LOG_TAG, "���������� ����� ��������� "+free);
						 changed = true;
						 mItems.get(csku.mCskuId).setFreeProduct(free);
					 }
			}
			return changed;
			
		}

		public Integer getBonusProductQuantity(long cskuId, long itemId) {
			PromoCsku csku = mItems.get(cskuId);
			if (csku==null) return -1; //��� � ������
			return csku.getBonusedProductQuantity(itemId);
		}

		public Integer getFreeProductQuantity(long cskuId, long itemId) {
			PromoCsku csku = mItems.get(cskuId);
			if (csku==null) return -1; //��� � ������
			return csku.getFreeProductQuantity(itemId);
		}

		public Integer getMinOrderQuantity(long cskuId) {
			PromoCsku csku = mItems.get(cskuId);
			if (csku==null) return -1; //��� � ������
			return this.mMinOrderQuantity;
		}
		//������� ���������� ���������� ���������� �������
			//������������ � ������� ������� ���������� ������� �����
		public int getOrderedQuantity() {
			int order = 0;
			for (PromoCsku csku: mItems.values()){
				order+=csku.getItemQuantity();	
			}
			
			return order;
		}
	
		
	}