package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.SfsEnum;

public class PromoType extends SfsEnum {
	public PromoType(int id) {
		super(new String[] { "Снижение цены", "Купи и получи", "Купи и получи (на период)", "Купи и получи (объемная)" }, id);
		
	}
	final public static int PRICING = 1;
	final public static int BAG_ORDER = 2;
	final public static int BAG_PERIOD = 3;
	final public static int BAG_VOLUME = 4;
	

}
