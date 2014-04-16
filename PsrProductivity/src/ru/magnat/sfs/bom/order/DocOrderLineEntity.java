package ru.magnat.sfs.bom.order;

import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericLineEntity;
import ru.magnat.sfs.bom.order.DocOrderLine.Units;
import ru.magnat.sfs.bom.ref.productitem.RefProductItem;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.ref.promo.RefPromoDetailsEntity;

@OrmEntityOwner(owner = DocOrderLine.class)
public class DocOrderLineEntity extends DocGenericLineEntity<DocOrderLine> {
	
	@OrmEntityField(DisplayName = "Номенклатура", isPrimary = 0, fields = "Item")
	public RefProductItemEntity ProductItem;
	@OrmEntityField(DisplayName = "Количество", isPrimary = 0, fields = "Quantity")
	public int Quantity;
	@OrmEntityField(DisplayName = "Единица", isPrimary = 0, fields = "UnitLevel")
	public int UnitLevel;
	@OrmEntityField(DisplayName = "Сумма", isPrimary = 0, fields = "Amount")
	public float Amount;
	@OrmEntityField(DisplayName = "Сумма без скидки", isPrimary = 0, fields = "AmountBase")
	public float AmountBase;
	@OrmEntityField(DisplayName = "Объем в SU", isPrimary = 0, fields = "Su")
	public float Su;
	
	// PROMO 2.0
	@OrmEntityField(DisplayName = "Детали промо-акции типа TPR", isPrimary = 0, fields = "Promo1")
	public RefPromoDetailsEntity Promo1;
	@OrmEntityField(DisplayName = "Процент предоставленной бонусной скидки типа TPR", isPrimary = 0, fields = "Bonus1Discount")
	public float Bonus1Discount;
	@OrmEntityField(DisplayName = "Сумма предоставленной бонусной скидки типа TPR", isPrimary = 0, fields = "Bonus1Amount")
	public float Bonus1Amount;
	
	@OrmEntityField(DisplayName = "Детали промо-акции типа BAG", isPrimary = 0, fields = "Promo2")
	public RefPromoDetailsEntity Promo2;
	@OrmEntityField(DisplayName = "Процент предоставленной бонусной скидки типа BAG", isPrimary = 0, fields = "Bonus2Discount")
	public float Bonus2Discount;
	@OrmEntityField(DisplayName = "Сумма предоставленной бонусной скидки типа BAG", isPrimary = 0, fields = "Bonus2Amount")
	public float Bonus2Amount;
	@OrmEntityField(DisplayName = "Количество предоставленных бонусов", isPrimary = 0, fields = "Bonus2Count")
	public int Bonus2Count;
	@OrmEntityField(DisplayName = "Детали промо-акции типа Объем", isPrimary = 0, fields = "Promo4")
	public RefPromoDetailsEntity Promo4;
	@OrmEntityField(DisplayName = "Тип компенсации акции 4", isPrimary = 0, fields = "Promo4CompensationType")
	public int Promo4CompensationType;
	@OrmEntityField(DisplayName = "Детали промо-акции типа CAG", isPrimary = 0, fields = "Promo3")
	public RefPromoDetailsEntity Promo3;
	@OrmEntityField(DisplayName = "Тип компенсации акции 3", isPrimary = 0, fields = "Promo3CompensationType")
	public int Promo3CompensationType;
	
	public Boolean IsRemoved = false;
	
	public DocOrderLineEntity() {
	
	}
	
	public DocOrderLineEntity(long id, long author, Long productItemId, int abc) {
		MasterDocId = id;
		MasterDocAuthor = author;
		RefProductItem ref = new RefProductItem(MainActivity.getInstance());
		ProductItem = ref.FindById(productItemId);
	
		ref.close();
	}

	@Override
	public boolean equals(Object o) {
		DocOrderLineEntity object = (DocOrderLineEntity) o;
		if (ProductItem == null) {
			return false;
		}
		if (object == null) {
			return false;
		}
		if (Quantity != object.Quantity) {
			return false;
		}
		if (UnitLevel != object.UnitLevel) {
			return false;
		}
		if (Amount != object.Amount) {
			return false;
		}
		if (AmountBase != object.AmountBase) {
			return false;
		}
		if (Su != object.Su) {
			return false;
		}
		if (Bonus1Amount != object.Bonus1Amount) {
			return false;
		}
		if (Bonus1Discount != object.Bonus1Discount) {
			return false;
		}
		if (Promo1 != object.Promo1) {
			return false;
		}
		if (Bonus2Amount != object.Bonus2Amount) {
			return false;
		}
		if (Bonus2Discount != object.Bonus2Discount) {
			return false;
		}
		if (Bonus2Count != object.Bonus2Count) {
			return false;
		}
		if (Promo2 != object.Promo2) {
			return false;
		}
		if (Promo4 != object.Promo4) {
			return false;
		}
		if (Promo4CompensationType != object.Promo4CompensationType) {
			return false;
		}
		if (Promo3 != object.Promo3) {
			return false;
		}
		if (Promo3CompensationType != object.Promo3CompensationType) {
			return false;
		}
		if (!ProductItem.equals(object.ProductItem)) {
			return false;
		}
		
		return Id == object.Id;
	}
	
	public RefProductItemEntity getProductItem() {
		return ProductItem;
	}

	public void setProductItem(RefProductItemEntity val) {
		ProductItem = val;
	}
	
	public void setProductItemId(Long val) {
		RefProductItem ref = new RefProductItem(MainActivity.getInstance()); 
		ProductItem = ref.FindById(val);
		ref.close();
	}

	public Integer getQuantity() {
		return Quantity;
	}

	public void setQuantity(Integer val, Units unit) {
		getQuantity();
		Quantity = val;
		setUnit(unit);
		// onLineQuantityChanged(old,Quantity);
	}

	public Units getUnit() {

		switch (UnitLevel) {
		case 0:
			return Units.Piece;
		case 1:
			return Units.Case;
		case 2:
			return Units.Block;

		}
		return Units.Piece;
	}

	public void setUnit(Units val) {
		getUnit();
		switch (val) {
		case Piece:
			UnitLevel = 0;
			break;
		case Case:
			UnitLevel = 1;
			break;
		case Block:
			UnitLevel = 2;
			break;
		default:
			UnitLevel = 0;
		}
		// onLineUnitChanged(old,getUnit());
	}

	public float getAmount() {
		return Amount;
	}

	public void setAmount(float val) {
		getAmount();
		Amount = val;
		
		// onLineAmountChanged(old, Amount);
	}

	public float getSu() {
		return Su;
	}

	public void setSu(float val) {
		getAmount();
		Su = val;
		// onLineAmountChanged(old, Amount);
	}

	public float getAmountBase() {
		return AmountBase;
	}

	public void setAmountBase(float val) {
		getAmountBase();
		AmountBase = val;
		// onLineAmountBaseChanged(old, AmountBase);
	}
	
	@Override
	public String toString(){
		return String.format("Строка заказа %d/%d %s кол-во %d (уровень единицы %d), сумма по базе %f, сумма со скидкой %f"
				, this.MasterDocAuthor,this.MasterDocId,this.ProductItem.toString(),this.Quantity,this.UnitLevel, this.AmountBase,this.Amount);
	}

}
