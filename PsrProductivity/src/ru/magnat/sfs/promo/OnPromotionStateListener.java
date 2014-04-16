package ru.magnat.sfs.promo;

import ru.magnat.sfs.bom.IEventListener;

public interface OnPromotionStateListener extends IEventListener {
	public void onPromotionCompensationTypeChanged(int compensationType, Long promotionId);
	public void onPromotionTypeAvailabilityChanged(Boolean availability, int promotionType);
	public void onPromotionAvailabilityChanged(Boolean availability, Long promotionId);
	public void onPromotionCounterChanged(Integer counter, Long promotionId);
}
