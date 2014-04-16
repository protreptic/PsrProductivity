package ru.magnat.sfs.bom.query.measures;

public interface TrpMeasureItemChangeListener {
	public void onTrpMeasureItemPriceReducedChange(Integer id, Boolean isPriceReduced);
	public void onTrpMeasureItemPriceLabelPresentedChange(Integer id, Boolean isPriceLabelPresented);
	public void onTrpMeasureItemPhotoChange(Integer id, String file);
}
