package ru.magnat.sfs.bom.invoice;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericLineEntity;
import ru.magnat.sfs.bom.order.DocOrderLine.Units;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;

@OrmEntityOwner(owner = DocInvoiceLine.class)
public class DocInvoiceLineEntity extends DocGenericLineEntity<DocInvoiceLine> {
	@OrmEntityField(DisplayName = "������������", isPrimary = 0, fields = "Item")
	public RefProductItemEntity ProductItem;
	@OrmEntityField(DisplayName = "����������", isPrimary = 0, fields = "Quantity")
	public int Quantity;
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "UnitLevel")
	public int UnitLevel;
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Amount")
	public float Amount;
	@OrmEntityField(DisplayName = "����� ��� ������", isPrimary = 0, fields = "AmountBase")
	public float AmountBase;
	@OrmEntityField(DisplayName = "����� � SU", isPrimary = 0, fields = "Su")
	public float Su;

	public RefProductItemEntity getProductItem() {
		return ProductItem;
	}

	public void setProductItem(RefProductItemEntity val) {
		ProductItem = val;
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
		return String.format("������ ������ %d/%d %s ���-�� %d (������� ������� %d), ����� �� ���� %f, ����� �� ������� %f"
				, this.MasterDocAuthor,this.MasterDocId,this.ProductItem.toString(),this.Quantity,this.UnitLevel, this.AmountBase,this.Amount);
	}

}
