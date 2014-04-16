package ru.magnat.sfs.bom.order;

public interface OnChangeQuantityListener{
	public void OnChangeQuantity(DocOrderLineEntity oldline, DocOrderLineEntity newline);
}	
